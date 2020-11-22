// The goal of this class is to simulate a universe specified in one of the data files

public class NBody{
  public static double readRadius(String r){
    In in = new In(r);
    /* Every time you call a read method from the In class, it reads the next thing from the file, assuming it is of the specified type. */
    /* Compare the calls below to the contents of BasicInDemo_input_file.txt */
    int firstItemInFile = in.readInt();
		return in.readDouble();
  }

  public static Body[] readBodies(String r){
    In in = new In(r);
    int n = in.readInt();
    Body[] array = new Body[n];
    double secondItemInFile = in.readDouble();
    for(int i = 0; i < n ; i+=1){
      double xp = in.readDouble();
      double yp = in.readDouble();
      double xv = in.readDouble();
      double yv = in.readDouble();
      double mass = in.readDouble();
      String img = in.readString();
      array[i] = new Body(xp, yp, xv, yv, mass, img);
    }
    return array;
  }

  publi void main(String args[]){
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);
    String filename = args[2];
    NBody nb = new NBody;
    double r = nb.readRadius(args[2]);
    Body[] bodies = nb.readBodies(args[2]);


    StdDraw.enableDoubleBuffering();

  		/** Sets up the universe so it goes from

  		  * -100, -100 up to 100, 100 */

		StdDraw.setScale(-r, r);
    StdDraw.clear();
		/* Stamps three copies of advice.png in a triangular pattern. */

		StdDraw.picture(-r, -r, "./images/starfield.jpg");
		/* Shows the drawing to the screen, and waits 2000 milliseconds. */

		StdDraw.show();

		StdDraw.pause(2000);

    
  }
}
