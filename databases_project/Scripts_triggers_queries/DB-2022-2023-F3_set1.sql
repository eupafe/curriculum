----------------
-- #Query 1.1#
--

SELECT DISTINCT s.name, bs.base_stat AS defense, bs2.base_stat AS special_defense, s.weight
FROM species AS s
JOIN has_stat AS hs1 ON s.id = hs1.id_species
JOIN base_statistics AS bs ON hs1.id_statistics = bs.id
JOIN has_stat AS hs2 ON s.id = hs2.id_species
JOIN base_statistics AS bs2 ON hs2.id_statistics = bs2.id
JOIN has_ability AS ha ON s.id = ha.id_species
JOIN ability a ON ha.id_ability = a.id
WHERE bs.name = 'defense' AND bs2.name = 'special defense' AND a.name = 'sturdy'
ORDER BY s.weight DESC
LIMIT 10;

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT ha.id_species, s.name
FROM species AS s
JOIN has_ability AS ha ON ha.id_species = s.id
WHERE id_ability = 5
ORDER BY s.weight DESC;

SELECT count(*)
FROM table
WHERE name LIKE “%a”

----------------
-- #Query 1.2#
--
SELECT AVG(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END) AS average_damage_bs,
       MAX(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END) AS max_damage_bs,
       MIN(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END) AS min_damage_bs
FROM species AS s
JOIN has_stat ON s.id = has_stat.id_species
JOIN base_statistics bs ON has_stat.id_statistics = bs.id
WHERE s.id IN (SELECT id_species
               FROM has_type
               WHERE type_name = 'fire');


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

SELECT s.name, ht.id_species, MAX(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END),
        MIN(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END)
FROM species AS s
JOIN has_type AS ht ON s.id = ht.id_species
JOIN has_stat hs on s.id = hs.id_species
JOIN base_statistics bs on hs.id_statistics = bs.id
WHERE ht.type_name = 'fire'
GROUP BY(s.name, ht.id_species);

SELECT AVG(CASE WHEN (bs.name = 'attack' OR bs.name = 'special attack' ) THEN bs.base_stat END)
FROM species AS s
JOIN has_stat hs on s.id = hs.id_species
JOIN base_statistics bs on hs.id_statistics = bs.id
WHERE s.id IN (SELECT id_species
               FROM has_type
               WHERE type_name = 'fire');

----------------
-- #Query 1.3#
--

SELECT a.name AS ability_name, a.short_description, s.name AS species_name
FROM species AS s JOIN has_ability AS ha ON s.id = ha.id_species
JOIN ability AS a ON a.id = ha.id_ability
WHERE s.basic_experience > (SELECT AVG(basic_experience)
                            FROM species)
ORDER BY (a.name, s.name) DESC;

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

SELECT AVG(basic_experience) AS average
FROM species;

SELECT s.name, s.basic_experience
FROM species AS s
WHERE s.basic_experience > 145.95
ORDER BY s.basic_experience DESC;

----------------
-- #Query 1.4#
--
SELECT stat_name, max_base_stat, evolved_species
FROM (
  SELECT evolved_species.name AS evolved_species, bs.name AS stat_name, bs.base_stat AS max_base_stat,
         ROW_NUMBER() OVER (PARTITION BY bs.name ORDER BY bs.base_stat DESC, evolved_species.id) AS rn
  FROM Species AS baby_species
  JOIN evolve ON baby_species.id = evolve.id_current_species
  JOIN Species AS evolved_species ON evolve.id_next_species = evolved_species.id
  JOIN Pokemon_Creature ON baby_species.id = Pokemon_Creature.species
  JOIN has_stat ON evolved_species.id = has_stat.id_species
  JOIN Base_Statistics AS bs ON has_stat.id_statistics = bs.id
  WHERE Pokemon_Creature.is_baby = TRUE
) AS subquery
WHERE rn = 1
ORDER BY max_base_stat DESC, stat_name;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 1.5#
--

