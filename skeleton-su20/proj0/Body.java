//Aim: simulating the motion of N objects in a plane, accounting for the gravitational forces mutually affecting each object as demonstrated by Sir Isaac Newton’s Law of Universal Gravitation. (https://cs61bl.org/su20/projects/nbody/)
public class Body{
  double xxPos; // The body’s current x position
  double yyPos; // The body’s current y position
  double xxVel; // The body’s current velocity in the x direction
  double yyVel; // The body’s current velocity in the y direction
  double mass; // The body’s mass
  String imgFileName; // The name of the file that corresponds to the image that depicts the body

  // Constructors
  public Body(double xP, double yP, double xV, double yV, double m, String img){
    xxPos = xP;
    yyPos = yP;
    xxVel = xV;
    yyVel = yV;
    mass = m;
    imgFileName = img;
  }

  public Body(Body p){
    xxPos = p.xxPos;
    yyPos = p.yyPos;
    xxVel = p.xxVel;
    yyVel = p.yyVel;
    mass = p.mass;
    imgFileName = p.imgFileName;
  }

  public double calcDistance(Body rocinante){
    double dx = (rocinante.xxPos-xxPos);
    double dy = (rocinante.yyPos-yyPos);
    return Math.sqrt((dx*dx+dy*dy));
  }

  public double calcForceExertedBy(Body rocinante){
    double r = this.calcDistance(rocinante);
    double G = 6.67e-11;
    return G*this.mass*rocinante.mass/(r*r);
  }

  public double calcForceExertedByX(Body rocinante){
    double dx = (rocinante.xxPos-xxPos);
    return this.calcForceExertedBy(rocinante)*dx/this.calcDistance(rocinante);
  }

  public double calcForceExertedByY(Body rocinante){
    double dy = (rocinante.yyPos-yyPos);
    return this.calcForceExertedBy(rocinante)*dy/this.calcDistance(rocinante);
  }

  public double calcNetForceExertedByX(Body[] allBodies){
    double fx = 0;
    for(Body i : allBodies){
      if (!i.equals(this)){
        fx = fx + this.calcForceExertedByX(i);
      }
    }
    return fx;
  }

  public double calcNetForceExertedByY(Body[] allBodies){
    double fy = 0;
    for(Body i : allBodies){
      if (!i.equals(this)){
        fy = fy + this.calcForceExertedByY(i);
      }
    }
    return fy;
  }

  public void update(double dt, double fx, double fy){
    this.xxVel = this.xxVel + dt*(fx/this.mass);
    this.yyVel = this.yyVel + dt*(fy/this.mass);
    this.xxPos = this.xxPos + dt*this.xxVel;
    this.yyPos = this.yyPos + dt*this.yyVel;
  }

  public void draw(){
    StdDraw.enableDoubleBuffering();
    /* Clears the screen. */
		StdDraw.clear();
		StdDraw.picture(this.xxPos, this.yyPos, imageToDraw);
		StdDraw.show();
  }
}
