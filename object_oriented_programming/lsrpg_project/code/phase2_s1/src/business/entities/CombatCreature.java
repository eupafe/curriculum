package business.entities;

import java.util.ArrayList;

/**
 * Class CombatCreature, this is the superclass used to generalize all creatures that play roles inside an adventure, whether it is
 * a character or a monster alike.
 */
public abstract class CombatCreature {

    protected String name;

    protected int initiative;
    protected int maxHP;
    protected int currentHP;

    protected int xp;

    protected boolean conscious;

    protected int creatureType;
    public CombatCreature(String name, int initiative, int maxHP, int currentHP, int xp,  boolean conscious, int creatureType) {
        this.name = name;
        this.initiative = initiative;
        this.maxHP = maxHP;
        this.currentHP = currentHP;
        this.conscious = conscious;
        this.creatureType = creatureType;
        this.xp = xp;
    }

    public String getName() {
        return name;
    }

    public int getInitiative() {
        return initiative;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getXp() {
        return xp;
    }

    public boolean isConscious() {
        return conscious;
    }

    public abstract Action takePreparationAction(ArrayList<CombatCreature> combatants);

    public abstract Action takeCombatAction(ArrayList<CombatCreature> combatants);

    public abstract Action takeShortRestAction(ArrayList<CombatCreature> combatants);

    public abstract String receiveAction(Action action);

    public abstract String updateXP(int XP);
    public boolean isPartyMember() {
        return (this.creatureType == 0);
    }

    public boolean isMonster() {
        return (this.creatureType == 1);
    }

    public abstract boolean canEvolve();

    public abstract CombatCharacter evolve();
}
