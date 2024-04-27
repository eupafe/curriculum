package business.entities;

import java.util.ArrayList;

/**
 * Boss class extends CombatMonster, differently to other monsters, bosses have added mechanics and damage resistances, which
 * are implemented by overriding the common methods in CombatMonster.
 */
public class Boss extends CombatMonster{
    public Boss(Monster monster) {
        super(monster);
    }

    /**
     * Method in which a boss takes a combat action, this action is always targeting all the party members.
     * @param combatants ArrayList of CombatCreatures.
     * @return Action attack which targets all the party members.
     * @see Action
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        Dice dx = new Dice(this.damageDice);
        Dice d10 = new Dice(10);

        ArrayList<CombatCreature> targetList = new ArrayList<>();
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember() && combatants.get(i).isConscious()) {
                targetList.add(combatants.get(i));
            }
        }
        CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
        int amount = dx.roll();
        String message = "\n\n" + this.getName() + " attacks ";

        for (int i = 0; i < targets.length; i++) {
            if (i < targets.length - 2) {
                message += targets[i].getName() + ", ";
            }
            else {
                if (i == targets.length - 1) {
                    message += targets[i].getName() + ".";
                }
                if (i == targets.length - 2) {
                    message += targets[i].getName() + " and ";
                }
            }
        }
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
     * Method in which the boss receives an action, this method is different to the generic monster, in the case that the
     * damageType of the action is the same as the bosses damageType the damage is cut in half.
     * @param action action that the boss should take (if the boss is one of the targets).
     * @return String message used if the boss dies.
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
                        if (action.getDamageType() == this.damageType) {
                            this.currentHP = Integer.max(0, this.currentHP - action.getAmount()/2);
                        }
                        else {
                            this.currentHP = Integer.max(0, this.currentHP - action.getAmount());
                        }
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

}
