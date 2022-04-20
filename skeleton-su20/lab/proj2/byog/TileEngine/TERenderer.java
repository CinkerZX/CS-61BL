package byog.TileEngine;

import byog.Core.Position;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the player or something similar.
 */
public class TERenderer<E> {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
        StdDraw.show();
    }

    /**
     * Adds a hexagon of side length s to a given position in the world
     * @param world size of the world
     * @param p the lower left corner of the hexagon: X>=s-1 ; Y>=0
     * @param s side length of the Hexagon
     *          if s = 3
     *            ***
     *           *****
     *          *******
     *          *******
     *           *****
     *            ***
     * @param t the tail
     */
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t){
        int smallestP_X = s-1;
        if (p.X >= smallestP_X) {
            try {
                for (int i = 0; i < 2*s; i++) { // i is the row
                    Position pOfRowLeft = addHexagonHelperPositionOfLeftTileRow(p, s, i);
                    int numOfRow = addHexagonHelperCountNumofKey(s, i);
                    addHexagonHelperAddTileRow(world, pOfRowLeft, numOfRow, t);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("The initial position has to be greater than " + smallestP_X);
            }
        }
    }

    /**
     * Draw multiple Hexagons
     * @param n the num of Hexagons
     * @param world
     * @param p the starting point of the first Hexagon to draw
     * @param s side length of the Hexagon
     */
    public static void addMultipleHexagon(TETile[][] world, int n, Position p, int s){
        for (int i = 0; i <n ; i++) {

        }
    }



    /**
     * Calculate the series of p for the n Hexagons
     * The drawing direction is from p0 to
     * @param world
//     * @param p0 the p of the first Hexagons
     * @param s side length of the Hexagon
     * @param n the number of Hexagons need to draw
     * @return
     */
//    public static Position[] addMultiHexagonHelperPositionOfP(TETile[][] world, int s, int n){
//
//    }

    /**
     * Calculate all the possible ps in the world
     * Staring from the left bottom of the world X = s-1+(s+s+2*(s-1))*i (i is odd) Y = s*2*j (j is odd)     ---i = 0,1,2 row j = 0,1,2 col
     * @param world
     * @param s
     * @return a position matrix
     */
    public static Position[][] addMultiHexagonHelperPossibleP(TETile[][] world, int s){
        int i = 0; // n row
        int j = 0; // n col
        int maxRow = world[0].length;
        int maxCol = world.length;
        int row = maxRow/(2*s);
        int a = maxCol/(4*s-2);
        int col = a+(maxCol-a*s)/(3*s-2);
        Position[][] matrixP = new Position[row][col];
        matrixP = Position.initializing(matrixP);
        Position temp = new Position(0,0);
        while(j < col){
            if (j%2 ==0){ // j is even
                temp.X = s-1+(2*s-1)*j; //s-1+(4*s-2)*j/2
                temp.Y = 0;
            }
            else{ // j is odd
                temp.X=3*s-2+(2*s-1)*(j-1);
                temp.Y = s;
            }
            i = 0;
            while(i < row && temp.Y+2*s <= maxRow && temp.X + 2*s <= maxCol){
                Position p = new Position(temp.X,temp.Y);
                matrixP[i][j] = p;
                temp.Y += 2*s;
                i++;
            }
            j++;
        }
        return matrixP;
    }


    /**
     * The position of the numOfRow th row
     * @param world
     * @param p the position of the left of the row
     * @param numOfRow the num of tiles that will be drawn
     * @param t the pointed tile
     */
    public static void addHexagonHelperAddTileRow(TETile[][] world, Position p, int numOfRow, TETile t){
        for (int i = 0; i < numOfRow; i++) {
            world[p.X+i][p.Y] = t;
        }
    }

    /**
     * Returen the position of the most left tile of r th row
     * @param p the lower left corner of the hexagon
     * @param s side length of the Hexagon
     * @param r the rth row
     * @return
     */
    public static Position addHexagonHelperPositionOfLeftTileRow(Position p, int s, int r){
        int X;
        int Y;
        Y = p.Y+r;
        if (r>=s){
            r = 2*s-1-r;
        }
        X = p.X - r;
        Position newP = new Position(X,Y);
        return newP;
    }

    /**
     * input argument is the side length of the Hexagon, and the order of the aimed row
     * return the num of keys of the aimed row
     * @param s side length of the Hexagon
     * @param r int: 0, 1, 2, ..., s-1, s, ..., 2*s-1
     */
    public static int addHexagonHelperCountNumofKey(int s, int r){
        if (r >= 0 && r <= 2*s-1) {
            try {
                int numOfKey;
                if (r>=s){
                    r = 2*s-1-r;
                }
                numOfKey = s+2*r;
                return numOfKey;
            } catch (IllegalArgumentException e) {
                System.out.println("The number of row need to be within [0,2*r-1]");
            }
        }
        return -1;
    }

    private static TETile[][] initializing(TETile[][] myTiles) {
        int row = myTiles[0].length;
        int col = myTiles.length;
        TETile myTile = Tileset.NOTHING;
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                myTiles[i][j] = myTile;
            }
        }
        return myTiles;
    }

    public static void main(String[] args) {
        // Test addHexagon
//        int WIDTH = 50;
//        int HEIGHT = 50;
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//
//        TETile[][]  myTiles = new TETile[WIDTH][HEIGHT];
//        for (int i = 0; i < WIDTH; i++) {
//            for (int j = 0; j < HEIGHT; j++) {
//                myTiles[i][j] = Tileset.NOTHING;
//            }
//        }
//        Position p = new Position(5,10);
//        addHexagon(myTiles, p, 3, Tileset.FLOWER);
//        ter.renderFrame(myTiles);

        //Test position matrix
        int WIDTH = 18; // col
        int HEIGHT = 16; // row
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][]  myTiles = new TETile[WIDTH][HEIGHT];
        myTiles = initializing(myTiles);
        Position[][] p = addMultiHexagonHelperPossibleP(myTiles, 2);
        int i = 0;
        int j;
        while(i<p.length){
            j = 0;
            while(j < p[0].length){
                if (p[i][j].X+p[i][j].Y !=0){
                    addHexagon(myTiles, p[i][j], 2, Tileset.FLOWER);
                }
                j++;
            }
            i++;
        }
        ter.renderFrame(myTiles);
    }



}
