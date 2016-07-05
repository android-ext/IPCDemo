package zxxk.ext.socketdemo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ext on 2016/7/4.
 */
public class Utils {

    public static void close(PrintWriter out) {
        out.close();
    }

    public static void close(BufferedReader in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
