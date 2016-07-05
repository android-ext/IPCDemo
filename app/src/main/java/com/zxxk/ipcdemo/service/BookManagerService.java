package com.zxxk.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zxxk.ipcdemo.IOnNewBookArrivedListener;
import com.zxxk.ipcdemo.domain.BookBean;
import com.zxxk.ipcdemo.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    /**
     * CopyOnWriteArrayList支持并发读/写，因为AIDL方法在服务端的Binder线程池中执行的，因此当多个客户端同时连接的时候
     * 会存在多个线程同时访问的情形，所以我们要在AIDL方法中处理线程同步，这里直接使用CopyOnWriteArrayList来进行自动的
     * 线程同步
     */
    private CopyOnWriteArrayList<BookBean> mBookList = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub () {

        @Override
        public List<BookBean> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(BookBean book) throws RemoteException {

            book.setBookId(mBookList.size() + 1);
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {

            if (!mListenerList.contains(listener)) {
                mListenerList.add(listener);
            } else {
                Log.d(TAG, "already exists.");
            }
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {

            if (mListenerList.contains(listener)) {
                mListenerList.remove(listener);
            } else {
                Log.d(TAG, "not found, can not unregister.");
            }
        }
    };


    public BookManagerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBookList.add(new BookBean(1, "Android"));
        mBookList.add(new BookBean(2, "iOS"));

        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();

    }

    private void onNewBookArrived(BookBean book) throws RemoteException {
        mBookList.add(book);
        for (int i = 0, len = mListenerList.size(); i < len; i++) {

            IOnNewBookArrivedListener listener = mListenerList.get(i);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
        }
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while(!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000 * 1);

                    int bookId = mBookList.size() + 1;
                    BookBean newBook = new BookBean(bookId, "new book#" + bookId);
                    try {
                        onNewBookArrived(newBook);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
