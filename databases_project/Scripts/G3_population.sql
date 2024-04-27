DROP TABLE IF EXISTS csv_locations CASCADE;
CREATE TABLE csv_locations (
    region VARCHAR(50),
    area VARCHAR(50),
    subarea VARCHAR(50),
    subareaID BIGINT,
    population BIGINT
);
COPY csv_locations(region, area, subarea, subareaID, population)  FROM '/Users/Shared/DB-2022-2023-datasets/locations.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO region (name)
SELECT DISTINCT region FROM csv_locations;

DROP TABLE IF EXISTS csv_Trainer CASCADE;
CREATE TABLE csv_Trainer(
    id INT,
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
INSERT INTO trainer
SELECT id, class, name, xp, gold FROM csv_Trainer;

DROP TABLE IF EXISTS csv_Growth_Rate;
CREATE TABLE csv_Growth_Rate (
    id INT,
    name VARCHAR(50),
    formula TEXT,
    level INT,
    experience INT
);
COPY csv_Growth_Rate(id, name, formula, level, experience)
    FROM '/Users/Shared/DB-2022-2023-datasets/growth_rates.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO Growth_Rate(id, title, formula)
SELECT DISTINCT id, name, formula FROM csv_Growth_Rate;

INSERT INTO Level(number)
SELECT DISTINCT level FROM csv_Growth_Rate;

COPY Ability(id, name, long_description, short_description) FROM '/Users/Shared/DB-2022-2023-datasets/abilities.csv'
DELIMITER ',' CSV HEADER;

COPY Type(id, name) FROM '/Users/Shared/DB-2022-2023-datasets/types.csv'
DELIMITER ',' CSV HEADER;

DROP TABLE IF EXISTS csv_Berrie_Flavor CASCADE;
CREATE TABLE csv_Berrie_Flavor (
	name VARCHAR(50),
	flavour VARCHAR(50),
	potency INT
);

COPY csv_Berrie_Flavor FROM '/Users/Shared/DB-2022-2023-datasets/berries_flavours.csv'
DELIMITER ',' CSV HEADER;
UPDATE csv_Berrie_Flavor SET name = CONCAT(name, ' berry');

INSERT INTO Flavour
SELECT DISTINCT flavour FROM csv_Berrie_Flavor;

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

ALTER TABLE csv_Item
ALTER COLUMN healing TYPE NUMERIC USING CAST(NULLIF(healing, '') AS NUMERIC);

INSERT INTO Item
SELECT id, name, price, effect, can_revive FROM csv_Item;

INSERT INTO berrie(id_item)
SELECT c.id FROM csv_item as c WHERE c.name LIKE '%berry';

DROP TABLE IF EXISTS csv_Base_Statistics;
CREATE TABLE csv_Base_Statistics (
    id SERIAL,
    stat VARCHAR(50),
    pokemon VARCHAR(50),
    base_stat INT,
    effort INT
);
COPY csv_Base_Statistics(stat, pokemon, base_stat, effort)
    FROM '/Users/Shared/DB-2022-2023-datasets/stats.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO Base_Statistics(id, name, effort, base_stat)
SELECT id, stat, effort, base_stat FROM csv_Base_Statistics;


INSERT INTO area (name, name_region)
SELECT DISTINCT area, region FROM csv_locations;

INSERT INTO city (id, population)
SELECT DISTINCT area.id, csv_locations.population from area
JOIN csv_locations ON csv_locations.area = area.name
WHERE csv_locations.population is not null;

DROP TABLE IF EXISTS csv_routes CASCADE;
CREATE TABLE csv_routes (
    name VARCHAR(50),
    north VARCHAR(50),
    east VARCHAR(50),
    west VARCHAR(50),
    south VARCHAR(50),
    pavement VARCHAR(50)
);
COPY csv_routes(name, north, east, west, south, pavement)  FROM '/Users/Shared/DB-2022-2023-datasets/routes.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO Route (id, pavement)
SELECT area.id, csv_routes.pavement FROM area
JOIN csv_routes ON lower(csv_routes.name) = lower(area.name);


INSERT INTO connect_ (id_area, id_route, cardinality)
SELECT ar.id, a.id, 'north'
FROM area a
    JOIN csv_routes ON LOWER(csv_routes.name) = lower(a.name)
    JOIN area ar ON lower(csv_routes.north) = lower(ar.name)
WHERE csv_routes.north is not null;

INSERT INTO connect_ (id_area, id_route, cardinality)
SELECT ar.id, a.id, 'east'
FROM area a
    JOIN csv_routes ON LOWER(csv_routes.name) = lower(a.name)
    JOIN area ar ON lower(csv_routes.east) = lower(ar.name)
WHERE csv_routes.east is not null;

INSERT INTO connect_ (id_area, id_route, cardinality)
SELECT ar.id, a.id, 'west'
FROM area a
    JOIN csv_routes ON LOWER(csv_routes.name) = lower(a.name)
    JOIN area ar ON lower(csv_routes.west) = lower(ar.name)
WHERE csv_routes.west is not null;

INSERT INTO connect_ (id_area, id_route, cardinality)
SELECT ar.id, a.id, 'south'
FROM area a
    JOIN csv_routes ON LOWER(csv_routes.name) = lower(a.name)
    JOIN area ar ON lower(csv_routes.south) = lower(ar.name)
WHERE csv_routes.south is not null;

INSERT INTO subarea (id, name)
SELECT subareaID, subarea FROM csv_locations
WHERE subareaID is not null;

UPDATE subarea
SET id_area = area.id
FROM area
JOIN csv_locations ON csv_locations.area = area.name AND csv_locations.region = area.name_region
WHERE csv_locations.subareaID = subarea.id;

DROP TABLE IF EXISTS csv_gyms CASCADE;
CREATE TABLE csv_gyms (
    leader VARCHAR(50),
    location VARCHAR(50),
    type VARCHAR(50),
    badge VARCHAR(50),
    name VARCHAR(50)
);
COPY csv_gyms(leader,location,type, badge, name) FROM '/Users/Shared/DB-2022-2023-datasets/gyms.csv'
DELIMITER ',' CSV HEADER;

DROP SEQUENCE IF EXISTS sub_area_id_seq;
CREATE SEQUENCE sub_area_id_seq;
SELECT setval('sub_area_id_seq', (SELECT MAX(id) FROM subarea));
INSERT INTO subarea (id, name, id_area)
SELECT nextval('sub_area_id_seq'), csv_gyms.name, area.id
FROM csv_gyms JOIN area ON lower(csv_gyms.location) = lower(area.name);

DROP TABLE IF EXISTS csv_Encounter CASCADE;
CREATE TABLE csv_Encounter(
    pokemon VARCHAR(50),
    subarea INT,
    meeting_method VARCHAR(50),
    condition_type VARCHAR(50),
    condition_value VARCHAR(50),
    probability INT,
    min_level INT,
    max_level INT
);
COPY csv_Encounter FROM '/Users/Shared/DB-2022-2023-datasets/encounters.csv'
    DELIMITER ',' CSV HEADER;
INSERT INTO encounter(subarea, meeting_method)
SELECT DISTINCT ON(subarea, meeting_method) subarea, meeting_method FROM csv_Encounter;


DROP TABLE IF EXISTS csv_Teams CASCADE;
CREATE TABLE csv_Teams(
    trainer INT,
    pokemon INT,
    slot INT,
    hp INT,
    status VARCHAR(50)
);
COPY csv_Teams FROM '/Users/Shared/DB-2022-2023-datasets/poketeams.csv' DELIMITER ',' CSV HEADER;
INSERT INTO team(trainer)
SELECT DISTINCT trainer FROM csv_Teams;


DROP TABLE IF EXISTS csv_Criminal_Organization CASCADE;
CREATE TABLE csv_Criminal_Organization(
    name VARCHAR(50),
    building VARCHAR(50),
    head_quarters VARCHAR(50),
    leader VARCHAR(50),
    region VARCHAR(50)
);
COPY csv_Criminal_Organization FROM '/Users/Shared/DB-2022-2023-datasets/villainous_organizations.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO criminal_organization
SELECT csv.name, building, area.id, region FROM csv_Criminal_Organization AS csv JOIN
    area ON lower(area.name) = lower(head_quarters);

COPY owns_items FROM '/Users/Shared/DB-2022-2023-datasets/traineritems.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO Evil_Criminal
SELECT id, CAST(CAST(money_steal AS FLOAT) AS BIGINT), phrase, FALSE, sidekick, criminal_organization
FROM csv_Trainer JOIN criminal_organization AS co ON co.name = criminal_organization;

UPDATE evil_criminal SET is_leader = true FROM csv_Criminal_Organization
JOIN trainer ON regexp_replace(trainer.name, '[^a-zA-Z0-9_]', '') =
                regexp_replace(leader, '[^a-zA-Z0-9_]', '')
WHERE evil_criminal.id = trainer.id;


DROP TABLE IF EXISTS csv_Nature CASCADE;
CREATE TABLE csv_Nature(
    name VARCHAR(50),
    id INT,
    stat_decrease VARCHAR(50),
    stat_increase VARCHAR(50),
    like_flavour VARCHAR(50),
    dislike_flavour VARCHAR(50)
);
COPY csv_Nature FROM '/Users/Shared/DB-2022-2023-datasets/natures.csv'
DELIMITER ',' CSV HEADER;


INSERT INTO nature SELECT name, id, stat_decrease, stat_increase
    FROM csv_Nature WHERE like_flavour LIKE'' AND dislike_flavour LIKE'';
INSERT INTO nature SELECT* FROM csv_Nature WHERE like_flavour NOT LIKE'' AND dislike_flavour NOT LIKE'';

INSERT INTO nature SELECT name, id, stat_decrease, stat_increase, like_flavour
    FROM csv_Nature WHERE like_flavour NOT LIKE'' AND dislike_flavour LIKE'';
INSERT INTO nature SELECT name, id, stat_decrease, stat_increase, dislike_flavour
    FROM csv_Nature WHERE dislike_flavour NOT LIKE'' AND like_flavour LIKE'';

INSERT INTO has_level(id_level, id_growth_rate, necessary_xp)
SELECT level, id, experience FROM csv_Growth_Rate;

DROP TABLE IF EXISTS csv_Species;
CREATE TABLE csv_Species(
    index INT,
    pokemon VARCHAR(50),
    baseExperience INT,
    height INT,
    weight INT,
    dex_order INT,
    growth_rate_ID INT,
    type1 VARCHAR(50),
    type2 VARCHAR(50)
);
COPY csv_Species
    FROM '/Users/Shared/DB-2022-2023-datasets/pokemon.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO Species(id, name, weight, height, basic_experience, dex_order, growth_rate)
SELECT index, pokemon, weight, height, baseExperience, dex_order, growth_rate_ID FROM csv_Species;

INSERT INTO has_stat(id_statistics, id_species)
SELECT csv_Base_Statistics.id, species.id FROM csv_Base_Statistics JOIN Species ON
REPLACE(REPLACE(regexp_replace(species.name, '[^a-zA-Z0-9 \_\.\-\s]', '', 'g'), ' ', ''), '-', '') = REPLACE(REPLACE(regexp_replace(pokemon, '[^a-zA-Z0-9\_\.\-\s]', '', 'g'), ' ', ''), '-', '');

INSERT INTO find(id_encounter, id_species, condition_type, condition_value, probability, min_level, max_level)
SELECT e.id, s.id, condition_type, condition_value, probability, min_level, max_level
FROM csv_Encounter JOIN species AS s ON s.name = pokemon JOIN encounter AS e ON
e.subarea = csv_Encounter.subarea AND e.meeting_method = csv_Encounter.meeting_method;


DROP TABLE IF EXISTS csv_Pokemon_Abilities;
CREATE TABLE csv_Pokemon_Abilities (
    speciesID INT,
    abilityID INT,
    slot INT,
    is_hidden BOOLEAN
);
COPY csv_Pokemon_Abilities(speciesID, abilityID, slot, is_hidden)
    FROM '/Users/Shared/DB-2022-2023-datasets/pokemon_abilities.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO has_ability(id_species, id_ability, hidden)
SELECT speciesID, abilityID, is_hidden FROM csv_Pokemon_Abilities;

INSERT INTO has_type(id_species, type_name)
SELECT index, type1 FROM csv_Species;
INSERT INTO has_type(id_species, type_name)
SELECT index, type2 FROM csv_Species WHERE type2 NOT LIKE '';

DROP TABLE IF EXISTS csv_Damage_Relations;
CREATE TABLE csv_Damage_Relations (
    attacker VARCHAR(50),
    defender VARCHAR(50),
    multiplier VARCHAR(50)
);
COPY csv_Damage_Relations(attacker, defender, multiplier)
    FROM '/Users/Shared/DB-2022-2023-datasets/damage_relations.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO affect(name_type1, name_type2, multiplier)
SELECT attacker, defender, multiplier FROM csv_Damage_Relations;


DROP TABLE IF EXISTS csv_Move;
CREATE TABLE csv_Move(
    id INT,
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

INSERT INTO move
SELECT id, name, accuracy_percentage, effect, pp, priority_order, target_type, type, damage_class, power,
       flinch_change, min_hits, max_hits, stat, stat_change_rate, change_amount  FROM csv_Move;

INSERT INTO healing SELECT id, hp_healing FROM csv_Move WHERE hp_healing > 0;
INSERT INTO healing SELECT id, hp_drain FROM csv_Move WHERE hp_drain > 0;

INSERT INTO damage SELECT id, hp_drain FROM csv_Move WHERE hp_drain < 0;
INSERT INTO damage SELECT id, hp_drain FROM csv_Move WHERE hp_healing < 0;

INSERT INTO status
SELECT id, ailment, ailment_chance FROM csv_Move WHERE ailment IS NOT NULL;


INSERT INTO Move_machine
SELECT itc.id, move.id FROM csv_Item as itc JOIN move ON move.name = itc.move WHERE itc.move NOT LIKE'';


DROP TABLE IF EXISTS csv_evolution CASCADE;
CREATE TABLE csv_evolution(
    base VARCHAR(50),
    evolution VARCHAR(50),
    is_baby boolean,
    trigger VARCHAR(50),
    gender VARCHAR(50),
    min_level INT,
    time_day VARCHAR(50),
    location VARCHAR(50),
    item VARCHAR(50),
    move VARCHAR(50),
    min_happiness VARCHAR(50)
);
COPY csv_evolution FROM '/Users/Shared/DB-2022-2023-datasets/evolutions.csv' DELIMITER ',' CSV HEADER;
ALTER TABLE csv_evolution
    ALTER COLUMN location TYPE NUMERIC USING CAST(NULLIF(location, '') AS INT),
    ALTER COLUMN min_happiness TYPE NUMERIC USING CAST(NULLIF(min_happiness, '') AS INT);


INSERT INTO Pokeball
SELECT id, top_capture_rate, min_capture_rate FROM csv_Item as itc WHERE itc.top_capture_rate IS NOT NULL AND itc.min_capture_rate IS NOT NULL;

DROP TABLE IF EXISTS csv_Pokemon_Creature CASCADE;
CREATE TABLE csv_Pokemon_Creature(
    id INT,
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
INSERT INTO pokemon_creature(id, species, nickname, trainer, level, experience, gender, date_time, subarea, method, evasion)
SELECT csv_Pokemon_Creature.id, species, nickname, trainer, level, experience, gender, date_time, subarea, method, random()*100
FROM csv_Pokemon_Creature;

UPDATE pokemon_creature SET position = csv.slot FROM csv_Teams AS csv WHERE pokemon_creature.id = pokemon;
UPDATE pokemon_creature SET status_condition = csv.status FROM csv_Teams AS csv WHERE pokemon_creature.id = pokemon;
UPDATE pokemon_creature SET team = team.id FROM csv_Teams AS csv JOIN team ON team.trainer = csv.trainer
        WHERE pokemon_creature.id = pokemon;

UPDATE pokemon_creature SET pokeball = item.id FROM csv_Pokemon_Creature JOIN item
    ON item.name = csv_Pokemon_Creature.pokeball WHERE csv_Pokemon_Creature.id = pokemon_creature.id;
UPDATE pokemon_creature SET nature = nature.id FROM csv_Pokemon_Creature JOIN nature ON nature.name = nature_name
    WHERE csv_Pokemon_Creature.id = pokemon_creature.id;

UPDATE pokemon_creature SET attack = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'attack';
UPDATE pokemon_creature SET defense = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'defense';
UPDATE pokemon_creature SET special_attack = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'special attack';
UPDATE pokemon_creature SET special_defense = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'special defense';
UPDATE pokemon_creature SET speed = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'speed';
UPDATE pokemon_creature SET hp = base_statistics.base_stat FROM base_statistics JOIN has_stat
    ON has_stat.id_statistics = base_statistics.id JOIN species AS s ON has_stat.id_species = s.id
    WHERE species = s.id AND base_statistics.name = 'hp';

UPDATE pokemon_creature SET is_baby = csv_evolution.is_baby FROM csv_evolution JOIN species ON
    species.name = csv_evolution.base WHERE pokemon_creature.species = species.id;
UPDATE pokemon_creature SET is_baby = FALSE WHERE is_baby IS NULL;

UPDATE pokemon_creature SET hp = csv.hp FROM csv_Teams AS csv WHERE pokemon_creature.id = pokemon;


INSERT INTO has_species (id_trainer, id_species)
SELECT DISTINCT ON(trainer, species) trainer, species FROM csv_Pokemon_Creature;

INSERT INTO cause_or_receive
SELECT csv_p.id, move.id FROM csv_Pokemon_Creature AS csv_p JOIN move ON move.id = move1;
INSERT INTO cause_or_receive
SELECT csv_p.id, move.id FROM csv_Pokemon_Creature AS csv_p JOIN move ON move.id = move2;
INSERT INTO cause_or_receive
SELECT csv_p.id, move.id FROM csv_Pokemon_Creature AS csv_p JOIN move ON move.id = move3;
INSERT INTO cause_or_receive
SELECT csv_p.id, move.id FROM csv_Pokemon_Creature AS csv_p JOIN move ON move.id = move4;

INSERT INTO uses_pokemon_item(creature, item, usable)
SELECT csv_p.id, item.id, true FROM csv_Pokemon_Creature AS csv_p
JOIN item ON item.name = csv_p.item;

INSERT INTO Loot
SELECT id, collector_price, quick_sell_price FROM csv_Item as itc WHERE quick_sell_price IS NOT NULL AND collector_price IS NOT NULL;

DROP TABLE IF EXISTS csv_Berrie CASCADE;
CREATE TABLE csv_Berrie (
	id_berrie INT,
	name VARCHAR(50),
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

UPDATE Berrie
SET growing_time = b.growth_time,
   maximum_amount = b.max_num_harvest,
   natural_gift_powder = b.natural_gift_powder,
   size = b.berry_avg_size,
   softness = b.smoothness,
   soil_dryness = b.soil_dryness,
   firmness = b.firmness,
   extra_int = b.id_berrie
FROM csv_Item as i
JOIN csv_Berrie as b ON i.name = b.name
WHERE Berrie.id_item = i.id;

INSERT INTO has_flavour
SELECT b.id_item, cb.flavour, cb.potency FROM berrie as b JOIN item as i ON b.id_item = i.id
   JOIN csv_Berrie_Flavor as cb
   ON cb.name = i.name;

INSERT INTO Healing_item
SELECT id,
CASE
  WHEN effect ~ 'Restores \d+ HP'
    THEN (substring(effect from position('Restores ' in effect) + length('Restores ') for position(' HP' in effect) - position('Restores ' in effect) - length('Restores '))::integer)
  ELSE 500
END AS restored_hp, random() * 100, random() * 100
FROM csv_Item
WHERE healing IS NOT NULL;


INSERT INTO Stat_enhancer
SELECT id, statistic, stat_increase_time FROM csv_Item as itc WHERE itc.statistic IS NOT NULL;

DROP TABLE IF EXISTS csv_Shop CASCADE;
CREATE TABLE csv_Shop (
	id INT,
	name VARCHAR(50),
	floors INT,
	city VARCHAR(50)
);
COPY csv_Shop FROM '/Users/Shared/DB-2022-2023-datasets/stores.csv'
DELIMITER ',' CSV HEADER;

DROP SEQUENCE IF EXISTS sub_area_id_seq;
CREATE SEQUENCE sub_area_id_seq;
SELECT setval('sub_area_id_seq', (SELECT MAX(id) FROM subarea));
INSERT INTO subarea (id, name, id_area)
SELECT nextval('sub_area_id_seq'), csv_Shop.name, area.id
FROM csv_Shop JOIN area ON lower(csv_Shop.city) = lower(area.name);

INSERT INTO shop
SELECT subarea.id, csv_shop.name, floors, csv_Shop.id FROM csv_shop JOIN subarea ON
    subarea.name = csv_shop.name;

DROP TABLE IF EXISTS csv_Shop_item CASCADE;
CREATE TABLE csv_Shop_item (
	store_id INT,
	item_id INT,
	stock INT,
	discount INT
);
COPY csv_Shop_item FROM '/Users/Shared/DB-2022-2023-datasets/storeitems.csv'
DELIMITER ',' CSV HEADER;
INSERT INTO sell
SELECT s.id, item_id, stock, discount FROM csv_Shop_item JOIN shop AS s ON s.id_file = store_id;

DROP TABLE IF EXISTS csv_purchase CASCADE;
CREATE TABLE csv_purchase (
  store_id INT,
  trainer_id INT,
  item_id INT,
  amount INT,
  cost INT,
  discount INT,
  date_time TIMESTAMP
);
COPY csv_purchase FROM '/Users/Shared/DB-2022-2023-datasets/purchases.csv'
DELIMITER ',' CSV HEADER;


INSERT INTO purchase
SELECT s.id, item_id, trainer_id, amount, cost, discount, date_time
FROM csv_purchase JOIN shop AS s ON s.id_file = store_id;

INSERT INTO gym (id, medal)
SELECT subarea.id, csv_gyms.badge FROM subarea
JOIN csv_gyms ON csv_gyms.name = subarea.name;


INSERT INTO gym_leader (id, gym)
SELECT trainer.id, subarea.id FROM trainer
JOIN csv_gyms ON regexp_replace(csv_gyms.leader, '[^a-zA-Z0-9 _.]', '') = regexp_replace(trainer.name, '[^a-zA-Z0-9 _.]', '')
JOIN subarea ON subarea.name = csv_gyms.name WHERE trainer.class LIKE'%Gym Leader%';

UPDATE gym AS table_gym SET id_item = item.id FROM csv_Trainer JOIN item ON item.name = csv_Trainer.gift
    JOIN gym_leader ON gym_leader.id = csv_Trainer.id WHERE csv_Trainer.gift IS NOT NULL
    AND table_gym.id = gym_leader.gym;


UPDATE gym SET id_type = type.id FROM csv_gyms JOIN type ON lower(type.name) = lower(csv_gyms.type)
    JOIN subarea ON subarea.name = csv_gyms.name WHERE subarea.id = gym.id;


INSERT INTO subarea (id)
SELECT DISTINCT location
FROM csv_evolution WHERE location IS NOT NULL
AND NOT EXISTS (
    SELECT id FROM subarea
    WHERE subarea.id = csv_evolution.location
);

DROP SEQUENCE IF EXISTS new_item;
CREATE SEQUENCE new_item;
SELECT setval('new_item', (SELECT MAX(id) FROM item));

INSERT INTO item (name, id)
SELECT DISTINCT item, nextval('new_item')
FROM csv_evolution WHERE item NOT LIKE''
AND NOT EXISTS (
    SELECT name FROM item
    WHERE item.name = csv_evolution.item
) GROUP BY (item);

INSERT INTO evolve(id_current_species, id_next_species, trigger, gender, time, level, id_subarea, id_item)
SELECT pb.id, pe.id, trigger, csv_evolution.gender, time_day, min_level, location, item.id FROM csv_evolution
    JOIN species AS pb ON pb.name = base JOIN species AS pe ON pe.name = evolution
    JOIN item ON item.name = item;

INSERT INTO evolve(id_current_species, id_next_species, trigger, gender, time, level, id_subarea)
SELECT pb.id, pe.id, trigger, csv_evolution.gender, time_day, min_level, subarea.id FROM csv_evolution
    JOIN species AS pb ON pb.name = base JOIN species AS pe ON pe.name = evolution
    LEFT JOIN subarea ON subarea.id = location WHERE item LIKE'';


DROP TABLE IF EXISTS csv_Battle CASCADE;
CREATE TABLE csv_Battle(
  id INT,
  winner INT,
  loser INT,
  star_time TIMESTAMP,
  gold INT,
  xp INT,
  duration INT
);
COPY csv_Battle FROM '/Users/Shared/DB-2022-2023-datasets/battles.csv'
DELIMITER ',' CSV HEADER;

INSERT INTO battle
SELECT csv_Battle.id, tw.id, tl.id, star_time, csv_Battle.gold, csv_Battle.xp, duration FROM csv_Battle
    JOIN team AS tw on tw.trainer = winner JOIN team AS tl ON tl.trainer = loser;

COPY fight
FROM '/Users/Shared/DB-2022-2023-datasets/battle_statistics.csv' DELIMITER ',' CSV HEADER;

--DROP TABLES
DROP TABLE csv_Pokemon_Creature;
DROP TABLE csv_Trainer;
DROP TABLE csv_Move;
DROP TABLE csv_Nature;
DROP TABLE csv_Criminal_Organization;
DROP TABLE csv_Pokemon_Abilities;
DROP TABLE csv_Damage_Relations;
DROP TABLE csv_Base_Statistics;
DROP TABLE csv_Species;
DROP TABLE csv_Growth_Rate;
DROP TABLE csv_locations;
DROP TABLE csv_Battle;
DROP TABLE csv_encounter;
DROP TABLE csv_berrie;
DROP TABLE csv_berrie_flavor;
DROP TABLE csv_item;
DROP TABLE csv_evolution;
DROP TABLE csv_gyms;
DROP TABLE csv_routes;
DROP TABLE csv_shop;
DROP TABLE csv_shop_item;
DROP TABLE csv_teams;
DROP TABLE csv_purchase;







