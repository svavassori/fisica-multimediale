package ui;

/**
 * Implements a Parameter object that reads params from
 * a String vector. Each item of the vector should have 
 * the form 
 *    name=value
 * with no spaces. Case is considered not significant for the names.
 */
public class StringVectorParameters extends Parameters
  { String params[];

    public StringVectorParameters(String params[])
      { this.params=new String[params.length];
        int i;

        for(i=0; i<params.length; i++)
          this.params[i]=params[i];
      }

    protected String getParameter(String name)
      { int i;

        for(i=0; i<params.length; i++)
          { int idx=params[i].indexOf('=');
            if (idx<0)
              continue;
            String str=params[i].substring(0, idx);
            if (name.equalsIgnoreCase(str))
              return params[i].substring(idx+1);
          }
        return null;
      }


  }
