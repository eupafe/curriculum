package presentation;

import business.*;
import business.entities.Character;
import business.entities.Encounter;
import presentation.views.UIManager;

public class UIController {


    // Instance of any class implementing the BusinessFacade interface, to talk to business layer
    // Instance of any class implementing the UIManager interface, to talk to the rest of the presentation layer
    private final UIManager ui;

    private CharacterManager characterManager;

    private MonsterManager monsterManager;

    private AdventureManager adventureManager;

    private final Tools tools;

    private PlayAdventureManager playAdventureManager;


    /**
     * Constructor with parameters to create a user interface controller.
     *
     * @param ui an instance of any class implementing the UIManager interface, to talk to the rest of the presentation layer
     *
     */
    public UIController(UIManager ui, Tools tools) {
        this.ui = ui;
        this.tools = tools;
        this.characterManager = null;
        this.monsterManager = null;
        this.adventureManager = null;
        this.playAdventureManager = null;

    }

    /**
     * Main method of the controller (and by extention main method of the application's logic).
     *
     * <p>It will run infinitely, presenting the user with the application's menu and executing the chosen option.
     */
    public void run()  {
        int run = 0;

        ui.showLoadingMessage();
        int option = ui.requestData();
        ui.showLoadingDataMessage();
        monsterManager = new MonsterManager(option);
        boolean checkMonsterDao = monsterManager.checkMonsterDAO();
        characterManager = new CharacterManager(option);
        adventureManager = new AdventureManager(option);

        boolean checkDao = checkMonsterDao && characterManager.checkCharacterDAO() && adventureManager.checkAdventureDAO();
        if (option==2 && !checkDao){
            ui.showErrorServerMessage();
            ui.showLoadingDataMessage();
            monsterManager = new MonsterManager(1);
            characterManager = new CharacterManager(1);
            adventureManager = new AdventureManager(1);
        }
        if (option==1 && !checkMonsterDao){
            ui.printErrorLoading("monsters.json");
        }else {
            playAdventureManager = new PlayAdventureManager(this, characterManager);
            ui.showCorrectLoadingMessage();
            while (run != 1) {
                switch (ui.showMainMenu()) {
                    case CHARACTER_CREATION:
                        characterCreation();
                        break;

                    case LIST_CHARACTERS:
                        listCharacters();
                        break;

                    case CREATE_ADVENTURE:
                        createAdventure();
                        break;

                    case START_ADVENTURE:
                       startAdventure();
                       break;

                    case EXIT:
                        run = 1;
                        break;

                }
            }
        }



    }

    public int characterCreation(){
        int error = 1;
        String name = " ";
        String nameFormatted;
        String player;
        int level = 0;
        boolean ok;


        name = ui.requestCharacterName();
        error = characterManager.checkCharacterName(name);
        if (error == 1) {
            ui.printErrorName();
            return 0;
        }
        if (error == 2) {
            ui.printErrorUnique();
            return 0;
        }

        nameFormatted = tools.formatCharacterName(name);
        ui.printWelcomeCharacter(nameFormatted);

        player = ui.requestCharacterPlayer();
        ui.printExperience();

        do {
            level = ui.requestCharacterLevel();
            error = tools.checkCharacterLevel(level);
            if (error == 1) {
                ui.printErrorLevel();
            }
        } while (error == 1);

        ui.printLevel(level);
        int[] body = characterManager.generateStatistic();
        int[] mind = characterManager.generateStatistic();
        int[] spirit = characterManager.generateStatistic();

        ui.printStatistics(body, mind, spirit);

        String classS = ui.requestCharacterClass();
        if (!classS.equals(characterManager.canEvolve(classS, level))) {
            classS = characterManager.canEvolve(classS, level);
            ui.printNewClass(classS);
        }

        ok = characterManager.addNewCharacter(nameFormatted, player, level, body[0], mind[0], spirit[0], classS);

        if (ok) {
            ui.printCharacterCreated(nameFormatted);
        }
        else{
            ui.printPersistenceError();
            return 0;
        }
        error = 1;
        return 1;
    }
    public int listCharacters(){
        String player = ui.requestFilterPlayer();

        if (player == "") {
            ui.printAllMessage();
        } else {
            ui.printSomeMessage();
        }

        if (characterManager.getCharacters(player) == null){
            ui.printErrorCharacters();
        }
        else{
            ui.listCharacters(characterManager.getCharacters(player));
            int choice = ui.requestCharacterNumber(characterManager.getCharacters(player).length);
            if (choice == 0) {
                return 0;
            }
            if (choice != -1) {

                ui.showCharacter(characterManager.getCharacters(player)[choice - 1]);

                int wrongName;
                do {
                    wrongName = 1;
                    ui.printNameToDelete(characterManager.getCharacters(player)[choice - 1].getName());
                    String name = ui.requestDeleteName();
                    if (characterManager.getCharacters(player)[choice - 1].getName().equalsIgnoreCase(name)) {
                        characterManager.deleteCharacter(characterManager.getCharacters(player)[choice - 1]);
                        ui.printDeleteCharacter(name);
                        wrongName = 0;
                    } else {

                        if (name == "") {
                            wrongName = 0;
                        } else {
                            ui.printErrorDeleteName();
                        }
                    }
                } while (wrongName == 1);
            }
        }
        return 1;
    }

