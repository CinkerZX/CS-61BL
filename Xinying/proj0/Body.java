
public class Body{
    double xxPos;
    double yyPos;
    double xxVel;
    double yyVel;
    double mass;
    String imgFileName;

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

    public double calcDistance(Body p){
        return Math.sqrt((xxPos-p.xxPos)*(xxPos-p.xxPos) + (yyPos-p.yyPos)*(this.yyPos-p.yyPos));
    }

    public double calcForceExertedBy(Body p){
        double G = 6.67e-11;
        double r = this.calcDistance(p);
        return G*mass*p.mass/(r*r);
    }

    public double calcForceExertedByX(Body p){
        double f = this.calcForceExertedBy(p);
        double r = this.calcDistance(p);
        double dx = p.xxPos-xxPos;
        return f*dx/r;
    }

    public double calcForceExertedByY(Body p){
        double f = this.calcForceExertedBy(p);
        double r = this.calcDistance(p);
        double dy = p.yyPos-yyPos;
        return f*dy/r;
    }

    public double calcNetForceExertedByX(Body[] ps){
        double netxx = 0;
        for(Body s : ps){
            if(this.equals(s)){continue;}
            else{ 
                netxx = netxx + this.calcForceExertedByX(s); 
            }
        }
        return netxx;
    }

    public double calcNetForceExertedByY(Body[] ps){
        double netyy = 0;
        for(Body s : ps){
            if(this.equals(s)){continue;}
            else{ 
                netyy = netyy + this.calcForceExertedByY(s); 
            }
        }
        return netyy;
    }

    public void update(double dt,double fX,double fY){
        double ax = fX/this.mass;
        double ay = fY/this.mass;

        this.xxVel = this.xxVel + dt*ax;
        this.yyVel = this.yyVel + dt*ay;

        this.xxPos = this.xxPos + dt*this.xxVel;
        this.yyPos = this.yyPos + dt*this.yyVel;
    }

    public void draw(){

        StdDraw.enableDoubleBuffering();

        StdDraw.picture(this.xxPos,this.yyPos,"./images/"+this.imgFileName);

        StdDraw.show();
    }

    

}