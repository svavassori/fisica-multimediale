package util;


/**
 * A class to format numbers and strings
 * with a specified width and precision, given a format expressed
 * as a string.
 * The string format has the form: [options][width][.prec]
 * where: <P>
 * <I>options</I> is a lists of the following chars: <P>
 * <UL>
 * <B>'-'</B>     left alignment<P>
 * <B>'^'</B>     center alignment<P>
 * <B>'+'</B>     insert sign for positive numbers too<P>
 * <B>SPACE</B> insert a leading space for positive numbers<P>
 * <B>'Z'</B>     pad with 0<P>
 * <B>'P'</B>     pad with +<P>
 * <B>'M'</B>     pad with -<P>
 * <B>'C'</B>     pad with ^<P>
 * <B>'D'</B>     pad with .<P>
 * <I>other</I> use that character for padding<P>
 * </UL>
 * <I>width</I> and <I>prec</I> are two integers.<P>
 * Example: "^Z8.3" means center alignment, pad with 0, width=8 and
 *          prec=3
 *
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class Format
  { static double log10=Math.log(10);

    public static String format(String fmt, Object obj)
      { return format(fmt, obj.toString());
      }

    public static String format(String fmt, String str)
      { FormatSpecifier fs=new FormatSpecifier(fmt);

        int l=str.length();

        if (fs.prec>0 && l==fs.prec)
          return str;
        if (fs.prec>0 && l>fs.prec)
          return str.substring(0, fs.prec);
        if (l>=fs.width)
          return str;
        int padlen=fs.width-l;
        int padleft=0, padright=0;
        if (fs.left)
          padright=padlen;
        else if (fs.center)
          { padleft=padlen/2;
            padright=padlen-padleft;
          }
        else
          padleft=padlen;

        int i;
        StringBuffer sb=new StringBuffer(padlen+l);
        for(i=0; i<padleft; i++)
          sb.append(fs.pad);
        sb.append(str);
        for(i=0; i<padright; i++)
          sb.append(fs.pad);

        return sb.toString();
      }


    /**
     * format as a fixed point number
     */
    public static String format(String fmt, double d)
      {
        FormatSpecifier fs=new FormatSpecifier(fmt);
        int ii=fmt.indexOf(fmt);

        if (ii<0)
          ii=fmt.length();


        String fmtNoPrec=fmt.substring(0, ii);
        if (Double.isNaN(d))
          return format(fmtNoPrec, "NaN");
        else if (Double.isInfinite(d) && Math.abs(d)!=1.0)
          return format(fmtNoPrec, (d>0)? "+Inf": "-Inf");

        if (Math.abs(d)>1e9)
          { String s1=(fs.width>0)? ""+fs.width+".": ".";
            String s2=(fs.prec>=0)? ""+fs.prec: "";
            return format_e(s1+s2, d);
          }

        double ad=(d>=0)? d: -d;
        String base;

        if (fs.prec>0)
          { 
            double  fr=ad-Math.floor(ad);
			//fd: old version ==> long frl=(long)(fr*Math.pow(10.0, fs.prec));
            long frl=(long)Math.round((fr*Math.pow(10.0, fs.prec)));
            

            String frstr1=String.valueOf(frl);
            int sforamento=0;
            long inl=0;

            if (frstr1.length()>fs.prec) {// da .99  passato a 100
                                               
            inl= (long) (Math.floor(ad)+1);
            frstr1=format("Z"+fs.prec,"0");
            base=""+inl+"."+frstr1;

            }
            else    
            {
            String frstr=format("Z"+fs.prec, frstr1);
            inl= (long) (Math.floor(ad));
            base=""+inl+"."+frstr;


            }
            

          }
        else if (fs.prec==0)
          { base=String.valueOf((long)ad);
          }
        else
          { base=String.valueOf(ad);
            if (base.indexOf('E')>=0)
              { String s1=(fs.width>0)? ""+fs.width+".": ".";
                String s2=(fs.prec>=0)? ""+fs.prec: "";
                return format_e(s1+s2, d);
              }
          }


        String sign="";

        if (d<0)
          sign="-";
        else if (fs.plus)
          sign="+";
        else if (fs.space)
          sign=" ";

        if (!fs.sign_before_pad)
          { base=sign+base;
            sign="";
          }
        else
          fs.width=fs.width-sign.length();

        int dot=base.indexOf('.');
        if (fs.width>0 && fs.prec<0 && dot>=0 && dot<fs.width 
             && fs.width<base.length())
          base=base.substring(0, fs.width);
        int bl=base.length();

        if (bl>=fs.width)
          return sign+base;
        int padlen=fs.width-bl;
        int padleft=0, padright=0;
        if (fs.left)
          padright=padlen;
        else if (fs.center)
          { padleft=padlen/2;
            padright=padlen-padleft;
          }
        else
          padleft=padlen;

        int i;
        StringBuffer sb=new StringBuffer(padlen+bl);
        sb.append(sign);
        for(i=0; i<padleft; i++)
          sb.append(fs.pad);
        sb.append(base);
        for(i=0; i<padright; i++)
          sb.append(fs.pad);

        return sb.toString();
      }


   /**
    * Removes not significant digits with respect to
    * a scale factor. The digits are considered not
    * significant if they are < 0.001 times the
    * scale factor.
    * Example: if the scale factor is 0.025 and
    * the number is 0.025001, it is converted to
    * the string 0.025. 
    */
   public static String remove(double number, double scale)

   { if (Double.isNaN(number) || Double.isNaN(scale))
         return "NaN";
       else if (Double.isInfinite(number) && Math.abs(number)!=1.0) 
                                                    // For a Netscape bug...
         return (number>0)? "+Inf": "-Inf";

       if (scale<0)
         scale=-scale;

       double abs=Math.abs(number);
       if (abs>1e6)
         return format_e(".3", number);
       if (abs<1e-6 && abs>0.5*scale)
         return format_e(".3", number);

       int dig=-(int)Math.floor(Math.log(scale)/log10);

       dig+=3;
       double n=number+((number>=0)? 0.49999: -0.49999)*Math.pow(10.0, -dig);
       String s=format("."+(dig>0? dig: 0), n);
       if (s.indexOf('E')>=0)
         { return format_e(".3", number);
         }

       int i=s.indexOf('.');
       if (i<0 && dig>0)
         return s;
       else if (dig>0)
         { i+=dig-1;
           if (i>=s.length())
             i=s.length()-1;
           while (s.charAt(i)=='0')
             i--;
           if (s.charAt(i)=='.')
             i--;
           return s.substring(0, i+1);
          }
       else 
          { if (i<0)
              i=s.length();
            i+=dig-1;
            if (i<=0)
              return "0";
            String ss=s.substring(0, i);
            while (i<s.length() && s.charAt(i)!='.')
              { ss=ss+"0";
                i++;
              }
            return ss;
          }
     }


    /**
     * Formats as a scientific notation number
     */
    public static String format_e(String fmt, double d)
      { if (Double.isNaN(d) || Double.isInfinite(d)) // For a Netscape bug...
          return format(fmt,d);

        FormatSpecifier fs=new FormatSpecifier(fmt);

        double ad=(d>=0)? d: -d;
        double dnorm;
        int    ex;

        if (ad>0)
          { ex=(int)Math.floor(Math.log(ad)/log10);
            dnorm=ad/Math.pow(10, ex);
            if (dnorm>=10.0)
              { dnorm/=10.0;
                ex++;
              }
            if (d<0)
              dnorm=-dnorm;
          }
        else
          { dnorm=0;
            ex=0;
          }
        String exponent;
        if (ex>99 || ex<-99)
          exponent="E"+Format.format("+Z4.0", ex);
        else
          exponent="E"+Format.format("+Z3.0", ex);
        
        String fmt2= " ."+ ((fs.prec>=0)? ""+fs.prec: "");
        String base=Format.format(fmt2, dnorm)+exponent;
        if (fs.width>0)
          return Format.format(""+fs.width, base);
        else
          return base;
      }

          
  }


