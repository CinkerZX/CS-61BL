package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Door {
    public static Position doorPosition;

    public static void printDoor(){
        int randomX;
        int randomY;
        Position doorPos;

        do {
            randomX = Game.random.nextInt(Game.WIDTH);
            randomY = Game.random.nextInt(Game.HEIGHT);

            doorPos = new Position(randomX, randomY);
        }while(!isDoorPosOk(doorPos));

        doorPosition = doorPos;

        World.fillPosWithTexture(new Position(doorPos.X, doorPos.Y), Tileset.LOCKED_DOOR);
    }

    private static boolean isDoorPosOk(Position pos){



        TETile middle = World.world[pos.X][pos.Y];
            if(pos.Y + 1 < Game.HEIGHT - 1){

                TETile above = World.getPosTexture(new Position(pos.X, pos.Y+1));

                if(pos.Y - 1 > 1){
                    TETile below = World.getPosTexture(new Position(pos.X, pos.Y-1));

                    boolean isAtWall = (middle == World.wallTexture);
                    boolean isAboveFloor = (above == World.roomTexture);
                    boolean isBelowNothing = (below == Tileset.NOTHING);

                    return isAtWall && isAboveFloor && isBelowNothing;
                }
            }

        return false;
    }
}