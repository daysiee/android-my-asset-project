package com.example.myapplication3;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Callable;

import static com.example.myapplication3.R.*;

public class MyRecyclerAdapter extends  RecyclerView.Adapter<MyRecyclerAdapter.ViewHodler>{

    List<CardItem> mDataList;

    public MyRecyclerAdapter(List<CardItem> datalist){
        mDataList = datalist;
    }
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout.item_card, parent, false);
        return new ViewHodler(view);
    } // viewHolder 생성

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHodler holder, int position) {
        CardItem item = mDataList.get(position);
        holder.title.setText(item.getTitle());
        holder.account.setText(item.getAccount());
        holder.contents.setText(item.getContents());
        holder.contents2.setText(item.getContents2());
        holder.contents2.setTextColor(Color.parseColor("#E91E63"));
        if(item.getContents2().charAt(0)=='0')
            holder.contents2.setTextColor(Color.parseColor("#BDBDBD"));
        if(item.getContents2().charAt(0)=='-')
            holder.contents2.setTextColor(Color.parseColor("#197DCC"));
    } // 생성된 viewHoler에 data 바인드

    @Override
    public int getItemCount() {
        return mDataList.size(); // adapter가 가지는 item 개수 지정
    }

    public static class ViewHodler extends  RecyclerView.ViewHolder{

        TextView title;
        TextView account;
        TextView contents;
        TextView contents2;

        Button share, more;
        public ViewHodler(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(id.title_text);
            account = itemView.findViewById(id.account);
            contents = itemView.findViewById(id.contents_text);
            contents2 = itemView.findViewById(id.contents_text2);


        }
    }
}
