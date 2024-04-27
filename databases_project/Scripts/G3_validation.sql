---- POKEMON ----------------------------------------------------------------------------------
SELECT COUNT(*) AS stats FROM Base_Statistics;

SELECT a.name, a.short_description, a.long_description
FROM Ability AS a
JOIN has_ability AS ha ON a.id = ha.id_ability
JOIN Species AS s ON s.id = ha.id_species
WHERE s.name = 'bulbasaur';

SELECT a.name, COUNT(s.id)
FROM ability AS a JOIN has_ability AS ha ON ha.id_ability = a.id
JOIN species AS s ON s.id = ha.id_species
WHERE a.name LIKE 'damp'
GROUP BY (a.name);

SELECT t.name
FROM Type AS t
JOIN has_type AS ht ON t.name = ht.type_name
JOIN Species AS s ON s.id = ht.id_species
WHERE s.name = 'bulbasaur';

SELECT gr.title, gr.formula
FROM Growth_Rate AS gr
JOIN Species AS s ON s.growth_rate = gr.id
WHERE s.name = 'bulbasaur';

SELECT species.id, species.growth_rate, has_level.necessary_xp, level.number
FROM Species AS species
JOIN growth_rate AS growth_rate ON growth_rate.id = species.growth_rate
JOIN has_level AS has_level ON has_level.id_growth_rate = growth_rate.id
JOIN level AS level ON level.number = has_level.id_level
WHERE species.id = 1;

SELECT species.id, species.growth_rate, COUNT(level.number)
FROM Species AS species JOIN growth_rate AS growth_rate ON growth_rate.id = species.growth_rate
JOIN has_level AS has_level ON has_level.id_growth_rate = growth_rate.id JOIN level AS level ON level.number = has_level.id_level
WHERE species.id = 1
GROUP BY (species.id, species.growth_rate);

SELECT species.name, affect.name_type1, affect.name_type2, affect.multiplier
FROM species AS species JOIN has_type AS ht ON species.id = ht.id_species
JOIN type AS t ON ht.type_name = t.name JOIN affect AS affect on t.name = affect.name_type1
WHERE species.name LIKE 'bulbasaur';

SELECT DISTINCT s1.name, s2.name, e.level
FROM evolve AS e
JOIN Species AS s1 ON e.id_current_species = s1.id
JOIN Species AS s2 ON e.id_next_species = s2.id
WHERE e.trigger LIKE 'level up';

SELECT t.id AS trainer, s.id AS species
FROM Species AS s JOIN has_species AS hs ON s.id = hs.id_species
JOIN Trainer AS t ON hs.id_trainer = t.id;



-----TRAINER ------------------------------------------------------------------------------------
SELECT COUNT(DISTINCT id_battle) AS Num_Battles FROM fight;

SELECT b.id AS battle, b.winner, pc.id AS pokemon_id, pc.nickname, pc.team
FROM fight JOIN battle b on fight.id_battle = b.id
JOIN pokemon_creature pc on fight.id_creature = pc.id
WHERE b.id = 0 AND pc.team = b.winner;

SELECT COUNT(id) FROM nature;

SELECT nature.name, stat_increase, lf.name, df.name FROM nature
JOIN flavour lf ON lf.name = like_flavour
JOIN flavour df ON df.name = nature.dislike_flavour
WHERE nature.name = 'bold';

SELECT COUNT(id) AS num_battles FROM battle;

SELECT battle.id, winner, loser, winner_trainer.id, loser_trainer.id
FROM battle JOIN team wt on battle.winner = wt.id
JOIN team lt ON battle.loser = lt.id
JOIN trainer winner_trainer ON wt.trainer = winner_trainer.id
JOIN trainer loser_trainer ON lt.trainer = loser_trainer.id
WHERE battle.id = 0 OR battle.id = 599;

SELECT COUNT(id) AS num_teams FROM team;

SELECT team.id, trainer.id, p.id, p.position, p.hp, p.status_condition
FROM pokemon_creature p JOIN team ON p.team = team.id
JOIN trainer ON team.trainer = trainer.id WHERE trainer.id = 0;

SELECT COUNT(id) AS num_trainers FROM trainer;

DROP TABLE IF EXISTS csv_Trainer CASCADE;
CREATE TABLE csv_Trainer(
    id INT PRIMARY KEY,
    class VARCHAR(50),
    name VARCHAR(50),
    xp INT,
    gold INT,
    gift VARCHAR(50),
    criminal_organization VARCHAR(50),
    sidekick INT,
    phrase TEXT,
    money_steal VARCHAR(50)
);
COPY csv_Trainer FROM '/Users/Shared/DB-2022-2023-datasets/trainers.csv' DELIMITER ',' CSV HEADER;

