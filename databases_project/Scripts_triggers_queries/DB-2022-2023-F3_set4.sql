----------------
----------------
-- #Query 4.1.1#
--

SELECT region.name AS region_name, COUNT(subarea.id) AS subarea_count
FROM region
JOIN area ON region.name = area.name_region
JOIN subarea ON area.id = subarea.id_area
GROUP BY region.name
ORDER BY subarea_count DESC
LIMIT 1;
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
----------------
-- #Query 4.1.2#
--

SELECT region.name AS region_name, subarea_count
FROM region
JOIN (
    SELECT area.name_region, COUNT(subarea.id) AS subarea_count
    FROM area
    JOIN subarea ON area.id = subarea.id_area
    GROUP BY area.name_region
) AS subarea_counts ON region.name = subarea_counts.name_region
ORDER BY subarea_count DESC
LIMIT 1;
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 4.2#
--

SELECT s.name AS gym_name, a.name AS city_it_belongs_to,
       ar.name AS route_directly_connected, oa.name AS area_on_the_other_side
FROM subarea s
JOIN gym g ON s.id = g.id
JOIN area a ON s.id_area = a.id
JOIN city c ON a.id = c.id
JOIN connect_ co ON a.id = co.id_area
JOIN route r ON co.id_route = r.id
JOIN area ar ON ar.id = r.id
JOIN connect_ co2 ON r.id = co2.id_route
JOIN area oa ON oa.id = co2.id_area
WHERE a.name <> oa.name;
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 4.3#
--

SELECT e.id, p.name AS pokemon_name, s.name AS subarea_name, e.meeting_method, f.condition_type, f.condition_value, f.probability,
       f.min_level, f.max_level
FROM encounter e
JOIN subarea s ON e.subarea = s.id
JOIN area a ON s.id_area = a.id
JOIN region r ON a.name_region = r.name
JOIN find f ON f.id_encounter = e.id
JOIN species p ON f.id_species = p.id
WHERE r.name = 'sinnoh' AND e.meeting_method = 'surf';
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 4.4#
--

SELECT e.id, p.name AS pokemon_name, f.probability, f.min_level
FROM encounter e
JOIN find f on e.id = f.id_encounter
JOIN species p on f.id_species = p.id
WHERE f.probability <= 1 AND f.min_level =
(SELECT f2.min_level FROM find f2 WHERE f2.probability <= 1
ORDER BY min_level ASC LIMIT 1);
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 4.5#
--

UPDATE encounter
SET meeting_method =
    CASE
        WHEN meeting_method = 'good' THEN 'good rod'
        WHEN meeting_method = 'old' THEN 'old rod'
        WHEN meeting_method = 'super' THEN 'super rod'
    END
WHERE meeting_method IN ('good', 'old', 'super');

CREATE TEMPORARY TABLE available_methods AS
SELECT DISTINCT meeting_method
FROM encounter
WHERE meeting_method <> 'rock';

UPDATE encounter
SET meeting_method = (
    SELECT meeting_method
    FROM available_methods
    ORDER BY RANDOM()
    LIMIT 1
)
WHERE meeting_method = 'rock';
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 4.6#
--

SELECT DISTINCT a.name, r.pavement, f.condition_value
FROM area a
JOIN subarea s ON a.id = s.id_area
JOIN encounter e ON e.subarea = s.id
JOIN find f ON e.id = f.id_encounter
JOIN route r ON a.id = r.id
WHERE r.pavement = 'Grass' AND  (f.condition_value like 'rain' OR f.condition_value like 'night ');
-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Trigger 4.1#
--

-- Drop the trigger
DROP TRIGGER IF EXISTS add_city_info_trigger ON city;

-- Drop the sequences
DROP SEQUENCE IF EXISTS trainer_id_seq;
DROP SEQUENCE IF EXISTS subarea_id_seq;

CREATE OR REPLACE FUNCTION add_city_info_function()
RETURNS TRIGGER AS $$
DECLARE
  region_name VARCHAR(255);
  gym_name VARCHAR(255);
  gym_leader_name VARCHAR(255);
  gym_leader_id INT;
  experience INT;
  gold INT;
  gym_medal VARCHAR(255);
  item_id INT;
  type_id INT;
    city_name VARCHAR(255);
     subarea_id INT;
