package byog.Core;



import java.util.*;

public class Rooms {
    public int width;
    public int height;
    public Position pos;
    public List<Position> fourConorPositions = new ArrayList<>();

    public static List<Position> randomPositionInsideEachRoom = new ArrayList<>();

    public static final int maxRoomSide = 12;
    public static final int minRoomSide = 5;

    public Rooms(){
        do{
            //randomly decide the position of the room(this is its bottom leftmost tile)
            int posX = Game.random.nextInt(Game.WIDTH);
            int posY = Game.random.nextInt(Game.HEIGHT);

            this.pos = new Position(posX, posY);

            //randomly decide the weight/height of the room
            int offset = maxRoomSide - minRoomSide;
            this.width = Game.random.nextInt(offset ) + minRoomSide;
            this.height = Game.random.nextInt(offset ) + minRoomSide;
        }while(!isRoomOk(this.pos, this.width, this.height));

        randomPositionInsideEachRoom.add(getRandomPositionInRoom(this));
    }

    public Rooms(Position pos, int width, int height){
        this.pos = pos;
        this.width = width;
        this.height = height;

        randomPositionInsideEachRoom.add(getRandomPositionInRoom(this));
    }

    private static void fillFourConorPositions(Rooms room){
        getToLeftBottomPos(room);
        getToLeftTopPos(room);
        getToRightBottomPos(room);
        getToRightTopPos(room);
    }



    private static Position getRandomPositionInRoom(Rooms room){
            int randomX;
            int randomY;

            boolean isXinsideRoom;
            boolean isYinsideRoom;

            do{
                randomX = room.pos.X + Game.random.nextInt(room.width);
                randomY = room.pos.Y + Game.random.nextInt(room.height);

                isXinsideRoom = (randomX > room.pos.X ) && (randomX < (room.pos.X + room.width-1));
                isYinsideRoom = (randomY > room.pos.Y ) && (randomY < (room.pos.Y + room.height-1));
            }while(!(isXinsideRoom && isYinsideRoom));

        return new Position(randomX, randomY);
    }

    public static boolean isRoomOk(Position pos, int width, int height){
        boolean notExceedingHorizontalEdges = (pos.X + width -1) < Game.WIDTH;
        boolean notExceedingVerticalEdges = (pos.Y + height - 1) < Game.HEIGHT;

        return notExceedingHorizontalEdges && notExceedingVerticalEdges && isRoomOverlap(pos, width, height);
    }


    private static boolean isRoomOverlap(Position pos, int width, int height){
        Position leftBottom = pos;
        Position leftTop = new Position(pos.X, pos.Y + height - 1);
        Position rightBottom = new Position(pos.X + width - 1, pos.Y);
        Position rightTop = new Position(pos.X + width - 1, pos.Y + height - 1);

        List<Position> positionList = new ArrayList<>();
        positionList.add(leftBottom);
        positionList.add(leftTop);
        positionList.add(rightBottom);
        positionList.add(rightTop);

        for(int i = 0; i < World.roomList.size(); i++){
            Rooms eachRoom = World.roomList.get(i);
            for(int k = 0; k < positionList.size(); k++){
                Position currentPos = positionList.get(k);
                if(isInRoom(eachRoom, currentPos)){
                    return false;
                }
            }

        }
        return true;
    }

    private static boolean isInRoom(Rooms room, Position pos){
        for(int x = room.pos.X; x < room.pos.X + room.width; x++){
            for(int y = room.pos.Y; y < room.pos.Y + room.height; y++){//iterate through the area of the rooms
                boolean condition1 = (pos.X == x && pos.Y == y);
                boolean condition2 = (pos.X + 1 == x || pos.X - 1 == x) && pos.Y == y;
                boolean condition3 = (pos.Y + 1 == y || pos.Y - 1 == y) && pos.X == x;
//                if(condition1 ){
//                    return true;
//                }else if(pos.X + 1 == x && (pos.Y == y)){
//                    return true;
//                }else if(pos.X - 1 == x && (pos.Y == y)){
//                    return true;
//                }else if(pos.Y + 1 == y && pos.X == x){
//                    return true;
//                }else if(pos.Y - 1 == y && pos.X == x){
//                    return true;
//                }

                if(condition1){
                    return true;
                }
                else if(condition2){
                    return true;
                }
                else if(condition3){
                    return true;
                }

//                else if((pos.X == x) && (pos.Y +1 == y || pos.Y - 1 == y)){
//                    return true;
//                }
            }
        }

        return false;
    }

    private static void getToLeftBottomPos(Rooms room){
        room.fourConorPositions.add(new Position(room.pos.X, room.pos.Y));
    }

    private static void getToLeftTopPos(Rooms room){
        int posX = room.pos.X;
        int posY = room.pos.Y + room.height - 1;
        room.fourConorPositions.add(new Position(posX, posY));
    }

    private static void getToRightBottomPos(Rooms room){
        int posX = room.pos.X + room.width - 1;
        int posY = room.pos.Y;
        room.fourConorPositions.add(new Position(posX, posY));
    }

    private static void getToRightTopPos(Rooms room){
        int posX = room.pos.X + room.width - 1;
        int posY = room.pos.Y + room.height - 1;
        room.fourConorPositions.add(new Position(posX, posY));
    }

}