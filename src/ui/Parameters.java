package ui;

import java.util.*;

/**
 * A class handling a set of parameters identified by
 * a string
 */
public abstract class Parameters
  { 

    public String getString(String name, String deflt)
      { String str=getParameter(name);
        return str==null? deflt: str;
      }

    public int getInt(String name, int deflt)
      { String str=getParameter(name);
        try 
          { if (str!=null)
              return Integer.parseInt(str);
          }
        catch (NumberFormatException e)
          {
          }
        return deflt;
      }

    public int getInt(String name, int deflt, int min, int max)
      { int x=getInt(name, deflt);
        if (x<min)
          x=min;
        else if (x>max)
          x=max;
        return x;
      }


    public long getLong(String name, long deflt)
      { String str=getParameter(name);
        try 
          { if (str!=null)
              return Long.parseLong(str);
          }
        catch (NumberFormatException e)
          {
          }
        return deflt;
      }

    public long getLong(String name, long deflt, long min, long max)
      { long x=getLong(name, deflt);
        if (x<min)
          x=min;
        else if (x>max)
          x=max;
        return x;
      }


    public double getDouble(String name, double deflt)
      { String str=getParameter(name);
        try 
          { if (str!=null)
              return Double.valueOf(str).doubleValue();
          }
        catch (NumberFormatException e)
          {
          }
        return deflt;
      }

    public double getDouble(String name, double deflt, double min, double max)
      { double x=getDouble(name, deflt);
        if (x<min)
          x=min;
        else if (x>max)
          x=max;
        return x;
      }

    public int getFromList(String name, String values[], int deflt)
      { String str=getParameter(name);

        if (str==null)
          return deflt;
        int i;
        for(i=0; i<values.length; i++)
          { if (values[i].equalsIgnoreCase(str))
              return i;
          }

        return deflt;
      }

    public boolean getBoolean(String name, boolean deflt)
      { String str=getParameter(name);

        if (str==null)
          return deflt;
        str=str.toLowerCase();
        if (str.equals("yes") || str.equals("y") || str.equals("true") ||
            str.equals("t") || str.equals("on") || str.equals("vero") ||

            str.equals("si"))
          return true;
        if (str.equals("no") || str.equals("n") || str.equals("false") ||
            str.equals("f") || str.equals("off") || str.equals("falso")||

            str.equals("no"))
          return false;

        return deflt;
      }


    public boolean[] getBooleanList(String name, String tags[])
      { String str=getParameter(name);
        boolean ans[]=new boolean[tags.length];
        int i;
        for(i=0; i<ans.length; i++)
          ans[i]=false;

        if (str==null)
          return ans;
        StringTokenizer tokenizer=new StringTokenizer(str, " \t,;:+/");
        while (tokenizer.hasMoreElements())
          { String tok=tokenizer.nextToken();
            for(i=0; i<tags.length; i++)
              { if (tags[i].equalsIgnoreCase(tok))
                 ans[i]=true;
              }
          }

        return ans;
      }


    protected abstract String getParameter(String name);
  }
