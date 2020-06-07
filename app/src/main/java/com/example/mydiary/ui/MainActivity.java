package com.example.mydiary.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mydiary.R;
import com.example.mydiary.dao.Note;
import com.example.mydiary.entity.ItemBean;
import com.example.mydiary.util.DBHelper;
import com.example.mydiary.util.ListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView noteListView;
    private Button addBtn;
    private List<ItemBean> noteList = new ArrayList<>();
    private ListAdapter mListAdapter;
    private SearchView searchView;
    private ImageView sortImg;

    private static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this, "MyNote.db", null, 1);
        initComponent();
        setListener();
        //跳转回主界面 刷新列表
        Intent intent = getIntent();
        if (intent != null) {
            getItemBeanList();
            mListAdapter.refreshDataSet();
        }
    }

    @Override
    protected void onResume() {//从别的活动切换回来，要重新加载刷新数据
        super.onResume();
        //跳转回主界面 刷新列表
        getItemBeanList();
        mListAdapter.refreshDataSet();
    }

    //获得控件
    private void initComponent() {
        noteListView = findViewById(R.id.item_list);
        addBtn = findViewById(R.id.btn_add);
        //获取noteList
        getItemBeanList();
        mListAdapter = new ListAdapter(MainActivity.this, noteList);
        noteListView.setAdapter(mListAdapter);
        searchView=findViewById(R.id.search_view);
        sortImg=findViewById(R.id.sort_img);
    }

    //设置监听器
    private void setListener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//添加
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//点击ItemBean显示内容
                ItemBean noteInfo = noteList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("noteInfo", (noteInfo));
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {//长按ItemBean从而删除
                final ItemBean noteInfo = noteList.get(position);
                String title = "警告";
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.note)
                        .setTitle(title)
                        .setMessage("确定要删除吗?")
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note.deleteNote(dbHelper, Integer.parseInt(noteInfo.getId()));
                                noteList.remove(position);
                                mListAdapter.refreshDataSet();
                                Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return true;
            }
        });

        //为SearchView组件设置事件监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //提交按钮的点击事件
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                getItemBeanListByTitle(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //当输入框内容改变的时候回调
                getItemBeanListByTitle(s);
                return true;
            }
        });

        //
        sortImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //注册菜单
                registerForContextMenu(view);
                //打开菜单
                openContextMenu(view);
            }
        });
    }

    //查找
    private void getItemBeanListByTitle(String s) {
        if(s.equals(""))
            getItemBeanList();
        else {
            noteList.clear();
            Cursor allItemBeansCursor = Note.getNotesByTitle(dbHelper, s);
            for (allItemBeansCursor.moveToFirst(); !allItemBeansCursor.isAfterLast(); allItemBeansCursor.moveToNext()) {
                ItemBean noteInfo = new ItemBean();
                noteInfo.setId(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note._id)));
                noteInfo.setTitle(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.title)));
                noteInfo.setContent(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.content)));
                noteInfo.setDate(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.time)));
                noteList.add(noteInfo);
            }
        }
        mListAdapter.refreshDataSet();
    }

    //从数据库中读取所有笔记 封装成List<ItemBean>
    private void getItemBeanList() {
        noteList.clear();
        Cursor allItemBeansCursor = Note.getAllNotes(dbHelper);
        for (allItemBeansCursor.moveToFirst(); !allItemBeansCursor.isAfterLast(); allItemBeansCursor.moveToNext()) {
            ItemBean noteInfo = new ItemBean();
            noteInfo.setId(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note._id)));
            noteInfo.setTitle(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.title)));
            noteInfo.setContent(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.content)));
            noteInfo.setDate(allItemBeansCursor.getString(allItemBeansCursor.getColumnIndex(Note.time)));
            noteList.add(noteInfo);
        }
    }

    //重写返回按钮处理事件
    @Override
    public void onBackPressed() {
        String title = "提示";
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.note)
                .setTitle(title)
                .setMessage("确定要退出吗?")
                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //给其他类提供dbHelper
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1,1,1,"按照修改时间升序");
        menu.add(1,2,1,"按照修改时间降序");
        menu.add(1,3,1,"按照标题升序");
        menu.add(1,4,1,"按照标题降序");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        sortItem(item.getItemId());
        return super.onContextItemSelected(item);
    }

    //排序
    private void sortItem(int type) {
        noteList.clear();
        Cursor cursor = Note.sortByTime(dbHelper, type);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ItemBean noteInfo = new ItemBean();
            noteInfo.setId(cursor.getString(cursor.getColumnIndex(Note._id)));
            noteInfo.setTitle(cursor.getString(cursor.getColumnIndex(Note.title)));
            noteInfo.setContent(cursor.getString(cursor.getColumnIndex(Note.content)));
            noteInfo.setDate(cursor.getString(cursor.getColumnIndex(Note.time)));
            noteList.add(noteInfo);
        }
        mListAdapter.refreshDataSet();
    }
}
