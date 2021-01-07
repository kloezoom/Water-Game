
package byow.Core;
import byow.TileEngine.TERenderer;
import byow.Proj3A.WorldGenerator;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import static java.lang.System.exit;

/**
 * This is the Engine for running to Main method.
 *
 * @author chloehu
 */
public class Engine {
    /** Instance variable for ter. */
    private static final TERenderer TER = new TERenderer();
    /** Instance variable for width. */
    private static final int WIDTH = 80;
    /** Instance variable for height. */
    private static final int HEIGHT = 30;
    /** Instance variable for Menu width. */
    private static final int MENUWIDTH = 400;
    /** Instance variable for Menu height. */
    private static final int MENUHEIGHT = 400;
    /** Instance variable for font size. */
    public static final int FONTSIZE = 20;
    /** Instance variable for number. */
    public static final int MENUNUM = 30;
    /** Instance variable for string builder. */
    private final StringBuilder stringBuilder = new StringBuilder();
    /** Instance variable for page. */
    private String page = "menu";
    /** Instance variable for boolean. */
    private boolean gameOn = true;
    /** World generator. */
    private WorldGenerator w;

    /**
     * Method used for exploring a fresh world. This method should
     * handle all inputs, including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        while (gameOn) {
            switch (page) {
                case "menu" -> {
                    initializeScreen();
                    drawMenu();
                }
                case "seed" -> drawEnterSeedPage();
                case "game" -> {
                    populateWorld();
                    enterGame();
                }
            }
        }
        System.exit(0);
    }


    /**
     * Private method to make screen.
     */
    private void initializeScreen() {
        StdDraw.setCanvasSize(MENUWIDTH, MENUHEIGHT);
        StdDraw.setXscale(0, MENUWIDTH);
        StdDraw.setYscale(0, MENUHEIGHT);
        StdDraw.clear(Color.BLACK);
    }

    /**
     * Private method to display seed instructions.
     */
    private void drawEnterSeedPage() {
        StdDraw.clear(Color.BLACK);
        Font title = new Font("Arial", Font.BOLD, FONTSIZE);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(title);
        StdDraw.text((float) MENUWIDTH / 2, (float) MENUHEIGHT / 2,
                "Enter a Seed");
        StdDraw.text((float) MENUWIDTH / 2, ((float) MENUHEIGHT / 2) - MENUNUM,
                "press 's' to finish");
        StdDraw.show();

        StringBuilder s = new StringBuilder();
        page = "seed";
        while (page.equals("seed")) {
            if (StdDraw.hasNextKeyTyped()) {
                char keyTyped = StdDraw.nextKeyTyped();
                if (Character.isLetterOrDigit(keyTyped)) {
                    String se = s.toString().matches("") ? "3" : s.toString();
                    s.append(processSeedPage(keyTyped, Long.valueOf(se)));
                    StdDraw.setPenColor(Color.BLACK);
                    StdDraw.filledRectangle((float) MENUWIDTH / 2, MENUNUM,
                            (float) MENUWIDTH / 2, (float) MENUNUM / 2);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text((float) MENUWIDTH / 2, MENUNUM, s.toString());
                }
            }
        }
    }

