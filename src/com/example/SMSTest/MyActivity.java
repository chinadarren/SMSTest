package com.example.SMSTest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    //   private static final String ACTIVITY_TAG="LogDemo";
    private TextView sender;
    private TextView content;
    private IntentFilter receiveFileter;
    private MessageReceiver messageReciver;

    private EditText to;
    private EditText msgInput;
    private Button send;

    private IntentFilter sendFilter;
    private SendStatusReceiver sendStatusReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.content);
        receiveFileter = new IntentFilter();
        receiveFileter.addAction("android.provider.Telephony.SMS_RECEIVED");
        receiveFileter.setPriority(100);
        messageReciver = new MessageReceiver();
        registerReceiver(messageReciver, receiveFileter);
        //  Log.d(MyActivity.ACTIVITY_TAG, "this is log1");

        to = (EditText) findViewById(R.id.to);
        msgInput = (EditText) findViewById(R.id.msg_input);
        send = (Button) findViewById(R.id.send);

        sendFilter = new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver,sendFilter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();

                Intent sentIntent = new Intent("SENT_SMS_ACTION");
                PendingIntent pi = PendingIntent.getBroadcast(MyActivity.this,0,sentIntent,0);
                smsManager.sendTextMessage(to.getText().toString(),null,msgInput.getText().toString(),pi,null);

                smsManager.sendTextMessage(to.getText().toString(),null,
                        msgInput.getText().toString(),null,null);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(messageReciver);
        unregisterReceiver(sendStatusReceiver);
        //   Log.d(MyActivity.ACTIVITY_TAG, "this is log2");
    }
    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(getResultCode() == RESULT_OK ){
                //短信发送成功
                Toast.makeText(context,"Send succeeded",Toast.LENGTH_SHORT).show();
            }else {
                //短信发送失败
                Toast.makeText(context,"Send failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //   Log.d(MyActivity.ACTIVITY_TAG,"this is log3");
            Bundle bundle = intent.getExtras();
            //提取短信消息
            // pdu密钥来提取 一个 SMS pdus 数组
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i = 0 ; i<messages.length;i++){
                //createFromPdu方法将每一个pdu字节数组转换为SmsMessage对象
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            //调用getOriginatingAddress方法获取发送方号码
            String address = messages[0].getOriginatingAddress();
            String fullMessage = "";

            for (SmsMessage message : messages){
                //调用getMessageBody方法获取短信内容
                fullMessage += message.getMessageBody();
            }
            sender.setText(address);
            content.setText(fullMessage);
            abortBroadcast();
        }
    }
}
