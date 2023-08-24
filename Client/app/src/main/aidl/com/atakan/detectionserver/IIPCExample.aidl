// IIPCExample.aidl
package com.atakan.detectionserver;

// Declare any non-default types here with import statements

interface IIPCExample {
    /** Get the modified image */
    Bitmap getImage();

    /** Set displayed value of screen */
    void postVal(in Bitmap image, String action);

}