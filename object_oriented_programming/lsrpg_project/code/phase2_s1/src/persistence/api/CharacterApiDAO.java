
package persistence.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import persistence.CharacterDAO;
import business.entities.Character;
import java.io.IOException;
import java.lang.reflect.Type;

public class CharacterApiDAO implements CharacterDAO {

    private ApiHelper api;

    private String url;
    public CharacterApiDAO() throws IOException {
        api = new ApiHelper();
        url = "https://balandrau.salle.url.edu/dpoo/S1-Project_11/characters";
        api.getFromUrl(url);

    }

    /**
     * Method that given a character, it saves it in the api server.
     * @throws IOException When it cannot be saved
     * @param character Character to be saved
     */
    @Override
    public void save(Character character) throws IOException {

        Gson gson = new Gson();
        String json = gson.toJson(character);
        api.postToUrl(url, json);

    }

    /**
     * Method that given a character, it deletes it in the api server.
     * @throws IOException When it cannot be saved
     * @param character Character to be saved
     */
    @Override
    public void delete(Character character) throws IOException {

        if ( checkIfCharacterExists( character.getName() ) ) {
            api.deleteFromUrl(url+"?name="+character.getName());

        }
    }

    /**
     * Method that gets all the characters stored in the system.
     * @throws IOException When it cannot be searched
     * @return Character[] Array of characters
     */
    @Override
    public Character[] getAll() throws IOException {

        String listCharacters = api.getFromUrl(url);
        Type characterType = new TypeToken<Character[]>() {}.getType();
        Character[] characters = new Gson().fromJson(listCharacters, characterType);

        return characters;
    }

    /**
     * Method that gets all the characters by player name stored in the system.
     * @throws IOException When it cannot be searched
     * @param player Player name to be searched
     * @return Character[] Array of characters
     */
    @Override
    public Character[] getByEither(String player) throws IOException {

        if (player == "") {
            return getAll();
        }
        else {

            String json = api.getFromUrl(url+"?player="+player);
            if (json.equals("[]")){
                return null;
            }
            else{
                Type characterType = new TypeToken<Character[]>() {}.getType();
                Character[] characters = new Gson().fromJson(json, characterType);
                return characters;
            }

        }

    }

    /**
     * Method that checks if the Character exists.
     * @throws IOException When it cannot be searched
     * @param name Character name to be searched
     * @return boolean if it exists or not
     */
    public boolean checkIfCharacterExists(String name) throws IOException{

        String json = api.getFromUrl(url+"?name="+name);
        if (json.equals("[]")){
            return false;
        }

        return true;
    }

    /**
     * Method that given a character, it updates this one in the api server.
     * @throws IOException When it cannot be updated
     * @param character Adventure to be updated
     */
    @Override
    public void update(Character character) throws IOException {

        Character newCharacter = new Character(character);

        String[] parts = newCharacter.getName().split(" ");

        if (parts.length > 1) {
            String formattedName = parts[0];
            for (int i = 1; i < parts.length; i++) {
                formattedName += "%20";
                formattedName += parts[i];
            }
            newCharacter.setName(formattedName);
        }

        delete(newCharacter);
        save(character);

    }



}