    /**
     * Private method to draw menu.
     */
    private void drawMenu() {
        Font title = new Font("Arial", Font.BOLD, FONTSIZE);
        Font contents = new Font("Arial", Font.PLAIN, FONTSIZE);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(title);
        int spacing = FONTSIZE;
        int middleX = MENUWIDTH / 2;
        int middleY = MENUHEIGHT / 2;
        StdDraw.text(middleX, middleY, "Water Getter");
        StdDraw.setFont(contents);
        StdDraw.text(middleX, middleY - spacing, "New Game (N)");
        StdDraw.text(middleX, middleY - 2 * spacing, "Load Game (L)");
        StdDraw.text(middleX, middleY - 3 * spacing, "Quit (Q)");
        StdDraw.show();

        page = "menu";
        while (page.equals("menu")) {
            if (StdDraw.hasNextKeyTyped()) {
                char keyTyped = StdDraw.nextKeyTyped();
                if (Character.isLetter(keyTyped)) {
                    processMenuPage(keyTyped);
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string
     * will be a series of characters (for example, "n123sswwdasdassadwas",
     * "n123sss:q", "lwww". The engine should behave exactly as if the user
     * typed these characters into the engine using interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite
     * save. For example, if we do interactWithInputString("n123sss:q"),
     * we expect the game to run the first 7 commands (n123sss) and then
     * quit and save. If we then do interactWithInputString("l"), we should
     * be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     * // to do: Fill out this method so that it run the engine using the input
     * // passed in as an argument, and return a 2D tile representation of the
     * // world that would have been drawn if the same inputs had been given
     * // to interactWithKeyboard().
     *
     * @param input the input string to feed to your program
     */
    public TETile[][] interactWithInputString(String input) {
        if (input.equals("")) {
            exit(0);
        }

        String str = input.toLowerCase();
        stringBuilder.append(str);
        String seed = str.replaceAll("\\D+", "");
        if (seed.isEmpty()) {
            load();
        }
        newGame();
        w.changeRandom(new Random(Long.parseLong(seed)));
        populateWorld();
        String commands = str.replaceAll("n[0-9]+s", "");
        for (char c : commands.toCharArray()) {
            w.changeFlowerPosition(c);
        }
        return w.world();
    }

    /**
     * This is the method that makes the rooms and hallways appear.
     *
     * @return TeTile[][]
     */
    private TETile[][] populateWorld() {
        w.populate();
        return w.world();
    }

    /**
     * This method differentiates the difference between every keyword
     * entered into the keyboard.
     *
     * @param character Char
     * @param seed seed
     * @return String
     */
    private String processSeedPage(char character, Long seed) {
        switch (character) {
            case ('s'):
            case ('S'):
                w.changeRandom(new Random(seed));
                stringBuilder.append(character);
                page = "game";
                break;
            default:
                if (Character.isDigit(character)) {
                    stringBuilder.append(character);
                    return Character.toString(character);
                }
        }
        return "";
    }

    /** Menu processor.
     * @param character char */
    private void processMenuPage(char character) {
        switch (character) {
            case 'n':
            case 'N':
                newGame();
                stringBuilder.append(character);
                page = "seed";
                break;
            case 'l':
            case 'L':
                load();
                break;
            case 'q':
            case 'Q':
                quit();
                break;
            default:
                break;
        }
    }

    /**
     * This is the the game generator.
     */
    private void newGame() {
        w = new WorldGenerator(WIDTH, HEIGHT);
    }

    /** Enters the game page when called and excecutes accordingly. */
    private void enterGame() {
        int hud = 1;
        TER.initialize(WIDTH, HEIGHT + hud);
        TER.renderFrame(w.world());
        int points = 0;
        boolean willQuit = false;
        updateHUD(points, hud);
        page = "game";
        while (page.equals("game")) {
            if (StdDraw.hasNextKeyTyped()) {
                char keyTyped = StdDraw.nextKeyTyped();
                if (willQuit && (keyTyped == 'q' || keyTyped == 'Q')) {
                    saveAndQuit();
                    return;
                }
                if (keyTyped == 'w' || keyTyped == 'W' || keyTyped == 'd'
                        || keyTyped == 'D' || keyTyped == 's' || keyTyped == 'S'
                        || keyTyped == 'a' || keyTyped == 'A') {
                    points += w.changeFlowerPosition(keyTyped);
                    stringBuilder.append(keyTyped);
                    TER.renderFrame(w.world());
                }

                willQuit = keyTyped == ':';
            }
            updateHUD(points, hud);
        }
    }

    /** Updates the heading when called.
     * @param hud hud
     * @param s int*/
    private void updateHUD(int s, int hud) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle((float) WIDTH / 2, HEIGHT - (float) (hud / 2),
                (float) WIDTH / 2, (float) hud / 2);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(3, HEIGHT - (float) (hud / 2), "points: " + s);
        TETile mouseHovering = w.mouse();
        StdDraw.text(13, HEIGHT - (float) (hud / 2),
                mouseHovering.description());
        StdDraw.show();
    }

    /** Saves and quit before closing. */
    private void saveAndQuit() {
        try {
            File worldFile = new File("saved.txt");
            if (!worldFile.exists()) {
                worldFile.createNewFile();
            }
            worldFile.setWritable(true);
            worldFile.setReadable(true);
            FileOutputStream stream = new FileOutputStream(worldFile);
            String result = stringBuilder.toString();
            stream.write(result.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        quit();
    }

    /** Loads the previous game upon call. */
    private void load() {
        try {
            File worldFile = new File("saved.txt");
            FileReader fr = new FileReader(worldFile);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder s = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                s.append(line);
                line = br.readLine();
            }

            System.out.println(s.toString());
            interactWithInputString(s.toString());
            enterGame();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Quits game upon call. */
    private void quit() {
        gameOn = false;
        page = "";
    }

    /**
     * This is the main method for testing.
     *
     * @param args args
     */
    public static void main(String[] args) {
        Engine e = new Engine();
        e.interactWithKeyboard();
    }
}
