package com.zxxk.ipcdemo.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ext on 2016/7/1.
 */
public class BookBean implements Parcelable {

    private int bookId;
    private String bookName;

    public BookBean(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected BookBean(Parcel in) {

        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<BookBean> CREATOR = new Creator<BookBean>() {
        @Override
        public BookBean createFromParcel(Parcel in) {
            return new BookBean(in);
        }

        @Override
        public BookBean[] newArray(int size) {
            return new BookBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }


    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
