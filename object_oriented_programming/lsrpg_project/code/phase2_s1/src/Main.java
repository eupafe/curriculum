
import business.Tools;
import presentation.UIController;
import presentation.views.UIManager;
import presentation.views.console.ConsoleUIManager;

public class Main {
    public static void main(String[] args) {

        UIManager uiManager = new ConsoleUIManager();
        Tools tools = new Tools();
        UIController controller = new UIController(uiManager, tools);
        controller.run();
    }

}