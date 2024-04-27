package business.entities;

/**
 * Class monster used to save and read all the information of a monster, to and from the persistence where the data is stored.
 */
public class Monster {


    private String name;
    private String challenge;
    private int experience;
    private int hitPoints;
    private int initiative;
    private String damageDice;
    private String damageType;

    public Monster(){
        this.name = " ";
        this.challenge = " ";
        this.experience = 0;
        this.hitPoints = 0;
        this.initiative = 0;
        this.damageDice = " ";
        this.damageType = " ";
    }
    public Monster(String name, String challenge, int experience, int hitPoints, int initiative, String damageDice, String damageType) {
        this.name = name;
        this.challenge = challenge;
        this.experience = experience;
        this.hitPoints = hitPoints;
        this.initiative = initiative;
        this.damageDice = damageDice;
        this.damageType = damageType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }


    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }


    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getName() {
        return name;
    }

    public String getChallenge() {
        return challenge;
    }

    public int getExperience() {
        return experience;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getInitiative() {
        return initiative;
    }

    public String getDamageDice() {
        return damageDice;
    }

    public String getDamageType() {
        return damageType;
    }
}