BEGIN
  -- Check if the region Lasalia exists
  SELECT name INTO region_name FROM region WHERE name = 'Lasalia';

  -- If not, create it
  IF region_name IS NULL THEN
    INSERT INTO region (name) VALUES ('Lasalia') RETURNING name INTO region_name;
  END IF;

   -- Generate random values
  city_name := 'City ' || (floor(random() * 1000) + 1)::text;
  gym_name := (floor(random() * 1000) + 1)::text || ' Gym';
  gym_medal := (floor(random() * 1000) + 1)::text || ' Badge';
  gym_leader_name := 'Gym Leader ' || (floor(random() * 1000) + 1)::text;
  experience := floor(random() * 1000) + 1;
  gold := floor(random() * 10000) + 1;

  -- Random item
  SELECT id FROM item ORDER BY random() LIMIT 1 INTO item_id;

  -- Random type
  SELECT id FROM type ORDER BY random() LIMIT 1 INTO type_id;

  -- Insert the city into area
  INSERT INTO area (id, name, name_region) VALUES (NEW.id, city_name, region_name);

  -- Get the next value from the subareas
  DROP SEQUENCE IF EXISTS sub_area_id_seq;
  CREATE SEQUENCE sub_area_id_seq;
  PERFORM setval('sub_area_id_seq', (SELECT MAX(id) FROM subarea));
  SELECT nextval('sub_area_id_seq') INTO subarea_id;

  -- Create the subarea associated with the gym
  INSERT INTO subarea (id, name, id_area) VALUES (subarea_id, gym_name, NEW.id);

  -- Create the gym associated with the city
  INSERT INTO gym (id, medal, id_item, id_type)
  VALUES (subarea_id, gym_medal, item_id, type_id);

  -- Get the next value from the trainers
  DROP SEQUENCE IF EXISTS trainer_id_seq;
  CREATE SEQUENCE trainer_id_seq;
  PERFORM setval('trainer_id_seq', (SELECT MAX(id) FROM trainer));
  SELECT nextval('trainer_id_seq') INTO gym_leader_id;

  -- Insert the gym leader into the trainer table
  INSERT INTO trainer (id, class, name, experience, gold) VALUES (gym_leader_id,'Gym Leader',gym_leader_name, experience, gold);

  -- Create the gym leader associated with the gym
  INSERT INTO gym_leader (id, gym) VALUES (gym_leader_id, subarea_id);

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER add_city_info_trigger
BEFORE INSERT ON city
FOR EACH ROW
EXECUTE FUNCTION add_city_info_function();

-- #Validation#
-- 

INSERT INTO city (id, population) VALUES (400, 100000);

----------------
-- #Trigger 4.2#
--

-- Drop the trigger
DROP TRIGGER IF EXISTS check_new_pokemon_trigger ON pokemon_creature;

--DROP TABLE IF EXISTS Warnings;
CREATE TABLE IF NOT EXISTS Warnings (
    id SERIAL PRIMARY KEY,
    trainer_id INT,
    species_name VARCHAR(255),
    route_name VARCHAR(255),
    message VARCHAR(255),
    date_obtained DATE
);

CREATE OR REPLACE FUNCTION check_new_pokemon()
RETURNS TRIGGER AS $$
DECLARE
    pavement_type VARCHAR(255);
    pokemon_min_level INT;
    pokemon_max_level INT;
    capture_chance INT;
    trainer_name VARCHAR(255);
    species_name VARCHAR(255);
    route_name VARCHAR(255);
BEGIN
    SELECT r.pavement INTO pavement_type
    FROM route r JOIN subarea s ON s.id_area = r.id
    WHERE s.id = NEW.subarea;

    SELECT p.name INTO species_name
    FROM species p WHERE p.id = NEW.species;

    SELECT t.name INTO trainer_name
    FROM trainer t WHERE t.id = NEW.trainer;

    SELECT f.min_level, f.max_level, f.probability
    INTO pokemon_min_level, pokemon_max_level, capture_chance
    FROM find f
    JOIN encounter e ON f.id_encounter = e.id
    WHERE f.id_species = NEW.species AND e.subarea = NEW.subarea
    LIMIT 1;

    SELECT a.name INTO route_name FROM area a
    JOIN subarea s ON a.id = s.id_area WHERE s.id = NEW.subarea;

    IF (pavement_type = 'water' AND NEW.method <> 'surf') THEN
        INSERT INTO Warnings (trainer_id, species_name, route_name, message, date_obtained)
        VALUES (NEW.trainer, species_name, route_name,
            CONCAT('Trainer ', trainer_name, ' captured ', species_name,
            ' in ', route_name,
            ' with an incorrect method, level, or capture chance.'), NEW.date_time);
    ELSIF (pavement_type <> 'water' AND (NEW.method <> 'walk' AND NEW.method <> 'headbutt')) THEN
        INSERT INTO Warnings (trainer_id, species_name, route_name, message, date_obtained)
        VALUES (NEW.trainer, species_name, route_name,
            CONCAT('Trainer ', trainer_name, ' captured ', species_name,
            ' in ', route_name,
            ' with an incorrect method, level, or capture chance.'), NEW.date_time);
    ELSIF (NEW.level < pokemon_min_level OR NEW.level > pokemon_max_level OR capture_chance <= 0) THEN
        INSERT INTO Warnings (trainer_id, species_name, route_name, message, date_obtained)
        VALUES (NEW.trainer, species_name, route_name,
            CONCAT('Trainer ', trainer_name, ' captured ', species_name,
            ' in ', route_name,
            ' with an incorrect method, level, or capture chance.'), NEW.date_time);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_new_pokemon_trigger
AFTER INSERT ON pokemon_creature
FOR EACH ROW
EXECUTE FUNCTION check_new_pokemon();

-- #Validation#
-- 

INSERT INTO pokemon_creature (id, species, nickname, trainer, level, experience, gender, nature, date_time, subarea, method, pokeball, is_baby, position, hp, status_condition, team, attack, defense, special_attack, special_defense, speed, evasion)
VALUES (13717, 72, 'octopus', 24, 20, 5423, 'male', 23, '2021-08-29 04:24:54.000000', 193, 'surf', 3, false, null, 70, null, null, 100,100,100,100,100,100);

