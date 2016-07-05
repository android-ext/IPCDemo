package zxxk.ext.socketdemo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import zxxk.ext.socketdemo.service.TCPServerService;
import zxxk.ext.socketdemo.service.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private Button mSendButton;
    private TextView mMessageTV;
    private EditText mMessageEt;

    private PrintWriter mPrintWriter;
    private Socket mClientSocket;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG: {
                    mMessageTV.setText(mMessageTV.getText() + (String)msg.obj);
                    break;
                }
                case MESSAGE_SOCKET_CONNECTED: {
                    mSendButton.setEnabled(true);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageTV = (TextView) findViewById(R.id.receive_msg_tv);
        mSendButton = (Button) findViewById(R.id.send_btn);
        mSendButton.setOnClickListener(this);
        mMessageEt = (EditText) findViewById(R.id.input_et);

        Intent intent = new Intent(this, TCPServerService.class);
        startService(intent);

        // 开子线程连接到服务器端
        new Thread(){
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    private void connectTCPServer() {

        Socket socket = null;
        while(socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
            } catch (IOException e) {
                SystemClock.sleep(2000);
                e.printStackTrace();
                System.out.println("connect tcp server failed, retry...");

            }
        }
        System.out.println("connect server success");
        try {
            // 接受服务器端的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(!MainActivity.this.isFinishing()) {
                String msg = br.readLine();
                System.out.println("receive: " + msg);
                if (msg != null) {
                    String time = formatDataTime(System.currentTimeMillis());
                    final String showMsg = "\nserver " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showMsg).sendToTarget();
                }
            }
            System.out.println("quit...");
            Utils.close(mPrintWriter);
            Utils.close(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDataTime(long l) {

        return new SimpleDateFormat("[HH:mm:ss)]").format(new Date(l));
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        if (v == mSendButton) {
            final String msg = mMessageEt.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                mPrintWriter.println(msg);
                mMessageEt.setText("");
                String time = formatDataTime(System.currentTimeMillis());
                final String showMsg = "\nself " + time + ":" + msg;
                mMessageTV.setText(mMessageTV.getText() + showMsg);
            }
        }
    }
}
