package persistence.api;

import business.entities.Adventure;
import business.entities.Encounter;
import business.entities.Monster;
import com.google.gson.Gson;
import persistence.AdventureDAO;
import java.io.IOException;
public class AdventureApiDAO implements AdventureDAO {

    private ApiHelper api;

    private String url;
    public AdventureApiDAO() throws IOException{

        api = new ApiHelper();
        url = "https://balandrau.salle.url.edu/dpoo/S1-Project_11/adventures";
        api.getFromUrl(url);

    }

    /**
     * Method that given an adventure, it saves it in the api server.
     * @throws IOException When it cannot be saved
     * @param adventure Adventure to be saved
     */
    @Override
    public void save(Adventure adventure) throws IOException {

        Gson gson = new Gson();
        String json = gson.toJson(adventure);
        api.postToUrl(url, json);
    }

    /**
     * Method that given an adventure, it updates this one in the api server.
     * @throws IOException When it cannot be updated
     * @param adventure Adventure to be updated
     */

    @Override
    public void update(Adventure adventure) throws IOException {

        String[] parts = adventure.getName().split(" ");
        String name = adventure.getName();

        if (parts.length > 1) {
            String formattedName = parts[0];
            for (int i = 1; i < parts.length; i++) {
                formattedName += "%20";
                formattedName += parts[i];
            }
            name = formattedName;
        }

        api.deleteFromUrl(url+"?name="+name);
        save(adventure);
    }

    /**
     * Method that given a name, it returns the corresponding adventure.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @return Adventure
     */

    @Override
    public Adventure getAdventureByName(String name) throws IOException {
        String json = api.getFromUrl(url+"?name="+name);
        Gson gson = new Gson();
        Adventure[] adventure = gson.fromJson(json, Adventure[].class);

        return adventure[0];
    }

    /**
     * Method that given a name and index of Encounter, it returns all the monsters.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @param indexEncounter Index encounter to be searched
     * @return Encounter
     */

    @Override
    public Encounter getMonstersByAdventureAndEncounter(String name, int indexEncounter) throws IOException {

        String json = api.getFromUrl(url+"?name="+name);
        Gson gson = new Gson();
        Adventure[] adventure = gson.fromJson(json, Adventure[].class);

        return adventure[0].getMonstersInEncounter(indexEncounter);
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
        Adventure adventure = getAdventureByName(name);

        Encounter encounter = adventure.getMonstersInEncounter(indexEncounter);

        for (int i = 0; i < encounter.getNumberDifferentMonsters(); i++) {
            if (encounter.getMonster(i).getName().equals(monster.getName())){
                index = i;
            }
        }

        return index;
    }

    /**
     * Method that gets all the adventures stored in the system.
     * @throws IOException When it cannot be searched
     * @return Adventure[] Array of adventures
     */
    @Override
    public Adventure[] getAll() throws IOException {
        String json = api.getFromUrl(url);
        Gson gson = new Gson();
        Adventure[] adventure = gson.fromJson(json, Adventure[].class);
        return adventure;
    }


    /**
     * Method that checks if the Adventure name is unique.
     * @throws IOException When it cannot be searched
     * @param name Adventure name to be searched
     * @return int if it's unique or not
     */
    @Override
    public int checkIfAdventureUnique(String name) throws IOException {

        String json = api.getFromUrl(url);
        if (json.equals("[]")){
           return 1;
        }
        else{

            String[] parts = name.split(" ");

            String formattedName = name;
            if (parts.length > 1) {
                formattedName = parts[0];
                for (int i = 1; i < parts.length; i++) {
                    formattedName += "%20";
                    formattedName += parts[i];
                }
            }
           String json1 = api.getFromUrl(url+"?name="+formattedName);

           if ( json1.equals("[]") ){
               return 1;
           }
           else{

               Gson gson = new Gson();
               Adventure[] adventure = gson.fromJson(json1, Adventure[].class);

               if (adventure[0].getName().equals(name)){

                   return 0;
               }
               else{
                   return 1;
               }
           }
        }

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
    public int checkIfMonsterAdded(String name, Monster monster, int indexEncounter) throws IOException {

        int number = 0;

        String[] parts = name.split(" ");

        String formattedName = name;
        if (parts.length > 1) {
            formattedName = parts[0];
            for (int i = 1; i < parts.length; i++) {
                formattedName += "%20";
                formattedName += parts[i];
            }
        }
        String json = api.getFromUrl(url+"?name="+formattedName);
        Gson gson = new Gson();
        Adventure[] adventure = gson.fromJson(json, Adventure[].class);

        Encounter encounter = adventure[0].getMonstersInEncounter(indexEncounter);

        for (int i = 0; i < encounter.getNumberDifferentMonsters() ; i++) {

            if ( monster.getName().equals(encounter.getMonster(i).getName()) ){
                number = encounter.getAmountMonster(i);
            }

        }
        return number;
    }
}
