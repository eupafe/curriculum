package business;


import business.entities.*;
import business.entities.Character;
import presentation.UIController;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to manage all the functionalities and stages of an encounter within an adventure.
 */
public class EncounterManager {

    private final UIController uiController;

    private final CharacterManager characterManager;
    private final String PREPARATION_STAGE_MESSAGE =
            "\n\n\n-------------------------" +
            "\n*** Preparation stage ***" +
            "\n-------------------------";

    private final String COMBAT_STAGE_MESSAGE =
            "\n\n--------------------" +
            "\n*** Combat stage ***" +
            "\n--------------------";

    private final String SHORT_REST_STAGE_MESSAGE =
            "\n------------------------" +
            "\n*** Short rest stage ***" +
            "\n------------------------\n";

    private final String PARTY_UNCONSCIOUS_MESSAGE = "\nTavern keeper: “Lad, wake up. Yes, your party fell unconscious.”\n" +
            "“Don’t worry, you are safe back at the Tavern.”\n";

    private final String ALL_DEFEATED_MESSAGE = "All enemies are defeated.\n";

    public EncounterManager(UIController uiController, CharacterManager characterManager) {
        this.uiController = uiController;
        this.characterManager = characterManager;
    }

    /**
     * Method to play an encounter, first creating the list of combatants and then calling on different methods to play the encounter.
     * @param party CombatCharacter Array of party members.
     * @param encounter to be played.
     * @return Boolean true if the encounter was successfully finished (no TPU), false if not successful.
     */
    public boolean playEncounter(CombatCharacter[] party, Encounter encounter) {
        CombatMonster[] monsters = createMonsters(encounter);
        ArrayList<CombatCreature> combatants = new ArrayList<>();

        Collections.addAll(combatants, party);
        Collections.addAll(combatants, monsters);
        preparationStage(combatants);
        if (!combatStage(combatants)) {
           return false;
        }
        shortRestStage(combatants);

        return true;
    }

    /**
     * String constructor method.
     * @param round Integer round in which the encounter is in.
     * @return String constructed.
     */
    private String stringEndRound(int round) {
        String string = "\n\nEnd of round " + round + "\n";
        return string;
    }

