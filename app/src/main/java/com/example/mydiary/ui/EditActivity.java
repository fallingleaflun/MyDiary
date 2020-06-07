package com.example.mydiary.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.dao.Note;
import com.example.mydiary.entity.ItemBean;
import com.example.mydiary.util.DBHelper;
import com.example.mydiary.util.DateStringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private Button btn_save;
    private Button btn_return;
    private Button btn_set_alarm;
    private TextView tv_now;
    private EditText et_title;
    private EditText et_content;
    private EditText tvRemindTime;
    //记录当前编辑的对象（用于比对是否改变）
    private ItemBean currentNote;
    //记录是否是插入状态 （因为也可能是更新（编辑）状态）
    private boolean insertOrNot = true;
    //管理闹钟
    final Calendar calendar=Calendar.getInstance();
    private AlarmManager alarmManager;
    CheckBox cb_use_alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initComponent();
        setListener();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //主界面点击ListView中的一个ItemBean跳转时
        if(bundle != null){
            currentNote = (ItemBean) bundle.getSerializable("noteInfo");
            et_title.setText(currentNote.getTitle());
            et_content.setText(currentNote.getContent());
            insertOrNot = false;
        }
    }
    //初始化视图
    private void initComponent(){
        btn_save = findViewById(R.id.btn_save);
        btn_return = findViewById(R.id.btn_return);
        btn_set_alarm = findViewById(R.id.btn_set_alarm);
        tv_now = findViewById(R.id.tv_now);
        et_content = findViewById(R.id.edit_content);
        et_title = findViewById(R.id.edit_title);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        tv_now.setText(sdf.format(date));
        tvRemindTime=findViewById(R.id.tv_remind_time_picker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        cb_use_alarm=findViewById(R.id.cb_use_alarm);
    }
    //设置监听器
    private void setListener(){
        //返回按钮
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置闹钟按钮
        btn_set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //得到系统现在的时间
                int init_year=calendar.get(Calendar.YEAR);
                int init_month=calendar.get(Calendar.MONTH);
                int init_dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
                final int init_hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);//得到的小时
                final int init_minute=calendar.get(Calendar.MINUTE);//分钟

                //弹出日期对话框
                DatePickerDialog dialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        //设置目标年月日
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                String time = year + "-" + month + "-" + dayOfMonth + " " + hourOfDay + ":" + minute;
                                tvRemindTime.setText(time);
                                //都已经选好就要开始设置闹钟?
                                //不，保存的时候才设置闹钟
                            }
                        }, init_hourOfDay, init_minute, false);
                        timePickerDialog.show();
                    }
                }, init_year, init_month, init_dayOfMonth);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dialog.show();
            }
        });

        //保存按钮
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( et_title.getText().toString().equals("") ||
                        et_content.getText().toString().equals("")){
                    Toast.makeText(EditActivity.this,R.string.save_fail,Toast.LENGTH_LONG).show();
                }else {
                    setAlarm();
                    saveNote();
                    /*不可以这样返回上一个活动
                    Intent intent = new Intent(EditActivity.this,MainActivity.class);
                    startActivity(intent);*/
                    finish();
                }
            }
        });

        /**
         * 弹出时间选择器，选择闹钟执行时间
         * @param view
         */
        tvRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = year + "-" + DateStringUtil.getLocalMonth(month) + "-" + DateStringUtil.getMultiNumber(dayOfMonth) + " " + DateStringUtil.getMultiNumber(hourOfDay) + ":" + DateStringUtil.getMultiNumber(minute);
                                tvRemindTime.setText(time);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dialog.show();
            }
        });
    }
    //保存笔记到数据库 判断是新建还是更新
    private void saveNote(){
        DBHelper dbHelper = MainActivity.getDbHelper();

        ContentValues values = new ContentValues();
        values.put(Note.title,et_title.getText().toString());
        values.put(Note.content,et_content.getText().toString());
        values.put(Note.time,tv_now.getText().toString());
        if (insertOrNot){//判断是插入还是更新
            Note.insertNote(dbHelper,values);
        }else{
            Note.updateNote(dbHelper,Integer.parseInt(currentNote.getId()),values);
        }
    }

    //重写手机上返回键处理函数，若更改过提示保存，否则直接返回主界面
    @Override
    public void onBackPressed() {
        boolean display = false;
        if (insertOrNot){
            if( !et_title.getText().toString().equals("") &&
                    !et_content.getText().toString().equals("")){
                display = true;
            }
        }else{
            if( !et_title.getText().toString().equals(currentNote.getTitle()) ||
                    !et_content.getText().toString().equals(currentNote.getContent())){
                display = true;
            }
        }
        if (display){
            String title = "警告";
            new AlertDialog.Builder(EditActivity.this)
                    .setIcon(R.drawable.note)
                    .setTitle(title)
                    .setMessage("是否保存当前内容?")
                    .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setAlarm();
                            saveNote();
                            Toast.makeText(EditActivity.this,R.string.save_succ,Toast.LENGTH_LONG).show();
                            //更新当前Note对象的值 防止选择保存后按返回仍显示此警告对话框
                            currentNote.setTitle(et_title.getText().toString());
                            currentNote.setContent(et_content.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }else{
            finish();
        }
    }

    private void setAlarm() {
        if(cb_use_alarm.isChecked()) {
            //实例化闹钟管理器
            alarmManager = (AlarmManager) EditActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent();
            intent.putExtra("title", et_title.getText().toString());
            //设置广播名字
            intent.setAction("com.example.myDiary2.Alarm");
            //将来执行的操作
            PendingIntent pendingIntent = PendingIntent.getBroadcast(EditActivity.this, 0x101, intent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(EditActivity.this, "已经设置闹钟提醒", Toast.LENGTH_LONG).show();
        }
    }
}
