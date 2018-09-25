package com.nr_yper.lockscreen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nr_yper.lockscreen.R;
import com.nr_yper.lockscreen.data.model.Transporter;

import java.util.ArrayList;
import java.util.List;

public class TranspoterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Transporter> transporters = new ArrayList<>();
    private Context context;

    public TranspoterAdapter(List<Transporter> transporters, Context context) {
        this.context = context;
        this.transporters = transporters;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_transpoter, viewGroup, false);
        return new TranspotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Transporter currentItem = transporters.get(i);
        TranspotViewHolder transpotViewHolder = (TranspotViewHolder) viewHolder;
        transpotViewHolder.tvTrans.setText(currentItem.getName());
        Glide.with(context).load(currentItem.getLogoUrl()).into(transpotViewHolder.imgTrans);
        transpotViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change colors
            }
        });
    }

    @Override
    public int getItemCount() {
        return transporters.size();
    }

    class TranspotViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout root;
        private ImageView imgTrans;
        private TextView tvTrans;

        public TranspotViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.layoutRootTranspoter);
            imgTrans = (ImageView) itemView.findViewById(R.id.imgTrans);
            tvTrans = (TextView) itemView.findViewById(R.id.tvTrans);
        }
    }
}
