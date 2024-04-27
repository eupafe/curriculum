package persistence.json;

import business.entities.Adventure;
import business.entities.Encounter;
import business.entities.Monster;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import persistence.AdventureDAO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class AdventureJsonDAO implements AdventureDAO {
    private final String path;
    private final File file;
    public AdventureJsonDAO(String path)  {

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
     * This function converts a json string to object Adventure
     * @param string json string
     * @return Adventure
     */
    public Adventure adventureFromJson(String string) {

        Gson gson = new Gson();
        JsonElement elem = gson.fromJson(string, JsonElement.class);
        JsonObject obj = elem.getAsJsonObject();
        String name = obj.get("name").getAsString();
        int numberEncounters = obj.get("numberEncounters").getAsInt();
        JsonArray encounters = obj.get("encounters").getAsJsonArray();

        Encounter[] finalEncounters = new Encounter[numberEncounters];

        int i = 0;
        for (JsonElement encounter : encounters) {
            finalEncounters[i] = new Encounter();
                        if (encounter.isJsonNull()) {
                            finalEncounters[i] = null;
                            continue;
                        }

                        JsonArray monsters = encounter.getAsJsonObject().get("monsters").getAsJsonArray();
                        for (JsonElement monster : monsters) {
                            JsonObject monsterObject = monster.getAsJsonObject();
                            String monsterName = monsterObject.get("name").getAsString();
                            String challenge = monsterObject.get("challenge").getAsString();
                            int xp = monsterObject.get("experience").getAsInt();
                            int hp = monsterObject.get("hitPoints").getAsInt();
                            int init = monsterObject.get("initiative").getAsInt();
                            String dDice = monsterObject.get("damageDice").getAsString();
                            String dType = monsterObject.get("damageType").getAsString();

                            finalEncounters[i].addMonsterToList(new Monster(monsterName, challenge, xp,hp, init, dDice, dType));
                        }

                        JsonArray indices = encounter.getAsJsonObject().get("indices").getAsJsonArray();
                        for (JsonElement index: indices){
                            int number = index.getAsInt();
                            finalEncounters[i].addIndexToList(number);
                        }
                        i++;
        }


        return new Adventure(name, numberEncounters, finalEncounters);
    }

    /**
     * This function converts an object Adventure to json string
     * @param adventure
     * @return String
     */

    private String adventureToJson(Adventure adventure) {

        return new Gson().toJson(adventure);
    }

    /**
     * Method that given an adventure, it saves it in the json file.
     * @throws IOException When it cannot be saved
     * @param adventure Adventure to be saved
     */
    public void save(Adventure adventure) throws IOException {

            String data =  new String(Files.readAllBytes(Paths.get(path)));


            if  (data.length() == 0) {
                data = "[" + adventureToJson(adventure) + "]";
            }
            else {
                data = data.substring(0, data.length() - 1) + "," + adventureToJson(adventure) + "]";
            }
            FileWriter file = new FileWriter(path, false);
            file.write(data);
            file.close();

    }

    /**
     * Method that given a name, it returns the corresponding adventure.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @return Adventure
     */
    @Override
    public Adventure getAdventureByName(String name) throws IOException{

        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Adventure adventure = new Adventure();

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            adventure = adventureFromJson(str);

            if (name.compareTo(adventure.getName()) == 0){

                break;
            }

        }
        return adventure;
    }

    /**
     * Method that given a name and index of Encounter, it returns all the monsters in an Encounter.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @param indexEncounter Index encounter to be searched
     * @return Encounter
     */
    @Override
    public Encounter getMonstersByAdventureAndEncounter(String name, int indexEncounter) throws IOException{

        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Adventure adventure = new Adventure();

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            adventure = adventureFromJson(str);

            if (name.compareTo(adventure.getName()) == 0){

                break;
            }

        }


        return adventure.getMonstersInEncounter(indexEncounter);
    }

    /**
     * Method that given a name, Monster and index of Encounter, it returns where the monster is located inside the Encounter.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @param monster Monster to be searched
     * @param indexEncounter Index encounter to be searched
     * @return int Position of the monster
     */
    @Override
    public int getMonsterIndex(String name, Monster monster, int indexEncounter) throws IOException {
        int index = 0;
        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Adventure adventure = new Adventure();

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            adventure = adventureFromJson(str);

            if (name.compareTo(adventure.getName()) == 0){

                break;
            }

        }

        Encounter encounter = adventure.getMonstersInEncounter(indexEncounter);

        for (int i = 0; i < encounter.getNumberDifferentMonsters(); i++) {
            if (encounter.getMonster(i).getName().equals(monster.getName())){
                index = i;
            }
        }
        return index;
    }

    /**
     * Method that checks if a specific Monster has already been added in one specific Encounter.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @param monster Monster to be searched
     * @param indexEncounter Index of encounter to be searched
     * @return int if it's unique or not
     */
    @Override
    public int checkIfMonsterAdded(String name, Monster monster, int indexEncounter) throws IOException{

        int number = 0;
        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Adventure adventure = new Adventure();

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            adventure = adventureFromJson(str);

            if (name.compareTo(adventure.getName()) == 0){

                break;
            }

        }


        Encounter encounter = adventure.getMonstersInEncounter(indexEncounter);

        for (int i = 0; i < encounter.getNumberDifferentMonsters() ; i++) {

            if ( monster.getName().equals(encounter.getMonster(i).getName()) ){
                number = encounter.getAmountMonster(i);
            }

        }
        return number;
    }

    /**
     * Method that gets all the adventures stored in the system.
     * @throws IOException When it cannot be searched
     * @return Adventure[] Array of adventures
     */
    @Override
    public Adventure[] getAll() throws IOException {

        String data = new String(Files.readAllBytes(Paths.get(path)));
        if (!data.equals("")) {
            JSONArray jsonArray = new JSONArray(data);

            Adventure[] adventures = new Adventure[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                String str = jsonArray.get(i).toString();
                adventures[i] = adventureFromJson(str);
            }

            return adventures;
        }
        else {
            return null;
        }
    }

    /**
     * Method that given an adventure, it updates this one in the json file.
     * @throws IOException When it cannot be updated
     * @param adventure Adventure to be updated
     */
    @Override
    public void update(Adventure adventure) throws IOException {

        Adventure[] copyArray = new Adventure[getAll().length];
        System.arraycopy(getAll(), 0, copyArray, 0, getAll().length);

        copyArray[getAll().length - 1] = adventure;
        String json = new Gson().toJson(copyArray);
        FileWriter file = new FileWriter(path, false);
        file.write(json);
        file.close();
    }

    /**
     * Method that checks if the Adventure name is unique.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @return int if it's unique or not
     */
    @Override
    public int checkIfAdventureUnique(String name) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(path)));

        if (!data.equals("")) {
            JSONArray jsonArray = new JSONArray(data);

            Adventure adventure = new Adventure();
            int exists = 1;

            for (int i = 0; i < jsonArray.length(); i++) {
                String str = jsonArray.get(i).toString();
                adventure = adventureFromJson(str);
                if (adventure.getName().compareTo(name) == 0) {
                    exists = 0;
                    break;
                }

            }
            return exists;
        }
        else {
            return 1;
        }
    }


}
