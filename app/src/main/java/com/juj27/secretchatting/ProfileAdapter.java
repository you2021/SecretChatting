package com.juj27.secretchatting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        View itemView = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.profile_item, parent, false);

        CircleImageView civ = itemView.findViewById(R.id.circle_pro);
        TextView tvName = itemView.findViewById(R.id.tv_nick);
        TextView tvAge = itemView.findViewById(R.id.tv_age);

        tvName.setText(item.proName);
        tvAge.setText(item.proAge+"세");

        Glide.with(context).load(item.proUrl).into(civ);
        
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();

                CharSequence[] list = {"친구목록 추가하기", "대화하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     //   Toast.makeText(context, list[which]+"선택", Toast.LENGTH_SHORT).show();
                        if (list[which] == list[0]){

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference rootRef = firebaseDatabase.getReference("friend");

                            DatabaseReference personRef = rootRef.child(G.nickName);
                            DatabaseReference child =  personRef.child(item.proName);
                            child.setValue("true");

                            Intent intent = new Intent(context, ListActivity.class);
                            context.startActivity(intent);
                        }else {

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference rootRef = firebaseDatabase.getReference("friend");

                            DatabaseReference personRef = rootRef.child(G.nickName);
                            DatabaseReference child =  personRef.child(item.proName);
                            child.setValue("true");

                            Intent intent = new Intent(context, ChattingActivity.class);
                            intent.putExtra("name",item.proName);
                            context.startActivity(intent);
                        }
                    }
                });

                builder.create();
                builder.show();
            }
        });

        return itemView;
    }
}
