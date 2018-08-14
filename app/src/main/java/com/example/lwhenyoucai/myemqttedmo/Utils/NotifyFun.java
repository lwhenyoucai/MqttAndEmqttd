package com.example.lwhenyoucai.myemqttedmo.Utils;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
/**
 * Created by lwhenyoucai on 2018/8/14.
 */
public class NotifyFun {

    //显示通知，点击消失
    public void showNotify(Context context) {
        NotifyHelper.with(context)
                .autoCancel(true)
                .when(System.currentTimeMillis())
                .defaults(Notification.DEFAULT_LIGHTS)
                .title("Title")
                .message("Content Content Content Content Content")
                .ticker("New Message")
                //.smallIcon(R.drawable.ic_launcher)
                //.largeIcon(R.drawable.liyujiang)
                .show();
    }

    //显示通知，点击显示提示框
    public void showNotifyWithData(Context context) {
        Bundle data = new Bundle();
        data.putString("test", "hello");
        NotifyHelper.with(context)
                .ongoing(true)
                .autoCancel(true)
                .title("title")
                .message("this is content")
               // .click(MainActivity.class, data)
                .show();
    }

    //显示通知，并可清理
    public void showNotifyCanClear(Context context) {
        NotifyHelper.with(context)
                .identifier(10086)
                .flags(Notification.FLAG_NO_CLEAR)
                .title("title")
                .message("this is content")
                .show();
    }

    //清理通知
    public void clearNotify(Context context) {
        NotifyHelper.with(context).cancel(10086);
    }


}
