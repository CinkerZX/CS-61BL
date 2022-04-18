package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Hallway {
//1.directky connected

    public static void drawHallways(){
        Collections.sort(Rooms.randomPositionInsideEachRoom);

        for(int i = 0; i < Rooms.randomPositionInsideEachRoom.size()-1; i++){
            Position currentPos = Rooms.randomPositionInsideEachRoom.get(i);
            Position nextPos = Rooms.randomPositionInsideEachRoom.get(i+1);
            connectTwoRooms(currentPos, nextPos);
        }
        closeHallways();
        clearHallway();
    }

    private static void connectTwoRooms(Position smallerX, Position biggerX){
        if(smallerX.X == biggerX.X){
            directlyConnect(smallerX, biggerX);
        }else{
            LLLShapeConnect(smallerX, biggerX);
        }
    }

    private static void LLLShapeConnect(Position pos1, Position pos2){
        Position midPoints = getMidPointOfTwoPos(pos1, pos2);
        if(pos1.Y > midPoints.Y){
            int height = pos1.Y - midPoints.Y;
            for(int y = midPoints.Y; y <= midPoints.Y + height; y++){
                if((World.isPosATexture(new Position(pos1.X, y), Tileset.NOTHING) || World.isPosATexture(new Position(pos1.X, y), World.wallTexture))){
                    World.fillPosWithTexture(new Position(pos1.X, y), World.hallwayTexture);
                }
            }
        }else{
            int height = midPoints.Y - pos1.Y;
            for(int y = midPoints.Y; y >= midPoints.Y - height; y-- ){
                if(World.isPosATexture(new Position(pos1.X, y), World.wallTexture) ||  World.isPosATexture(new Position(pos1.X, y), Tileset.NOTHING)){
                    World.fillPosWithTexture(new Position(pos1.X, y), World.hallwayTexture);
                }

            }
        }


        int width = pos2.X - midPoints.X;
        for(int x = midPoints.X; x <= midPoints.X + width; x++){
            if(World.isPosATexture(new Position(x, pos2.Y), World.wallTexture)||  World.isPosATexture(new Position(x, pos2.Y), Tileset.NOTHING)){
                World.fillPosWithTexture(new Position(x, pos2.Y), World.hallwayTexture);
            }
        }

    }



    private static void closeHallways(){
        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                if(World.isPosATexture(new Position(x,y), World.hallwayTexture)){
                    List<Position> aroundPos = new ArrayList<>();


                    for(int i = x-1; i <= x+1; i+=2 ){
                        aroundPos.add(new Position(i, y));
                    }
                    for(int k = y-1; k <= y+1; k+=2){
                        aroundPos.add(new Position(x, k));
                    }

                    for(int m = 0; m < aroundPos.size();m++){
                        Position eachPos = aroundPos.get(m);
                        if(eachPos != null && World.isPosATexture(new Position(eachPos.X, eachPos.Y), Tileset.NOTHING)){
                            World.fillPosWithTexture(new Position(eachPos.X, eachPos.Y), World.wallTexture);
                        }
                    }
                }
            }
        }
    }

    private static Position getMidPointOfTwoPos(Position pos1, Position po2){
        int midX = pos1.X;
        int midY = po2.Y;
        return new Position(midX, midY);
    }

    private static void directlyConnect(Position pos1, Position pos2){
        Position above = pos1;
        Position below = pos2;

        if(isOnePosAboveAnother(pos1, pos2)){
            above = pos2;
            below = pos1;
        }

        int height = above.Y - below.Y;
        for(int y = below.Y; y <= below.Y + height; y++ ){
            if(World.isPosATexture(new Position(above.X, y), World.wallTexture) || World.isPosATexture(new Position(above.X, y), Tileset.NOTHING)){
                World.fillPosWithTexture(new Position(above.X, y), World.hallwayTexture);
            }
        }
    }

    //Check if pos2.Y is greater than pos1.Y
    private static boolean isOnePosAboveAnother(Position pos1, Position pos2){
        return pos2.Y > pos1.Y;
    }

    private static void clearHallway(){
        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                if(World.isPosATexture(new Position(x,y), World.hallwayTexture)){
                    World.fillPosWithTexture(new Position(x,y), World.roomTexture);
                }
            }
        }
    }

}