/**
 * This class is used to convert a format specifier, expressed
 * as a string, into a set of parameters
 */
class FormatSpecifier
  { int width=-1;
    int prec=-1;
    boolean left=false;    // Align to the left 
    boolean center=false;  // Center the string 
    boolean plus=false;    // '+' sign for positive numbers
    boolean space=false;   // ' ' sign for positive numbers
    boolean sign_before_pad=false; // put the sign before the pad
    char pad=' ';          // pad char


    /**
     * @param fmt  The string format. It has the form: [options][width][.prec]
     * where: <P>
     * <I>options</I> is a lists of the following chars: <P>
     * <UL>
     * -     left alignment<P>
     * ^     center alignment<P>
     * +     insert sign for positive numbers too<P>
     * SPACE insert a leading space for positive numbers<P>
     * Z     pad with 0
     * P     pad with +
     * M     pad with -
     * C     pad with ^
     * D     pad with .
     * other use that character for padding
     * </UL>
     * <I>width</I> and <I>prec<I> are two integers.<P>
     * Example: "^Z8.3" means center alignment, pad with 0, width=8 and
     *          prec=3
     */
    FormatSpecifier(String fmt)
      { int i, j;
        int fmtlen=fmt.length();

        for(i=0; i<fmtlen; i++)
          { char c=fmt.charAt(i);
            if (c=='-')
              left=true;
            else if (c=='^')
              center=true;
            else if (c=='+')
              plus=true;
            else if (c==' ')
              space=true;
            else if (c=='Z' || c=='z')
              pad='0';
            else if (c=='P' || c=='p')
              pad='+';
            else if (c=='M' || c=='m')
              pad='-';
            else if (c=='C' || c=='c')
              pad='^';
            else if (c=='D' || c=='d')
              pad='.';
            else if ((c>='0' && c<='9') || c=='.')
              break;
            else
              pad=c;
            
          }

        if (pad=='0')
          sign_before_pad=true;

        if (i==fmtlen)
          return;

        j=fmt.indexOf('.', i);
        if (j==-1)
          j=fmtlen;
        try
              { width=Integer.parseInt(fmt.substring(i,j));
              }
        catch (NumberFormatException e)
              { width=-1;
              }

        if (j<fmtlen)
          { try
              { prec=Integer.parseInt(fmt.substring(j+1));
              }
            catch (NumberFormatException e)
              { prec=-1;
              }
          }
      }

    public String toString()
      { return "Format: width="+width+" prec="+prec+" pad=<"+pad+">";
      }
  }
