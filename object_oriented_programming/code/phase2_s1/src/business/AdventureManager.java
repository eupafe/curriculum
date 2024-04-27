package business;

import business.entities.Adventure;
import business.entities.Encounter;
import business.entities.Monster;
import persistence.AdventureDAO;
import persistence.api.AdventureApiDAO;
import persistence.json.AdventureJsonDAO;

import java.io.IOException;

/**
 * Class that is used to manage the information of all the adventures.
 */
public class AdventureManager {

    // Abstraction from the persistence layer in the form of a Data Access Object, specifically for adventures
    private AdventureDAO dao;

    private boolean openFile = true;
    private int option;

    /**
     * Adventure constructor, depending on the user input deciding whether to use the json or the API persistence options, it creates
     * the constructor with a json dao or with an API dao.
     * @param option input from the user to choose the dao.
     */
    public AdventureManager(int option)  {
        this.option = option;
        if (option == 1){
            dao = new AdventureJsonDAO("src/adventures.json");
        }
        else{
            try {
                dao = new AdventureApiDAO();
            } catch (IOException e) {
                openFile = false;
            }

        }

    }

    public boolean checkAdventureDAO(){
        return openFile;
    }

    /**
     * Method used to create a new adventure, for this we need the name of the adventure and the amount of encounters it has, the adventure
     * will be created with the encounters empty/ null.
     * @param name String name for the adventure.
     * @param numberEncounters Integer number of encounters this adventure should have.
     * @return Boolean true if the adventure was successfully created, false if not.
     */
    public boolean createNewAdventure(String name, int numberEncounters) {

        Adventure adventure = new Adventure(name, numberEncounters);
        for (int i = 0; i < numberEncounters; i++) {
            adventure.addEncounter(i);
        }
        try {
            dao.save(adventure);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to add a monster to a certain encounter.
     * @param adventure Adventure that holds the encounter we want to add a monster to.
     * @param index_encounter Integer index of the encounter we want to add a monster to.
     * @param monster Monster to be added.
     * @param amount Integer amount of that monster type we want to add.
     * @return Boolean true if the monster was successfully added, false if not.
     */
    public boolean addMonster(Adventure adventure, int index_encounter, Monster monster, int amount){


        try {
            int monsters = dao.checkIfMonsterAdded(adventure.getName(), monster, index_encounter);

            if (monsters == 0){
                adventure.addMonster(index_encounter, monster, amount);
            }
            else{
                int index_monster =  dao.getMonsterIndex(adventure.getName(), monster, index_encounter);
                adventure.updateMonsterEncounter(index_encounter, index_monster, amount);
            }

            dao.update(adventure);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to delete a monster from a specific encounter.
     * @param adventure Adventure which holds the encounter from which we want to delete a monster from.
     * @param index_encounter Integer index to tell us which encounter to delete from.
     * @param indexMonster Integer index of the monster we want to delete.
     * @return Boolean true if the monster was successfully deleted, false if not.
     */
    public boolean deleteMonster(Adventure adventure, int index_encounter, int indexMonster) {

        adventure.deleteMonster(index_encounter, indexMonster);
        try {
            dao.update(adventure);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to return an adventure searching for it in persistence with a name.
     * @param name String name of the adventure we are searching for.
     * @return Adventure that matches the name, null if not found.
     */
    public Adventure getAdventureByName(String name) {
        try {
            String formattedName = name;
            if (option==2){
                formattedName = format(name);
            }
            return dao.getAdventureByName(formattedName);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method to get the monsters in an encounter, knowing the name of the adventure, and the index of the encounter.
     * @param name String name of the adventure from which we need to return the monsters from.
     * @param indexEncounter Integer index of which encounter we need the monsters from.
     * @return Encounter (Array of Monsters).
     */
    public Encounter getMonstersByAdventureAndEncounter(String name, int indexEncounter) {
        try {
            String formattedName = name;
            if (option==2){
                formattedName = format(name);
            }
            return dao.getMonstersByAdventureAndEncounter(formattedName, indexEncounter);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method to return all the adventures currently persisted.
     * @return Adventure Array with all the currently persisted adventures.
     */
    public Adventure[] getAllAdventures()  {
        try {
            return dao.getAll();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method to check if an adventure name is unique or not.
     * @param name String name that we need to check if it already is used for another adventure.
     * @return Integer to let us know if it is unique or not.
     */
    public int checkUnique(String name) {
        try {
            return dao.checkIfAdventureUnique(name);
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Method to get an adventure name knowing the ID/ index of the adventure.
     * @param adventureNumber Integer ID/ index of the adventure's name we want to find.
     * @return String name of the adventure with a matching ID.
     */
    public String getAdventureNameByID(int adventureNumber)  {
        if (getAllAdventures() == null){
            return null;
        }
        else {
            return getAllAdventures()[adventureNumber].getName();
        }
    }

    /**
     * Method to correctly format an adventure name before being added to persistence.
     * @param name String name to be formatted.
     * @return String name but after formatting.
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
