package persistence.json;

import business.entities.Character;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.CharacterDAO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;


public class CharacterJsonDAO implements CharacterDAO {

    private final File file;
    private final String path;
    public CharacterJsonDAO(String path)  {

        this.file = new File(path);
        if (!file.exists()) {
            try {
                Files.createFile(Paths.get(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.path = path;
    }

    /**
     * This function converts a json string to object Character
     * @param string json string
     * @return Character
     */
    public Character characterFromJson(String string)  {

        JSONObject object1 = new JSONObject(string);

        String name = object1.getString("name");
        String player = object1.getString("player");
        int xp = object1.getInt("xp");
        int body = object1.getInt("body");
        int mind = object1.getInt("mind");
        int spirit = object1.getInt("spirit");
        String classCharacter = object1.getString("class");

        return new Character(name, player, xp, body, mind, spirit, classCharacter);
    }

    /**
     * This function converts an object Character to json string
     * @param character
     * @return String
     */

    private String characterToJson(Character character) {
        return new Gson().toJson(character);
    }

    /**
     * Method that given a character, it saves it in the json file.
     * @throws IOException When it cannot be saved
     * @param character Character to be saved
     */

    @Override
    public void save(Character character) throws IOException {

            String data =  new String(Files.readAllBytes(Paths.get(path)));

            if (data.length() == 0) {
                data = "[" + characterToJson(character) + "]";
            }
            else {
                data = data.substring(0, data.length() - 1) + "," + characterToJson(character) + "]";
            }

            FileWriter file = new FileWriter(path, false);
            file.write(data);
            file.close();

    }

    /**
     * Method that given a character, it deletes it in the json file.
     * @throws IOException When it cannot be saved
     * @param character Character to be deleted
     */
    @Override
    public void delete(Character character) throws IOException {
        String json = new String();
        for (int i = 0; i < getAll().length; i++) {
            if (character.getName().equals(getAll()[i].getName())) {
                Character[] copyArray = new Character[getAll().length - 1];
                System.arraycopy(getAll(), 0, copyArray, 0, i);
                System.arraycopy(getAll(), i + 1, copyArray, i, getAll().length - i - 1);
                json = new Gson().toJson(copyArray);
            }
        }
        FileWriter file = new FileWriter(path, false);
        file.write(json);
        file.close();
    }

    /**
     * Method that gets all the characters stored in the system.
     * @throws IOException When it cannot be searched
     * @return Character[] Array of characters
     */
    @Override
    public Character[] getAll() throws IOException {

        String data = new String(Files.readAllBytes(Paths.get(path)));
        if (data.length() == 0) {
            return new Character[0];
        }

        JSONArray jsonArray = new JSONArray(data);
        Character[] characters = new Character[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            characters[i] = characterFromJson(str);
        }

        return characters;
    }

    /**
     * Method that gets all the characters stored in the system for a specific player.
     * @throws IOException When it cannot be searched
     * @param player Player to be searched
     * @return Character[] Array of characters
     */
    public Character[] getByEither(String player) throws IOException {
        int found = 0;
        int j = 0;

        if (player == "") {
            return getAll();
        }
        else {
            String data = new String(Files.readAllBytes(Paths.get(path)));
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                String str = jsonArray.get(i).toString();
                Character temp = characterFromJson(str);
                if (temp.getPlayer().contains(player)) {
                    found++;
                }
            }

            if (found == 0) {
                return null;
            } else {
                Character[] characters = new Character[found];

                for (int i = 0; i < jsonArray.length(); i++) {
                    String str = jsonArray.get(i).toString();
                    Character character = characterFromJson(str);
                    if (character.getPlayer().contains(player)) {
                        characters[j] = character;
                        j++;
                    }
                }

                return characters;
            }
        }

    }

    /**
     * Method that checks if the Character exists by searching its name in the system.
     * @throws IOException When it cannot be searched
     * @param name Character name to be searched
     * @return boolean if it exists or not
     */

    public boolean checkIfCharacterExists(String name) throws IOException{
        String data = new String(Files.readAllBytes(Paths.get(path)));
        if (data.length() == 0) {
            return false;
        }
        JSONArray jsonArray = new JSONArray(data);
        Character character = new Character();
        boolean exists = false;

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            character = characterFromJson(str);
            if (character.getName().compareTo(name) == 0){
                exists = true;
                break;
            }

        }
        return exists;
    }

    /**
     * Method that given a character, it updates this one in the json file.
     * @throws IOException When it cannot be updated
     * @param character Character to be updated
     */
    @Override
    public void update(Character character) throws IOException {
        Character[] copyArray = getAll();
        for (int i = 0; i < copyArray.length; i++) {
            if (copyArray[i].getName().equals(character.getName())) {
                copyArray[i] = character;
            }
        }
        String json = new Gson().toJson(copyArray);
        FileWriter file = new FileWriter(path, false);
        file.write(json);
        file.close();
    }
}


