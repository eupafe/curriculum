package business.entities;

/**
 * Class CombatCharacter, a class which holds all the information a character needs inside an adventure, this class inherits
 * from the generic CombatCreature class used inside adventures.
 */
public abstract class CombatCharacter extends CombatCreature {
    protected int body;
    protected int mind;
    protected int spirit;
    protected String classGiven;
    private int defaultBody;
    private int defaultMind;
    private int defaultSpirit;

    private String player;


    public CombatCharacter(Character character) {
        super(character.getName(), 0,0,0, character.getXp(),  true, 0);
        this.body = this.defaultBody = character.getBody();
        this.mind = this.defaultMind =character.getMind();
        this.spirit = this.defaultSpirit = character.getSpirit();
        this.player = character.getPlayer();
        this.classGiven = character.getClassGiven();
    }

    public abstract void initializeHP();

    public abstract void initializeInitiative();

    public String getName() {
        return name;
    }

    public int getBody() {
        return body;
    }

    public int getMind() {
        return mind;
    }

    public int getSpirit() {
        return spirit;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getInitiative() {
        return initiative;
    }

    public String getPlayer() {
        return player;
    }

    public String getClassGiven() {
        return classGiven;
    }

    public void resetStats() {
        this.body = this.defaultBody;
        this.mind = this.defaultMind;
        this.spirit = this.defaultSpirit;
    }



}
