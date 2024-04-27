----------------
----------------
-- #Query 3.1#
--

SELECT item_id, i.name, p.trainer_id, t.name
FROM purchase as p
JOIN item as i on p.item_id = i.id
JOIN trainer as t ON p.trainer_id = t.id
WHERE trainer_id = (SELECT p.trainer_id
FROM purchase as p
GROUP BY p.trainer_id
ORDER BY SUM(p.final_price * amount) DESC
LIMIT 1)
ORDER BY amount DESC
LIMIT 1;

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.2#
--

SELECT o.trainer_id, t.name, t.experience, COUNT(DISTINCT o.item_id)
FROM owns_items as o
JOIN trainer as t ON o.trainer_id = t.id
JOIN item as i ON o.item_id = i.id
WHERE t.id IN (SELECT t.id
FROM trainer AS t
LEFT JOIN purchase AS p ON t.id = p.trainer_id
WHERE p.trainer_id IS NULL
ORDER BY t.name)
GROUP BY o.trainer_id, t.name, t.experience
HAVING COUNT(DISTINCT o.item_id) > 10 and t.experience > 3000
ORDER BY COUNT(DISTINCT o.item_id);

-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.3#
--
SELECT DISTINCT hf.flavour, COUNT(DISTINCT i.id) as num_flavours
FROM sell as s
JOIN item as i on s.item_id = i.id
JOIN berrie as b on i.id = b.id_item
JOIN has_flavour hf on b.id_item = hf.id_berrie
JOIN flavour as f on hf.flavour = f.name
GROUP BY hf.flavour
ORDER BY  COUNT(DISTINCT i.id)
LIMIT 1;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.4#
--
SELECT combined.item_id, MAX(combined.trainers) AS trainers, MAX(combined.stock) AS stock, MAX(combined.sum) AS sum
FROM
(
    SELECT item_id, trainers, 0 AS stock, 0 AS sum
    FROM
    (
        SELECT item_id, COUNT(DISTINCT trainer_id) AS trainers
        FROM owns_items
        GROUP BY item_id
    ) AS trainers_query

    UNION

    SELECT item_id, 0 AS trainers, stock, 0 AS sum
    FROM sell
    WHERE stock = (SELECT MIN(stock) FROM sell)

    UNION

    SELECT item_id, 0 AS trainers, 0 AS stock, SUM(amount) AS sum
    FROM purchase
    GROUP BY item_id
) AS combined
GROUP BY combined.item_id
ORDER BY trainers, stock, sum;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.5#
--
SELECT
    ( SUM(money) - SUM(total_discount) ) AS total_discount_paid
FROM (
    SELECT
        (i.base_price * p.amount) AS money,
        (p.final_price * p.amount) AS total_discount
    FROM purchase AS p
    JOIN item AS i ON i.id = p.item_id
    WHERE EXTRACT(YEAR FROM p.date_time) = 2020
) AS discounts;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.6.1#
--
SELECT *
FROM berrie
WHERE berrie.id_item = (
SELECT item_id
FROM owns_items
JOIN item i on i.id = owns_items.item_id
JOIN berrie b on i.id = b.id_item
WHERE obtention_method LIKE '%HARVESTED'
GROUP BY item_id
ORDER BY COUNT(obtention_method) DESC, item_id DESC
LIMIT 1);


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Query 3.6.2#
--
SELECT b.id_item, growing_time, maximum_amount, natural_gift_powder, size, softness, soil_dryness, firmness, extra_int
FROM owns_items oi
JOIN berrie b ON oi.item_id = b.id_item
WHERE oi.obtention_method LIKE '%HARVESTED'
GROUP BY oi.item_id, b.id_item, growing_time, maximum_amount, natural_gift_powder, size, softness, soil_dryness, firmness, extra_int
ORDER BY COUNT(DISTINCT oi.trainer_id) DESC, item_id DESC
LIMIT 1;


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Trigger 3.1#
--

DROP FUNCTION IF EXISTS calculate_healing CASCADE;
CREATE OR REPLACE FUNCTION calculate_healing() RETURNS TRIGGER AS $$
DECLARE
  heal INTEGER;
  maxHeal INTEGER;
  currentHP INTEGER;
