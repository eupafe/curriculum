package business;

import business.entities.Character;
import business.entities.Dice;
import persistence.CharacterDAO;
import persistence.api.CharacterApiDAO;
import persistence.json.CharacterJsonDAO;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class that manages all of the characters.
 */
public class CharacterManager {

    // Abstraction from the persistence layer in the form of a Data Access Object, specifically for characters
    private CharacterDAO dao;

    private boolean openFile = true;
    private int option;

    /**
     * Default constructor that, depending on the choice of the user initializes the dao as JSON or API.
     * @param option user's input.
     */
    public CharacterManager(int option)  {
        this.option = option;

        if (option == 1){
            dao = new CharacterJsonDAO("src/characters.json");
        }
        else{
            try {
                dao = new CharacterApiDAO();
            } catch (IOException e) {
                openFile = false;
            }
        }

    }

    public boolean checkCharacterDAO(){
        return openFile;
    }


    /**
     * Method to randomly generate the statistics of a character.
     * @return Array of Integers array[0] = statistic, [1] = first roll, [2] = second roll.
     */
    public int[] generateStatistic(){
        int stats[] = new int[3];
        Dice d6 = new Dice(6);

        stats[1] = d6.roll();
        stats[2] = d6.roll();

        int sum = stats[1] + stats[2];
        if (sum == 2){
            stats[0] = -1;
        }
        else if (sum>= 6 && sum<= 9){
            stats[0] = 1;
        }
        else if (sum>= 10 && sum<= 11){
            stats[0] = 2;
        }
        else if (sum == 12){
            stats[0] = 3;
        }

        return stats;
    }

    /**
     * Method that adds a character to the system, creating it from all its attributes.
     * @param name String name of the character that is to be added.
     * @param player String name of the player that is creating the character.
     * @param level Integer initial level the character should start with.
     * @param body Integer body statistic of the character.
     * @param mind Integer mind statistic of the character.
     * @param spirit Integer spirit statistic of the character.
     * @param classCharacter String indicating the class of the character.
     * @return Boolean true if the character was successfully added, false otherwise.
     */
    public boolean addNewCharacter(String name, String player, int level, int body, int mind, int spirit, String classCharacter) {

        int xp = (level*100) - 100;
        Character character = new Character(name, player, xp, body, mind, spirit, classCharacter);
        try {
            dao.save(character);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method that gets all the characters currently stored in the system.
     * @return an Object Array of Characters.
     */
    public Character[] getAllCharacters()  {
        try {
            return dao.getAll();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Method to ger all of the characters of a specific player.
     * @param player String player to tell us which of the player's characters we need to return.
     * @return Array of Characters belonging to a player.
     */
    public Character[] getCharacters(String player)  {
        Character[] characters;
        try {
            String formattedName = player;
            if (option == 2 && player != "") {
              formattedName = format(player);
            }
            characters = dao.getByEither(formattedName);
        } catch (IOException e){
            return null;
        }
        return characters;
    }

    /**
     * Method to delete a character from what is persisted.
     * @param character to be deleted.
     * @return Boolean true if it was successfully deleted, if not false.
     */
    public boolean deleteCharacter(Character character) {
        try {
            if (option == 2){
                character.setName(format(character.getName()));
            }
            dao.delete(character);
        }catch(IOException e){
            return false;
        }
        return true;
    }

    /**
     * Method to check the uniqueness of a character.
     * @param name String to check whether it is unique.
     * @return Boolean if the character exists or not.
     */
    public boolean checkIfCharacterExists(String name) {
        try {
            return dao.checkIfCharacterExists(name);
        }catch(IOException e){
            return false;
        }
    }

    /**
     * Method to check if a characters name is correctly formatted, and if it unique or not.
     * @param name String to check.
     * @return Integer 1 if the name is empty or incorrectly formatted, 2 if it's not unique, 0 if all is okay.
     */
    public int checkCharacterName(String name) {
        int error = 0;

        Pattern p = Pattern.compile("[^A-Za-z\\s]");
        Matcher m = p.matcher(name);
        String formattedName = name;
        if (option == 2){
            formattedName = format(name);
        }
        if (m.find() == true || name == ""){
            error = 1;
        }
        else if (checkIfCharacterExists(formattedName)) {
            error = 2;
        }

        return error;
    }

    /**
     * Method to check if the minimum amount of characters to play an adventure have been created or not.
     * @return Integer: -1 if there are no characters, 1 if there is enough characters and 0 if not.
     */
    public int checkMinimumCharacters() {
        if (getAllCharacters()==null){
            return -1;
        }
        if (getAllCharacters().length >= 3) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Method to update a character in persistence.
     * @param character Character to be updated.
     * @return Boolean true if it was successfully updated, if not false.
     */
    public boolean updateCharacter(Character character) {
        try {
            dao.update(character);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Method to check if the character, when being created is initialized with a high enough level to evolve.
     * @param classS String telling us what class the character being created is.
     * @param level Integer level of the character.
     * @return String with either the new evolved class name or the same one if there is no evolution.
     */
    public String canEvolve(String classS, int level) {
        switch (classS) {
            case "Adventurer":
                if (level > 7) {
                    classS = "Champion";
                }
                else {
                    if (level > 3) {
                        classS = "Warrior";
                    }
                }


                break;
            case "Cleric":
                if (level > 4) {
                    classS = "Paladin";
                }
                break;
        }
        return classS;
    }

    /**
     * Method to format the name for the API.
     * @param name String to be formatted.
     * @return String after formatting.
     */
    public String format(String name){

        String[] parts = name.split(" ");

        if (parts.length > 1) {
            String formattedName = parts[0];
            for (int i = 1; i < parts.length; i++) {
                formattedName += "%20";
                formattedName += parts[i];
            }
            return formattedName;
        }
        else {
            return name;
        }
    }

 }
