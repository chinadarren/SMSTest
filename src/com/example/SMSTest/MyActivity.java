package com.example.SMSTest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
   // private static final String ACTIVITY_TAG="LogDemo";
    private TextView sender;

    private TextView content;
    private IntentFilter receiveFileter;
    private MessageReceiver messageReciver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      //  sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        receiveFileter = new IntentFilter();
        receiveFileter.addAction("adnroid.provider.Telephony.SMS_RECEIVED");
        messageReciver = new MessageReceiver();
        registerReceiver(messageReciver, receiveFileter);
      //  Log.d(MyActivity.ACTIVITY_TAG, "this is log1");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(messageReciver);
      //  Log.d(MyActivity.ACTIVITY_TAG, "this is log2");
    }

    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
         //   Log.d(MyActivity.ACTIVITY_TAG,"this is log3");
            sender = (TextView) findViewById(R.id.sender);
            Bundle bundle = intent.getExtras();
            //��ȡ������Ϣ
            // pdu��Կ����ȡ һ�� SMS pdus ����
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i = 0 ; i<messages.length;i++){
                //createFromPdu������ÿһ��pdu�ֽ�����ת��ΪSmsMessage����
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            //����getOriginatingAddress������ȡ���ͷ�����
            String address = messages[0].getOriginatingAddress();
            String fullMessage = "";

            for (SmsMessage message : messages){
                //����getMessageBody������ȡ��������
                fullMessage += message.getMessageBody();
            }
            sender.setText("sss");
            content.setText(fullMessage);
        }
    }
}
