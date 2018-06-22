package com.firstexample.emarkova.session2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    static final String LOG_TAG = "myLog";
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_SET_INT_VAL = 3;
    Messenger mClient;
    Messenger mMesseger = new Messenger(new IncomingHandler());
    int number;

    private final Random mGenerator = new Random();

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    number = getRandomNumber();
                    if(mClient != null) {
                        Message message = Message.obtain(null, MSG_SET_INT_VAL, number, 0);
                        try {
                            mClient.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mMesseger.getBinder();
    }

    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
}
