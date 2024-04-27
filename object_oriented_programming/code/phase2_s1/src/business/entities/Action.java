package business.entities;

/**
 * The action class allows us to create a generic set of instructions that can be used for
 * attack, support and healing actions. An action has a target array to know the targets of the action,
 * as well as other strings like type, to know what type of action is being taken, finally it also
 * has an amount, which can be amount to heal, attack... etc.
 */
public class Action {

    private CombatCreature[] targets;
    private int amount;
    private String message;
    private String type;
    private String targetStat;

    private String damageType;

    /**
     *
     * @param targets CombatCreature array to which this action is intended to affect.
     * @param amount Integer amount that should be added (in case of Heal or Support), or subtracted (attack).
     * @param message String message that is sent to the ui when this action is enacted.
     * @param type String that tells us which type of action this is (Heal, Support, Attack).
     * @param targetStat String that, in the case of a Support action tells us which stat it affects (Mind, Body, Spirit).
     * @param damageType String that, in the case of an Attack action tells us which type it is (Physical, Magical, Psychical)
     */
    public Action(CombatCreature[] targets, int amount, String message, String type, String targetStat, String damageType) {
        this.targets = targets;
        this.amount = amount;
        this.message = message;
        this.type = type;
        this.targetStat = targetStat;
        this.damageType = damageType;
    }

    public CombatCreature[] getTargets() {
        return targets;
    }

    public int getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTargetStat() {
        return targetStat;
    }

    public String getDamageType() {
        return damageType;
    }
}
