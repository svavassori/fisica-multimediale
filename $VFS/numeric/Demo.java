import numeric.ODE;
import numeric.ODESolver;

public class Demo implements ODE
  { double r=10.0;
    double v=1.0;
    double a=v*v/r;
    double omega=v/r;
    double t=0;
    double s[]={0, r, v, 0};

    public static void main(String argv[])
      { Demo demo=new Demo();
        double r=10.0;
        double v=1.0;
        double a=v*v/r;
        double omega=v/r;
        double t=0;
        double s[]={0, r, v, 0};
        ODESolver os=new ODESolver(demo, t, s, 0.5, 1e-5);

        
        while(os.nextStep()==ODESolver.OK && (t=os.getX())<4*Math.PI/omega)
          { double x=s[0];
            double y=s[1];
            double xx=r*Math.sin(omega*t);
            double yy=r*Math.cos(omega*t);
            double err=Math.sqrt((x-xx)*(x-xx)+(y-yy)*(y-yy));

            System.out.println("t="+t+"  err="+err);
          }
        System.out.println("status="+os.getStatus());
      }

    public void derive(double t, double s[], double s1[])
      { s1[0]=s[2];
        s1[1]=s[3];
        s1[2]=-a*s[0]/r;
        s1[3]=-a*s[1]/r;
      }

    public int getDimension()
      { return 4;
      }
  }

