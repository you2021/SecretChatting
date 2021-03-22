package com.juj27.secretchatting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAdapter extends BaseAdapter {

    Context context;
    ArrayList<ProfileVOItem> items;

    public ProfileAdapter(Context context, ArrayList<ProfileVOItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ProfileVOItem item = items.get(position);

//        Intent intent = new Intent(context, ChattingActivity.class);
//        intent.putExtra("name",item.proName);
//        context.startActivity(intent);

        View itemView = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.profile_item, parent, false);

        CircleImageView civ = itemView.findViewById(R.id.circle_pro);
        TextView tvName = itemView.findViewById(R.id.tv_nick);
        TextView tvAge = itemView.findViewById(R.id.tv_age);

        tvName.setText(item.proName);
        tvAge.setText(item.proAge+"세");

        Glide.with(context).load(item.proUrl).into(civ);

        return itemView;
    }
}
