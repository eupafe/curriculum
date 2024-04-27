package persistence;

import business.entities.Monster;
import java.io.IOException;


public interface MonsterDAO {
    Monster[] getAll() throws IOException;

    Monster getMonsterByIndex(int index) throws IOException;
}
