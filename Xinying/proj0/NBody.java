public class NBody{


    public static double readRadius(String filename){
        In in = new In(filename);
        int N = in.readInt();
        double Radius = in.readDouble();
        return Radius;
    }

    public static Body[] readBodies(String filename){
        In in = new In(filename);
        int N = in.readInt();
        double Radius = in.readDouble();

        Body[] p = new Body[N];

        for(int i =0;i<N;i++){
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass = in.readDouble();
            String imgFileName = in.readString();
            p[i] = new Body(xxPos,yyPos,xxVel,yyVel,mass,imgFileName);
        }

        return p;
    }




    public static void main(String[] args) {
        //Collecting All Needed Input 
        NBody nb = new NBody();
        Double T = Double.parseDouble(args[0]);
        Double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        Double Radius = nb.readRadius(filename);
        Body[] bodies = nb.readBodies(filename);
        String imageToDraw = "D:/SU/CS61/cs61bl/proj0/images/starfield.jpg";
        
        //Drawing the background
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-Radius, Radius);

        int count = 0;
        
        while(count < 300){
            StdDraw.clear();
            StdDraw.picture(0,0,imageToDraw);
            for(Body i : bodies){
                i.draw();
                i.update(dt, i.calcNetForceExertedByX(bodies),i.calcNetForceExertedByY(bodies));
            }
            StdDraw.show();
            StdDraw.pause(20);
            count += 1;
        }

    }
}