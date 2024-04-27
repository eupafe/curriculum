----------------
----------------
-- #Query 2.1#
--

SELECT tr.name AS trainer, pc.id AS firstPokemon 
FROM trainer tr JOIN team t ON t.trainer = tr.id
JOIN battle b on t.id = b.winner JOIN team glt ON glt.id = b.loser
JOIN gym_leader gl ON glt.trainer = gl.id JOIN pokemon_creature pc ON pc.trainer = tr.id
GROUP BY tr.id, pc.id 
ORDER BY COUNT( gl.id) DESC, MIN(pc.date_time) 
LIMIT 1;

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT tr.name AS trainer, pc.id AS firstPokemon, pc.date_time AS captured, COUNT( gl.id) AS defeated
FROM trainer tr JOIN team t ON t.trainer = tr.id
JOIN battle b on t.id = b.winner JOIN team glt ON glt.id = b.loser
JOIN gym_leader gl ON glt.trainer = gl.id JOIN pokemon_creature pc ON pc.trainer = tr.id
GROUP BY tr.id, pc.id ORDER BY COUNT( gl.id) DESC, MIN(pc.date_time);

INSERT INTO battle(id, winner, loser)
VALUES (700, 177, 424);

----------------
-- #Query 2.2#
--
SELECT pc.nickname AS pokemon, tr.name AS trainer, COUNT(b.id) AS defeats
FROM pokemon_creature pc JOIN fight f on f.id_creature = pc.id JOIN battle b ON f.id_battle = b.id
JOIN trainer tr ON tr.id = pc.trainer
WHERE b.winner = pc.team AND f.life_remaining = 0
GROUP BY pc.id, tr.id ORDER BY COUNT(b.id) DESC, MIN(pc.date_time) 
LIMIT 5;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT pc.id, pc.nickname AS pokemon, tr.name AS trainer, COUNT(b.id) AS defeats, pc.hp AS hp, pc.team AS team, pc.date_time AS captured
FROM pokemon_creature pc JOIN fight f on f.id_creature = pc.id JOIN battle b ON f.id_battle = b.id
JOIN trainer tr ON tr.id = pc.trainer
WHERE b.winner = pc.team AND f.life_remaining = 0
GROUP BY pc.id, tr.id ORDER BY COUNT(b.id) DESC, MIN(pc.date_time) LIMIT 5;

SELECT pc.nickname, b.id
FROM pokemon_creature pc JOIN fight f on pc.id = f.id_creature
JOIN battle b on f.id_battle = b.id
WHERE f.life_remaining = 0 AND pc.team = b.winner AND pc.team = 189 AND pc.id = 3575;

----------------
-- #Query 2.3#
--
SELECT tr.id AS trainer, COUNT(pc.id) AS numpokemon
FROM trainer tr JOIN team t on tr.id = t.trainer
LEFT JOIN battle b ON t.id = b.winner JOIN pokemon_creature pc ON pc.trainer = tr.id
WHERE b.winner IS NULL AND tr.experience > tr.gold^2
GROUP BY tr.id;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT tr.id AS trainer, COUNT(pc.id) AS numpokemon, tr.experience, tr.gold
FROM trainer tr JOIN team t on tr.id = t.trainer
LEFT JOIN battle b ON t.id = b.winner JOIN pokemon_creature pc ON pc.trainer = tr.id
WHERE b.winner IS NULL AND tr.experience > tr.gold^2
GROUP BY tr.id;

INSERT INTO pokemon_creature(id, trainer) VALUES (15000, 426);

SELECT tr.id AS trainer, bw.winner, bl.loser AS loser
FROM trainer tr JOIN team t ON tr.id = t.trainer
LEFT JOIN battle bl on t.id = bl.loser
LEFT JOIN battle bw ON t.id = bw.winner
WHERE tr.id = 426;
----------------
-- #Query 2.4#
--
SELECT o.name AS organzation, t.name AS villain, ec.money_steal AS stolen
FROM criminal_organization o
JOIN evil_criminal ec ON o.name = ec.criminal_organization
JOIN trainer t ON t.id = ec.id
WHERE ec.money_steal IN
(SELECT evc.money_steal
    FROM evil_criminal evc
    WHERE o.name = evc.criminal_organization
    ORDER BY evc.money_steal DESC
    LIMIT 2)
ORDER BY o.name, money_steal DESC;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT o.name, t.name, ec.money_steal FROM criminal_organization o JOIN evil_criminal ec
on o.name = ec.criminal_organization JOIN trainer t ON t.id = ec.id
ORDER BY ec.money_steal DESC;

SELECT ec.id FROM criminal_organization co
JOIN evil_criminal ec on co.name = ec.criminal_organization
WHERE co.name = 'Plasma';

INSERT INTO evil_criminal(id, money_steal, criminal_organization) VALUES (1, 9999999, 'Aqua');

----------------
-- #Query 2.5#
--
SELECT pc.nickname AS pokemon, e.nickname AS enemy, pc.status_condition AS ailment
FROM pokemon_creature pc JOIN fight f ON f.id_creature = pc.id
JOIN battle b ON f.id_battle = b.id JOIN fight fe ON fe.id_battle = b.id
JOIN pokemon_creature e ON fe.id_creature = e.id
JOIN cause_or_receive cor ON e.id = cor.creature
JOIN status s ON cor.move = s.id
WHERE pc.status_condition NOT LIKE 'none' AND e.id != pc.id
AND s.special_effect = pc.status_condition AND pc.team != e.team
GROUP BY pc.id, e.id, s.special_effect;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
SELECT pc.id, pc.nickname, e.nickname, e.id, s.special_effect
FROM pokemon_creature pc JOIN fight f ON f.id_creature = pc.id
JOIN battle b ON f.id_battle = b.id JOIN fight fe ON fe.id_battle = b.id
JOIN pokemon_creature e ON fe.id_creature = e.id
JOIN cause_or_receive cor on e.id = cor.creature JOIN status s ON cor.move = s.id
WHERE pc.id = 134
ORDER BY s.special_effect;

