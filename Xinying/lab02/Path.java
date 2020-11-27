/** A class that represents a path via pursuit curves. */
public class Path {

    // TODO
    public Point curr;
    public Point next;

    public Path(double x, double y){
        this.next = new Point(x,y);
    }

    public double getCurrX(){
        return curr.getX();
    }

    public double getCurrY(){
        return curr.getY();
    }

    public double getNextX(){
        return next.getX();
    }

    public double getNextY(){
        return next.getY();
    }

    public Point getCurrentPoint(){
        return curr;
    }

    public void setCurrentPoint(Point point){
        this.curr = point;
    }

    public void iterate(double dx, double dy){
        this.setCurrentPoint(next);
        next = new Point(this.getCurrX()+dx,this.getCurrY()+dy);
    }

    

}