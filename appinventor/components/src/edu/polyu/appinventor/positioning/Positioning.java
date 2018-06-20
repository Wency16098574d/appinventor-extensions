package edu.polyu.appinventor.positioning;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesPermissions;

import com.google.appinventor.components.common.ComponentCategory;

import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.Deleteable;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.SdkLevel;
import com.google.appinventor.components.runtime.util.YailList;
import gnu.lists.FString;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


//???????????????????????verion, nonvisible
@DesignerComponent(version = 20171107,
        description = "indoor " +
                "positioning",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        helpUrl = "http://",
        iconName = "images/positioning.png")
//???????external
@SimpleObject(external = true)
//???usesPermissions
@UsesPermissions(permissionNames = "android.permission.BLUETOOTH, " + "android.permission.BLUETOOTH_ADMIN,"
        + "android.permission.ACCESS_COARSE_LOCATION")

//public class Positioning {}


public class Positioning extends AndroidNonvisibleComponent implements Component {
    private static final String LOG_TAG = "Positioning";

    private final ComponentContainer container;

    /**
     * Creates a Positioning component.
     *
     * @param container container, component will be placed in
     */
    public Positioning(ComponentContainer container) {
        super(container.$form());
        this.container = container;
    }

    /**
     * Method to add known beacons (the anchors)
     */
    @SimpleFunction
    public void AddBeacons() {

    }
}
