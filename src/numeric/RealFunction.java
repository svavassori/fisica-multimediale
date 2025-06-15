package numeric;

/**
 * This interface represents a function from R to R. It is used
 * by ZeroFinder.
 */
public interface RealFunction
  { 
    /**
     * Compute the value of the function
     */
    double compute(double x);
  }
