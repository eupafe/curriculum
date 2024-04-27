DROP TABLE IF EXISTS Level CASCADE;
CREATE TABLE Level(
    number INT PRIMARY KEY
);

DROP TABLE IF EXISTS Ability CASCADE;
CREATE TABLE Ability(
    id INT PRIMARY KEY,
    name VARCHAR(50),
    short_description TEXT,
    long_description TEXT
);

DROP TABLE IF EXISTS Type CASCADE;
CREATE TABLE Type(
    id INT,
    name VARCHAR(50) PRIMARY KEY
);

DROP TABLE IF EXISTS Flavour CASCADE;
CREATE TABLE Flavour (
	name VARCHAR(50) PRIMARY KEY
);

DROP TABLE IF EXISTS Item CASCADE;
CREATE TABLE Item (
	id INT PRIMARY KEY,
	name VARCHAR(50),
	base_price INT,
	description TEXT,
	can_revive BOOLEAN
);

DROP TABLE IF EXISTS Base_Statistics CASCADE;
CREATE TABLE Base_Statistics(
	id INT PRIMARY KEY,
	effort INT,
	base_stat INT,
	name VARCHAR(50)
);

DROP TABLE IF EXISTS Trainer CASCADE;
CREATE TABLE Trainer(
    id INT PRIMARY KEY,
    class VARCHAR(50),
    name VARCHAR(50),
    experience INT,
    gold INT
);

DROP TABLE IF EXISTS Region CASCADE;
CREATE TABLE Region (
	name VARCHAR(50) PRIMARY KEY
);

DROP TABLE IF EXISTS Area CASCADE;
CREATE TABLE Area (
	id SERIAL PRIMARY KEY,
	name VARCHAR (50),
	name_region VARCHAR (50),
	FOREIGN KEY (name_region) REFERENCES Region (name)
);

DROP TABLE IF EXISTS City CASCADE;
CREATE TABLE City (
	id INTEGER PRIMARY KEY,
    population BIGINT,
    FOREIGN KEY (id) REFERENCES Area (id)
);

DROP TABLE IF EXISTS Route CASCADE;
CREATE TABLE Route (
	id INTEGER PRIMARY KEY,
	pavement VARCHAR(50),
	FOREIGN KEY (id) REFERENCES Area (id)
);

DROP TABLE IF EXISTS Connect_ CASCADE;
CREATE TABLE Connect_ (
	id_area INTEGER,
	id_route INTEGER,
	cardinality VARCHAR(50),
	PRIMARY KEY (id_area, id_route, cardinality),
	FOREIGN KEY (id_area) REFERENCES Area (id),
	FOREIGN KEY (id_route) REFERENCES Route (id)
);

DROP TABLE IF EXISTS Subarea CASCADE;
CREATE TABLE Subarea (
	id INTEGER PRIMARY KEY,
	name VARCHAR (50),
	id_area INTEGER,
	FOREIGN KEY (id_area) REFERENCES Area (id)
);

DROP TABLE IF EXISTS Encounter CASCADE;
CREATE TABLE Encounter(
    id SERIAL PRIMARY KEY,
    subarea INT,
    meeting_method VARCHAR(50),
    FOREIGN KEY (subarea) REFERENCES subarea(id)
);

DROP TABLE IF EXISTS Team CASCADE;
CREATE TABLE Team(
    id  SERIAL PRIMARY KEY,
    trainer INT,
    FOREIGN KEY (trainer) REFERENCES Trainer (id)
);

DROP TABLE IF EXISTS Criminal_Organization CASCADE;
CREATE TABLE Criminal_Organization(
    name VARCHAR(50) PRIMARY KEY,
    building VARCHAR(50),
    base_location INT,
    region VARCHAR(50),
    FOREIGN KEY (base_location) REFERENCES area (id)
);

DROP TABLE IF EXISTS owns_items CASCADE;
CREATE TABLE owns_items (
	trainer_id INT ,
	item_id INT,
	obtention_method VARCHAR(50),
	date_time TIMESTAMP,
	PRIMARY KEY (trainer_id, item_id, date_time),
	FOREIGN KEY (trainer_id) REFERENCES Trainer (id),
	FOREIGN KEY (item_id) REFERENCES Item (id)
);

