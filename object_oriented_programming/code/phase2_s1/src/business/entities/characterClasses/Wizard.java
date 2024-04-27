package business.entities.characterClasses;

import business.entities.*;
import business.entities.Character;

import java.util.ArrayList;

/**
 * Wizart class, extends from combat characters and overrides certain methods making it so that wizards take and receive
 * actions differently from other character classes.
 */
public class Wizard extends CombatCharacter {

    private int shield;

    public Wizard(Character character) {
        super(character);
        this.shield = 0;
    }

    /**
     * Method to initialize the HP of a champion.
     */
    @Override
    public void initializeHP() {
        int body = this.getBody();
        int lvl = (this.getXp() + 100)/100;
        this.maxHP = this.currentHP = (10 + body)*lvl;
    }

    /**
     * Method to initialize the initiative of a champion.
     */
    @Override
    public void initializeInitiative() {
        Dice d20 = new Dice(20);
        int mind = this.getMind();
        this.initiative = d20.roll() + mind;
    }

    /**
     * Method in which a wizard takes a preparation action.
     * @param combatants ArrayList of CombatCreature's which tell the wizard every enemy and party member in the encounter.
     * @return Action to take during preparation stage, in the wizard's case this will always be "Mage shield".
     * @see Action
     */
    @Override
    public Action takePreparationAction(ArrayList<CombatCreature> combatants) {
        Dice d6 = new Dice(6);
        CombatCreature[] targets = new CombatCreature[1];
        targets[0] = this;
        int amount = (d6.roll() + this.mind) * (this.getXp() + 100)/100;
        String message = "\n" + this.name + " uses Mage shield. Shield recharges to " + amount;
        String type = "Support";
        String targetStat = "Shield";
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which a wizard takes a combat action.
     * @param combatants ArrayList of CombatCreature's which tell the wizard every enemy and party member in the encounter.
     * @return Action to take during combat stage, in the wizard's case this will either be "Fireball" if there are three or more
     * enemies left standing or "Arcane comet" if not.
     * @see Action
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        Dice d4 = new Dice(4);
        Dice d6 = new Dice(6);
        Dice d10 = new Dice(10);

        if (moreThanTwoEnemies(combatants)) {
            ArrayList<CombatCreature> targetList = new ArrayList<>();
            for (int i = 0; i < combatants.size(); i++) {
                if (combatants.get(i).isMonster() && combatants.get(i).isConscious()) {
                    targetList.add(combatants.get(i));
                }
            }
            CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
            int amount = d4.roll() + this.mind;
            String message = "\n\n" + this.name + " attacks ";
            for (int i = 0; i < targets.length; i++) {
                if (i < targets.length - 2) {
                    message += targets[i].getName() + ", ";
                }
                else {
                    if (i == targets.length - 1) {
                        message += targets[i].getName() + " with Fireball.";
                    }
                    if (i == targets.length - 2) {
                        message += targets[i].getName() + " and ";
                    }
                }
            }

            String type = "Attack";
            String targetStat = null;
            String damageType = "Magical";

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
        else {
            CombatCreature[] targets = new CombatCreature[1];
            targets[0] = highestEnemy(combatants);
            int amount = d6.roll() + this.mind;
            String message = "\n\n" + this.name + " attacks " + targets[0].getName() + " with Arcane comet.";
            String type = "Attack";
            String targetStat = null;
            String damageType = "Magical";

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
    }

    /**
     * Method in which a wizard takes a short rest action.
     * @param combatants ArrayList of CombatCreature's which tell the wizard every enemy and party member in the encounter.
     * @return Action to take during short rest stage, in the wizard's case this will always be "Reading a book".
     * @see Action
     */
    @Override
    public Action takeShortRestAction(ArrayList<CombatCreature> combatants) {
        CombatCreature[] targets = new CombatCreature[0];
        int amount = 0;
        String message = "\n" + this.name + " is reading a book.";
        String type = null;
        String targetStat = null;
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which the wizard receives an action, first checking if this wizard is a target, then sorting whether it is
     * a Heal, Support or Attack action and then affecting the corresponding stat. In the case of a wizard if the action
     * is of type "Attack" and the damageType is "Magical" then the amount that is subtracted from health is divided in two.
     * Not only that but in the case of an "Attack" the amount will be subtracted from the wizards magical shield, if there is more
     * damage dealt than magical shield, then the remainder will be subtracted from the HP.
     * @param action Action which the wizard should take (if he is a target of this action).
     * @return String message, this message is used only when the champion falls unconscious due to an attack inflicted on it.
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
                            case "Magical":
                                int reducedDamage = Integer.max(0, action.getAmount() - ((this.getXp() + 100)/100));
                                if (reducedDamage > this.shield) {
                                    int reminder = reducedDamage - this.shield;
                                    this.currentHP = Integer.max(0, reminder);
                                }
                                this.shield = Integer.max(0, this.shield - reducedDamage);
                                break;
                            case "Physical":
                            case "Psychical":
                                if (action.getAmount() > this.shield) {
                                    int reminder = action.getAmount() - this.shield;
                                    this.currentHP = Integer.max(0, reminder);
                                }
                                this.shield = Integer.max(0, this.shield - action.getAmount());
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
     * Method in which the XP of the wizard gets updated, also checking for possible level up.
     * @param XP Integer experience value to be added to wizard's xp.
     * @return String message to be sent to ui, this tells the user how much xp is gained and if there was a level up.
     */
    @Override
    public String updateXP(int XP) {
        String message = this.name + " gains " + XP + " xp. ";
        int lvlBefore = (this.getXp() + 100)/100;
        this.xp += XP;
        int lvlNow = (this.xp + 100)/100;
        if (lvlBefore < lvlNow && lvlNow < 10) {
            message += this.name + " levels up. They are now lvl " + lvlNow + "!\n";
            this.currentHP = this.maxHP;
        }
        else {
            message += "\n";
        }
        return message;
    }

    @Override
    public boolean canEvolve() {
        return false;
    }

    @Override
    public CombatCharacter evolve() {
        return null;
    }

    /**
     * Method to check if there are more than two enemies in combat.
     * @param combatants ArrayList of CombatCreatures.
     * @return Boolean true if there are two or more enemies alive, if not false.
     */
    private boolean moreThanTwoEnemies(ArrayList<CombatCreature> combatants) {
        int numEnemies = 0;
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isMonster() && combatants.get(i).isConscious()) {
                numEnemies++;
            }
        }
        return (numEnemies > 2);
    }

    /**
     * Method which finds the enemy with the most health.
     * @param combatants ArrayList of CombatCreatures in which the highest HP enemy is searched for.
     * @return CombatCreature the enemy with the highes health.
     */
    private CombatCreature highestEnemy(ArrayList<CombatCreature> combatants) {
        int highestHP = 0;
        int highestIndex = -1;

        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isMonster() && combatants.get(i).isConscious()) {
                if (combatants.get(i).getCurrentHP() > highestHP) {
                    highestHP = combatants.get(i).getCurrentHP();
                    highestIndex = i;
                }
            }
        }
        return combatants.get(highestIndex);
    }
}
