package com.example.mydiary.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mydiary.util.DBHelper;

import java.util.HashMap;

public class Note {
    // 定义表名
    public static String tableName = "Note";
    // 定义各字段名
    public static String _id = "_id"; // _id选择自增主键，为了方便使用，此处定义对应字段名
    public static String title = "title"; // 标题
    public static String content = "content"; // 内容
    public static String time = "date"; // 时间
    //采用单例模式，私有化构造方法
    private Note(){}
    //初始化实例
    private static Note note = new Note();
    //只提供一个实例
    public static Note getInstance(){
        return note;
    }

    //实现表的创建
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "
                + Note.tableName
                + " (  "
                + "_id integer primary key autoincrement, "
                + Note.title + " TEXT, "
                + Note.content + " TEXT, "
                + Note.time + " TEXT "
                + ");";
        db.execSQL( sql );
    }
    //实现表的更新
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ( oldVersion < newVersion ) {
            String sql = "DROP TABLE IF EXISTS " + Note.tableName;
            db.execSQL( sql );
            this.onCreate( db );
        }
    }


    // 插入
    public static void insertNote(DBHelper dbHelper, ContentValues userValues ) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert( Note.tableName, null, userValues );
        db.close();
    }

    // 删除一条笔记
    public static void deleteNote( DBHelper dbHelper, int _id ) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(  Note.tableName, Note._id + "=?",new String[] { _id + "" }  );
        db.close();

    }

    // 删除所有笔记
    public static void deleteAllNote( DBHelper dbHelper ) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(  Note.tableName, null, null  );
        db.close();
    }

    // 修改
    public static void updateNote( DBHelper dbHelper,  int _id, ContentValues infoValues ) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(Note.tableName, infoValues, Note._id + " =? ", new String[]{ _id + "" });
        db.close();
    }

    // 以HashMap<String, Object>键值对的形式获取一条信息
    public static HashMap<String, Object> getNote(DBHelper dbHelper, int _id ){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        HashMap<String, Object> NoteMap = new HashMap<>();
        // 此处要求查询Note._id为传入参数_id的对应记录，使游标指向此记录
        Cursor cursor = db.query( Note.tableName, null, Note._id + " =? ", new String[]{ _id + "" }, null, null, null);
        cursor.moveToFirst();
        NoteMap.put(Note.title, cursor.getLong(cursor.getColumnIndex(Note.title)));
        NoteMap.put(Note.content, cursor.getString(cursor.getColumnIndex(Note.content)));
        NoteMap.put(Note.time, cursor.getString(cursor.getColumnIndex(Note.time)));
        cursor.close();
        return NoteMap;
    }

    // 获得查询指向Note表的游标
    public static Cursor getAllNotes(DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Note.tableName, null, null, null, null, null, "date desc");
        cursor.moveToFirst();
        return cursor;
    }

    //根据标题搜索Note
    public static Cursor getNotesByTitle(DBHelper dbHelper, String s) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Note.tableName, null, "title like '%" + s + "%'", null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    //以下是排序
    public static Cursor sortByTime(DBHelper dbHelper, int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (type) {
            case 1:
                cursor = db.query(Note.tableName, null, null, null, null, null,"date asc");
                break;
            case 2:
                cursor = db.query(Note.tableName, null, null, null, null, null,"date desc");
                break;
            case 3:
                cursor = db.query(Note.tableName, null, null, null, null, null,"title asc");
                break;
            case 4:
                cursor = db.query(Note.tableName, null, null, null, null, null,"title desc");
                break;
        }
        if(cursor!=null)
            cursor.moveToFirst();
        return cursor;
    }
}
