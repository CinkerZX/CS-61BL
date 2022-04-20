package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

public class Position implements Comparable<Position>{
    public int X;
    public int Y;

    public Position(int x, int y){
        this.X = x;
        this.Y = y;
    }

    public int compareTo(Position pos){
        if(this.X > pos.X){
            return 1;
        }
        return -1;
    }

    public static Position[][] initializing(Position[][] p) {
        int row = p[0].length;
        int col = p.length;
        Position position = new Position(0,0);
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                p[i][j] = position;
            }
        }
        return p;
    }
}