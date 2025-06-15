package ui;

import java.applet.*;
import java.net.*;
import ui.HelpDisplayer;
import ui.MessageBox;

/**
 * The help displayer for an Applet. Shows an HTML document in
 * a new window, using an URL relative to the code base of the
 * applet of the form topic.html#item, where topic and item
 * are the parameters of displayHelp
 */
public class AppletHelpDisplayer implements HelpDisplayer
  { URL base;
    AppletContext context;
    String suffix=".html";

    public AppletHelpDisplayer(Applet applet)
      { base=applet.getCodeBase();
        context=applet.getAppletContext();
      }

    public void displayHelp(String topic, String item)
      { String name=topic+suffix;

        if (item!=null && !item.equals(""))
          name=name+"#"+item;

        try 
          { URL url=new URL(base, name);

            context.showDocument(url, "Help");
          }
        catch (MalformedURLException e)
          { MessageBox.message(null,"Help error", "Invalid URL: "+name);
          }

      }
  }
