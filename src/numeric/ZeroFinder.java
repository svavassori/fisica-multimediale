package numeric;

/**
 * This class implements the simplified Dekker-Brent algorithm
 * for finding a zero of a real function.
 * The algorithm is a combination of regula falsi and bisection,
 * using the usually fast convergence of the former but reverting to the 
 * latter when the former does not converge or converges slowly.
 */
public class ZeroFinder
  { private static final double minDouble=100*Double.MIN_VALUE;
    private static final double epsDouble=1e-14;

    private int status; // one of the following values

    /**
     * Value of the status. All is fine.
     */
    public static final int OK=0;

    /**
     * Value of the status. One of the parameters was not correct.
     */
    public static final int BAD_PARAMETERS=-1;

    /**
     * Value of the status. The number of iterations has exceeded
     * the maximum allowed.
     */
    public static final int TOO_MANY_ITERATIONS=1;


    /**
     * Returns the status of the finder, which can be one of
     * OK, BAD_PARAMETERS, TOO_MANY_ITERATIONS
     */
    public int getStatus() 
      { return status; 
      }

    /**
     * Returns true if the status is OK
     */
    public boolean isOk()
      { return status==OK;
      }

    private RealFunction f;   // The function whose zeroes are sought
    private int maxiter;      // Max. number of iterations
    private double xtol;      // Relative tolerance on x (the pos. of the 0)
    private double ftol;      // Absolute tolerance on f(x)
    private double x;         // Position of the found zero
    private double fx;        // f(x)
    private double errbound;  // Bound on the error of the provided solution
    private double errest;  // Estimate of the error of the provided solution

    /**
     * Creates a new ZeroFinder.
     * @param f     the function whose zeroes are sought
     * @param ftol  absolute tolerance on the value of f at the zero
     *              The search is stopped at x if |f(x)| < ftol.
     *              ftol must be greater than 0.
     * @param maxiter   maximum number of iterations 
     */
    public ZeroFinder(RealFunction f, double ftol, int maxiter)
      { status=OK;
        this.f=f;
        this.maxiter=maxiter;
        this.ftol=ftol;

        if (maxiter<1 || ftol<=0)
          status=BAD_PARAMETERS;
        x=Double.NaN;
        fx=Double.NaN;
        errbound=-1;
        errest=-1;
      }

    public ZeroFinder(RealFunction f, double ftol)
      { this(f, ftol, 35);
      }

    public ZeroFinder(RealFunction f, int maxiter)
      { this(f, 1000*minDouble, maxiter);
      }

    public ZeroFinder(RealFunction f)
      { this(f, 1000*minDouble, 35);
      }


    /**
     * Get the position of the last found zero
     */
    public double getLastZeroPosition()
      { return x;
      }

    /**
     * Get the value of the last found zero
     */
    public double getLastZeroValue()
      { return fx;
      }

    /**
     * Get an upper bound on the error of the position of the last zero
     */
    public double getLastZeroErrorBound()
      { return errbound;
      }

    /**
     * Get an estimate of the error of the position of the last zero
     */
    public double getLastZeroErrorEstimate()
      { return errest;
      }

    /**
     * Finds a zero between a and b.
     * The function should have different sign at a and b.
     * a must be < b
     */
    public double find(double a, double b)
      { return find(a, b, 1e-8);
      }

    /**
     * Finds a zero between a and b.
     * The function should have different sign at a and b.
     * a must be < b
     * @param xtol the relative tolerance on the value of the position
     *             of the zero. The search is stopped at step k if
     *             |x[k]-x[k-1]| < xtol*|x[k]|. xtol must be greater than 0.
     */
    public double find(double a, double b, double xtol)
      { this.xtol=xtol;
        if (xtol<=0 || a>=b)
          status=BAD_PARAMETERS;


        // x1 and y1 delimit the interval which enclose the zero
        // x0 is the previous value of x1
        double x0=a;
        double y1=a;
        double x1=b;
        double eb=Math.abs(x1-y1); // eb is the error bound

        double f0=f.compute(x0);
        double f1=f.compute(x1);

        if (Functions.sign(f1)*Functions.sign(f0)>0)
          status=BAD_PARAMETERS;

        if (status==BAD_PARAMETERS)
          return Double.NaN;


        // Main Loop
        // yct counts the iterations for which y1 is not changed
        int yct=0; 
        // ebct counts the iterations for which eb changes too slowly 
        int ebct=0; 
        int iter=0;
        double dx=x1-x0;

        while ( iter<maxiter &&
                Math.abs(f1) > ftol &&
                Math.abs(dx) > xtol*Math.abs(x1) )
          { double p=f1*(x1-x0);
            
            if ( Math.abs(f1-f0) > minDouble*p && yct<2  && ebct<3)
              { // Use regula falsi
                dx=-p/(f1-f0);

                // Check if the new lies outside of the interval
                if ( Functions.sign(dx) != Functions.sign(y1-x1) ||
                     Math.abs(dx) >= Math.abs(x1-y1) )
                  { // Revert to bisection
                    dx= 0.5*(y1-x1);
                  }
              }
            else
              { // Use bisection.
                dx= 0.5*(y1-x1);
              }

            // Update x0 and x1
            x0=x1;
            x1+=dx;
            f0=f1;
            f1=f.compute(x1);

            // Update y1, if needed
            if (Functions.sign(f1)*Functions.sign(f0) < 0)
              { y1=x0;
                yct=0;
              }
            else
              { yct++;
              }

            // Check the new error bound
            double eb1=Math.abs(x1-y1);
            if (eb1<0.6*eb)
              { ebct=0;
              }
            else
              { ebct++;
              }
            eb=eb1;

            iter++;
          }

        
        if (iter<=maxiter)
          status=OK;
        else
          status=TOO_MANY_ITERATIONS;
        this.x=x1;
        this.fx=f1;
        this.errest=Math.abs(x1-x0);
        this.errbound=Math.abs(x1-y1);


        return x;
      }
  }
