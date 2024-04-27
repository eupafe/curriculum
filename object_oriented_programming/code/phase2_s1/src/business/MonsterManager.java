package business;

import business.entities.Monster;
import persistence.MonsterDAO;
import persistence.api.MonsterApiDAO;
import persistence.exceptions.DaoInitializationException;
import persistence.json.MonsterJsonDAO;
import java.io.IOException;


/**
 * Class to manage all of the monsters in persistence.
 */
public class MonsterManager {

    private MonsterDAO dao = null;
    private boolean openFile = true;

    /**
     * Constructor in which depending on the users input one dao is used or another.
     * @param option user's input.
     */
    public MonsterManager(int option)  {

        if (option == 1) {
            try {
                dao = new MonsterJsonDAO("src/monsters.json");
            } catch (DaoInitializationException e) {
                openFile = false;
            }
        }
        else{
            try {
                dao = new MonsterApiDAO();
            } catch (IOException e) {
                openFile = false;
            }
        }
    }

    public boolean checkMonsterDAO(){
        return openFile;
    }

    /**
     * Method to return all of the monsters currently persisted.
     * @return Array of Monsters.
     */
    public Monster[] getAllMonsters()  {
        try {
            return dao.getAll();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method to get a monster knowing it's index/ position.
     * @param index Integer index/ position of the monster.
     * @return Monster if it is found if not then null.
     */
    public Monster getMonsterByIndex(int index) {
        try {
            return dao.getMonsterByIndex(index);
        } catch (IOException e) {
            return null;
        }
    }


}
