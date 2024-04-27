package presentation.views.console;

import business.entities.Adventure;
import business.entities.Character;
import business.entities.Encounter;
import business.entities.Monster;
import presentation.views.EncounterMenuOptions;
import presentation.views.MainMenuOptions;
import presentation.views.UIManager;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleUIManager implements UIManager {

    // Scanner to interact with the user in the Console
    private final Scanner scanner;

    private static final String LOADING_FILES_MESSAGE = "\nWelcome to Simple LSRPG.";
    private static final String LOCAL_CLOUD_MESSAGE = "\nDo you want to use your local or cloud data?\n"+
            "\t1) Local data\n"+
            "\t2) Cloud data\n";


    private static final String LOADED_CORRECTLY_MESSAGE = "Data was successfully loaded";

    private static final String ENTER_OPTION = "-> Answer: ";

    private static final String MAIN_MENU_MESSAGE =
            "\n1) Character creation\n" +
                    "2) List characters\n" +
                    "3) Create an adventure\n" +
                    "4) Start an adventure\n" +
                    "5) Exit\n";

    private static final String ERROR_WRONG_OPTION = "\nError, the entered option is not a valid option.\n";

    private static final String EXIT_MESSAGE = "\nTavern keeper: “Are you leaving already? See you soon, adventurer.”";

    private static final String LOADING_DATA_MESSAGE = "\nLoading data...";
    private static final String ERROR_SERVER_MESSAGE = "Couldn’t connect to the remote server.\n" +
            "Reverting to local data.";
    private static final String CHARACTER_CREATION_MESSAGE = "\nTavern keeper: “Oh, so you are new to this land.”\n" +
            "“What’s your name?”\n";

    private static final String ERROR_ENTER_NAME = "\nError, the name you have entered contains invalid characters.\nRedirecting to main menu...";

    private static final String ERROR_UNIQUE_NAME = "\nError, that name is already taken.\nRedirecting to main menu...";

    private static final String ERROR_ENTER_EXPERIENCE = "\nError, please enter a valid number in the range [1-10].\n";


    private static final String ERROR_PERSISTENCE_MESSAGE = "\nError with persistence";

    private static final String INPUT_MISMATCH_MESSAGE = "\nError, value introduced is not a number";

    private static final String MEET_SOME_MESSAGE = "You watch as some adventurers get up from their chairs and approach you.\n";

    private static final String MEET_ALL_MESSAGE = "You watch as all adventurers get up from their chairs and approach you.\n";

    private static final String ERROR_CHARACTERS_MESSAGE = "Error, there are no characters created under that name.\n";
    private static final String ENTER_NAME_MESSAGE = "-> Enter your name: ";

    private static final String ENTER_PLAYER_MESSAGE = "“And now, if I may break the fourth wall, who is your Player?”\n"
            + "\n-> Enter the player’s name: ";

    private static final String EXPERIENCE_MESSAGE = "\nTavern keeper: “I see, I see...”\n" +
            "“Now, are you an experienced adventurer?”\n" ;
    private static final String ENTER_EXPERIENCE_MESSAGE = "-> Enter the character’s level [1..10]: ";

    private static final String ENTER_FILTER_MESSAGE = "Tavern keeper: “Lads! They want to see you!”\n" +
            "“Who piques your interest?”\n" + "\n-> Enter the name of the Player to filter: ";

    private static final String ENTER_DELETE_NAME = "[Enter name to delete, or press enter to cancel]";

    private static final String ADVENTURE_CREATION_MESSAGE = "\nTavern keeper: “Planning an adventure? Good luck with that!\n";

    private static final String ENTER_ADVENTURE_NAME_MESSAGE = "-> Name your adventure: ";

    private static final String ENTER_NUMBER_ENCOUNTERS_MESSAGE = "\n-> How many encounters do you want [1..4]: ";

    private static final String ERROR_NUMBER_ENCOUNTERS = "\nError, please enter a valid number [1-4]";

    private static final String ERROR_TOO_MANY = "\nYou have commited too many errors.\nRedirecting to main menu...";

    private static final String ERROR_ADVENTURE_UNIQUE = "\nError, the name you have entered is already taken\nRedirecting to main menu...";

    private static final String ERROR_MONSTER_BOSS = "\nError, monsters with Boss as challenge cannot be added more than once";

    private static final String REMOVE_MONSTER_MESSAGE = "-> Which monster do you want to delete: ";

    private static final String ERROR_REMOVE_MONSTER = "\nError. There's no monsters in this encounter";

    private static final String ERROR_INDEX_MONSTER = "\nError. Index entered does not correspond to any monster";

    private static final String ERROR_BOSS_ADDED = "\nError. You can only have one boss";

    private static final String ERROR_NOT_ENOUGH_CHARACTERS = "\nError. You can only start an adventure once three or more characters have been created";

    private static final String START_ADVENTURE_MESSAGE = "\nTavern keeper: “So, you are looking to go on an adventure?”\n" +
            "“Where do you fancy going?”";

    private static final String ENTER_ADVENTURE_NUMBER = "-> Choose an adventure: ";

    private static final String ENTER_NUMBER_CHARACTERS = "-> Choose a number of characters [3..5]: ";

    private static final String ERROR_ADVENTURE_MESSAGE = "\nError. There are no adventures stored in the system.";

    private static final String ENTER_ENCOUNTER_OPTION = "-> Enter an option [1..3]: ";

    private static final String ENCOUNTER_MENU_MESSAGE =
            "\n1. Add monster\n"+
                    "2. Remove monster\n" +
                    "3. Continue\n";
    /**
     * Default constructor (without parameters) that initializes the manager
     */
    public ConsoleUIManager() {
        scanner = new Scanner(System.in);
    }


    @Override
    public void showLoadingMessage(){
        System.out.println(LOADING_FILES_MESSAGE);
    }

    public void showCorrectLoadingMessage(){
        System.out.println(LOADED_CORRECTLY_MESSAGE);
    }

    @Override
    public void printErrorLoading(String name){
        System.out.println("\nError: The "+name+" file can’t be accessed.");
    }
    /**
     * Method that shows the program's main menu to the user, asking them to choose an option.
     *
     * @return an item in the {@link MainMenuOptions} enumeration representing the option chosen by the user
     */
    @Override
    public MainMenuOptions showMainMenu() {
        do {
            System.out.println(MAIN_MENU_MESSAGE);
            System.out.print(ENTER_OPTION);

            try {
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        System.out.println(CHARACTER_CREATION_MESSAGE);
                        return MainMenuOptions.CHARACTER_CREATION;
                    case 2:
                        return MainMenuOptions.LIST_CHARACTERS;
                    case 3:
                        return MainMenuOptions.CREATE_ADVENTURE;
                    case 4:
                        return MainMenuOptions.START_ADVENTURE;
                    case 5:
                        System.out.print(EXIT_MESSAGE);
                        return MainMenuOptions.EXIT;
                    default:
                        throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println(ERROR_WRONG_OPTION);
            }
        } while (true);
    }

    @Override
    public int requestData() {
        System.out.print(LOCAL_CLOUD_MESSAGE);
        System.out.print(ENTER_OPTION);
        int num = Integer.parseInt(scanner.nextLine());
        while (num != 1 && num != 2) {
            System.out.println(ERROR_WRONG_OPTION);
            System.out.print(ENTER_OPTION);
            num = Integer.parseInt(scanner.nextLine());
        }
        return num;
    }

    @Override
    public void showLoadingDataMessage(){
        System.out.println(LOADING_DATA_MESSAGE);
    }

    @Override
    public void showErrorServerMessage(){
        System.out.println(ERROR_SERVER_MESSAGE);
    }
    /**
     * Method that prompts the user to enter a characters name.
     *
     * @return a string representation of a characters name
     */
    @Override
    public String requestCharacterName() {
        System.out.print(ENTER_NAME_MESSAGE);
        return scanner.nextLine();
    }

    @Override
    public void printErrorName() {
        System.out.println(ERROR_ENTER_NAME);
    }

    @Override
    public void printWelcomeCharacter(String name) {
        System.out.println("\nTavern keeper: “Hello, "+ name + ", be welcome.\"");
    }
    @Override
    public void printErrorUnique() { System.out.println(ERROR_UNIQUE_NAME); }


    @Override
    public void printErrorLevel(){
        System.out.println(ERROR_ENTER_EXPERIENCE);
    };

    @Override
    public void printLevel(int level){
        System.out.println("\nTavern keeper: “Oh, so you are level " + level + "!\n”" +
                "Great, let me get a closer look at you...”\n");
    }

    @Override
    public void printStatistics(int[] body, int[] mind, int[] spirit){
        System.out.println("Generating your stats...\n");
        System.out.println("Body:\tYou rolled " + (body[1] + body[2]) + " (" + body[1] + " and " + body[2] + ").");
        System.out.println("Mind:\tYou rolled " + (mind[1] + mind[2]) + " (" + mind[1] + " and " + mind[2] + ").");
        System.out.println("Spirit:\tYou rolled " + (spirit[1] + spirit[2]) + " (" + spirit[1] + " and " + spirit[2] + ").");

        System.out.println("\nYour stats are:");
        System.out.println(" - Body: "+body[0]);
        System.out.println(" - Mind: "+mind[0]);
        System.out.println(" - Spirit: "+spirit[0]);
    }

    @Override
    public void printCharacterCreated(String nameFormatted){
        System.out.println("\nThe new character "+ nameFormatted + " has been created.\n");
    }

    @Override
    public void printExperience(){
        System.out.println(EXPERIENCE_MESSAGE);
    }
    @Override
    public String requestCharacterPlayer(){
        System.out.print(ENTER_PLAYER_MESSAGE);
        return scanner.nextLine();
    }

    @Override
    public int requestCharacterLevel(){
        System.out.print(ENTER_EXPERIENCE_MESSAGE);
        int level = scanner.nextInt();
        scanner.nextLine();
        return level;
    }

    @Override
    public String requestFilterPlayer(){
        System.out.print(ENTER_FILTER_MESSAGE);
        return scanner.nextLine();
    }

    @Override
    public void printPersistenceError(){
        System.out.println(ERROR_PERSISTENCE_MESSAGE);
    }

    public void printSomeMessage() {
        System.out.println(MEET_SOME_MESSAGE);
    }

    public void printAllMessage() {
        System.out.println(MEET_ALL_MESSAGE);
    }
    @Override
    public void printErrorWrongOption() {
        System.out.println(ERROR_WRONG_OPTION);
    }

    @Override
    public void printErrorCharacters(){
        System.out.println(ERROR_CHARACTERS_MESSAGE);
    }
    @Override
    public void listCharacters(Character[] characters) {
        for (int i = 0; i < characters.length; i++) {
            System.out.println("\t" + (i + 1) + ". " + characters[i].getName());
        }
        int num = characters.length;
        System.out.print("\n\t0. Back\n\nWho would you like to meet [0.." + num + "]: ");
    }

    @Override
    public int requestCharacterNumber(int charactersLength){

        int num = Integer.parseInt(scanner.nextLine());
        if ((num<1 || num>charactersLength) && num!=0){
            printErrorWrongOption();
            num = -1;
        }

        return num;
    }

    @Override
    public void showCharacter(Character character) {
        System.out.println("\n* Name:\t    " + character.getName());
        System.out.println("* Player:\t" + character.getPlayer());
        System.out.println("* Class:\t" + character.getClassGiven());
        System.out.println("* Level:\t" + (Integer.min(10, (character.getXp() +100)/100 )));
        System.out.println("* XP:\t\t" + character.getXp());
        System.out.println("* Body:\t\t" + character.getBody());
        System.out.println("* Mind:\t\t" + character.getMind());
        System.out.println("* Spirit:\t" + character.getSpirit() + "\n");

    }

    @Override
    public String requestDeleteName() {
        return scanner.nextLine();
    }

    @Override
    public void printNameToDelete(String name) {
        System.out.println(ENTER_DELETE_NAME);
        System.out.print("Do you want to delete " + name + "? ");
    }

    @Override
    public void printDeleteCharacter(String name){
        System.out.println("\nTavern keeper: “I’m sorry kiddo, but you have to leave.”\n" +
                "Character "+name+" left the Guild.");
    }

    @Override
    public void printErrorDeleteName(){
        System.out.println("\nError. Wrong name entered");
    }

    @Override
    public void printPlanningAdventure(){
        System.out.println(ADVENTURE_CREATION_MESSAGE);
    }

    @Override
    public String requestAdventureName(){
        System.out.print(ENTER_ADVENTURE_NAME_MESSAGE);
        return scanner.nextLine();
    }

    @Override
    public void printWelcomeAdventure(String name){
        System.out.println("\nTavern keeper: “You plan to undertake "+ name+ ", really?”\n" + "“How long will that take?”");
    }

    @Override
    public int requestNumberEncounters(){
        int number = 0;
        System.out.print(ENTER_NUMBER_ENCOUNTERS_MESSAGE);
        String numberS = scanner.nextLine();
        if (numberS == "") {
            number = -1;
        }
        else {
            number = Integer.parseInt(numberS);
            if (number > 4 || number < 1){
                number = -1;
            }
        }

        return number;
    }

    @Override
    public void printTavernEncounters(int number){
        System.out.println("\nTavern keeper: “"+ number + " encounters? That is too much for me...”");
    }

    public EncounterMenuOptions showEncounterMenu() {
        do {
            System.out.println(ENCOUNTER_MENU_MESSAGE);
            System.out.print(ENTER_ENCOUNTER_OPTION);

            try {
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        return EncounterMenuOptions.ADD_MONSTER;
                    case 2:
                        return EncounterMenuOptions.REMOVE_MONSTER;
                    case 3:
                        return EncounterMenuOptions.CONTINUE;
                    default:
                        throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println(ERROR_WRONG_OPTION);
            }
        } while (true);
    }
    @Override
    public void printEncounter(int count, int numberEncounters, Encounter encounter){
        System.out.println("\n* Encounter "+count+" / "+ numberEncounters);
        System.out.println("* Monsters in encounter");
        for (int i = 0; i < encounter.getMonsters().size(); i++) {
            System.out.print("\t"+(i+1)+". "+encounter.getMonster(i).getName() );

            if (encounter.getAmountMonster(i) == 1){
                System.out.print(" (" + encounter.getMonster(i).getChallenge() +")\n" );
            }
            else{
                System.out.print(" (x" +  encounter.getAmountMonster(i) +")\n");
            }

        }
        if (encounter.getMonsters().size() == 0){
            System.out.println("\t# Empty");
        }
    }

    @Override
    public void printDeleteMonster(int amount, String name){
        System.out.println("\n"+amount+" "+name+" were removed from the encounter");
    }

    @Override
    public void printErrorBossAdded(){
        System.out.println(ERROR_BOSS_ADDED);
    }

    @Override
    public int listMonsters(Monster[] monsters) {
        if (monsters == null) {
            System.out.println("Error, there are no monsters created in the system.\n");
            return 1;
        }
        else {
            for (int i = 0; i < monsters.length; i++) {
                System.out.println("\t" + (i + 1) + ". " + monsters[i].getName() + " ("+ monsters[i].getChallenge() + ")");
            }
            //int num = monsters.length;
            return 0;
        }
    }

    @Override
    public int requestIndexMonster(int max){

        int number = 0;
        try {
            do {
                System.out.print("\n-> Choose a monster to add [1.."+ max + "]: ");
                number = scanner.nextInt();
                scanner.nextLine();
                if (number < 1 || number > max) {
                    printErrorIndexMonster();
                }
            } while (number < 1 || number > max);

        } catch (InputMismatchException e) {
            number = 17123;
            scanner.nextLine();
        }

        return number;
    }

    @Override
    public void printErrorIndexMonster(){

        System.out.println(ERROR_INDEX_MONSTER);
    }

    @Override
    public int requestNumberMonsters(String name){

        int number = 0;
        try {
            System.out.print("-> How many "+ name + "(s) do you want to add: ");
            number = scanner.nextInt();

        } catch (InputMismatchException e) {
            number = 17123;
        }
        scanner.nextLine();

        return number;
    }

    @Override
    public void printErrorNumberEncounter() {
        System.out.println(ERROR_NUMBER_ENCOUNTERS);
    }
    @Override
    public void printTooManyErrors() {
        System.out.println(ERROR_TOO_MANY);
    }

    @Override
    public void printAdventureNotUnique() {
        System.out.println(ERROR_ADVENTURE_UNIQUE);
    }

    @Override
    public void printErrorBoss(){
        System.out.println(ERROR_MONSTER_BOSS);
    }

    @Override
    public void printErrorRemoveMonster(){
        System.out.println(ERROR_REMOVE_MONSTER);
    }
    @Override
    public int requestRemoveMonster(){
        int number = 0;
        try {
            System.out.print(REMOVE_MONSTER_MESSAGE);
            number = scanner.nextInt();
        } catch (InputMismatchException e) {
           number = 17123;
        }
        scanner.nextLine();
        return number;
    }

    @Override
    public void printErrorInputMismatch(){
        System.out.println(INPUT_MISMATCH_MESSAGE);
    }

    @Override
    public void printErrorNotEnoughCharacters() {
        System.out.println(ERROR_NOT_ENOUGH_CHARACTERS);
    }

    @Override
    public void printStartAdventure() {
        System.out.println(START_ADVENTURE_MESSAGE);
    }

    @Override
    public void listAdventures(Adventure[] adventures) {
        if (adventures == null) {
            System.out.println("\nError. Please come back when an adventure has been created");
        }
        else {
            System.out.println("\nAvailable adventures:");
            for (int i = 0; i < adventures.length; i++) {
                System.out.println("\t" + (i + 1) + ". " + adventures[i].getName());
            }
            System.out.println();
        }
    }

    public int requestStartAdventure(int length) {
        System.out.print(ENTER_ADVENTURE_NUMBER);
        int adventure = scanner.nextInt();
        scanner.nextLine();

        while (adventure < 1 || adventure > length) {
            printErrorWrongOption();
            System.out.print(ENTER_ADVENTURE_NUMBER);
            adventure = scanner.nextInt();
            scanner.nextLine();
        }
        return adventure;
    }

    public void printErrorAdventure(){
        System.out.println(ERROR_ADVENTURE_MESSAGE);
    }
    @Override
    public void printStartAdventureName(String adventureName) {
        System.out.println("\nTavern keeper: " + adventureName + " it is!”\n" +
                "“And how many people shall join you?”\n");
    }

    @Override
    public int requestNumberCharacters() {

        int numCharacters = 0;
        try {
            System.out.print(ENTER_NUMBER_CHARACTERS);
            numCharacters = scanner.nextInt();

        } catch (InputMismatchException e) {
            numCharacters = 17123;
        }
        scanner.nextLine();

        return numCharacters;
    }

    @Override
    public void printNumCharacters(int numCharacters) {
        System.out.println("\nTavern keeper: “Great, " + numCharacters + " it is.”" +
"\n“Who among these lads shall join you?”");
    }

    @Override
    public void showCurrentParty(Character[] party, int numCharacters, int currNumCharacters) {
        System.out.println("\n------------------------------" +
                "\nYour party (" + currNumCharacters + "/" + numCharacters + "):");

        for (int i = 0; i < numCharacters; i++) {
            System.out.print("\t" + (i + 1) + ". ");
            if (party[i] == null) {
                System.out.println("Empty");
            }
            else {
                System.out.println(party[i].getName());
            }
        }
        System.out.println("------------------------------");
    }

    @Override
    public void listAvailableCharacters(Character[] availableCharacters) {
        System.out.println("Available characters:");
        for (int i = 0; i < availableCharacters.length; i++) {
                System.out.println("\t" + (i + 1) + ". " + availableCharacters[i].getName());
        }
        System.out.println();
    }

    @Override
    public int requestPartyMember(int index) {
        System.out.print("-> Choose character " + (index + 1) + " in your party: ");
        int partyMember = scanner.nextInt();
        scanner.nextLine();
        return partyMember;
    }

    @Override
    public void printAdventureStartsSoon(String adventureName) {
        System.out.println("\nTavern keeper: “Great, good luck on your adventure lads!”\n\n" +
                adventureName +" will start soon...");
    }

    @Override
    public void printThis(String string) {
        System.out.print(string);
    }

    @Override
    public String requestCharacterClass() {
        String classS;
        System.out.print("\nTavern keeper: “Looking good!”\n“And, lastly, ?”\n\n-> Enter the character’s initial class [Adventurer, Cleric, Wizard]: ");
        classS =  scanner.nextLine();
        while (!classS.equals("Adventurer") && !classS.equals("Cleric") && !classS.equals("Wizard")) {
            System.out.println("That initial class is not available.\n-> Please choose in between: [Adventurer, Cleric, Wizard]: ");
            classS = scanner.nextLine();
        }
        return classS;
    }

    @Override
    public void printNewClass(String classS) {
        System.out.println("\nTavern keeper: “Any decent party needs one of those.”\n" +
                "“I guess that means you’re a " + classS + " by now, nice!”");
    }
}