BEGIN
    IF OLD.item_id IN (SELECT id From healing_item) THEN

        currentHP := (SELECT hp FROM pokemon_creature
        WHERE pokemon_creature.id = (SELECT p.id FROM pokemon_creature as p
        WHERE p.position = 1 and p.trainer = OLD.trainer_id));

        heal :=  currentHP + (SELECT hi.status_heal
        from healing_item as hi
        WHERE OLD.item_id = hi.id);

        maxHeal := (SELECT ((2 * hp + (level ^ 2) / 4) / 100 + 5) * nature
        FROM pokemon_creature
        WHERE pokemon_creature.id = (SELECT p.id FROM pokemon_creature as p
        WHERE p.position = 1 and p.trainer = OLD.trainer_id));

        IF heal <= maxHeal THEN
            UPDATE pokemon_creature
            SET hp = heal
            WHERE pokemon_creature.id = (SELECT p.id
            FROM pokemon_creature as p
            WHERE p.position = 1 and p.trainer = OLD.trainer_id);
        ELSE
            UPDATE pokemon_creature
            SET hp = maxHeal
            WHERE pokemon_creature.id = (SELECT p.id
            FROM pokemon_creature as p
            WHERE p.position = 1 and p.trainer = OLD.trainer_id);

        end if;


    END IF;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS healing_result ON owns_items CASCADE;
CREATE TRIGGER healing_result AFTER DELETE ON owns_items
FOR EACH ROW
EXECUTE FUNCTION calculate_healing();


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)

----------------
-- #Trigger 3.2#
--
DROP FUNCTION IF EXISTS modify_purchase CASCADE;
CREATE OR REPLACE FUNCTION modify_purchase() RETURNS TRIGGER AS $$
DECLARE
  price INTEGER;
  newStock INTEGER;
  newGold INTEGER;
BEGIN

    price := (SELECT i.base_price
                       FROM item as i
                       WHERE NEW.item_id = i.id) - (SELECT i.base_price
                       FROM item as i
                       WHERE NEW.item_id = i.id) * NEW.discount / 100;

    UPDATE purchase
    SET final_price = price
    WHERE NEW.trainer_id = trainer_id AND NEW.item_id = item_id AND
          NEW.store_id = store_id AND NEW.date_time = date_time;


    newGold := (SELECT gold FROM trainer WHERE NEW.trainer_id = trainer.id) - ( price * NEW.amount);
    IF newGold < 0 THEN
        INSERT INTO warnings
        SELECT CONCAT(
            'Trainer #', t.id,
            ' tried to buy ', i.name,
            ' but his card has been declined, insufficient funds.'
        )
        FROM purchase
        JOIN trainer as t ON purchase.trainer_id = t.id
        JOIN item as i ON purchase.item_id = i.id
        JOIN shop as s ON purchase.store_id = s.id
        WHERE NEW.trainer_id = t.id and NEW.item_id = i.id AND NEW.store_id=s.id and NEW.date_time = date_time;

        DELETE FROM purchase
        WHERE trainer_id = NEW.trainer_id and item_id = NEW.item_id and store_id = NEW.store_id and
              date_time = NEW.date_time and amount=NEW.amount and discount=NEW.discount;
    else

         newStock = (SELECT stock FROM SELL
         where NEW.store_id = sell.store_id and NEW.item_id = sell.item_id) - NEW.AMOUNT;
        IF newStock < 0 THEN
            INSERT INTO warnings
            SELECT CONCAT(
                'Trainer #', t.id,
                ' tried to buy ', i.name,
                ' at ', s.name, ' store in ',
                sub.name, ', however the store ran out of stock.'
            )
            FROM purchase
            JOIN trainer as t ON purchase.trainer_id = t.id
            JOIN item as i ON purchase.item_id = i.id
            JOIN shop as s ON purchase.store_id = s.id
            JOIN subarea as sub on s.id = sub.id
            WHERE NEW.trainer_id = t.id and NEW.item_id = i.id and NEW.store_id = s.id;

            DELETE FROM purchase
            WHERE trainer_id = NEW.trainer_id and item_id = NEW.item_id and store_id = NEW.store_id and
            date_time = NEW.date_time and amount=NEW.amount and discount=NEW.discount;

        else

            INSERT INTO owns_items
            VALUES (NEW.trainer_id, NEW.item_id, 'PURCHASED', NEW.date_time);
             UPDATE trainer
             SET gold = newGold
             WHERE NEW.trainer_id = trainer.id;
             UPDATE sell
             SET stock = sell.stock - NEW.amount
             WHERE NEW.store_id = sell.store_id and NEW.item_id = sell.item_id ;
        end if;


    end if;

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_purchase ON purchase CASCADE;
CREATE TRIGGER insert_purchase AFTER INSERT ON purchase
FOR EACH ROW
EXECUTE FUNCTION modify_purchase();


-- #Validation#
-- if needed, write the validation queries (select, update, inserte, delete)


