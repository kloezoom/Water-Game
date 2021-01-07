package byow.Proj3A;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;
import java.util.Random;

/**
 * The world generator class.
 * Sources:
 * https://youtu.be/qs8vVgy5AMc
 * https://youtu.be/GtOt7EBNEwQ
 * @author chloehu
 */
public class WorldGenerator {

    /** Instance variable for width. */
    private final int width;
    /** Instance variable for height. */
    private final int height;
    /**Instance variable for random. */
    private Random random;
    /** Instance variable for world. */
    private TETile[][] world;

    /** Seed. */
    private long seed;

    /** Room array. */
    private ArrayList<Room> roomArray = new ArrayList<>();

    /** Min rooms created. */
    public static final int MINROOMS = 50;
    /** Max rooms created. */

    /** Global flower position. */
    private Position flowerPosition;

    /** The instance variable for rooms. */
    public static final int MAXROOMS = 75;
    /** World generator.
     * @param h h
     * @param w w*/
    public WorldGenerator(int w, int h) {
        this.width = w;
        this.height = h;
        this.world = new TETile[w][h];
    }

    /** Random.
     * @param r r */
    public void changeRandom(Random r) {
        this.random = r;
    }

    /**
     * Makes the background of the game. Default black background.
     */
    private void worldSpace() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Class for position. Borrowed from HexWorld.
     */
    private static class Position {
        /** Position x. */
        private final int x;
        /** Position y. */
        private final int y;


        /** This is Position.
         * @param xx x
         * @param yy y */
        Position(int xx, int yy) {
            this.x = xx;
            this.y = yy;
        }
    }

    /** Room class. */
    private static class Room {
        /** The lower left. */
        private final Position ll;
        /** The upper right. */
        private final Position ur;
        /** The center of the room created. */
        private final Position center;

        /** The Room.
         * @param lL lower left corner
         * @param uR upper right corner */
        Room(Position lL, Position uR) {
            this.ll = lL;
            this.ur = uR;
            this.center = new Position((int) Math.round((ll.x + ur.x) / 2),
                    (int) Math.round((ll.y + ur.y) / 2));
        }
    }

    /**
     * Makes a room by checking if the array item touching the outside and
     * if it is, then make it a tile, or else make it a floor. Takes in a
     * randomly generated seed.
     * Source: HKN tutor, Kimberly Zhu helped me go over a high level
     * implementation.
     * @param lowerLeft lower left
     * @param upperRight upper right
     */

