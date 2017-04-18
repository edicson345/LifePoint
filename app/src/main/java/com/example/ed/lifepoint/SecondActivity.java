package com.example.ed.lifepoint;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.DateFormat;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;
import java.util.Date;

public class SecondActivity extends Activity implements View.OnClickListener
{
    MqttAndroidClient client;
    MqttAndroidClient BPMclient;
    MqttAndroidClient detectionClient;
    MqttConnectOptions options;
    TextView subText;
    TextView fall;
    TextView date;
    Vibrator vibrator;
    TextView BPM;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        Button one = (Button)findViewById(R.id.button1);
        one.setOnClickListener(this);
        Button two = (Button)findViewById(R.id.button2);
        two.setOnClickListener(this);
        subText = (TextView)findViewById(R.id.textview2);
        BPM = (TextView)findViewById(R.id.textview3);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        fall = (TextView)findViewById(R.id.fall);
        date = (TextView)findViewById(R.id.dateTime);
        final Calendar t = Calendar.getInstance();
        date.setText(DateFormat.getTimeFormat(SecondActivity.this).format(t.getTime()));
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://io.adafruit.com:1883", clientId);

        String clientId2 = MqttClient.generateClientId();
        BPMclient = new MqttAndroidClient(this.getApplicationContext(), "tcp://io.adafruit.com:1883", clientId2);

        String clientId3 = MqttClient.generateClientId();
        detectionClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://io.adafruit.com:1883", clientId3);

        options = new MqttConnectOptions();
        options.setUserName("edicson345");
        options.setPassword("3c70ebad6acb4a5aa68974d00149f7f3".toCharArray());

        try
        {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken)
                {
                    Toast.makeText(SecondActivity.this, "Connected!", Toast.LENGTH_LONG).show();
                    setSub();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable)
                {
                    Toast.makeText(SecondActivity.this, "Connection failed!", Toast.LENGTH_LONG).show();
                }
            });


        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }

        try
        {
            IMqttToken BPMtoken = BPMclient.connect(options);
            BPMtoken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    setBPMSub();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

                }
            });
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }

        try
        {
            IMqttToken fallToken = detectionClient.connect(options);
            fallToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    setFallSub();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Toast.makeText(SecondActivity.this, "Fall Feed Connection failed!", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }

        detectionClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage fallMessage) throws Exception {
                fall.setText(new String(fallMessage.getPayload()));
                final Calendar t = Calendar.getInstance();
                date.setText(DateFormat.getTimeFormat(SecondActivity.this).format(t.getTime()));
                vibrator.vibrate(2000);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));
                //vibrator.vibrate(500);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        BPMclient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                BPM.setText(new String(mqttMessage.getPayload()));
                //vibrator.vibrate(500);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });


    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button1:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                unSub();
                BPMunSub();
                unSubFall();
                finish();
                break;

        }

    }

    private void setSub()
    {
        String topic = "edicson345/feeds/battery";
        try
        {
            client.subscribe(topic, 0);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void setBPMSub()
    {
        String topic = "edicson345/feeds/heartbeat";
        try
        {
            BPMclient.subscribe(topic, 0);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void setFallSub()
    {
        String topic = "edicson345/feeds/falldetected";
        try
        {
            detectionClient.subscribe(topic, 0);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void unSub()
    {
        String topic = "edicson345/feeds/battery";

        try
        {
            client.unsubscribe(topic);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void BPMunSub()
    {
        String topic = "edicson345/feeds/heartbeat";

        try
        {
            BPMclient.unsubscribe(topic);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void unSubFall()
    {
        String topic = "edicson345/feeds/falldetected";

        try
        {
            detectionClient.unsubscribe(topic);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }
}
