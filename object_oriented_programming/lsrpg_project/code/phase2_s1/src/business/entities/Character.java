package business.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Class Character, used to store and read information from persistence, holds all the information needed for a character, outside\
 * an adventure.
 */
public class Character {

    private String name;
    private String player;
    private int xp;
    private int body;
    private int mind;
    private int spirit;

    @SerializedName(value = "class", alternate = "classGiven")
    private String classGiven;

    public Character() {
        this.name = "";
        this.player = "";
        this.xp = 0;
        this.body = 0;
        this.mind = 0;
        this.spirit = 0;
        this.classGiven = "";
    }
    public Character(String name, String player, int xp, int body, int mind, int spirit, String classGiven) {
        this.name = name;
        this.player = player;
        this.xp = xp;
        this.body = body;
        this.mind = mind;
        this.spirit = spirit;
        this.classGiven = classGiven;
    }

    public Character(CombatCharacter character) {
        this.name = character.getName();
        this.player = character.getPlayer();
        this.xp = character.getXp();
        this.body = character.getBody();
        this.mind = character.getMind();
        this.spirit = character.getSpirit();
        this.classGiven = character.getClassGiven();
    }

    //copy constructor
    public Character(Character character) {
        this.name = character.getName();
        this.player = character.getPlayer();
        this.xp = character.getXp();
        this.body = character.getBody();
        this.mind = character.getMind();
        this.spirit = character.getSpirit();
        this.classGiven = character.getClassGiven();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public void setMind(int mind) {
        this.mind = mind;
    }

    public void setSpirit(int spirit) {
        this.spirit = spirit;
    }

    public String getName() {
        return name;
    }

    public String getPlayer() {
        return player;
    }

    public int getXp() {
        return xp;
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

    public String getClassGiven() {
        return classGiven;
    }
}

