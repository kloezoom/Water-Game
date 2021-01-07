package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

//import byow.TileEngine.TERenderer;
import byow.TileEngine.*;
//import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t){
        Position rowPos;
        int width = 0;
        for (int y = 0; y < s * 2; y ++) {
            int rowX = p.x - xHelper(s, y);
            int rowY = p.y + y;
            rowPos = new Position(rowX, rowY);
            
            addRow(world, t, rowPos, width);
        }
    }
    
    private static int xHelper(int s, int y) {
        if (y < s) {
            return y;
        } else {
            return (s * 2) - 1 - y;
        }
    }
    
    private static int wHelper(int s, int y) {
        if (y < s) {
            return s + (2 * y);
        } else {
            return ((s * 2) - 1 - y) * 2 + s;
        }
    }

    public static void addRow(TETile[][] world, TETile t, Position rowPos, int width) {
        for (int x = 0; x < width; x++) {
            world[rowPos.x + x][rowPos.y] = t;
        }
    }
}
