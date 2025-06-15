package ui;
import java.awt.*;
/**
 * An object capable of displaying a status line
 */
public interface CStatusDisplayer
  { void showStatus(String s);
    void showStatus(String s, Color c);
  }
