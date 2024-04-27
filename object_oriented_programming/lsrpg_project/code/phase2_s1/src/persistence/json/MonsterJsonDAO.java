package persistence.json;

import business.entities.Monster;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.MonsterDAO;
import persistence.exceptions.DaoInitializationException;
import java.io.*;
import java.nio.file.*;

public class MonsterJsonDAO implements MonsterDAO {


    private final File file;
    private final String path;

    public MonsterJsonDAO(String path) throws DaoInitializationException {

        this.file = new File(path);
        if (!file.exists()) {
            throw new DaoInitializationException("DAO can't be initialized because the file doesn't exists");
        }
        this.path = path;

    }

    /**
     * This function converts a json string to object Monster
     * @param string json string
     * @return Monster
     */
    public Monster monsterFromJson(String string) {


            JSONObject object1 = new JSONObject(string);

            String name = object1.getString("name");
            String challenge = object1.getString("challenge");
            int experience = object1.getInt("experience");
            int hitPoints = object1.getInt("hitPoints");
            int initiative = object1.getInt("initiative");
            String damageDice = object1.getString("damageDice");
            String damageType = object1.getString("damageType");


        return new Monster(name, challenge, experience, hitPoints, initiative, damageDice, damageType);
    }

    /**
     * Method that gets all the monsters stored in the system.
     * @throws IOException When it cannot be searched
     * @return Monster[] Array of monsters
     */
    @Override
    public Monster[] getAll() throws IOException {

        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Monster[] monsters = new Monster[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            String str = jsonArray.get(i).toString();
            monsters[i] = monsterFromJson(str);
        }

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

        String data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray jsonArray = new JSONArray(data);

        Monster monster = new Monster();

        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == index){
                String str = jsonArray.get(i).toString();
                monster = monsterFromJson(str);

                break;
            }

        }
        return monster;
    }
}
