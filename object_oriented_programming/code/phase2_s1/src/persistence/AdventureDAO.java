package persistence;

import business.entities.Adventure;
import business.entities.Encounter;
import business.entities.Monster;


import java.io.IOException;

public interface AdventureDAO {

    void save(Adventure adventure) throws IOException;

    Adventure getAdventureByName(String name) throws IOException;

    Encounter getMonstersByAdventureAndEncounter(String name, int indexEncounter) throws IOException;

    int getMonsterIndex(String name, Monster monster, int indexEncounter) throws IOException;

    Adventure[] getAll() throws IOException;

    void update(Adventure adventure) throws IOException;

    int checkIfAdventureUnique(String name) throws IOException;
    int checkIfMonsterAdded(String name, Monster monster, int indexEncounter) throws IOException;
}
