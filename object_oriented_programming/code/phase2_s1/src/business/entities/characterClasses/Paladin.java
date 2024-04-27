package business.entities.characterClasses;

import business.entities.*;
import business.entities.Character;

import java.util.ArrayList;

/**
 * Paladin class, extends from combat characters and overrides certain methods making it so that paladins take and receive
 * actions differently from other character classes.
 */
public class Paladin extends CombatCharacter {

    public Paladin(Character character) {
        super(character);
    }

    public Paladin(CombatCharacter character) {
        super(new Character(character));
        initializeHP();
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
     * Method in which a paladin takes a preparation action.
     * @param combatants ArrayList of CombatCreature's which tell the paladin every enemy and party member in the encounter.
     * @return Action to take during preparation stage, in the paladin's case this will always be "Blessing of good luck".
     * @see Action
     */
    @Override
    public Action takePreparationAction(ArrayList<CombatCreature> combatants) {
        Dice d3 = new Dice(3);
        ArrayList<CombatCreature> targetList = new ArrayList<>();
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                targetList.add(combatants.get(i));
            }
        }
        CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
        int amount = d3.roll();
        String message = "\n" + this.name + " uses Blessing of good luck. Everyone's Mind increases in +" + amount + ".";
        String type = "Support";
        String targetStat = "Mind";
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }

    /**
     * Method in which a paladin takes a combat action.
     * @param combatants ArrayList of CombatCreature's which tell the paladin every enemy and party member in the encounter.
     * @return Action to take during combat stage, in the paladin's case this will either be "Prayer of mass healing" or "Never
     * on my watch" depending on whether the party needs healing or not..
     * @see Action
     */
    @Override
    public Action takeCombatAction(ArrayList<CombatCreature> combatants) {
        if (toHealOrNotToHeal(combatants)) {
            ArrayList<CombatCreature> targetList = new ArrayList<>();
            for (int i = 0; i < combatants.size(); i++) {
                if (combatants.get(i).isPartyMember()) {
                    targetList.add(combatants.get(i));
                }
            }
            CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
            Dice d10 =  new Dice(10);
            int amount = d10.roll() + this.mind;
            String message = "\n" + this.name + " uses Prayer of mass healing. Heals " + amount + " hit points to ";
            for (int i = 0; i < targets.length; i++) {
                if (i < targets.length - 2) {
                    message += targets[i].getName() + ", ";
                } else {
                    if (i == targets.length - 1) {
                        message += targets[i].getName() + ".";
                    }
                    if (i == targets.length - 2) {
                        message += targets[i].getName() + " and ";
                    }
                }

            }
            String type = "Heal";
            String targetStat = null;
            String damageType = null;
            return new Action(targets, amount, message, type, targetStat, damageType);
        }
        else {
            Dice d8 = new Dice(8);
            Dice d10 = new Dice(10);
            CombatCreature[] targets = new CombatCreature[1];
            targets[0] = chooseRandom(combatants);
            int amount = d8.roll();
            String message = "\n\n" + this.name + " attacks " + targets[0].getName() + " with Never on my watch.";
            String type = "attack";
            String targetStat = null;
            String damageType = "Psychical";
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
     * Method in which a paladin takes a short rest action.
     * @param combatants ArrayList of CombatCreature's which tell the paladin every enemy and party member in the encounter.
     * @return Action to take during short rest stage, in the paladin's case this will always be "Prayer of mass healing".
     * @see Action
     */
    @Override
    public Action takeShortRestAction(ArrayList<CombatCreature> combatants) {
        ArrayList<CombatCreature> targetList = new ArrayList<>();
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember()) {
                targetList.add(combatants.get(i));
            }
        }
        CombatCreature[] targets = targetList.toArray(CombatCreature[]::new);
        Dice d10 =  new Dice(10);
        int amount = d10.roll() + this.mind;
        String message = "\n" + this.name + " uses Prayer of mass healing. Heals " + amount + " hit points to ";
        for (int i = 0; i < targets.length; i++) {
            if (i < targets.length - 2) {
                message += targets[i].getName() + ", ";
            } else {
                if (i == targets.length - 1) {
                    message += targets[i].getName() + " and ";
                }
                if (i == targets.length - 2) {
                    message += targets[i].getName() + ".";
                }
            }

        }
        String type = "Heal";
        String targetStat = null;
        String damageType = null;
        return new Action(targets, amount, message, type, targetStat, damageType);
    }


    /**
     * Method in which the paladin receives an action, first checking if this champion is a target, then sorting whether it is
     * a Heal, Support or Attack action and then affecting the corresponding stat. In the case of a paladin if the action
     * is of type "Attack" and the damageType is "Psychical" then the amount that is subtracted from health is divided in two.
     * @param action Action which the paladin should take (if he is a target of this action).
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
                            case "Psychical":
                                this.currentHP = Integer.max(0, this.currentHP - (action.getAmount()/2));
                                break;
                            case "Magical":
                            case "Physical":
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
     * Method in which the XP of the paladin gets updated, also checking for possible level up.
     * @param XP Integer experience value to be added to paladin's xp.
     * @return String message to be sent to ui, this tells the user how much xp is gained, if there was a level up.
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
     * Method to see if there are party members that need healing or not.
     * @param combatants ArrayList of CombatCreatures which for each party member we check if they need healing or not.
     * @return Boolean true if the party needs healing false if the party does not.
     */
    private boolean toHealOrNotToHeal(ArrayList<CombatCreature> combatants) {
        //that is the question
        boolean toHealOrNotToHeal = false;
        for (int i = 0; i < combatants.size(); i++) {
            if (combatants.get(i).isPartyMember() && combatants.get(i).getCurrentHP() < (combatants.get(i).getMaxHP()/2)) {
                toHealOrNotToHeal = true;
            }
        }
        return toHealOrNotToHeal;
    }

    /**
     * Method in which an enemy is chosen at random.
     * @param combatants ArrayList of CombatCreatures from which one is chosen at random.
     * @return CombatCreature an enemy chosen at random.
     */
    private CombatCreature chooseRandom(ArrayList<CombatCreature> combatants) {
        int randomIndex;
        do {
            randomIndex = (int) ((Math.random() * (combatants.size() - 1)));
        } while (combatants.get(randomIndex).isPartyMember() || !combatants.get(randomIndex).isConscious());

        return combatants.get(randomIndex);
    }
}
