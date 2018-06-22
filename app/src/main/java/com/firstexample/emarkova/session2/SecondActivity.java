package com.firstexample.emarkova.session2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class SecondActivity extends AppCompatActivity {
    ServiceConnection mConnection;
    Messenger mClient = new Messenger(new IncomingHandler());
    Messenger mService = null;
    boolean mBound = false;
    TextView helloText;

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_SET_INT_VAL:
                    Log.d(MyService.LOG_TAG, "SECAC");
                    helloText.setText("" + msg.arg1);
                    Log.d(MyService.LOG_TAG, "DONE");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        helloText = (TextView)findViewById(R.id.textView);
        Log.d(MyService.LOG_TAG,"created");
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                mService = new Messenger(binder);
                Message msg = Message.obtain(null,MyService.MSG_REGISTER_CLIENT);
                msg.replyTo = mClient;
                Log.d(MyService.LOG_TAG, "TRY TO SEND");
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBound = false;
            }
        };
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
    }
}
