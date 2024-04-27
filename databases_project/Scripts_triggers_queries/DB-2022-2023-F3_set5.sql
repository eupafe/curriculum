----------------
----------------
-- #Query 5.1#
--

SELECT r.name, COUNT(t.id)
FROM region r JOIN criminal_organization co ON r.name = co.region
JOIN evil_criminal ec ON co.name = ec.criminal_organization
JOIN team tc ON tc.trainer = ec.id JOIN battle b ON tc.id = b.winner
JOIN team t ON t.id = b.loser
GROUP BY r.name
ORDER BY COUNT(t.id) DESC
LIMIT 1;

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

SELECT count(*)
FROM table
WHERE name LIKE “%a”

----------------
-- #Query 5.2#
--
SELECT pc.id, s.name AS species_name, a.name AS area_name
FROM pokemon_creature pc
JOIN species s on pc.species = s.id
JOIN subarea sa on pc.subarea = sa.id
JOIN area a ON sa.id_area = a.id
WHERE sa.id NOT IN (SELECT sa2.id FROM subarea sa2
   JOIN encounter e on pc.subarea = e.subarea
   JOIN find f on e.id = f.id_encounter
   JOIN species s2 on f.id_species = s2.id
   WHERE s2.id = s.id);


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.3#
--



-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.4#
--
SELECT tr.name AS gym_leader, gl.gym AS gym, t.name AS gym_type,
      p.id AS pokemon_id, s.name AS species_name, ht.type_name AS pokemon_type,
      tm.id AS team_id, m.name AS move_name, m.type AS move_type
FROM gym_Leader gl
JOIN gym g ON gl.gym = g.id
JOIN trainer tr ON gl.id = tr.id
JOIN team tm ON tr.id = tm.trainer
JOIN pokemon_Creature p ON tm.id = p.team
JOIN species s ON p.species = s.id
JOIN has_type ht ON p.species = ht.id_species
JOIN type t ON g.id_type = t.id
JOIN cause_or_receive cr ON p.id = cr.creature
JOIN move m ON cr.move = m.id
WHERE NOT EXISTS (
   SELECT 1
   FROM move mv
   WHERE mv.type = t.name
   AND mv.id = m.id
)
AND NOT EXISTS (
   SELECT 1
   FROM has_type ht2
   WHERE ht2.id_species = p.species
   AND ht2.type_name = t.name
)
ORDER BY gl.id, p.id, m.id;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.5#
--
SELECT ec.id AS criminal, pc.id AS pokemon
    FROM evil_criminal ec JOIN pokemon_creature pc ON pc.trainer = ec.id
    JOIN cause_or_receive cor ON pc.id = cor.creature
    JOIN move m ON cor.move = m.id JOIN healing h on m.id = h.id
    WHERE pc.method = 'gift' AND ec.is_leader = TRUE
    AND m.accuracy_percentage != 0 AND pc.subarea IS NOT NULL;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.6#
--



-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.7#
--
SELECT p.id, p.species, p.nickname, p.level, p.experience, p.nature,
       p.hp, (((2 * hp + (level ^ 2) / 4) / 100 + 5) * nature) as update_hp,
       p.attack, (((2 * p.attack + (level ^ 2) / 4) / 100 + 5) * nature) as updated_attack,
       p.defense, (((2 * p.defense + (level ^ 2) / 4) / 100 + 5) * nature) as updated_defense,
       p.special_attack, (((2 * p.special_attack + (level ^ 2) / 4) / 100 + 5) * nature) as updated_special_attack,
       p.special_defense, (((2 * p.special_defense + (level ^ 2) / 4) / 100 + 5) * nature) as updated_special_defense,
       p.speed, (((2 * p.speed + (level ^ 2) / 4) / 100 + 5) * nature) as updated_speed
FROM pokemon_creature as p
WHERE EXTRACT(YEAR FROM p.date_time) >= 2022 and EXTRACT(YEAR FROM p.date_time) <= 2023;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 5.8#
--



-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