    public int createAdventure(){
        boolean errorNumEncounter = false;
        int numberEncounters = 0;
        int numErrors = 0;
        int index = -1;
        boolean boss = false;

        ui.printPlanningAdventure();
        String adventureName = ui.requestAdventureName();

        if (!tools.adventureNameOK(adventureName)) {
            ui.printErrorName();
            return 0;
        }
        if (adventureManager.checkUnique(adventureName) == -1){
            ui.printPersistenceError();
            return 0;
        }
        if (adventureManager.checkUnique(adventureName) == 0) {
            ui.printAdventureNotUnique();
            return 0;
        }

        ui.printWelcomeAdventure(adventureName);
        do {
            errorNumEncounter=false;
            numberEncounters = ui.requestNumberEncounters();

            if (numberEncounters == -1) {
                errorNumEncounter = true;
                ui.printErrorNumberEncounter();
                numErrors++;
            }
        } while (errorNumEncounter && numErrors != 3);

        if (errorNumEncounter) {
            ui.printTooManyErrors();
            return 0;
        }

        ui.printTavernEncounters(numberEncounters);

        if (!adventureManager.createNewAdventure(adventureName, numberEncounters)) {
            ui.printPersistenceError();
            return 0;
        }

        int count = 0;
        while (count != numberEncounters) {
            if ( adventureManager.getMonstersByAdventureAndEncounter(adventureName, count) == null){
                ui.printPersistenceError();
                break;
            }
            ui.printEncounter(count + 1, numberEncounters, adventureManager.getMonstersByAdventureAndEncounter(adventureName, count));


            switch (ui.showEncounterMenu()) {
                case ADD_MONSTER:

                    if (monsterManager.getAllMonsters() == null){
                        ui.printPersistenceError();
                        break;
                    }

                    ui.listMonsters(monsterManager.getAllMonsters());

                    index = ui.requestIndexMonster(monsterManager.getAllMonsters().length);
                    if (index == 17123){
                        ui.printErrorInputMismatch();
                        break;
                    }

                    int number = ui.requestNumberMonsters(monsterManager.getMonsterByIndex(index - 1).getName());
                    if (number == 17123){
                        ui.printErrorInputMismatch();
                        break;
                    }
                    if (monsterManager.getMonsterByIndex(index-1)== null){
                        ui.printPersistenceError();
                        break;
                    }
                    boolean isBoss = monsterManager.getMonsterByIndex(index - 1).getChallenge().equals("Boss");
                    if (number > 1 && isBoss) {
                        ui.printErrorBoss();
                    }
                    else {
                        if (!boss && isBoss) {
                            if ( !adventureManager.addMonster(adventureManager.getAdventureByName(adventureName), count, monsterManager.getMonsterByIndex(index - 1), number)){
                                ui.printPersistenceError();
                                break;
                            }

                            boss = true;
                        } else if (boss && isBoss) {
                            ui.printErrorBossAdded();
                        } else if (!isBoss) {
                            if ( !adventureManager.addMonster(adventureManager.getAdventureByName(adventureName), count, monsterManager.getMonsterByIndex(index - 1), number)){
                                ui.printPersistenceError();
                                break;
                            }

                        }

                    }
                    break;
                case REMOVE_MONSTER:

                    Encounter encounter = adventureManager.getMonstersByAdventureAndEncounter(adventureName, count);
                    if (encounter.getMonsters().size() != 0) {
                        int indexMonster = ui.requestRemoveMonster();
                        if (indexMonster==17123){
                            ui.printErrorInputMismatch();
                            break;
                        }
                        if (indexMonster > 0 && indexMonster <= encounter.getMonsters().size()) {
                            ui.printDeleteMonster(encounter.getAmountMonster(indexMonster - 1), encounter.getMonster(indexMonster - 1).getName());
                            if (!adventureManager.deleteMonster(adventureManager.getAdventureByName(adventureName), count, indexMonster - 1)){
                                ui.printPersistenceError();
                                break;
                            }
                        } else {
                            ui.printErrorIndexMonster();
                            break;
                        }

                    } else {
                        ui.printErrorRemoveMonster();
                    }
                    break;
                case CONTINUE:
                    count++;
                    break;
            }
        }
        return 1;
    }

