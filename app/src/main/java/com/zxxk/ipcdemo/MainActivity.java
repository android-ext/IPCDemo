package com.zxxk.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.zxxk.ipcdemo.domain.BookBean;
import com.zxxk.ipcdemo.service.BookManagerService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "receive new book: " + msg.obj);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };


    private IBookManager bookManager;

    private IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub () {
        @Override
        public void onNewBookArrived(BookBean newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            bookManager = IBookManager.Stub.asInterface(service);
            try {
                List<BookBean> list = bookManager.getBookList();
                for (int i = 0, len = list.size(); i < len; i++) {
                    Log.i(TAG, list.get(i).getBookId() + " , " + list.get(i).getBookName());
                }
                bookManager.registerListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
            try {
                bookManager.unregisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    public void addBookClick(View view) {

        try {
            BookBean book = new BookBean(0, "Swift");
            bookManager.addBook(book);

            List<BookBean> list = bookManager.getBookList();
            for (int i = 0, len = list.size(); i < len; i++) {
                Log.i(TAG, list.get(i).getBookId() + " , " + list.get(i).getBookName());
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
