package business;

import business.entities.Character;
import org.apache.commons.lang3.text.WordUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which is used to store certain methods that are purely logic based, for example checking if a name is formatted or not etc.
 */
public class Tools {

    public Tools() {
    }

    /**
     * Method that formats a character's name.
     * @param name String name to be formatted.
     * @return String formatted name.
     */
    public String formatCharacterName(String name){

        String nameFormatted = WordUtils.capitalizeFully(name);

        return nameFormatted;
    }

    /**
     * Method to check if the characters level is within the [1-10] range.
     * @param level Integer level to be checked.
     * @return Integer 0 if there is no error, 1 if the level is out of bounds.
     */
    public int checkCharacterLevel(int level){
        int error = 0;
        if (level < 1 || level > 10){
            error = 1;
        }
        return error;
    }

    /**
     * Method to check if an adventure name is correctly formatted.
     * @param name String adventure's name.
     * @return Boolean, true if the adventure's name is correctly formatted, false if not.
     */
    public boolean adventureNameOK(String name) {
        boolean nameOK = true;

        Pattern p = Pattern.compile("[^A-Za-z\\s]");
        Matcher m = p.matcher(name);
        if (m.find() || name.equals("")){
            nameOK = false;
        }
        return nameOK;
    }

    /**
     * Method to check if the number of characters is within the [3-5] bound.
     * @param numCharacters Integer number of characters.
     * @return Boolean true if the number is within range if not false.
     */
    public boolean checkNumberCharacters(int numCharacters) {
        return !(numCharacters < 3 || numCharacters > 5);
    }

    /**
     * Method to check if the party member introduced, is available or not.
     * @param partyMember Integer party member to be checked.
     * @param availableCharacters List of available characters.
     * @param party List of current party members.
     * @return Boolean true if the party member is available, false if not.
     */
    public boolean checkPartyMember(int partyMember, Character[] availableCharacters, Character[] party)  {
        boolean check = true;

        if (partyMember >= 1 && partyMember <= availableCharacters.length) {

            for (int i = 0; i < party.length; i++) {
                if (availableCharacters[partyMember - 1] == party[i]) {
                    check = false;
                }
            }

            return check;
        }
        else {
            return false;
        }
    }
}
