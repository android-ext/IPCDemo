// IBookManager.aidl
package com.zxxk.ipcdemo;

import com.zxxk.ipcdemo.domain.BookBean;

import com.zxxk.ipcdemo.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     List<BookBean> getBookList();

     void addBook(in BookBean book);

     void registerListener(IOnNewBookArrivedListener listener);

     void unregisterListener(IOnNewBookArrivedListener listener);
}
