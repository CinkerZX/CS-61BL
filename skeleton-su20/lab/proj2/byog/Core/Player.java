package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Player {
    public static Position playerPos;

    public static void printPlayer(){
        int randomX;
        int randomY;
        Position pos;

        do {
            randomX = Game.random.nextInt(Game.WIDTH);
            randomY = Game.random.nextInt(Game.HEIGHT);

            pos = new Position(randomX, randomY);
        }while(!isPlayerPosOk(pos));
        playerPos = pos;
        World.fillPosWithTexture(new Position(pos.X, pos.Y), Tileset.PLAYER);
    }

    private static boolean isPlayerPosOk(Position pos){
        double isPlayerFarFromDoor = World.distanceBetweenTwoPos(pos, Door.doorPosition);
        return World.isPosATexture(pos, World.roomTexture) && (isPlayerFarFromDoor > Game.WIDTH/3);
    }
}