SELECT COUNT(id) AS num_criminals FROM csv_Trainer
WHERE criminal_organization IS NOT NULL;

SELECT COUNT(id) AS num_evil_criminals FROM evil_criminal;

SELECT id, sidekick, criminal_organization FROM csv_Trainer
WHERE criminal_organization IS NOT NULL AND sidekick IS NOT NULL;

SELECT trainer.id, sidekick.id, organization.name from trainer JOIN evil_criminal
ON trainer.id = evil_criminal.id JOIN evil_criminal sidekick ON evil_criminal.sidekick = sidekick.id
JOIN criminal_organization organization on evil_criminal.criminal_organization = organization.name;

SELECT COUNT(id) AS num_gifts FROM csv_Trainer WHERE gift IS NOT NULL;

SELECT COUNT(id) AS num_gifts_gym FROM gym WHERE id_item IS NOT NULL;

SELECT id, gift FROM csv_Trainer WHERE gift IS NOT NULL;

SELECT trainer.id, gym.id_item, item.name FROM trainer JOIN gym_leader gl ON trainer.id = gl.id
JOIN gym ON gl.gym = gym.id JOIN item ON gym.id_item = item.id;


SELECT co.name, co.building, a.name, t.name, co.region FROM criminal_organization co
JOIN area a on co.base_location = a.id JOIN evil_criminal ec on co.name = ec.criminal_organization
JOIN trainer t ON ec.id = t.id WHERE ec.is_leader = TRUE;

DROP TABLE IF EXISTS csv_Move;
CREATE TABLE csv_Move(
    id INT PRIMARY KEY,
    name VARCHAR(50),
    accuracy_percentage INT,
    effect TEXT,
    pp INT,
    priority_order INT,
    target_type VARCHAR(50),
    type VARCHAR(50),
    damage_class VARCHAR(50),
    hp_healing INT,
    hp_drain INT,
    power INT,
    flinch_change INT,
    min_hits INT,
    max_hits INT,
    ailment VARCHAR(50),
    ailment_chance INT,
    stat VARCHAR(50),
    stat_change_rate INT,
    change_amount INT
);
COPY csv_Move FROM '/Users/Shared/DB-2022-2023-datasets/moves.csv'
    DELIMITER ',' CSV HEADER;

SELECT COUNT(id) AS num_moves FROM move;

SELECT id, name, accuracy_percentage, pp, power, hp_healing, hp_drain
FROM csv_Move WHERE hp_healing > 0 OR hp_drain > 0;
SELECT m.id, m.name, m.accuracy_percentage, m.pp, m.power, life_points
FROM healing JOIN move m ON healing.id = m.id;

SELECT id, name, accuracy_percentage, pp, power, hp_healing, hp_drain
FROM csv_Move WHERE hp_healing < 0 OR hp_drain < 0;
SELECT m.id, m.name, m.accuracy_percentage, m.pp, m.power, strength
FROM damage JOIN move m ON damage.id = m.id;

SELECT id, name, accuracy_percentage, pp, power, ailment, ailment_chance
FROM csv_Move WHERE ailment IS NOT NULL;

SELECT m.id, m.name, m.accuracy_percentage, m.pp, m.power, special_effect, chance_to_apply
FROM status JOIN move m ON status.id = m.id;

DROP TABLE IF EXISTS csv_Pokemon_Creature CASCADE;
CREATE TABLE csv_Pokemon_Creature(
    id INT PRIMARY KEY,
    species INT,
    nickname VARCHAR(50),
    trainer INT,
    level INT,
    experience INT,
    gender VARCHAR(50),
    nature_name VARCHAR(50),
    item VARCHAR(50),
    date_time TIMESTAMP,
    subarea INT,
    method VARCHAR(50),
    pokeball VARCHAR(50),
    move1 INT,
    move2 INT,
    move3 INT,
    move4 INT
);
COPY csv_Pokemon_Creature FROM '/Users/Shared/DB-2022-2023-datasets/pokemon_instances.csv'
    DELIMITER ',' CSV HEADER;

SELECT id, nickname, species, trainer, nature_name, subarea, pokeball FROM csv_Pokemon_Creature;
SELECT pc.id, pc.nickname, s.name, t.name, n.name, subarea.name, i.name FROM pokemon_creature pc
JOIN species s ON pc.species = s.id JOIN trainer t ON pc.trainer = t.id JOIN nature n on pc.nature = n.id
LEFT JOIN subarea on pc.subarea = subarea.id JOIN pokeball p on pc.pokeball = p.id JOIN item i ON i.id = p.id
    ORDER BY (pc.id) ASC;

