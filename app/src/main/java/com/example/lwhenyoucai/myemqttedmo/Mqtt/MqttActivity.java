package com.example.lwhenyoucai.myemqttedmo.Mqtt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lwhenyoucai.myemqttedmo.R;
import com.example.lwhenyoucai.myemqttedmo.ServerUrl;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lwhenyoucai on 2018/8/14.
 */
public class MqttActivity extends AppCompatActivity implements View.OnClickListener {
    private String mClintId;
    public static MqttClient mMqClint;
    public MqttConnectOptions mMqttConnectOptions;
    private ScheduledExecutorService scheduler;
    private final static String TAG = MqttActivity.class.getSimpleName();
    private Button mBtSub, mBtSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        mBtSub = (Button) this.findViewById(R.id.bt_sub);
        mBtSend = (Button) this.findViewById(R.id.bt_send);
        mBtSend.setOnClickListener(this);
        mBtSub.setOnClickListener(this);
        initMqtt();
    }

    /**
     * 初始化mqtt
     */
    private void initMqtt() {
        mClintId = "client3";
        if (mMqClint == null) {

            try {
                mMqClint = new MqttClient(ServerUrl.host, mClintId, new MemoryPersistence());
                mMqttConnectOptions = new MqttConnectOptions();
                //清除缓存
                mMqttConnectOptions.setCleanSession(true);
                //设置用户名
                mMqttConnectOptions.setUserName(ServerUrl.userName);
                //设置用户密码
                mMqttConnectOptions.setPassword(ServerUrl.passWord.toCharArray());
                // 设置超时时间，单位：秒
                mMqttConnectOptions.setConnectionTimeout(10);
                // 心跳包发送间隔，单位：秒
                mMqttConnectOptions.setKeepAliveInterval(20);
                //设置回调
                mMqClint.setCallback(new PushCallBack());
                //MqttTopic topic = client.getTopic(ConnectUrl.SUBSCRIBETOPIC);
                //options.setWill(topic, "close".getBytes(), 0, true);
                //订阅消息
                connect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * mqtt链接
     */
    public void connect() {
        try {
            if (mMqClint != null) {
                //开始链接
                mMqClint.connect(mMqttConnectOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时检查mqtt是否连接
     */
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mMqClint.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sub:
                if (mMqClint.isConnected()) {
                    try {
                        mMqClint.subscribe("Mytopic_1/#", 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_send:
                MqttTopic topic = mMqClint.getTopic("Mytopic_1/3");
                MqttMessage message = new MqttMessage();
                message.setPayload("要发送的消息".getBytes());
                try {
                    topic.publish(message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static class PushCallBack implements MqttCallback {

        @Override
        public void connectionLost(Throwable throwable) {
            Log.e(TAG, "connectionLost: 链接丢失");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.e(TAG, "messageArrived: 接收消息回调" + message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG, "deliveryComplete: 发布消息回调");
        }
    }
}
