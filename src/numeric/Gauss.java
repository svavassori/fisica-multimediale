package numeric;

/**
 * Diagonalize a matrix using the Gauss-Jordan algorithm
 * with row pivoting
 */
public class Gauss
  { // Error codes
    public static final int OK=0;
    public static final int SINGULAR_MATRIX=1;




    /**
     * Diagonalize the first columns of the matrix. If the
     * matrix is NxM, the number of diagonalizations is min(M, N)
     * @return a status code
     */
    public static int diagonalize(double matrix[][])
      { int n=matrix.length;
        int m=matrix[0].length;
        int d=Math.min(m,n);
        int el[]=new int[d];
        int i;
        for(i=0; i<d; i++)
          el[i]=i;
        return diagonalize(matrix, el);
      }

    /**
     * Diagonalize a matrix with respect to a set of columns
     * @param col  the columns with respect to which the matrix has to 
     *             be diagonalized.
     * @return a status code
     */
    public static int diagonalize(double matrix[][], int col[])
      { int d=col.length;
        int n=matrix.length;
        int m=matrix[0].length;

        int k;
        for(k=0; k<d; k++)
          { int c=col[k];

            // Find the pivot
            int ipiv=c;
            double apiv=Math.abs(matrix[ipiv][c]);
            int i;
            for(i=c+1; i<n; i++)
              if (Math.abs(matrix[i][c])>apiv)
                { ipiv=i;
                  apiv=Math.abs(matrix[i][c]);
                }

            // Exchange the row k with row ipiv
            double tmp[]=matrix[k];
            matrix[k]=matrix[ipiv];
            matrix[ipiv]=tmp;

            // now divide the k-th row by the pivot
            double piv=matrix[k][c];

            int j;

            for(j=0; j<m; j++)
              { if (apiv>Math.abs(matrix[k][j])*1e-15)
                  matrix[k][j]/=piv;
                else
                  return SINGULAR_MATRIX;
              }

            // now, reduce the c-th column
            for(i=0; i<n; i++)
              if (i!=k)
                { double fact=matrix[i][c];
                  for(j=0; j<m; j++)
                    { matrix[i][j]-=fact*matrix[k][j];
                    }
                  matrix[i][c]=0.0;
                }
          }
        
        return OK;
      }

  }
