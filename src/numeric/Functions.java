package numeric;

/**
 * This class exports some useful functions
 */
public class Functions
  { public static final double log10=Math.log(10);

    public static double hypot(double x, double y)
      { return Math.sqrt(x*x+y*y);
      }

    public static double hypot2(double x, double y)
      { return x*x+y*y;
      }

    public static double sqr(double x)
      { return x*x;
      }

    /**
     * Chooses a "round" increment value close to one specified by
     * the user
     */
    public static double chooseIncrement(double base)
      { 
        int exp=(int)Math.floor(Math.log(base)/log10);
        double p10=Math.pow(10.0, exp);
        double ratio=base/p10;
        if (ratio>=4.9)
          return 5*p10; 
        else if (ratio>2.49)
          return 2.5*p10;
        else if (ratio>1.9)
          return 2*p10;
        else
          return p10; 
      }


    public static int sign(double v)
      { if (v<0)
          return -1;
        else if (v>0)
          return 1;
        else
          return 0;
      }

    /**
     * Reduces an angle to [-PI,PI[
     */
    public static double reduceAngle(double ang)
      { while (ang>=Math.PI)
          ang-=2*Math.PI;
        while (ang<-Math.PI)
          ang+=2*Math.PI;
        return ang;
      }
  }
