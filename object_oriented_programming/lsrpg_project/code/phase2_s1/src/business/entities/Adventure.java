package business.entities;

import java.util.Arrays;

/**
 * Class adventure, has a name a number of encounters and an array of encounters which are played when playing the adventure.
 */
public class Adventure {
    private String name;
    private int numberEncounters;

    // List of students that are members of a group
    private Encounter[] encounters;

    /**
     * Initial constructor in the case which we want to create an empty adventure to then edit the contents one by one.
     */
    public Adventure(){
        this.name = " ";
        this.numberEncounters = 0;
        this.encounters = null;

    }


    /**
     * Constructor method which is used when we already know the adventure name and the number of encounters that there are.
     * @param name String name of the adventure
     * @param numberEncounters Integer number of encounters
     */
    public Adventure(String name, int numberEncounters) {
        this.name = name;
        this.numberEncounters = numberEncounters;
        this.encounters = new Encounter[numberEncounters];

    }

    /**
     * Constructor method in which we already know all the information of the adventure
     * @param name String name of the adventure
     * @param numberEncounters Integer number of encounters
     * @param encounters Encounter array which have all the information of an encounter
     */
    public Adventure(String name, int numberEncounters, Encounter[] encounters) {
        this.name = name;
        this.numberEncounters = numberEncounters;
        this.encounters = Arrays.copyOf(encounters, encounters.length);

    }

    public String getName() {
        return name;
    }

    public Encounter[] getEncounters() {
        return this.encounters;
    }

    /**
     * Method utilized to get all the monsters in an encounter.
     * @param indexEncounter Integer index of the encounter/ position of the encounter
     * @return Arraylist of Monster in that encounter.
     */
    public Encounter getMonstersInEncounter(int indexEncounter) {
        return this.encounters[indexEncounter];
    }

    /**
     * Method to add an empty encounter to the adventure
     * @param index_encounter which index/ position in the adventures array of encounters it should be added to.
     */
    public void addEncounter(int index_encounter){
        this.encounters[index_encounter] = new Encounter();
    }

    /**
     * Method to add a monster or multiple of one monster type to a specific encounter.
     * @param index_encounter Integer index to tell us which encounter to add to.
     * @param monster Monster to add to the encounter.
     * @param amount Integer amount to tell us how many of that monster type should be added.
     */
    public void addMonster(int index_encounter, Monster monster, int amount){
        this.encounters[index_encounter].addMonster(monster, amount);
    }

    /**
     * Method to delete a monster given a specific encounter index. NOTE: this will delete all the monsters of that type in that
     * specific encounter.
     * @param index_encounter Integer index to tell us which encounter to add to.
     * @param indexMonster Integer index to tell us which monster to delete from the encounter.
     */
    public void deleteMonster(int index_encounter, int indexMonster){
        this.encounters[index_encounter].deleteMonster(indexMonster);
    }

    /**
     * Method to update the amount there is stored of a specific monster type in a specific encounter.
     * @param index_encounter Integer index telling us which encounter to update.
     * @param index Integer index telling us which monster we want to update the amount for.
     * @param amount Integer amount of that monster type to add to the encounter.
     */
    public void updateMonsterEncounter(int index_encounter, int index, int amount){
        this.encounters[index_encounter].updateAmountMonster(index, amount);
    }
}
