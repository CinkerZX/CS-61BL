package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class World {
    public static TETile[][] world = new TETile[Game.WIDTH][Game.HEIGHT];
    public static List<Rooms> roomList = new ArrayList<>();
    public static final TETile roomTexture = Tileset.FLOOR;
    private static final int colorDifference = 64;
    public static final TETile wallTexture = TETile.colorVariant(Tileset.WALL, colorDifference, colorDifference, colorDifference, Game.random);
    public static final TETile hallwayTexture = Tileset.WATER;
    public static final int maxNumOfRooms = 50;
    public static final int minNumOfRooms = 25;

    private static void addRoom(){
        Rooms newRooom = new Rooms();
        roomList.add(newRooom);
    }

    public static void printRooms(){

        for(int i = 0; i < roomList.size(); i++){
            Rooms eachRoom = roomList.get(i);
            for(int x = eachRoom.pos.X; x < eachRoom.pos.X + eachRoom.width; x++){
                for(int y = eachRoom.pos.Y; y < eachRoom.pos.Y + eachRoom.height; y++){
                    boolean isXSidesEdge = (x == eachRoom.pos.X || x == eachRoom.pos.X + eachRoom.width-1);
                    boolean isYSidesEdge = (y == eachRoom.pos.Y || y == eachRoom.pos.Y + eachRoom.height-1);
                    Position roomPos = new Position(x,y);
                    if(isXSidesEdge || isYSidesEdge){
                        fillPosWithTexture(roomPos, wallTexture);
                    }else{
                        fillPosWithTexture(roomPos, roomTexture);
                    }
                }
            }
        }
    }

    public static void fillPosWithTexture(Position pos, TETile texture){
        world[pos.X][pos.Y] = texture;
    }

    public static TETile getPosTexture(Position pos){
        return world[pos.X][pos.Y];
    }

    public static boolean isPosATexture(Position pos, TETile texture){
        return getPosTexture(pos) == texture;
    }

    public static double distanceBetweenTwoPos(Position pos1, Position pos2){
        int distanceX =(int) Math.pow((pos1.X - pos2.X),2);
        int distanceY =(int) Math.pow((pos1.Y - pos2.Y),2);
        return Math.sqrt(distanceX+distanceY);
    }

    public static void printRoomsWithPos(Position pos, int width, int height){
            for(int x = pos.X; x < pos.X + width; x++){
                for(int y = pos.Y; y < pos.Y + height; y++){
                    fillPosWithTexture(new Position(x,y), Tileset.NOTHING);
                }
            }
    }

    public static void addRandomNumberOfRooms(){
        int maxNumOfRooms = Game.random.nextInt(World.maxNumOfRooms - World.minNumOfRooms) + World.minNumOfRooms;
        for(int i = 0; i < maxNumOfRooms; i++){
            addRoom();
        }
    }

    public static void initializeWorld(){
        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                //Choose the type of tiles to cover the window
                fillPosWithTexture(new Position(x,y), Tileset.NOTHING);
            }
        }
    }

    public static void printRandomPosInRoom(){
        for(int i = 0; i < Rooms.randomPositionInsideEachRoom.size(); i++){
            Position eachRoomPos = Rooms.randomPositionInsideEachRoom.get(i);
            int x = eachRoomPos.X;
            int y = eachRoomPos.Y;
            fillPosWithTexture(new Position(x,y), Tileset.SAND);
        }
    }



    public static void printHallways(){
        Hallway.drawHallways();
    }

    public static void printDoor(){
        Door.printDoor();
    }

    public static void printPlayer(){
        Player.printPlayer();
    }

    public static void main(String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);

        initializeWorld();
        addRandomNumberOfRooms();
        printRooms();
        printHallways();
        printDoor();
        printPlayer();

        ter.renderFrame(world);
    }
}