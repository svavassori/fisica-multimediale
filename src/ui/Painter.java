package ui;

import java.awt.*;

/**
 * This interface represents an object which knows how to paint
 * another object
 */
public interface Painter
  { void paint(Component com, Graphics g);
  }
