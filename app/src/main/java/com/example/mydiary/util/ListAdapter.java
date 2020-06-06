package com.example.mydiary.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydiary.R;
import com.example.mydiary.entity.ItemBean;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private List<ItemBean> noteList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ViewHolder holder = null;

    public ListAdapter(Context context,List<ItemBean> noteList) {
        this.noteList = noteList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {//适配器中数据集的数据个数
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {//获取数据集中与索引对应的数据项
        return noteList.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {//获取指定行对应的ID
        return Long.parseLong(noteList.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//获取每一行ItemBean的显示内容
        if (convertView == null){//如果view未被实例化过，缓存池中没有对应的缓存
            convertView = layoutInflater.inflate(R.layout.item_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.itemNoteTitle.setText(noteList.get(position).getTitle());
        holder.itemNoteDate.setText(noteList.get(position).getDate());
        /*holder.delete = convertView.findViewById(R.id.delete_button);
        //为删除按钮设置监听器
        holder.delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(v.getContext(), "you clicked delete button", Toast.LENGTH_SHORT).show();
            }
        });*/
        return convertView;
    }

    public void remove(int index) {
        noteList.remove(index);
    }

    public void refreshDataSet(){
        notifyDataSetChanged();
    }
}

//item中所有的控件
class ViewHolder{
    public ImageView itemIcon;
    public TextView itemNoteTitle;
    public TextView itemNoteDate;

    View itemView;
    //View delete;

    public ViewHolder(View itemView) {
        if (itemView == null){
            throw new IllegalArgumentException("ViewHolder空View错误");
        }
        this.itemView = itemView;
        itemIcon = itemView.findViewById(R.id.rand_icon);
        itemNoteTitle = itemView.findViewById(R.id.item_note_title);
        itemNoteDate = itemView.findViewById(R.id.item_note_date);

    }
}