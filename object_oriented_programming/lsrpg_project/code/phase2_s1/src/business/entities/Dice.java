package business.entities;

/**
 * Class that is used to roll randomized dice, this class is constructed with either a String (dx: example: d10 creates a dice
 * that when rolled returns a random value in between [1-10], or with an integer (10: creating a dice [1-10]).
 */
public class Dice {

    private int type;

    public Dice(int type) {
        this.type = type;
    }

    public Dice(String type) {
        this.type = Integer.valueOf(type.substring(1));
    }

    /**
     * Method to, depending on the dice type, return a random integer in between [1-type].
     * @return Integer, random integer between [1-type].
     */
    public int roll() {
        return (int) ((Math.random() * (type)) + 1);
    }

}