DROP TABLE IF EXISTS Evil_Criminal CASCADE;
CREATE TABLE Evil_Criminal(
    id INT PRIMARY KEY,
    money_steal BIGINT,
    signature_phrase VARCHAR(255),
    is_leader BOOLEAN,
    sidekick INT,
    criminal_organization VARCHAR(50),
    FOREIGN KEY (id) REFERENCES Trainer(id),
    FOREIGN KEY (sidekick) REFERENCES Evil_Criminal(id),
    FOREIGN KEY (criminal_organization) REFERENCES Criminal_Organization (name)
);

DROP TABLE IF EXISTS Growth_Rate CASCADE;
CREATE TABLE Growth_Rate(
    id INT PRIMARY KEY,
    title VARCHAR(50),
    formula TEXT
);

DROP TABLE IF EXISTS Nature CASCADE;
CREATE TABLE Nature(
    name VARCHAR(50),
    id INT PRIMARY KEY,
    stat_decrease VARCHAR(50),
    stat_increase VARCHAR(50),
    like_flavour VARCHAR(50),
    dislike_flavour VARCHAR(50),
    FOREIGN KEY (like_flavour) REFERENCES flavour(name),
    FOREIGN KEY (dislike_flavour) REFERENCES flavour(name)
);

DROP TABLE IF EXISTS has_level CASCADE;
CREATE TABLE has_level(
    id_level INT,
    id_growth_rate INT,
    necessary_xp INT,
    PRIMARY KEY (id_level, id_growth_rate),
    FOREIGN KEY (id_level) REFERENCES Level(number),
    FOREIGN KEY (id_growth_rate) REFERENCES Growth_Rate(id)
);

DROP TABLE IF EXISTS Species CASCADE;
CREATE TABLE Species(
    id INT PRIMARY KEY,
    name VARCHAR(50),
    weight INT,
    height INT,
    basic_experience INT,
    dex_order INT,
    growth_rate INT,
    FOREIGN KEY (growth_rate) REFERENCES Growth_Rate(id)
);

DROP TABLE IF EXISTS has_stat CASCADE;
CREATE TABLE has_stat(
    id_species INT,
    id_statistics INT,
    PRIMARY KEY(id_species, id_statistics),
    FOREIGN KEY (id_species) REFERENCES species(id),
    FOREIGN KEY (id_statistics) REFERENCES base_statistics(id)
);

DROP TABLE IF EXISTS Find CASCADE;
CREATE TABLE Find(
    id SERIAL PRIMARY KEY,
    id_encounter INT,
    id_species INT,
    condition_type VARCHAR(50),
    condition_value VARCHAR(50),
    probability INT,
    min_level INT,
    max_level INT,
    FOREIGN KEY (id_encounter) REFERENCES Encounter(id),
    FOREIGN KEY (id_species) REFERENCES Species(id)
);

DROP TABLE IF EXISTS has_ability CASCADE;
CREATE TABLE has_ability(
    id_species INT,
    id_ability INT,
    hidden BOOLEAN,
    PRIMARY KEY (id_species, id_ability),
    FOREIGN KEY (id_species) REFERENCES Species(id),
    FOREIGN KEY (id_ability) REFERENCES Ability(id)
);

DROP TABLE IF EXISTS has_type CASCADE;
CREATE TABLE has_type(
    id_species INT,
    type_name VARCHAR(50),
    PRIMARY KEY (id_species, type_name),
    FOREIGN KEY (id_species) REFERENCES Species(id),
    FOREIGN KEY (type_name) REFERENCES Type(name)
);

DROP TABLE IF EXISTS affect CASCADE;
CREATE TABLE affect(
    id SERIAL,
    multiplier VARCHAR(50),
    name_type1 VARCHAR(50),
    name_type2 VARCHAR(50),
    PRIMARY KEY (name_type1, name_type2, id),
    FOREIGN KEY (name_type1) REFERENCES Type(name),
    FOREIGN KEY (name_type2) REFERENCES Type(name)
);

DROP TABLE IF EXISTS Move CASCADE;
CREATE TABLE Move(
    id INT PRIMARY KEY,
    name VARCHAR(50),
    accuracy_percentage INT,
    effect TEXT,
    pp INT,
    priority_order INT,
    target_type VARCHAR(50),
    type VARCHAR(50),
    damage_class VARCHAR(50),
    power INT,
    flinch_change INT,
    min_hits INT,
    max_hits INT,
    stat VARCHAR(50),
    stat_change_rate INT,
    change_amount INT,
    FOREIGN KEY (type) REFERENCES type(name)
);