SELECT id, name FROM species WHERE name = 'clefable' OR name = 'buizel'
OR name = 'sharpedo' OR name = 'baltoy' OR name = 'jumpluff';
SELECT id, name FROM trainer WHERE name = 'Gema';
SELECT id, name FROM subarea WHERE id = 379 OR id = 496 OR id = 481;


----SHOPPING--------------------------------------------------------------------------------------

SELECT i.id, name, base_price, description, can_revive, b.growing_time, b.maximum_amount,
       b.natural_gift_powder, b.size, b.softness, b.soil_dryness, b.firmness, b.extra_int
FROM Item as i JOIN Berrie as b ON i.id = b.id_item
ORDER BY b.extra_int
LIMIT 8;

DROP TABLE IF EXISTS csv_Item CASCADE;
CREATE TABLE csv_Item (
	id INT,
	name VARCHAR(50),
	price INT,
	effect TEXT,
	healing VARCHAR(50),
	can_revive BOOLEAN,
	statistic VARCHAR(50),
	stat_increase_time INT,
	top_capture_rate INT,
	min_capture_rate INT,
	quick_sell_price INT,
	collector_price INT,
	move VARCHAR(50)
);

COPY csv_Item FROM '/Users/Shared/DB-2022-2023-datasets/items.csv'
DELIMITER ',' CSV HEADER;
SELECT * FROM csv_item WHERE name LIKE '%berry'
ORDER BY id
LIMIT 8;

DROP TABLE IF EXISTS csv_Berrie CASCADE;
CREATE TABLE csv_Berrie (
	id_berrie INT,
	name VARCHAR(50) PRIMARY KEY,
	growth_time INT,
	max_num_harvest INT,
	natural_gift_powder INT,
	berry_avg_size INT,
	smoothness INT,
	soil_dryness INT,
	firmness VARCHAR(50)
);

COPY csv_Berrie FROM '/Users/Shared/DB-2022-2023-datasets/berries.csv'
DELIMITER ',' CSV HEADER;
SELECT * FROM csv_berrie
ORDER BY id_berrie
LIMIT 8;

SELECT b.id_item, i.name, f.name, h.strength
FROM Item as i JOIN Berrie as b ON i.id = b.id_item
   JOIN has_flavour as h ON h.id_berrie = b.id_item
JOIN flavour as f ON h.flavour = f.name
LIMIT 8;

DROP TABLE IF EXISTS csv_Berrie_Flavor CASCADE;
CREATE TABLE csv_Berrie_Flavor (
	name VARCHAR(50),
	flavour VARCHAR(50),
	potency INT
);

COPY csv_Berrie_Flavor FROM '/Users/Shared/DB-2022-2023-datasets/berries_flavours.csv'
DELIMITER ',' CSV HEADER;
SELECT * from csv_Berrie_Flavor
LIMIT 8;

SELECT b.id_item, i.name, f.name, h.strength
FROM Item as i JOIN Berrie as b ON i.id = b.id_item
   JOIN has_flavour as h ON h.id_berrie = b.id_item
JOIN flavour as f ON h.flavour = f.name
WHERE h.strength = 20;

SELECT * FROM csv_Berrie_Flavor WHERE potency = 20;
SELECT i.id, i.name, i.base_price, i.description, i.can_revive, l.collector_price, l.quick_sell_price
FROM Item as i JOIN Loot as l ON i.id = l.id
ORDER BY i.id;

SELECT id, name, price, effect, can_revive, collector_price, quick_sell_price
FROM csv_item WHERE collector_price IS NOT NULL and quick_sell_price IS NOT NULL
ORDER BY id ;

SELECT i.id, i.name, i.base_price, i.description, i.can_revive, b.min_catch_rate, b.max_catch_rate
FROM Item as i JOIN Pokeball as b ON i.id = b.id
ORDER BY i.id
LIMIT 9;

SELECT id, name, price, effect, can_revive, min_capture_rate, top_capture_rate
FROM csv_item WHERE min_capture_rate IS NOT NULL and top_capture_rate IS NOT NULL
ORDER BY id
LIMIT 9;

SELECT i.id, i.name, i.base_price, i.description, i.can_revive, m.name
FROM Item as i JOIN move_machine as mm ON i.id = mm.id
JOIN move as m ON mm.move = m.id
ORDER BY i.id;

SELECT id, name, price, effect, can_revive, move FROM csv_item WHERE move NOT LIKE ''
ORDER BY id ;

SELECT i.id, i.name, i.base_price, i.description, i.can_revive, h.hp_recovered, h.status_heal,
       h.hp_percentage
FROM Item as i JOIN healing_item as h ON i.id = h.id
ORDER BY i.id;


