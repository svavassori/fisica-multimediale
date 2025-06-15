package numeric;

/**
 * Allows to obtain the solution of an Ordinary Differential Equations
 * system at given values of x, by linear interpolation of the
 * results provided by an ODESolver
 * @author   Pasquale Foggia
 * @version  0.99, Dec 1997
 * @see numeric.ODESolver
 */
public class ODEInterpolator
  { ODESolver solver;
    double x1, x2;
    double y1[], y2[];


    public static final int OK=0;

    /**
     * Value returned if it has been requested a value for
     * x that is before the one of the last step
     */
    public static final int BAD_X_VALUE=100;




    public ODEInterpolator(ODESolver solv)
      { solver=solv;
        x1=solver.getX();
        y1=new double[solver.getDimension()];
        solver.getY(y1);

        if (solver.isOk() &&
              solver.nextStep()==ODESolver.OK)
          { x2=solver.getX();
            y2=new double[solver.getDimension()];
            solver.getY(y2);
          }

      }

      
    /**
     * Compute the value of y at the specified x
     * @return  OK if all is fine, BAD_X_VALUE
     *          if the value for X is less than the one
     *          of the last step, or the status
     *          returned by the ODESolver
     */
    public int interpolate(double x, double y[])
      { if (x<x1)
          return BAD_X_VALUE;


        if (!solver.isOk())
          return solver.getStatus();


        while (x>x2)
          { 
            int status=solver.nextStep();
            if (status!=ODESolver.OK)
              return status;
            x1=x2;
            double tmp[]=y1;
            y1=y2;
            y2=tmp;
            x2=solver.getX();
            solver.getY(y2);
          }

        double c1=(x2-x)/(x2-x1);
        double c2=(x-x1)/(x2-x1);
        int i;
        for(i=0; i<y1.length; i++)
          y[i]=c1*y1[i]+c2*y2[i];

        return OK;
      }

  }
