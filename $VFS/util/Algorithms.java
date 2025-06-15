package util;


/**
 * A class containing some fundamental algorithms
 * @version 0.2, Dec 1997
 * @author Pasquale Foggia
 */
public class Algorithms
  { 
    /**
     * binary search in a double[] array.
     * @return the index of the last element which is <= value
     * @param  vec   the vector to search
     * @param  first index of the first element of the vector
     * @param  last index of the last element of the vector
     * @param  value the value to be looked for
     */
    public static int binSearch(double vec[], int first, int last,
                                double value)
      { int i=first, j=last;

        while (i<j)
          { int m=(i+j)/2;

            if (vec[m]<=value)
              { i=m+1;
              }
            else
              { j=m;
              }
          }
        
        if (vec[i]>value)
          i--;

        return i;
      }

  }