DROP TABLE IF EXISTS Healing CASCADE;
CREATE TABLE Healing(
    id INT PRIMARY KEY,
    life_points INT,
    FOREIGN KEY (id) REFERENCES Move(id)
);

DROP TABLE IF EXISTS Damage CASCADE;
CREATE TABLE Damage(
    id INT PRIMARY KEY,
    strength INT,
    FOREIGN KEY (id) REFERENCES Move(id)
);

DROP TABLE IF EXISTS Status CASCADE;
CREATE TABLE Status(
    id INT PRIMARY KEY,
    special_effect VARCHAR(50),
    chance_to_apply INT,
    FOREIGN KEY (id) REFERENCES Move(id)
);

DROP TABLE IF EXISTS Move_machine CASCADE;
CREATE TABLE Move_machine (
	id INT PRIMARY KEY,
	move INT,
	FOREIGN KEY (id) REFERENCES Item(id),
	FOREIGN KEY (move) REFERENCES move(id)
);

DROP TABLE IF EXISTS evolve CASCADE;
CREATE TABLE evolve(
    id SERIAL,
    id_current_species INT,
    id_next_species INT,
    trigger VARCHAR(50),
    PRIMARY KEY(id, id_current_species, id_next_species),
    gender VARCHAR(50),
    time VARCHAR(50),
    level INT,
    id_item INT,
    id_subarea INT,
    FOREIGN KEY (id_current_species) REFERENCES Species(id),
    FOREIGN KEY (id_next_species) REFERENCES Species(id),
    FOREIGN KEY (id_item) REFERENCES Item(id),
    FOREIGN KEY (id_subarea) REFERENCES subarea(id)
);

DROP TABLE IF EXISTS has_species CASCADE;
CREATE TABLE has_species(
    id_trainer INT,
    id_species INT,
    PRIMARY KEY(id_species, id_trainer),
    FOREIGN KEY (id_species) REFERENCES Species(id),
    FOREIGN KEY (id_trainer) REFERENCES Trainer(id)
);

DROP TABLE IF EXISTS Pokeball CASCADE;
CREATE TABLE Pokeball (
	id INT PRIMARY KEY,
	max_catch_rate INT,
	min_catch_rate INT,
	FOREIGN KEY (id) REFERENCES Item(id)
);

DROP TABLE IF EXISTS Pokemon_Creature CASCADE;
CREATE TABLE Pokemon_Creature(
    id INT PRIMARY KEY,
    species INT,
    nickname VARCHAR(50),
    trainer INT,
    level INT,
    experience INT,
    gender VARCHAR(50),
    nature INT,
    date_time TIMESTAMP,
    subarea INT,
    method VARCHAR(50),
    pokeball INT,
    is_baby BOOLEAN,
    position INT,
    hp INT,
    status_condition VARCHAR(50),
    team INT,
    attack INT,
    defense INT,
    special_attack INT,
    special_defense INT,
    speed INT,
    evasion INT,
    FOREIGN KEY (subarea) REFERENCES subarea (id),
    FOREIGN KEY (trainer) REFERENCES Trainer (id),
    FOREIGN KEY (team) REFERENCES Team (id),
    FOREIGN KEY (species) REFERENCES species (id),
    FOREIGN KEY (nature) REFERENCES Nature (id),
    FOREIGN KEY (pokeball) REFERENCES Pokeball(id)
);

DROP TABLE IF EXISTS cause_or_receive CASCADE;
CREATE TABLE cause_or_receive(
    creature INT,
    move INT,
    id SERIAL,
    PRIMARY KEY (id, creature, move),
    FOREIGN KEY (creature) REFERENCES Pokemon_Creature(id),
    FOREIGN KEY (move) REFERENCES move(id)
);

DROP TABLE IF EXISTS uses_pokemon_item;
CREATE TABLE uses_pokemon_item(
    item INT,
    creature INT,
    usable BOOLEAN,
    FOREIGN KEY (item) REFERENCES Item (id),
    FOREIGN KEY (creature) REFERENCES pokemon_creature (id)
);