    private void roomGenerator(Position lowerLeft, Position upperRight) {
        for (int x = lowerLeft.x; x < upperRight.x; x++) {
            for (int y = lowerLeft.y; y < upperRight.y; y++) {
                if (x == lowerLeft.x || x == upperRight.x - 1) {
                    world[x][y] = Tileset.WALL;
                } else if (y == lowerLeft.y || y == upperRight.y - 1) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * This helps randomly place rooms in grid.
     */
    private void roomDistributor() {
        int rooms = MINROOMS + random.nextInt(MAXROOMS);
        roomArray.add(new Room(new Position(1, 1), new Position(4, 4)));

        for (int i = 0; i < rooms; i++) {

            Position randomLowerLeft = new Position(random.nextInt(width - 3),
                    random.nextInt(height - 3));
            Position randomUpperRight = getRandomUpperRight(randomLowerLeft);

            if (!overlaps(randomLowerLeft, randomUpperRight)) {
                roomGenerator(randomLowerLeft, randomUpperRight);
                Room r = new Room(randomLowerLeft, randomUpperRight);
                roomArray.add(r);
            }
        }
    }

    /** Checks if two rooms overlap.
     * @param randomLowerLeft rll
     * @param randomUpperRight rur
     * @return boolean */

    private boolean overlaps(Position randomLowerLeft,
                             Position randomUpperRight) {
        for (Room r : roomArray) {
            if (r.ll.x <= randomLowerLeft.x && r.ur.x >= randomLowerLeft.x
                    || r.ll.x <= randomUpperRight.x && r.ur.x
                    >= randomUpperRight.x) {
                if (r.ll.y <= randomLowerLeft.y && r.ur.y >= randomLowerLeft.y
                        || r.ll.y <= randomUpperRight.y && r.ur.y
                        >= randomUpperRight.y) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Gets a random number for upper corner.
     * @param randomLowerLeft rll
     * @return position */
    private Position getRandomUpperRight(Position randomLowerLeft) {
        int offset = width - randomLowerLeft.x - 3;
        int takeoff = height - randomLowerLeft.y - 3;

        int boundX = Math.min(15, offset);
        int boundY = Math.min(15, takeoff);

        int x = random.nextInt(boundX) + randomLowerLeft.x + 3;
        int y = random.nextInt(boundY) + randomLowerLeft.y + 3;

        return new Position(x, y);
    }

    /** The hallway maker for horizontal.
     * @param pointOne start
     * @param pointTwo end */
    private void horizontalHallwayGenerator(Position pointOne,
                                            Position pointTwo) {
        Position start = pointOne, end = pointTwo;
        if (pointOne.x > pointTwo.x) {
            start = pointTwo;
            end = pointOne;
        }
        int y = start.y;
        for (int x = start.x; x <= end.x; x++) {
            world[x][y] = Tileset.FLOOR;
            if (world[x][y + 1] == Tileset.NOTHING) {
                world[x][y + 1] = Tileset.WALL;
            }
            if (world[x][y - 1] == Tileset.NOTHING) {
                world[x][y - 1] = Tileset.WALL;
            }
        }
    }

    /** The vertical hallway generator.
     * @param pointOne start
     * @param pointTwo end*/
    private void verticalHallwayGenerator(Position pointOne,
                                          Position pointTwo) {
        Position start = pointOne, end = pointTwo;
        if (pointOne.y > pointTwo.y) {
            start = pointTwo;
            end = pointOne;
        }
        int x = start.x;
        for (int y = start.y; y <= end.y; y++) {
            world[x][y] = Tileset.FLOOR;
            if (world[x + 1][y] == Tileset.NOTHING) {
                world[x + 1][y] = Tileset.WALL;
            }
            if (world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
        }
    }

    /** Distributes hallway. */
    private void hallwayDistributor() {
        for (int r = 2; r < roomArray.size(); r++) {
            Position cur = roomArray.get(r).center;
            Position prev = roomArray.get(r - 1).center;
            if (random.nextInt(2) == 1) {
                Position intersection = new Position(cur.x, prev.y);
                verticalHallwayGenerator(intersection, cur);
                horizontalHallwayGenerator(intersection, prev);
            } else {
                Position intersection = new Position(prev.x, cur.y);
                horizontalHallwayGenerator(intersection, cur);
                verticalHallwayGenerator(intersection, prev);
            }
        }
    }

    /** Distributes a flower depending on the first room's location.
     * Is randomly placed. */
    private void flowerDistributor() {
        Room flowerStartingRoom = roomArray.get(1);
        flowerPosition = flowerStartingRoom.center;
        assert world != null;
        world[flowerPosition.x][flowerPosition.y] = Tileset.FLOWER;
    }

    /** Changes the flower position based on WASD.
     * @param direction dir
     * @return int */
    public int changeFlowerPosition(Character direction) {
        switch (direction) {
            case 'w', 'W' -> {
                Position proj0 = new Position(flowerPosition.x,
                        flowerPosition.y + 1);
                if (spaceEmpty(proj0)) {
                    boolean hitsWater = hitsWater(proj0);
                    world[flowerPosition.x][flowerPosition.y] = Tileset.FLOOR;
                    world[flowerPosition.x][flowerPosition.y + 1] = Tileset.FLOWER;
                    flowerPosition = new Position(flowerPosition.x, flowerPosition.y + 1);
                    if (hitsWater) {
                        return 1;
                    }

                }
            }
            case 'a', 'A' -> {
                Position proj1 = new Position(flowerPosition.x - 1, flowerPosition.y);
                if (spaceEmpty(proj1)) {
                    boolean hitsWater = hitsWater(proj1);
                    world[flowerPosition.x][flowerPosition.y] = Tileset.FLOOR;
                    world[flowerPosition.x - 1][flowerPosition.y] = Tileset.FLOWER;
                    flowerPosition = new Position(flowerPosition.x - 1, flowerPosition.y);
                    if (hitsWater) {
                        return 1;
                    }
                }
            }
            case 's', 'S' -> {
                Position proj2 = new Position(flowerPosition.x, flowerPosition.y - 1);
                if (spaceEmpty(proj2)) {
                    boolean hitsWater = hitsWater(proj2);
                    world[flowerPosition.x][flowerPosition.y] = Tileset.FLOOR;
                    world[flowerPosition.x][flowerPosition.y - 1] = Tileset.FLOWER;
                    flowerPosition = new Position(flowerPosition.x, flowerPosition.y - 1);
                    if (hitsWater) {
                        return 1;
                    }
                }
            }
            case 'd', 'D' -> {
                Position proj3 = new Position(flowerPosition.x + 1, flowerPosition.y);
                if (spaceEmpty(proj3)) {
                    boolean hitsWater = hitsWater(proj3);
                    world[flowerPosition.x][flowerPosition.y] = Tileset.FLOOR;
                    world[flowerPosition.x + 1][flowerPosition.y] = Tileset.FLOWER;
                    flowerPosition = new Position(flowerPosition.x + 1, flowerPosition.y);
                    if (hitsWater) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /** Checks if space next to flower is empty.
     * @param projectedPosition proj
     * @return boolean */
    private boolean spaceEmpty(Position projectedPosition) {
        return world[projectedPosition.x][projectedPosition.y] != Tileset.WALL;
    }

    /** Checks if flower hits water.
     * @param projectedPosition proj
     * @return boolean */
    private boolean hitsWater(Position projectedPosition) {
        return world[projectedPosition.x][projectedPosition.y] == Tileset.WATER;
    }

    /** Distributes water in each room. */
    private void waterDistributor() {
        for (int i = 2; i < roomArray.size(); i++) {
            Room waterStartingRoom = roomArray.get(i);
            Position waterStartingPosition = waterStartingRoom.center;
            world[waterStartingPosition.x][waterStartingPosition.y]
                    = Tileset.WATER;
        }
    }

    /** checks where the mouse is located.
     * @return TETile */
    public TETile mouse() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        mouseX = Math.min(Math.max(0, mouseX), width - 1);
        mouseY = Math.min(Math.max(0, mouseY), height - 1);
        return world[mouseX][mouseY];
    }

    /** Fills up grid. */
    public void populate() {
        worldSpace();
        roomDistributor();
        hallwayDistributor();
        flowerDistributor();
        waterDistributor();
    }

    /** The world.
     * @return TETile*/
    public TETile[][] world() {
        return world;
    }

    /** The main method.
     * @param args args*/
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        WorldGenerator c = new WorldGenerator(MINROOMS, MINROOMS);
        ter.initialize(c.width, c.height);
        c.populate();
        c.changeRandom(new Random(MAXROOMS));
        ter.renderFrame(c.world);
        c.changeFlowerPosition('w');
    }
}
