package business.entities.characterClasses;

import business.entities.*;
import business.entities.Character;

import java.util.ArrayList;

/**
 * Warrior class, extends from combat characters and overrides certain methods making it so that warriors take and receive
 * actions differently from other character classes.
 */
public class Warrior extends CombatCharacter {
    public Warrior(Character character) {
        super(character);
    }

    public Warrior(CombatCharacter character) {
        super(new Character(character));
        initializeHP();
    }
    /**
     * Method to initialize the HP of an adventurer.
     */
    @Override
    public void initializeHP() {
        int body = this.getBody();
        int lvl = (this.getXp() + 100)/100;
        this.maxHP = this.currentHP = (10 + body)*lvl;

    }

    /**
     * Method to initialize the initiative of an adventurer.
     */
    @Override
    public void initializeInitiative() {
        Dice d12 = new Dice(12);
        int spirit = this.getSpirit();
        this.initiative = d12.roll() + spirit;
    }

    /**
     * Method in which a warrior takes a preparation action.
     * @param combatants ArrayList of CombatCreature's which tell the warrior every enemy and party member in the encounter.
     * @return Action to take during preparation stage, in the warrior's case this will always be "Self-Motivated".
     * @see Action
     */
    @Override
    public Action takePreparationAction(ArrayList<CombatCreature> combatants) {
        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = this;
        int amount = 1;
        String message = "\n" + this.name + " uses Self-Motivated. Their Spirit increases in +1.";
        String type = "Support";
        String targetStat = "Spirit";
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which a warrior takes a combat action.
     * @param combatants ArrayList of CombatCreature's which tell the warrior every enemy and party member in the encounter.
     * @return Action to take during combat stage, in the warrior's case this will always be "Improved sword slash".
     * @see Action
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        Dice d10 = new Dice(10);

        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = lowestEnemy(combatants);
        int amount = d10.roll() + this.body;
        String message = "\n\n" + this.name + " attacks " + targets[0].getName() + " with Improved sword slash.";
        String type = "Attack";
        String targetStat = null;
        String damageType = "Physical";

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
     * Method in which a warrior takes a short rest action.
     * @param combatants ArrayList of CombatCreature's which tell the warrior every enemy and party member in the encounter.
     * @return Action to take during short rest stage, in the warrior's case this will always be "Bandage time".
     * @see Action
     */
    @Override
    public Action takeShortRestAction(ArrayList<CombatCreature> combatants) {
        Dice d8 = new Dice(8);

        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = this;
        int amount = d8.roll() + this.mind;
        String message = "\n" + this.name + " uses Bandage time. Heals " + amount + " hit points.";
        String type = "Heal";
        String targetStat = null;
        String damageType = null;

        if (!this.conscious) {
            return new Action(null, 0, "\n" + this.name + " is unconscious.", null, null, null);
        }

        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which the warrior receives an action, first checking if this warrior is a target, then sorting whether it is
     * a Heal, Support or Attack action and then affecting the corresponding stat. In the case of a warrior if the action
     * is of type "Attack" and the damageType is "Physical" then the amount that is subtracted from health is divided in two.
     * @param action Action which the warrior should take (if he is a target of this action).
     * @return String message, this message is used only when the adventurer falls unconscious due to an attack inflicted on it.
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
                        switch (action.getDamageType()) {
                            case "Physical":
                                this.currentHP = Integer.max(0, this.currentHP - (action.getAmount()/2));
                                break;
                            case "Magical":
                            case "Psychical":
                                this.currentHP = Integer.max(0, this.currentHP -action.getAmount());
                                break;

                        }

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
     * Method in which the XP of the warrior gets updated, also checking for possible level up or evolution.
     * @param XP Integer experience value to be added to warrior's xp.
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

        if (lvlNow > 7) {
            message += this.name + " evolves to Champion!\n";
        }
        return message;
    }

    /**
     * Method to check if this warrior can evolve.
     * @return Boolean true if the warrior can evolve, if not false.
     */
    @Override
    public boolean canEvolve() {
        if ((this.xp + 100)/100 > 7){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method for evolving a warrior, this is when they evolve into a Champion, changing the class type of the character.
     * @return Champion with the same information (name, player, etc.) as the warrior.
     */
    @Override
    public CombatCharacter evolve() {
        this.classGiven = "Champion";
        return new Champion(this);
    }

    /**
     * Method that returns the lowest enemy currently in an encounter.
     * @param combatants ArrayList of CombatCreature's to find the lowest enemy in.
     * @return CombatCreature the lowest enemy in the encounter.
     */
    private CombatCreature lowestEnemy(ArrayList<CombatCreature> combatants) {
        int lowestHP = Integer.MAX_VALUE;
        int lowestIndex = -1;

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isMonster() && combatants.get(i).isConscious()) {
                if (combatants.get(i).getCurrentHP() < lowestHP) {
                    lowestHP = combatants.get(i).getCurrentHP();
                    lowestIndex = i;
                }
            }
        }
        return combatants.get(lowestIndex);
    }
}
