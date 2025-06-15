import java.awt.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import util.Preloader;

class MacExe extends Frame {
    File fil_fis = null ;
    FileInputStream fis = null ;
    DataInputStream dis = null;

    String list[] ={"", "cinemat.Cinemat", "cinemat.Cinemat", "cinemat.Cinemat" };
    Object obj[] ;
    Constructor cost[] ;
    StringTokenizer tk = null ;

//-----------------------------------------------
    public static void main( String s[] ) {
        new MacExe();
    }
//-----------------------------------------------
    public MacExe() {
        super("ATTENDERE PREGO...");
        try{
            //Runtime.getRuntime().exec("menu1");
            ;
        }
        catch( Exception e ) { e.printStackTrace(); }

        // set preloading parameters
        Preloader.setMac();
        Preloader.activePreload();
        // end setting preloading parameters

        String filename = Preloader.getBufferFileName() ;
        fil_fis = new File( filename );

        try{
            obj = new Object[4] ;
            String s0[]= { "moto_base=rotatorio"};
            String s1[]= { "moto_base=traslatorio","vel_base=2"};
            String s2[]= { "moto_base=traslatorio","vel_base=3","acc_base=5"};

            String totale[][]=new String[3][];
            totale[0]=s0; 
            totale[1]=s1;
            totale[2]=s2;

            for( int i=1;i<4;i++) { 
                System.out.println( list[i] );
                Class pippo = Class.forName( list[ i ] );
                cost = pippo.getDeclaredConstructors();

                String strs[] ={ "" };
                Object obja[] ={ (Object)( /*strs*/totale[i-1] ) };
                obj[i] = cost[0].newInstance(obja);
            }
        }

        catch( Exception e ) { e.printStackTrace(); }
        try{
            int previous = -1;
            int valore = 0;
            long last = fil_fis.lastModified();

            setVisible(true); /////////////////////////////
            toBack();
            setVisible(false); /////////////////////////////
            while( true ) {
                String val = null ;

                while( true ) {
                    try {
                        Thread.sleep( 500 );
                    } catch( Exception e ) {}

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
                }

                if( valore == 0 ) {
                    previous = valore; 
                    continue; 
                }
                previous = valore;
                setVisible(true);
                ((Frame)obj[valore]).setVisible( true );
                setVisible(false);
            }   
        } 
        catch( Exception e ) {
            e.printStackTrace() ;
        }
    }
}