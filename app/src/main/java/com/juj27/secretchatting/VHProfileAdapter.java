package com.juj27.secretchatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VHProfileAdapter extends RecyclerView.Adapter<VHProfileAdapter.VH> {

    Context context;
    ArrayList<ProfileVOItem> items;

    public VHProfileAdapter(Context context, ArrayList<ProfileVOItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_profile, parent, false);
        VH vh = new VH(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ProfileVOItem item = items.get(position);

        holder.tvName.setText(item.proName);
        holder.tvAge.setText(item.proAge+"세");

        Glide.with(context).load(item.proUrl).into(holder.civ);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class  VH extends RecyclerView.ViewHolder {

        CircleImageView civ;
        TextView tvName;
        TextView tvAge;

        public VH(@NonNull View itemView) {
            super(itemView);

             civ = itemView.findViewById(R.id.circle_pro);
             tvName = itemView.findViewById(R.id.tv_nick);
             tvAge = itemView.findViewById(R.id.tv_age);

        }
    }
}