(SELECT
    'Strength' AS type, attacker.name AS strongest_type, COUNT(*) AS strength_count
FROM affect AS a
JOIN Type AS attacker ON attacker.name = a.name_type1
WHERE a.multiplier = 'x2'
GROUP BY attacker.name
ORDER BY strength_count DESC
LIMIT 2)

UNION ALL

(SELECT
    'Resistance' AS type, defender.name AS resistant_type, COUNT(*) AS resistance_count
FROM affect AS a
JOIN Type AS defender ON defender.name = a.name_type2
WHERE a.multiplier = 'x0.5'
GROUP BY defender.name
ORDER BY resistance_count DESC
LIMIT 1);

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 1.6#
--
SELECT sc1.id AS species_id, sc1.name AS species_name, sc1.basic_experience AS species_experience,
       sc2.id AS evolved_species_id, sc2.name AS evolved_species_name, (sc1.basic_experience + sc2.basic_experience) AS evolved_species_experience,
       sc3.id AS final_evolved_species_id, sc3.name AS final_evolved_species_name,
       (sc1.basic_experience + sc2.basic_experience + sc3.basic_experience) AS final_evolved_species_experience
FROM Species AS sc1
JOIN evolve AS ev1 ON sc1.id = ev1.id_current_species
JOIN Species AS sc2 ON ev1.id_next_species = sc2.id
JOIN evolve AS ev2 ON sc2.id = ev2.id_current_species
JOIN Species AS sc3 ON ev2.id_next_species = sc3.id
WHERE ev1.trigger = 'level up'
  AND ev1.level <> 0
  AND ev2.trigger = 'level up'
  AND ev2.level <> 0
ORDER BY final_evolved_species_experience DESC
LIMIT 1;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

UPDATE species
SET basic_experience = 10000000
WHERE id = 1;

----------------
-- #Trigger 1.1#
--

DROP FUNCTION IF EXISTS check_max_types CASCADE;
CREATE OR REPLACE FUNCTION check_max_types() RETURNS TRIGGER AS $$
DECLARE
    types_count INT;
    total_entries INT;
BEGIN
    SELECT COUNT(id_species) INTO types_count
    FROM has_type
    WHERE id_species = NEW.id_species;

    total_entries := types_count + 1;

    IF types_count > 2 THEN
        INSERT INTO warnings (affected_table, date, error_message, "user")
        VALUES ('has_type', CURRENT_DATE, 'A ' || (total_entries - 1 )|| 'th entry has been inserted into the has_type table', current_user);

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS max_types_trigger ON has_type CASCADE;
CREATE TRIGGER max_types_trigger
AFTER INSERT OR UPDATE ON has_type
FOR EACH ROW
EXECUTE FUNCTION check_max_types();

SELECT * FROM warnings;


DROP FUNCTION IF EXISTS check_max_abilities CASCADE;
CREATE OR REPLACE FUNCTION check_max_abilities() RETURNS TRIGGER AS $$
DECLARE
    abilities_count INT;
    total_entries INT;
BEGIN
    SELECT COUNT(id_ability) INTO abilities_count
    FROM has_ability
    WHERE id_species = NEW.id_species AND hidden = FALSE;

    total_entries := abilities_count + 1;

    IF abilities_count > 2 THEN
        INSERT INTO warnings (affected_table, date, error_message, "user")
        VALUES ('has_ability', CURRENT_DATE, 'A ' || (total_entries - 1) || 'th entry has been inserted into the has_ability table', current_user);

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS max_abilities_trigger ON has_ability CASCADE;
CREATE TRIGGER max_abilities_trigger
AFTER INSERT OR UPDATE ON has_ability
FOR EACH ROW
EXECUTE FUNCTION check_max_abilities();


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

INSERT INTO Type (id, name)
VALUES (22, 'b1');
INSERT INTO has_type (id_species, type_name)
VALUES (1, 'b1');

----------------
-- #Trigger 1.2#
--



-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