SELECT m.name, s.special_effect
FROM move m JOIN cause_or_receive cor ON m.id = cor.move
LEFT JOIN status s ON m.id = s.id
JOIN pokemon_creature pc ON cor.creature = pc.id
WHERE pc.id = 8843;
----------------
-- #Query 2.6#
--
SELECT ty.name, COUNT(b.loser)
FROM team t LEFT JOIN battle b on b.loser = t.id
JOIN gym_leader gl ON gl.id = t.trainer JOIN gym g ON gl.gym = g.id
JOIN type ty ON g.id_type = ty.id
GROUP BY ty.name ORDER BY COUNT(b.loser)
LIMIT 1;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
INSERT INTO battle(id, winner, loser) VALUES (650, 196, 321);
INSERT INTO battle(id, winner, loser) VALUES (651, 196, 413);
INSERT INTO battle(id, winner, loser) VALUES (652, 196, 468);

----------------
-- #Trigger 2.1#
--
DROP FUNCTION IF EXISTS teach_move CASCADE;
CREATE OR REPLACE FUNCTION teach_move() RETURNS TRIGGER AS $$
BEGIN
    IF (EXISTS
       (SELECT *
            FROM trainer t JOIN pokemon_creature pc on pc.trainer = t.id
            JOIN owns_items o on t.id = o.trainer_id JOIN move_machine mm ON o.item_id = mm.id
            WHERE mm.move = NEW.move AND pc.id = NEW.creature))
        THEN

        --remove move machine
        DELETE FROM owns_items oi WHERE oi.trainer_id IN
                (SELECT t.id FROM trainer t JOIN pokemon_creature p on t.id = p.trainer WHERE p.id = NEW.creature)
                AND oi.item_id =
                (SELECT mm.id FROM move_machine mm JOIN move m ON mm.move = m.id WHERE m.id = NEW.move);

        --delete previous move
        DELETE FROM cause_or_receive WHERE slot = NEW.slot AND creature = NEW.creature AND move != NEW.move;

    ELSE
        --error message
        INSERT INTO warnings(affected_table, error_message, date, user_name)
        SELECT 'trainer', concat('<',t.class, '><', t.name, '> attempted to teach his <', s.id, '> the move <', m.name,
        '> without the necessary move machine.'), CURRENT_DATE, t.id
            FROM trainer t JOIN pokemon_creature pc ON pc.trainer = t.id JOIN species s ON pc.species = s.id
            JOIN move m ON m.id = NEW.move
            WHERE pc.id = NEW.creature;

        --revert insert
        DELETE FROM cause_or_receive WHERE id = NEW.id;

    END IF;
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS teach_new_move ON cause_or_receive CASCADE;
CREATE TRIGGER teach_new_move AFTER INSERT ON cause_or_receive
FOR EACH ROW
EXECUTE FUNCTION teach_move();


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
INSERT INTO cause_or_receive(creature, move, slot) VALUES (8, 68, 1); --trainer 0 and move machine 322

SELECT *
FROM trainer t JOIN owns_items oi ON oi.trainer_id = t.id
JOIN move_machine mm ON oi.item_id = mm.id
WHERE t.id = 0 AND mm.id = 322;
----------------
-- #Trigger 2.2#
--
DROP FUNCTION IF EXISTS battle_reward CASCADE;
CREATE OR REPLACE FUNCTION battle_reward() RETURNS TRIGGER AS $$
BEGIN
        --Add winner gold and xp
        UPDATE trainer
            SET gold = gold + NEW.gold,
            experience = experience + 2.0/3.0 * NEW.xp
            FROM team tm
            WHERE tm.id = NEW.winner AND tm.trainer = trainer.id;

        --Add loser xp
        UPDATE trainer
            SET experience = experience + 1.0/3.0 * NEW.xp
            FROM team tm
            WHERE tm.id = NEW.loser AND tm.trainer = trainer.id;

        --Subtract gold if defeated by villain
        UPDATE trainer
            SET gold = gold - NEW.gold
            FROM evil_criminal ec
            JOIN team tec ON ec.id = tec.trainer
            JOIN team tt ON tt.id = NEW.loser
            WHERE tec.id = NEW.winner AND tt.trainer = trainer.id;

        --Give Gym Leader item to its defeater
        INSERT INTO owns_items(trainer_id, item_id, obtention_method, date_time)
            SELECT NEW.winner, i.id, 'WON IN BATTLE', CURRENT_TIMESTAMP
                FROM team t JOIN gym_leader gl ON t.trainer = gl.id
                JOIN gym g ON gl.gym = g.id JOIN item i ON g.id_item = i.id
                WHERE t.id = NEW.loser;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS battle_result ON battle CASCADE;
CREATE TRIGGER battle_result AFTER INSERT ON battle
FOR EACH ROW
EXECUTE FUNCTION battle_reward();


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)
INSERT INTO battle(id, winner, loser, gold, xp)
VALUES(600, 196, 392, 100, 100);
--Villain winner
INSERT INTO battle(id, winner, loser, gold, xp)
VALUES(601, 13, 392, 100, 100);
--Gym Leader loser
INSERT INTO battle(id, winner, loser, gold, xp)
VALUES(603, 196, 424, 100, 100);
