// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0
package com.google.appinventor.client.editor.youngandroid.properties;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.widgets.properties.ChoicePropertyEditor;

/**
 * Property editor for button shape.
 * 
 * @author feeney.kate@gmail.com (Kate Feeney)
 */
public class YoungAndroidPositioningAlgorithmChoicePropertyEditor extends ChoicePropertyEditor {

  // Positioning filter choices
  private static final Choice[] filters = new Choice[] {
    new Choice(MESSAGES.trilaterationPositioningAlgorithm(), "0"),
    new Choice(MESSAGES.overlapareaPositioningAlgorithm(), "1"),
  };

  public YoungAndroidPositioningAlgorithmChoicePropertyEditor() {
    super(filters);
  }
}
