package persistence;

import business.entities.Character;
import java.io.IOException;

public interface CharacterDAO {

    /**
     * Method that saves a specific character, persisting its information.
     *
     * @param character the character to save
     * @throws IOException if something goes wrong when persisting
     */
    void save(Character character) throws IOException;

    Character[] getAll() throws IOException;

    void delete(Character character) throws IOException;

    Character[] getByEither(String player) throws IOException;

    boolean checkIfCharacterExists(String name) throws IOException;

    void update(Character character) throws IOException;
}
