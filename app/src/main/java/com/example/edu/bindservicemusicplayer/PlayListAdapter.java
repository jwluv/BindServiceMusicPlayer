package com.example.edu.bindservicemusicplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {

    MusicPlayer musicPlayer;
    ArrayList<HashMap<String,Object>> arrayList = null;
    public PlayListAdapter(MusicPlayer musicPlayer, ArrayList<HashMap<String,Object>> arrayList) {
        this.musicPlayer = musicPlayer;
        this.arrayList = new ArrayList<HashMap<String, Object>>();
        this.arrayList = arrayList;
    }
    public void addItem(int position, HashMap<String,Object> hashMap){
        this.arrayList.add(hashMap);
        notifyItemInserted(this.arrayList.size());
//        notifyItemInserted(position);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflate = LayoutInflater.from(viewGroup.getContext());
        View view = inflate.inflate(R.layout.playlist_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        HashMap<String,Object> hashMap = arrayList.get(i);
        myViewHolder.textViewTitle.setText((String)hashMap.get("title"));
        myViewHolder.textViewDetail.setText((String)hashMap.get("artist"));
        myViewHolder.itemImage.setImageResource((int) hashMap.get("image"));

//        myViewHolder.itemImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(v.getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void removeItem(int position){
        this.arrayList.remove(position);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView itemImage;
        TextView textViewTitle, textViewDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            textViewTitle = itemView.findViewById(R.id.item_title);
            textViewDetail = itemView.findViewById(R.id.item_detail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            musicPlayer.play(position);
        }
    }
}


