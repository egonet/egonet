package com.endlessloopsoftware.ego.author;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 */

import java.awt.KeyboardFocusManager;
import java.util.Collections;

import javax.swing.JTextArea;

/**
 * Extends JTextArea to make tabs focus change events in question text areas
 */
public class NoTabTextArea extends JTextArea
{
   public NoTabTextArea()
   {
      super();
      setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
      setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
   }
}