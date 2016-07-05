// IOnNewBookArrivedListener.aidl
package com.zxxk.ipcdemo;
import com.zxxk.ipcdemo.domain.BookBean;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in BookBean newBook);
}
