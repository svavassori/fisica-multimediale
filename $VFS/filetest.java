import java.io.*;
import util.Preloader;

class FileTest {
    File fil_fis = null ;
    FileInputStream fis = null ;
    DataInputStream dis = null;
    

//-----------------------------------------------
    public static void main( String s[] ) {
        new FileTest(s);
    }
//-----------------------------------------------
    public FileTest(String args[]) {
        String f1,f2;

        
        System.out.println ("User Home: "+System.getProperty("user.home"));
        System.out.println ("Separator: "+System.getProperty("file.separator"));
        
        f1=System.getProperty("user.home")+System.getProperty("file.separator")+"FileDiProva";
        System.out.println ("Writing  : "+f1);

        try {
            Writer out = new FileWriter(f1);
            out.write("Prova di file");
            out.flush(); 
            out.close();
        }   
        catch(Exception e) {
            System.out.println("Non riesco a scrivere su: "+f1);
        }

        System.out.println ("Reading  : "+f1);
        try {
            
            fis = new FileInputStream( f1);
            dis = new DataInputStream( fis );
            String line = dis.readLine();
            
            System.out.println ("Read     : "+line);
        }   
        catch(Exception e) {
            System.out.println("Non riesco a leggere su: "+f1);
        }
    }
}
        