    /**
     * String constructor method.
     * @param round Integer round in which the encounter is in.
     * @return String constructed.
     */
    private String stringStartRound(ArrayList<CombatCreature> combatants, int round) {
        String string = "\nRound " + round + ":\nParty:";
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                string += "\n  - " + combatants.get(i).getName();

                if (combatants.get(i).getName().length() < 4) {
                    string += "\t\t\t" + combatants.get(i).getCurrentHP() + " / " +
                            combatants.get(i).getMaxHP() + " hit points";
                }
                else {

                    if (combatants.get(i).getName().length() > 7) {
                        string += "\t" + combatants.get(i).getCurrentHP() + " / " +
                                combatants.get(i).getMaxHP() + " hit points";
                    } else {
                        string += "\t\t" + combatants.get(i).getCurrentHP() + " / " +
                                combatants.get(i).getMaxHP() + " hit points";
                    }
                }
            }
        }
        return string;
    }

    /**
     * Method to determine if there is a Total Party Unconscious scenario.
     * @param combatants ArrayList of CombatCreatures, from which we check if the party members are conscious or not.
     * @return Boolean true if all party members area unconscious or false if there are still party members that are alive.
     */
    private boolean TPU(ArrayList<CombatCreature> combatants) {
        boolean allUnconscious = true;
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                allUnconscious &= !combatants.get(i).isConscious();
            }
        }
        return allUnconscious;
    }

    /**
     * Metho to check if there are still monsters alive in the encounter.
     * @param combatants ArrayList of CombatCreatures to check the monsters status from.
     * @return Boolean true if there are no monsters left alive, false if there are some still standing.
     */
    private boolean combatWon(ArrayList<CombatCreature> combatants) {
        boolean allDead = true;
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isMonster()) {
                allDead &= !combatants.get(i).isConscious();
            }
        }
        return allDead;
    }

    /**
     * Method to transform the monster list from the Monster class to CombatMonster class.
     * @param encounter Encounter from which to transform the monsters.
     * @return CombatMonster Array.
     */
    public CombatMonster[] createMonsters(Encounter encounter) {
        int monsterIndex = 0;
        int monsterAmount = 0;

        for (int i = 0; i < encounter.getMonsters().size(); i++) {
            monsterAmount += encounter.getAmountMonster(i);
        }

        CombatMonster[] monsters = new CombatMonster[monsterAmount];
        for (int i = 0; i < encounter.getMonsters().size(); i++) {
            for (int j = 0; j < encounter.getAmountMonster(i); j++) {
                if (!encounter.getMonster(i).getChallenge().equals("Boss")) {
                    monsters[monsterIndex] = new CombatMonster(encounter.getMonster(i));
                    monsterIndex++;
                }
                else {
                    monsters[monsterIndex] = new Boss(encounter.getMonster(i));
                    monsterIndex++;
                }

            }

        }
        return monsters;
    }

    /**
     * Method to sort the ArrayList of combatants by their initiative value.
     * @param combatants ArrayList of CombatCreatures to be sorted.)
     * @return ArrayList of CombatCreatures after sorting.
     */
    public ArrayList<CombatCreature> sortByInitiative(ArrayList<CombatCreature> combatants) {
        CombatCreature aux;
        for (int i = 0; i < combatants.size() - 1; i++) {
            for (int j = 0; j < combatants.size() - i - 1; j++) {
                if (combatants.get(j).getInitiative() < combatants.get(j + 1).getInitiative()) {
                    aux = combatants.get(j);
                    combatants.set(j, combatants.get(j + 1));
                    combatants.set(j + 1, aux);
                }
            }
        }
        return combatants;
    }

    /**
     * String builder method.
     * @param combatants ArrayList of CombatCreatures.
     * @return String to be sent to the uiController.
     */
    public String stringRollingInitiative(ArrayList<CombatCreature> combatants) {
        String string = "\n\nRolling initiative...";
        for (int i = 0; i < combatants.size(); i++) {
            string = string + "\n  - " + combatants.get(i).getInitiative() + "\t" + combatants.get(i).getName();
        }
        return string + "\n";
    }

    /**
     * Method in which all the combatants take and/or receive the preparation stage actions.
     * @param combatants ArrayList of CombatCreatures, the combatants of the encounter.
     */
    public void preparationStage(ArrayList<CombatCreature> combatants) {
        uiController.printThis(PREPARATION_STAGE_MESSAGE);

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isConscious()) {
                Action action = combatants.get(i).takePreparationAction(combatants);
                if (action != null) {
                    uiController.printThis(action.getMessage());
                    for (int j = 0; j < combatants.size(); j++) {
                        String message = combatants.get(j).receiveAction(action);
                        if (message != null) {
                            uiController.printThis(message);
                        }
                    }
                }
            }
        }
        combatants = sortByInitiative(combatants);
        uiController.printThis(stringRollingInitiative(combatants));
    }

    /**
     * Method in which all the combatants take and/or receive the combat stage actions. This is done in order of initiative
     * and in a round based system.
     * @param combatants ArrayList of CombatCreatures, the combatants of the encounter.
     */
    public boolean combatStage(ArrayList<CombatCreature> combatants) {
        boolean adventureStatus = true;
        uiController.printThis(COMBAT_STAGE_MESSAGE);
        int round = 0;
        while (true) {
            round++;
            uiController.printThis(stringStartRound(combatants, round));
            combatRound(combatants);
            uiController.printThis(stringEndRound(round));
            if (TPU(combatants)) {
                uiController.printThis(PARTY_UNCONSCIOUS_MESSAGE);
                adventureStatus = false;
                break;
            }
            if (combatWon(combatants)) {
                uiController.printThis(ALL_DEFEATED_MESSAGE);
                break;
            }
        }
        return adventureStatus;
    }

    /**
     * Method in which each round the combatants take and/or receive their corresponding combat stage actions.
     * @param combatants ArrayList of CombatCreatures, the combatants of the encounter.
     */
    public void combatRound(ArrayList<CombatCreature> combatants) {
        for (int i = 0; i < combatants.size(); i++) {
            if (!TPU(combatants) && !combatWon(combatants)) {
                if ( combatants.get(i).isConscious()) {
                    Action action = combatants.get(i).takeCombatAction(combatants);
                    if (action != null) {
                        uiController.printThis(action.getMessage());
                        for (int j = 0; j < combatants.size(); j++) {
                            String message = combatants.get(j).receiveAction(action);
                            if (message != null) {
                                uiController.printThis(message);
                            }
                        }
                    }
                }
            }
            else {
                break;
            }
        }
    }

    /**
     * Method in which all the combatants take and/or receive the short rest stage actions. Also, where the xp rewards are
     * added to the characters and where leveling up and evolving is managed.
     * @param combatants ArrayList of CombatCreatures, the combatants of the encounter.
     */
    public void shortRestStage(ArrayList<CombatCreature> combatants) {
        uiController.printThis(SHORT_REST_STAGE_MESSAGE);
        int XP = 0;

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isMonster()) {
                XP += combatants.get(i).getXp();
            }
        }

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                uiController.printThis(combatants.get(i).updateXP(XP));
                if (combatants.get(i).canEvolve()) {
                    combatants.set(i,combatants.get(i).evolve());
                }
            }
        }

        updateCharacters(combatants);


        for (int i = 0; i < combatants.size(); i++) {
            Action action = combatants.get(i).takeShortRestAction(combatants);
            if (action != null) {
                uiController.printThis(action.getMessage());
                for (int j = 0; j < combatants.size(); j++) {
                    String message = combatants.get(j).receiveAction(action);
                    if (message != null) {
                        uiController.printThis(message);
                    }
                }
            }
        }
        uiController.printThis("\n");
    }

    /**
     * Method to update a character in persistence.
     * @param combatants ArrayList of CombatCreatures.
     */
    private void updateCharacters(ArrayList<CombatCreature> combatants) {
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                Character character = new Character((CombatCharacter) combatants.get(i));
                characterManager.updateCharacter(character);
            }
        }
    }
}
