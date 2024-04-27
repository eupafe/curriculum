package presentation.views;

import business.entities.Adventure;
import business.entities.Character;
import business.entities.Encounter;
import business.entities.Monster;

public interface UIManager {

    void showLoadingMessage();
    /**
     * Method that shows the program's main menu to the user, asking them to choose an option.
     *
     * @return an item in the {@link MainMenuOptions} enumeration representing the option chosen by the user
     */
    MainMenuOptions showMainMenu();


    void printErrorLoading(String name);

    int requestData();

    void showLoadingDataMessage();

    void showErrorServerMessage();
    /**
     * Method that promts the user to enter a characters name.
     *
     * @return a string representation of a characters name
     */
    String requestCharacterName();

    void printErrorName();

    void printWelcomeCharacter(String name);

    void printErrorLevel();

    void printLevel(int level);
    void printStatistics(int[] body, int[] mind, int[] spirit);

    void printCharacterCreated(String nameFormatted);
     void printExperience();

    String requestCharacterPlayer();


    int requestCharacterLevel();

    String requestFilterPlayer();

    void printPersistenceError();


    void printSomeMessage();

    void printAllMessage();

    void printErrorWrongOption();

    void printErrorCharacters();

    void listCharacters(Character[] characters);

    int requestCharacterNumber(int charactersLength);

    void showCharacter(Character character);

    String requestDeleteName();

    void printErrorUnique();

    void printNameToDelete(String name);

    void printDeleteCharacter(String name);

    void printErrorDeleteName();

    void printPlanningAdventure();

    String requestAdventureName();

    void printWelcomeAdventure(String name);

    int requestNumberEncounters();
    void printTavernEncounters(int number);

    EncounterMenuOptions showEncounterMenu();

    void printEncounter(int count, int numberEncounters, Encounter encounter);

    int listMonsters(Monster[] monsters);

    int requestIndexMonster(int max);

    void printErrorIndexMonster();

    int requestNumberMonsters(String name);

    void printErrorNumberEncounter();

    void printTooManyErrors();

    void printAdventureNotUnique();

    void printErrorBoss();

    int requestRemoveMonster();

    void printErrorRemoveMonster();

    void printErrorBossAdded();

    void printDeleteMonster(int amount, String name);

    void printErrorNotEnoughCharacters();

    void printStartAdventure();

    void listAdventures(Adventure[] adventures);

    int requestStartAdventure(int length);

    void printStartAdventureName(String adventureName);

    int requestNumberCharacters();

    void printNumCharacters(int numCharacters);

    void showCurrentParty(Character[] party, int numCharacters, int currNumCharacters);

    void listAvailableCharacters(Character[] availableCharacters);

    int requestPartyMember(int index);

    void printAdventureStartsSoon(String adventureName);

    void printThis(String string);

    void showCorrectLoadingMessage();

    String requestCharacterClass();

    void printNewClass(String classS);

    void printErrorInputMismatch();

    void printErrorAdventure();
}