SELECT id, name, price, effect, can_revive, healing FROM csv_item WHERE healing IS NOT NULL
ORDER BY id ;

SELECT i.id, i.name, i.base_price, i.description, i.can_revive, s.stat_to_double, s.time_period
FROM Item as i JOIN stat_enhancer as s ON i.id = s.id
ORDER BY i.id;

SELECT id, name, price, effect, can_revive, statistic, stat_increase_time
FROM csv_item WHERE statistic IS NOT NULL and  stat_increase_time IS NOT NULL
ORDER BY id ;

SELECT t.id, i.id, o.obtention_method, o.date_time
FROM Trainer as t JOIN owns_items AS o ON t.id = o.trainer_id
JOIN Item as i ON o.item_id = i.id
WHERE obtention_method LIKE '%FOUND%';

SELECT * FROM owns_items
WHERE obtention_method LIKE '%FOUND%';

SELECT t.name, i.name, o.obtention_method, o.date_time
FROM Trainer as t JOIN owns_items AS o ON t.id = o.trainer_id
JOIN Item as i ON o.item_id = i.id
WHERE obtention_method LIKE '%FOUND%';

SELECT s.id, s.name, i.id, se.stock, se.discount
FROM shop as s
    JOIN sell as se ON s.id = se.store_id
JOIN item as i ON se.item_id = i.id
WHERE se.discount > 40 and se.discount <43
ORDER BY discount;

SELECT s.id, t.id, i.id, p.amount, p.final_price, p.discount, p.date_time
FROM shop as s JOIN purchase as p ON s.id = p.store_id
JOIN item as i ON p.item_id = i.id
JOIN trainer as t ON p.trainer_id = t.id
WHERE p.amount < 2 and p.final_price < 10 and p.discount > 56;

DROP TABLE IF EXISTS csv_purchase CASCADE;
CREATE TABLE csv_purchase (
  store_id INT,
  trainer_id INT,
  item_id INT,
  amount INT,
  cost INT,
  discount INT,
  date_time TIMESTAMP,
  PRIMARY KEY (store_id, trainer_id, item_id, date_time)
);
COPY csv_purchase FROM '/Users/Shared/DB-2022-2023-datasets/purchases.csv'
DELIMITER ',' CSV HEADER;
SELECT * FROM csv_purchase
WHERE amount < 2 and cost <10 and discount>56;

SELECT s.id, t.name, i.name, p.amount, p.final_price, p.discount, p.date_time
FROM shop as s JOIN purchase as p ON s.id = p.store_id
JOIN item as i ON p.item_id = i.id
JOIN trainer as t ON p.trainer_id = t.id
WHERE p.amount < 2 and p.final_price < 10 and p.discount > 56;

DROP TABLE IF EXISTS csv_Shop CASCADE;
CREATE TABLE csv_Shop (
	id INT,
	name VARCHAR(50),
	floors INT,
	city VARCHAR(50)
);
COPY csv_Shop FROM '/Users/Shared/DB-2022-2023-datasets/stores.csv'
DELIMITER ',' CSV HEADER;

SELECT name FROM csv_item where id=413;
SELECT name FROM csv_Trainer where id=5;
SELECT name FROM csv_shop where id=12;


---EXPLORATION-------------------------------------------------------------------------------------
SELECT count(*) FROM find;

SELECT * FROM Encounter WHERE subarea NOT IN (SELECT id FROM SubArea);

SELECT e.id, e.subarea, e.meeting_method FROM encounter e
 	JOIN find ON e.id = find.id_encounter
 	JOIN species s on find.id_species = s.id
 WHERE s.name = 'bulbasaur';

SELECT count(*) FROM Gym;

SELECT count(*) FROM gym join type on type.id = GYM.id_type
WHERE type.name = 'rock';

SELECT count(*) FROM subarea join area a on a.id = subarea.id_area
WHERE a.name = 'granite cave';

SELECT * FROM subarea WHERE name LIKE '%roaming%';

SELECT count(*) FROM City;

SELECT count(*) FROM area WHERE name LIKE 'city' OR name LIKE '%town%' OR name LIKE '%island%';

SELECT count(*) FROM Route;

SELECT count(*) FROM connect_ join route r on r.id = connect_.id_route
join area a on a.id = r.id WHERE a.name = 'route 2';


--DROP TABLES
DROP TABLE csv_Trainer;
DROP TABLE csv_Move;
DROP TABLE csv_Pokemon_Creature;
DROP TABLE csv_Item;
DROP TABLE csv_Berrie;
DROP TABLE csv_Berrie_Flavor;
DROP TABLE csv_purchase;
DROP TABLE csv_Shop;

