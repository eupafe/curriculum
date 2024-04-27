package business;

import business.entities.Adventure;
import business.entities.Character;
import business.entities.CombatCharacter;
import business.entities.Encounter;
import business.entities.characterClasses.*;
import presentation.UIController;

/**
 * Class that manages the playing of an adventure.
 */
public class PlayAdventureManager {
    private final UIController uiController;
    private final EncounterManager encounterManager;
    private final CharacterManager characterManager;
    public PlayAdventureManager(UIController uiController, CharacterManager characterManager) {
        this.uiController = uiController;
        this.encounterManager = new EncounterManager(uiController, characterManager);
        this.characterManager = characterManager;
    }

    /**
     * Method to play the adventure, first creating the party and initializing the statistics and then playing all of the encounters.
     * @param partyC Character Array to play the adventure.
     * @param adventure Adventure to be played.
     */
    public void play(Character[] partyC, Adventure adventure) {
      CombatCharacter[] party = createParty(partyC);

      initializeHP(party);
      initializeInitiative(party);
      boolean succeeded = true;

        for (int i = 0; i < adventure.getEncounters().length; i++) {
            uiController.printThis(stringStartingEncounter(adventure.getEncounters()[i], i));
            resetStats(party);
            if (!encounterManager.playEncounter(party, adventure.getEncounters()[i]) ) {
                succeeded = false;
                break;
            }
        }
        if (succeeded) {
            uiController.printThis(stringAdventureFinished(adventure.getName()));
        }
    }

    /**
     * Method to create a Combat Character array (party) from a Character array (partyC), calling on different constructor methods depending
     * on the class of each character.
     * @param partyC Character array to transform into CombatCharacter Array.
     * @return CombatCharacyer Array.
     */
    public CombatCharacter[] createParty(Character[] partyC) {
        CombatCharacter[] party = new CombatCharacter[partyC.length];
        for (int i = 0; i < partyC.length; i++) {
            switch(partyC[i].getClassGiven()) {
                case "Adventurer":
                    party[i] = new Adventurer(partyC[i]);
                    break;
                case "Warrior":
                    party[i] = new Warrior(partyC[i]);
                    break;
                case "Champion":
                    party[i] = new Champion(partyC[i]);
                    break;
                case "Cleric":
                    party[i] = new Cleric(partyC[i]);
                    break;
                case "Paladin":
                    party[i] = new Paladin(partyC[i]);
                    break;
                case "Wizard":
                    party[i] = new Wizard(partyC[i]);
                    break;
            }
        }
        return party;
    }

    /**
     * Method to initialize the HP of all the party members.
     * @param party CombatCharacter array for which to initialize the HP for.
     */
    public void initializeHP(CombatCharacter[] party) {
        for (int i = 0; i < party.length; i++) {
            party[i].initializeHP();
        }
    }

    /**
     * Method to initialize the initiative of all the party members.
     * @param party CombatCharacter array for which to initialize the HP for.
     */
    public void initializeInitiative(CombatCharacter[] party) {
        for (int i = 0; i < party.length; i++) {
            party[i].initializeInitiative();
        }
    }

    /**
     * Method to build string for the uiController.
     * @param encounter Encounter
     * @param numEncounter Integer index of the encounter.
     * @return String message for the uiController.
     */
    public String stringStartingEncounter(Encounter encounter, int numEncounter) {
        String string = "\n---------------------" +
                "\nStarting encounter " + (numEncounter + 1) + ":";
        for (int i = 0; i < encounter.getMonsters().size(); i++) {
            string += "\n\t- " + encounter.getAmountMonster(i) + "x " + encounter.getMonster(i).getName();
        }
        string = string + "\n---------------------";
        return string;
    }

    /**
     * Method to reset the stats (mind, body, spirit) of the party members before each encounter.
     * @param party CombatCharacter Array to reset the stats.
     */
    public void resetStats(CombatCharacter[] party) {
        for (int i = 0; i < party.length; i++) {
            party[i].resetStats();
        }
    }

    /**
     * String builder.
     * @param adventureName String name of the adventure.
     * @return String built.
     */
    public String stringAdventureFinished(String adventureName) {
        String string = "\nCongratulations, your party completed " + adventureName + "\n";
        return string;
    }
}
