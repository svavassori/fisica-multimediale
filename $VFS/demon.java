import java.awt.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import util.Preloader;
import ui.Parameters;
import ui.StringVectorParameters;
import ui.WindowsTracker;
import ui.TrackedFrame;


class Demon /*extends Frame*/ {
	File fil_fis = null ;
    FileInputStream fis = null ;
    DataInputStream dis = null;
    final int numobjs=18;

    String classlist[] ={"",
        "cinemat.Cinemat", 
        "cinemat.Cinemat", 
        "cinemat.Cinemat",
        "fismod.FisMod",
        "fismod.FisMod",
        "fismod.FisMod",
        "circuiti.Circuiti",
        "dinam1.Dinam1",
        "dinam2.Dinam2",
        "elemag.EleMag",
        "gas.Gas",
        "onde.Onde",
        "oscill.Oscill",
        "ottica.Ottica",
        "relat.Relat",
        "cinemat.Cinemat",
        "cinemat.Cinemat",
          };
    
    String paramlist[][] ={ {""},
        {"moto_base=rotatorio","Forced=3"},
        {"moto_base=traslatorio","vel_base=2","acc_base=0","Forced=1"},
        {"moto_base=traslatorio","vel_base=3","acc_base=5","Forced=2"},
        {"tipo=bohr"},
        {"tipo=spettro"},
        {"tipo=compton"},
        {""},
        {""},
        {""},
        {""},
        {""},
        {""},
        {""},
        {""},
        {""},
        {"moto_base=rotatorio","Forced=4"},
        {""}
    };
    
    Frame  hiddenFrame[];
    Object obj[] ;
    Constructor constructors[][] ;
    StringTokenizer tk = null ;
//-----------------------------------------------
    public Object constructsAnObject (int i) {
        try {
            Object obja[] ={ (Object)( paramlist[i] ) },obj;
            obj=constructors[i][0].newInstance(obja);
            WindowsTracker.reset();
            WindowsTracker.firstwin=null;
            return obj;
        } catch( Exception e ) { e.printStackTrace(); return null;}
    }
//-----------------------------------------------
    public static void main( String s[] ) {
        Parameters tmpparam=new StringVectorParameters(s);
        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));
        WindowsTracker.fullscreen=tmpparam.getBoolean("fullscreen",false);        
        new Demon(s);
    }
//-----------------------------------------------
    public Demon(String args[]) {
        //super("ATTENDERE PREGO...");
        Parameters parm=new StringVectorParameters(args);
        try{
            //Runtime.getRuntime().exec("menu1");
            ;
        }
        catch( Exception e ) { e.printStackTrace(); }

        // set preloading parameters
        Preloader.activePreload();
        Preloader.setBufferFileName(parm.getString("bufferfile",Preloader.getBufferFileName()));
        Preloader.setSimulFileName(parm.getString("simulfile",Preloader.getSimulFileName()));
        
        // end setting preloading parameters

        String filename = Preloader.getBufferFileName() ;
        fil_fis = new File( filename );
        long last = fil_fis.lastModified();

        try{
            obj = new Object[numobjs] ;
            constructors = new Constructor[numobjs][];
            hiddenFrame=new Frame[numobjs] ;

            for( int i=1;i<numobjs;i++) { 
                System.out.println( classlist[i] );
                Class pippo = Class.forName( classlist[ i ] );
                constructors[i] = pippo.getDeclaredConstructors();
                
                WindowsTracker.firstwin=hiddenFrame[i]=new Frame();
                WindowsTracker.firstwin.setSize(1,1);
                WindowsTracker.firstwin.setLocation(10000,10000);
                WindowsTracker.firstwin.setTitle("Laboratorio");

                obj[i] = constructsAnObject(i);
                WindowsTracker.reset();
                WindowsTracker.firstwin=null;
            }
        }

        catch( Exception e ) { e.printStackTrace(); }
        try{
            int previous = -1;
            int valore = 0;
            
            while( true ) {
                String val = null ;

                while( true ) {
                    try{
                        long lupd = fil_fis.lastModified();
                        if( lupd != last ) {
                            System.out.println( "Date diverse");
                            last = lupd;
                            fis = new FileInputStream( filename );
                            dis = new DataInputStream( fis );
                            String line = dis.readLine();
                            dis.close();
                            fis.close();
                            tk = new StringTokenizer( line, " " );

                            val = tk.nextToken() ;
                            valore = Integer.parseInt(val);
  							if (valore==99) System.exit(0);
                            break ;
                        }
                    } catch( Exception e ) { 
                        System.out.println("WWWW");
                        e.printStackTrace(); 
                    }
                    try {
                        Thread.sleep( 100 );
                    } catch( Exception e ) {}
                }                                
                
                if((valore == 0) || (valore == 98))
                {
                    valore=0;
                    System.out.println("Closing previous laboratory");                    
                    if (previous>0) {
                        ((TrackedFrame)obj[previous]).deliverEvent(new Event(obj[previous], Event.ACTION_EVENT, "*Exit*"));
                        WindowsTracker.disposeAndReset();                                            
                        obj[previous]=constructsAnObject(previous);
                    }
                    previous = valore;
                } else if (valore>0 && WindowsTracker.getLastOpenedWin()==WindowsTracker.firstwin) {
                    System.out.println("Opening a new laboratory");                    
                    if (previous>0)
                        obj[previous]=constructsAnObject(previous);
                    WindowsTracker.reset();
                    WindowsTracker.firstwin=hiddenFrame[valore];
                    ((TrackedFrame)obj[valore]).centerOnScreen();
                    WindowsTracker.add((Window)obj[valore]);
                    ((TrackedFrame)obj[valore]).setVisible(true);
                    previous = valore;
                }
                // updates pointers
                last = fil_fis.lastModified();
            }   
        } 
        catch( Exception e ) {
            e.printStackTrace() ;
        }
    }
}