DROP TABLE IF EXISTS Loot CASCADE;
CREATE TABLE Loot (
	id INT PRIMARY KEY,
	collector_price INT,
	quick_sell_price INT,
	FOREIGN KEY (id) REFERENCES Item(id)
);

DROP TABLE IF EXISTS Berrie CASCADE;
CREATE TABLE Berrie (
  id_item INT PRIMARY KEY ,
  growing_time INT,
  maximum_amount INT,
  natural_gift_powder INT,
  size INT,
  softness INT,
  soil_dryness INT,
  firmness VARCHAR(50),
  extra_int INT,
  FOREIGN KEY (id_item) REFERENCES Item(id)
);

DROP TABLE IF EXISTS has_flavour CASCADE;
CREATE TABLE has_flavour (
  id_berrie INT,
  flavour VARCHAR(50),
  strength INT,
  PRIMARY KEY (id_berrie, flavour),
  FOREIGN KEY (id_berrie) REFERENCES Berrie (id_item),
  FOREIGN KEY (flavour) REFERENCES Flavour (name)
);

DROP TABLE IF EXISTS Healing_item CASCADE;
CREATE TABLE Healing_item (
  id INT PRIMARY KEY,
  hp_recovered INT,
  status_heal INT,
  hp_percentage INT,
  FOREIGN KEY (id) REFERENCES Item(id)
);

DROP TABLE IF EXISTS Stat_enhancer CASCADE;
CREATE TABLE Stat_enhancer (
	id INT PRIMARY KEY,
	stat_to_double VARCHAR(50),
	time_period INT,
	FOREIGN KEY (id) REFERENCES Item(id)
);

DROP TABLE IF EXISTS Shop CASCADE;
CREATE TABLE Shop (
	id INT PRIMARY KEY,
	name VARCHAR(50),
	floors INT,
	id_file INT,
	FOREIGN KEY (id) REFERENCES subarea(id)
);

DROP TABLE IF EXISTS sell CASCADE;
CREATE TABLE sell (
	store_id INT,
	item_id INT,
	stock INT,
	discount INT,
	PRIMARY KEY (store_id, item_id),
	FOREIGN KEY (store_id) REFERENCES Shop (id),
	FOREIGN KEY (item_id) REFERENCES Item (id)
);

DROP TABLE IF EXISTS purchase CASCADE;
CREATE TABLE purchase (
  store_id INT,
  item_id INT,
  trainer_id INT,
  amount INT,
  final_price INT,
  discount INT,
  date_time TIMESTAMP,
  PRIMARY KEY (store_id, item_id, trainer_id, date_time),
  FOREIGN KEY (store_id) REFERENCES Shop (id),
  FOREIGN KEY (item_id) REFERENCES Item (id),
  FOREIGN KEY (trainer_id) REFERENCES Trainer (id)
);

DROP TABLE IF EXISTS Gym CASCADE;
CREATE TABLE Gym (
	id INTEGER PRIMARY KEY,
	medal VARCHAR(50),
	id_item INTEGER,
	id_type INTEGER,
	FOREIGN KEY (id) REFERENCES Subarea (id),
	FOREIGN KEY (id_item) REFERENCES Item (id)
);

DROP TABLE IF EXISTS Gym_Leader CASCADE;
CREATE TABLE Gym_Leader (
	id INTEGER PRIMARY KEY,
	gym INTEGER,
	FOREIGN KEY (gym) REFERENCES Gym (id),
    FOREIGN KEY (id) REFERENCES trainer (id)
);

DROP TABLE IF EXISTS Battle CASCADE;
CREATE TABLE Battle(
  id INT PRIMARY KEY,
  winner INT,
  loser INT,
  star_time TIMESTAMP,
  gold INT,
  xp INT,
  duration INT,
  gym INT,
  FOREIGN KEY (winner) REFERENCES Team (id),
  FOREIGN KEY (loser) REFERENCES Team (id),
  FOREIGN KEY (gym) REFERENCES Gym(id)
);

DROP TABLE IF EXISTS Fight CASCADE;
CREATE TABLE Fight(
    id_battle INT,
    id_creature INT,
    life_remaining INT,
    damage_received INT,
    damage_inflicted INT,
    PRIMARY KEY (id_battle, id_creature),
    FOREIGN KEY (id_battle) REFERENCES Battle(id),
    FOREIGN KEY (id_creature) REFERENCES Pokemon_Creature(id)
);
