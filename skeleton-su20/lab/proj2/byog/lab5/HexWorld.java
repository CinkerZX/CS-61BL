package byog.lab5;

import edu.princeton.cs.algs4.ST;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;
import byog.Core.RandomUtils;

import javax.naming.ldap.Rdn;
import javax.swing.text.Position;

public class HexWorld {
    public static class Position{
        public int x;
        public int y;

        public Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;

    private static final long SEED = 2723;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Adds a hexagon to the world.
     * @param world the world to draw on
     * @param pos the bottom left coordinate of the hexagon
     * @param size the size of the hexagon
     * @param type the tile to draw
     */
    public static void addHexagon(TETile[][] world, Position pos, int size, TETile type){


            //A hexagon have 2*size rows, the code below iterates from bottom to top
            for(int i = 0; i < size*2; i++){
                //choose the Y coordinate of the row
                int rowY = pos.y + i;
                //choose the X coordinate of the row
                int rowX = pos.x + hexRowOffset(size,i);
                //choose the position at which the row starts
                Position rowStartPos = new Position(rowX, rowY);
                //choose the row width
                int rowWidth = hexRowWidth(size,i);
                //add the rows
                addRow(world, rowStartPos, rowWidth, type);
            }


    }

    public static int hexRowOffset(int size, int level){
        int offset = level;
        if(level >= size){
            offset = (2 * size) - offset - 1;
        }

        return -offset;
    }

    public static int hexRowWidth(int size, int i){
        int width = i;
        if(i >= size){
            width = (2*size) - width - 1;
        }

        return size + (2*width);
    }

    /** Adds a row of the same tile.
     * @param world the world to draw on
     * @param pos the leftmost position of the row
     * @param width the number of tiles wide to draw
     * @param type the tile to draw
     */
    public static void addRow(TETile[][] world, Position pos, int width, TETile type){
        for(int j = 0; j < width; j++){
            int xCoordinate = pos.x + j;
            int yCoordinate = pos.y;
            Random r = new Random();
            world[xCoordinate][yCoordinate] = TETile.colorVariant(type,32,32,32,new Random());
        }
    }

    public  static void drawRandomVerticalHexes( TETile[][] world, Position pos, int size, int number){
//        Position pos2 = new Position(pos.x, pos.y + size*2);
//        Position pos3 = new Position(pos.x, pos.y+size*4);
//        addHexagon(world, pos, size, Tileset.GRASS);
//        addHexagon(world, pos2, size, Tileset.FLOWER);
//        addHexagon(world, pos3, size, Tileset.WALL);

        for(int i = 0; i < number; i++){
            int newYPos = pos.y + (2 * size * i);
            Position rowPos = new Position(pos.x, newYPos);
            TETile newTile = randomTile();
            addHexagon(world, rowPos, size, newTile);
        }

    }

    public static void drawRandomRowsOfHexesOfSizeN(TETile[][] world, Position pos, int size, int maxRowNumber, int startRowNumber){
        if(maxRowNumber < 1 || startRowNumber < 1){
            throw new IllegalArgumentException("Cannot be less than 1!");
        }else if(size < 2){
            throw new IllegalArgumentException("Hexagon must be at least size 2");
        }else if(startRowNumber > maxRowNumber ){
            throw new IllegalArgumentException("Start Row Number cannot be larger than Max Row Number!");
        }

        for(int i = startRowNumber; i <= maxRowNumber; i++){
            drawRandomVerticalHexes(world, pos, size, i);
            pos = getNeighborBottomLeftPosFromAbove(pos,size);
        }

        pos = getNeighborBottomRightPosFromBelow(pos,size);
        pos = getNeighborBottomLeftPosFromBelow(pos,size);

        for(int j = maxRowNumber - 1; j >= startRowNumber; j--){
            drawRandomVerticalHexes(world, pos, size, j);
            pos = getNeighborBottomLeftPosFromBelow(pos,size);
        }
    }

    public static Position getNeighborBottomLeftPosFromAbove(Position pos, int size){
        int newXPos = (pos.x + size * 2) - 1;
        int newYPos = (pos.y - size);

        return new Position(newXPos, newYPos);
    }

    public static Position getNeighborBottomLeftPosFromBelow(Position pos, int size){
        int newXPos = (pos.x + size * 2) - 1;
        int newYPos = (pos.y + size);

        return new Position(newXPos, newYPos);
    }

    public static Position getNeighborBottomRightPosFromBelow(Position pos, int size){
        int newPosX = (pos.x - size * 2)+1;
        int newPosY = (pos.y + size);

        return new Position(newPosX, newPosY);
    }

    public static TETile randomTile(){
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.MOUNTAIN;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            default: return Tileset.SAND;
        }
    }

    public static void main(String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Position pos = new Position(20,100);
        TETile t = Tileset.GRASS;

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int size = 4;
        int number = 1;
        int maxRowNumber = 8;

        drawRandomRowsOfHexesOfSizeN(world, pos, size, maxRowNumber, number);


        ter.renderFrame(world);

    }

//    @Test
//    public void testHexRowWidth() {
//        assertEquals(3, hexRowWidth(3, 5));
//        assertEquals(5, hexRowWidth(3, 4));
//        assertEquals(7, hexRowWidth(3, 3));
//        assertEquals(7, hexRowWidth(3, 2));
//        assertEquals(5, hexRowWidth(3, 1));
//        assertEquals(3, hexRowWidth(3, 0));
//        assertEquals(2, hexRowWidth(2, 0));
//        assertEquals(4, hexRowWidth(2, 1));
//        assertEquals(4, hexRowWidth(2, 2));
//        assertEquals(2, hexRowWidth(2, 3));
//    }
//
//    @Test
//    public void testHexRowOffset() {
//        assertEquals(0, hexRowOffset(3, 5));
//        assertEquals(-1, hexRowOffset(3, 4));
//        assertEquals(-2, hexRowOffset(3, 3));
//        assertEquals(-2, hexRowOffset(3, 2));
//        assertEquals(-1, hexRowOffset(3, 1));
//        assertEquals(0, hexRowOffset(3, 0));
//        assertEquals(0, hexRowOffset(2, 0));
//        assertEquals(-1, hexRowOffset(2, 1));
//        assertEquals(-1, hexRowOffset(2, 2));
//        assertEquals(0, hexRowOffset(2, 3));
//    }
}