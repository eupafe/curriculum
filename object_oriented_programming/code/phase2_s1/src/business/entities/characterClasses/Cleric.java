package business.entities.characterClasses;

import business.entities.*;
import business.entities.Character;

import java.util.ArrayList;

/**
 * Cleric class, extends from combat characters and overrides certain methods making it so that clerics take and receive
 * actions differently from other character classes.
 */
public class Cleric extends CombatCharacter {
    public Cleric(Character character) {
        super(character);
    }

    /**
     * Method to initialize the HP of a cleric.
     */
    @Override
    public void initializeHP() {
        int body = this.getBody();
        int lvl = (this.getXp() + 100)/100;
        this.maxHP = this.currentHP = (10 + body)*lvl;
    }

    /**
     * Method to initialize the initiative of a cleric.
     */
    @Override
    public void initializeInitiative() {
        Dice d10 = new Dice(10);
        int spirit = this.getSpirit();
        this.initiative = d10.roll() + spirit;
    }


    /**
     * Method in which a cleric takes a preparation action.
     * @param combatants ArrayList of CombatCreature's which tell the cleric every enemy and party member in the encounter.
     * @return Action to take during preparation stage, in the cleric's case this will always be "Prayer of good luck".
     * @see Action
     */
    @Override
    public Action takePreparationAction(ArrayList<CombatCreature> combatants) {
        ArrayList<CombatCreature> targetList = new ArrayList<>();
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                targetList.add(combatants.get(i));
            }
        }
        CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
        int amount = 1;
        String message = "\n" + this.name + " uses Prayer of good luck. Everyone's Mind increases in +1.";
        String type = "Support";
        String targetStat = "Mind";
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which a cleric takes a combat action.
     * @param combatants ArrayList of CombatCreature's which tell the cleric every enemy and party member in the encounter.
     * @return Action to take during combat stage, in the cleric's case this will always be "Prayer of healing".
     * @see Action
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = lowestAlly(combatants);
        Dice d10 =  new Dice(10);
        int amount = d10.roll() + this.mind;
        String message = "\n" + this.name + " uses Prayer of healing. Heals " + amount + " hit points to " + targets[0].getName() + ".";
        String type = "Heal";
        String targetStat = null;
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which a cleric takes a short rest action.
     * @param combatants ArrayList of CombatCreature's which tell the cleric every enemy and party member in the encounter.
     * @return Action to take during short rest stage, in the cleric's case this will always be "Prayer of healing".
     * @see Action
     */
    @Override
    public Action takeShortRestAction(ArrayList<CombatCreature> combatants) {
        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = this;
        Dice d10 =  new Dice(10);
        int amount = d10.roll() + this.mind;
        String message = "\n" + this.name + " uses Prayer of healing. Heals " + amount + " hit points.";
        String type = "Heal";
        String targetStat = null;
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     *  Method in which the cleric receives an action, first checking if this cleric is a target, then sorting whether it is
     *  a Heal, Support or Attack action and then affecting the corresponding stat.
     * @param action  Action which the cleric should take (if he is a target of this action).
     * @return String message, this message is used only when the cleric falls unconscious due to an attack inflicted on it.
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
                        this.currentHP = Integer.max(0, this.currentHP -action.getAmount());
                        if (this.currentHP < 1) {
                            message = "\n" + this.name + " falls unconscious.";
                            this.conscious = false;
                        }
                        break;
                    case "Heal":
                        this.currentHP = Integer.min(maxHP, this.currentHP + action.getAmount());
                        if (this.currentHP > 0) {
                            this.conscious = true;
                        }
                        break;
                    case "Support":
                        switch (action.getTargetStat()) {
                            case "Body":
                                this.body += action.getAmount();
                                break;
                            case "Mind":
                                this.mind += action.getAmount();
                                break;
                            case "Spirit":
                                this.spirit += action.getAmount();
                                break;
                        }
                        break;
                }
            }
        }
        return message;
    }

    /**
     * Method in which the XP of the cleric gets updated, also checking for possible level up or evolution.
     * @param XP Integer experience value to be added to cleric's xp.
     * @return String message to be sent to ui, this tells the user how much xp is gained, if there was a level up
     * and also if there was an evolution.
     */
    @Override
    public String updateXP(int XP) {
        String message = this.name + " gains " + XP + " xp. ";
        int lvlBefore = (this.getXp() + 100)/100;
        this.xp += XP;
        int lvlNow = (this.xp + 100)/100;
        if (lvlBefore < lvlNow) {
            message += this.name + " levels up. They are now lvl " + lvlNow + "!\n";
            this.currentHP = this.maxHP;
        }
        else {
            message += "\n";
        }

        if (lvlNow > 4) {
            message += this.name + " evolves to Paladin!\n";
        }
        return message;
    }

    /**
     * Method to check if this cleric can evolve.
     * @return Boolean true if the cleric can evolve, if not false.
     */
    @Override
    public boolean canEvolve() {
        if ((this.xp + 100)/100 > 4){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method for evolving a cleric, this is when they evolve into a Paladin, changing the class type of the character.
     * @return Paladin with the same information (name, player, etc.) as the cleric.
     */
    @Override
    public CombatCharacter evolve() {
        this.classGiven = "Paladin";
        return new Paladin(this);
    }

    /**
     * Method in which the lowest HP ally is found in the list of combatants.
     * @param combatants ArrayList of CombatCreature's to find the lowest HP ally.
     * @return CombatCreature ally with the lowest HP.
     */
    private CombatCreature lowestAlly(ArrayList<CombatCreature> combatants) {
        int lowestHP = Integer.MAX_VALUE;
        int lowestIndex = -1;

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                if (combatants.get(i).getCurrentHP() < lowestHP) {
                    lowestHP = combatants.get(i).getCurrentHP();
                    lowestIndex = i;
                }
            }
        }
        return combatants.get(lowestIndex);
    }
}