    public int startAdventure(){
        if (characterManager.checkMinimumCharacters() == 0) {
            ui.printErrorNotEnoughCharacters();
            return 0;
        } else if (characterManager.checkMinimumCharacters() == -1) {
            ui.printPersistenceError();
            return 0;
        }

        ui.printStartAdventure();
        if (adventureManager.getAllAdventures() == null){
            ui.printPersistenceError();
            return 0;
        }
        if (adventureManager.getAllAdventures().length == 0){
            ui.printErrorAdventure();
            return 0;
        }
        ui.listAdventures(adventureManager.getAllAdventures());
        if (adventureManager.getAllAdventures() == null) {
            return 0;
        }

        int adventureNumber = ui.requestStartAdventure(adventureManager.getAllAdventures().length);


        String playAdventureName = adventureManager.getAdventureNameByID(adventureNumber - 1);
        ui.printStartAdventureName(playAdventureName);

        int numCharacters = ui.requestNumberCharacters();
        if (numCharacters == 17123){
            ui.printErrorInputMismatch();
            return 0;
        }
        while (!tools.checkNumberCharacters(numCharacters)) {
            ui.printErrorWrongOption();
            numCharacters = ui.requestNumberCharacters();
        }
        ui.printNumCharacters(numCharacters);

        Character[] party = new Character[numCharacters];

        int currNumCharacters = 0;
        int partyMember;
        Character[] availableCharacters = characterManager.getAllCharacters();
        if (availableCharacters == null){
            ui.printPersistenceError();
            return 0;
        }

        while (currNumCharacters != numCharacters) {
            ui.showCurrentParty(party, numCharacters, currNumCharacters);
            ui.listAvailableCharacters(availableCharacters);
            partyMember = ui.requestPartyMember(currNumCharacters);
            while (!tools.checkPartyMember(partyMember, availableCharacters, party)) {
                ui.printErrorWrongOption();
                partyMember = ui.requestPartyMember(currNumCharacters);
            }
            party[currNumCharacters] = availableCharacters[partyMember - 1];
            currNumCharacters++;
        }
        ui.showCurrentParty(party, numCharacters, currNumCharacters);
        ui.printAdventureStartsSoon(playAdventureName);

        playAdventureManager.play(party, adventureManager.getAdventureByName(playAdventureName));
        return 1;
    }

    public void printThis(String string) {
        ui.printThis(string);
    }


}
