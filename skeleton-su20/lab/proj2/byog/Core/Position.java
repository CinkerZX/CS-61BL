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
}