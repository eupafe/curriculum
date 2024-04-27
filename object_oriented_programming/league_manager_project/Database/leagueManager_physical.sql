--table creation query--

	DROP TABLE IF EXISTS player CASCADE;
	CREATE TABLE player (
		DNI VARCHAR(255), 
		email VARCHAR(255) UNIQUE NOT NULL,
		player_password VARCHAR(255) NOT NULL, 
		player_name VARCHAR(255) NOT NULL,
		squad_number INT NOT NULL,
		phone VARCHAR(255) NOT NULL,
		PRIMARY KEY (DNI)
	);
	
	DROP TABLE IF EXISTS team CASCADE;
	CREATE TABLE team (
		team_name VARCHAR(255) PRIMARY KEY
	);
	
	DROP TABLE IF EXISTS league CASCADE;
	CREATE TABLE league (
		league_name VARCHAR(255) PRIMARY KEY,
		start_day DATE NOT NULL,
		start_time TIME NOT NULL
	);

	DROP TABLE IF EXISTS has_league;
	CREATE TABLE has_league (
		team_name VARCHAR(255),
		league_name VARCHAR(255),
		matches_won INT,
		matches_lost INT,
		matches_tied INT,
		PRIMARY KEY(team_name, league_name),
		FOREIGN KEY (team_name) REFERENCES team (team_name),
		FOREIGN KEY (league_name) REFERENCES league (league_name)
	);
	
	DROP TABLE IF EXISTS matches CASCADE;
	CREATE TABLE matches ( 
		team_1 VARCHAR(255),
		team_2 VARCHAR(255),
		league_name VARCHAR(255),
		PRIMARY KEY(team_1, team_2, league_name, match_day),
		FOREIGN KEY (team_1) REFERENCES team (team_name),
		FOREIGN KEY (team_2) REFERENCES team (team_name),
		FOREIGN KEY (league_name) REFERENCES league (league_name),
		match_date DATE NOT NULL,
		match_time TIME NOT NULL, 
		duration INT NOT NULL,
		match_day INT NOT NULL,
		match_result INT NOT NULL
	);
	
	DROP TABLE IF EXISTS has_team;
	CREATE TABLE has_team (
		DNI VARCHAR(255),
		team_name VARCHAR(255),
		PRIMARY KEY (DNI, team_name), 
		FOREIGN KEY (DNI) REFERENCES player (DNI),
		FOREIGN KEY (team_name) REFERENCES team (team_name)
	);
	
	DROP TABLE IF EXISTS scores;
	CREATE TABLE scores (
		team_name VARCHAR(255),
		league_name VARCHAR(255),
		match_day INT,
		score INT,
		PRIMARY KEY (team_name, league_name, match_day),
		FOREIGN KEY (team_name) REFERENCES team (team_name),
		FOREIGN KEY (league_name) REFERENCES league (league_name)
	);
	
	ALTER TABLE IF EXISTS public.has_league
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.has_team
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.league
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.matches
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.player
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.team
    OWNER TO league_manager;
	ALTER TABLE IF EXISTS public.scores
	OWNER TO league_manager;
