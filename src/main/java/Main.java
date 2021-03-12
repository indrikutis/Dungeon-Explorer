import gui.GUI;
import gui.GameScreen;
import gui.Renderer;
import gui.menu.LocationInformation;
import gui.menu.MainMenu;
import gui.menu.TutorialMenu;
import logic.*;
import logic.entities.Player;
import logic.entities.PlayerEntity;
import org.jsfml.graphics.*;
import org.jsfml.window.event.Event;
import resources.BackgroundMusic;
import resources.SoundManager;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {

        //System.load(System.getProperty("user.dir")+"/libfixXInitThreads.so");


        SaveGame save = new SaveGame();

        // Initialises window
        GameLogic.startGame();
        RenderWindow window = new GameScreen().getWindow();

        MainMenu menu = new MainMenu(window);
        boolean newGame = menu.getNewGame();
        boolean loadPreviousSave = menu.getLoadGame();

        // Changes back to the default view when not in the main menu
        window.setView(window.getDefaultView());

        // Creates a new game when player selects new game via menu, change to true for testing
        if (newGame) {
            GUI gui = GameScreen.getGui();
            Player player = PlayerEntity.initPlayer(gui.getArmList());
            GameLogic.setCurrentLevel(0);
            LocationInformation informationAboutTier = new LocationInformation(window);
            Renderer renderer = new Renderer();
            BackgroundMusic backgroundMusic = BackgroundMusic.getInstance();
            SoundManager soundManager = SoundManager.INSTANCE;

            soundManager.setSound(menu.getSound());

            TutorialMenu tutorialScreen = new TutorialMenu(window);
            RenderItems elements = new RenderItems();

            // Loads previous save if user opted for load recent game
            try {
                if (loadPreviousSave) {
                    save.loadFromFile();
                }
            } catch (Exception e) {
                System.out.println("User does not have a valid save file. Loaded new game.");
            }

            player.updateItemEquip();

            // Main loop
            while (window.isOpen()) {

                window.clear();

                if(menu.getSound()) {
                    backgroundMusic.playSound("Background/FREE MEDIEVAL BACKGROUND MUSIC (BURGLAR) [NO COPYRIGHT] [FREE DOWNLOAD]");
                }

                // Render level
                renderer.renderLevel(GameLogic.getCurrentPlace(), window);

                // Render level items
                elements.renderLevelItems();

                player.checkStatus();
                player.drawPt2();

                gui.draw(player);
                gui.getHealthBar().setHealth((int) player.getHealthPer());

                informationAboutTier.draw(GameLogic.getCurrentPlace().getLevelName());

                tutorialScreen.driver();
                window.display();

                // Handle events
                for (Event event : window.pollEvents()) {
                    if (event.type == Event.Type.CLOSED) {
                        // The user pressed the close button
                        window.close();
                    }
                }
            }
        }
    }
}