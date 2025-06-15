package util;

import java.io.File;
import java.lang.System;

public class Preloader {
	private static boolean preload=false; // activate pre-loading
	private static boolean mac=false;	// true - mac; false - windows
	private static String simul="";   // in win C:\simul.txt
                                      // in mac primary hd name:simul.txt
	private static String buffer="";  // in win C:\lm\buffer
                                      // in mac primary hd name:lm:buffer
    
    static public void activePreload() {
        preload=true;
    }

    static public boolean isPreloadActive() {
        return preload;
    }

    static public boolean thereIsSimulFile() {
        return !simul.equals("");
    }
    
	static public void setMac() {
		mac=true;
		simul="";
		buffer="";
	}

	static public void setWin() {
		mac=false;
		simul="";
		buffer="";
	}

	static public boolean isMac() {
		return mac;
	}
    
    static public boolean isWin() {
		return !mac;
	}


    static public void setSimulFileName(String pathname) {
        simul=pathname;
    }

    static public void setBufferFileName(String pathname) {
        buffer=pathname;
    }
		
	static public String getSimulFileName() {
		/*if (simul=="" && isMac() ) {
			// cerca la root direcory		
			String dirname="";
			File f1 = new File(dirname+":");
			while (f1.isDirectory()) {
			    dirname+=":";
				f1=new File(dirname+":");
			}
		
            simul=dirname+"simul.txt";
        } else if (simul=="" && isWin()) {
            simul="C:\\simul.txt";
        }*/
        try {
            if (simul.equals("")) 
                simul=System.getProperty ("user.home")+System.getProperty("file.separator")+"simul.txt";
        } catch (SecurityException e) {
            int i=5;
            i*=7;
        }

        //System.out.println("Simul filename: "+simul);

		return simul;
	}

    static public String getBufferFileName() {
        try {
            if (buffer.equals("")) 
                buffer=System.getProperty ("user.home")+System.getProperty("file.separator")+"buffer";
        } catch (SecurityException e) {
            int i=5;
            i*=7;
        }

        //System.out.println("Buffer filename: "+buffer);
/*
		if (buffer=="" && isMac() ) {
			// cerca la root direcory		
			String dirname="";
			File f1 = new File(dirname+":");
			while (f1.isDirectory()) {
			    dirname+=":";
				f1=new File(dirname+":");
			}
		            
            buffer=dirname+"buffer";
        } else if (buffer=="" && isWin()) {
            buffer="C:\\lm\\buffer";
        }*/
		return buffer;
	}
}


            /*
			f1 = new File(dirname);
			if (f1.isDirectory()) {
			    System.out.println("Dir of "+dirname);
				String s[]=f1.list();
				for (int i=0; i<s.length;i ++) {
			        File f=new File(dirname+""+s[i]);
					if (f.isDirectory())
			            System.out.println(f.getAbsolutePath()+" (dir)");
			        else
						System.out.println(f.getAbsolutePath()+" (file)");
				}
			}
            */
