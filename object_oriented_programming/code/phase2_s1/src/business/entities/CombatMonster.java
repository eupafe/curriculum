package business.entities;

import java.util.ArrayList;

/**
 * class CombatMonster, this is the general class for any monster inside an adventure, this class inherits from combatCreature
 * which generalizes all creatures inside adventures.
 */
public class CombatMonster extends CombatCreature{

    private String challenge;
    protected String damageDice;
    protected String damageType;

    public CombatMonster(Monster monster) {
        super(monster.getName(), monster.getInitiative(), monster.getHitPoints(), monster.getHitPoints(), monster.getExperience(), true, 1);
        this.challenge = monster.getChallenge();
        this.damageDice = monster.getDamageDice();
        this.damageType = monster.getDamageType();
    }


    @Override
    public Action takePreparationAction(ArrayList<CombatCreature> combatants) {
        return null;
    }

    /**
     * Method to take combat action for any monster that is not "Boss", this method attacks a single character chosen at random.
     * @param combatants ArrayList of CombatCreature that are used to choose one party member to attack at random.
     * @return Action the attack that should be enacted on the targeted party member.
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        Dice dx = new Dice(this.damageDice);
        Dice d10 = new Dice(10);

        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = chooseRandom(combatants);
        int amount = dx.roll();
        String message = "\n\n" + this.getName() + " attacks " + targets[0].getName() + ".";
        String type = "Attack";
        String targetStat = null;
        String damageType = this.damageType;

        int accuracy = d10.roll();
        if (accuracy == 1) {
            amount = 0;
            message = message + "\nFails and deals ";
        } else if (accuracy == 10) {
            amount *= 2;
            message = message + "\nCritical hit and deals ";
        } else {
            message = message + "\nHits and deals ";
        }
        message = message + amount + " " + damageType + " damage.";

        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method used by the monster to choose a single enemy at random
     * @param combatants ArrayList of CombatCreatures from which to choose one party member at random.
     * @return CombatCreature, randomly chosen party member.
     */
    public CombatCreature chooseRandom(ArrayList<CombatCreature> combatants) {
        int randomIndex;
        do {
            randomIndex = (int) ((Math.random() * (combatants.size() - 1)));
        } while (combatants.get(randomIndex).isMonster() || !combatants.get(randomIndex).isConscious());

        return combatants.get(randomIndex);
    }
    @Override
    public Action takeShortRestAction(ArrayList<CombatCreature> combatants) {
        return null;
    }

    /**
     * Method used by any monster that is not "Boss" to receive any given action, first checking if the action is meant for this
     * monster, and then based on the information of the action, change the statistics of this monster.
     * @param action Action that should be enacted on this monster.
     * @return Message, this message is to be sent to the ui, this is only used in the case that the monster dies.
     */
    @Override
    public String receiveAction(Action action) {
        String message = null;
        if (action.getTargets() == null) {
            return message;
        }
        for (int i = 0; i < action.getTargets().length; i++) {
            if (action.getTargets()[i].equals(this) ) {
                switch (action.getType()) {
                    case "Attack":
                        this.currentHP = Integer.max(0, this.currentHP - action.getAmount());
                        if (this.currentHP < 1) {
                            message = "\n" + this.name + " dies.";
                            this.conscious = false;
                        }
                        break;
                    case "Heal":
                        //left for further versions of the game in which monsters can also heal
                        break;
                    case "Support":
                        //left for further versions of the game in which monsters can also support
                        break;
                }
            }
        }
        return message;
    }

    @Override
    public String updateXP(int XP) {
        return null;
    }

    @Override
    public boolean canEvolve() {
        return false;
    }

    @Override
    public CombatCharacter evolve() {
        return null;
    }
}
