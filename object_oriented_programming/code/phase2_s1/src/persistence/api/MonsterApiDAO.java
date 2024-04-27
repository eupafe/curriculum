package persistence.api;

import business.entities.Monster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import persistence.MonsterDAO;
import java.io.IOException;
import java.lang.reflect.Type;

public class MonsterApiDAO implements MonsterDAO {


    private ApiHelper api;

    private String balandrau;
    public MonsterApiDAO() throws IOException {

        api = new ApiHelper();
        balandrau = "https://balandrau.salle.url.edu/dpoo/";
        api.getFromUrl(balandrau+"monsters");
    }

    /**
     * Method that gets all the monsters stored in the system.
     * @throws IOException When it cannot be searched
     * @return Monster[] Array of monsters
     */
    @Override
    public Monster[] getAll() throws IOException {

        String listMonsters = api.getFromUrl(balandrau + "shared/monsters");
        Type monsterType = new TypeToken<Monster[]>() {}.getType();
        Monster[] monsters = new Gson().fromJson(listMonsters, monsterType);

        return monsters;
    }

    /**
     * Method that gets a specific monster by his index in the system.
     * @throws IOException When it cannot be searched
     * @param index Index to be searched
     * @return Monster
     */
    @Override
    public Monster getMonsterByIndex(int index) throws IOException{

        String json = api.getFromUrl(balandrau + "/shared/monsters/"+index);
        Monster monster = new Gson().fromJson(json, Monster.class);
        return monster;
    }
}

