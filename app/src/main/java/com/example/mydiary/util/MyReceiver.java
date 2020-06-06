package com.example.mydiary.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mydiary.ui.AlarmActivity;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.example.myDiary2.Alarm".equals(intent.getAction())){
            Toast.makeText(context, "备忘录有待办事项提醒", Toast.LENGTH_SHORT).show();
            //跳转到Activity
            String extra = intent.getStringExtra("title");
            Intent intent1=new Intent(context, AlarmActivity.class);
            intent1.putExtra("title", extra);
            //设置标志位(Flag)
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
