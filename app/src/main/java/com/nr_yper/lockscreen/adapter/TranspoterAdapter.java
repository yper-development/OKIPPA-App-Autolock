package com.nr_yper.lockscreen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nr_yper.lockscreen.R;
import com.nr_yper.lockscreen.data.model.Transporter;

import java.util.ArrayList;
import java.util.List;

public class TranspoterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Transporter> transporters = new ArrayList<>();
    private Context context;
    private TransporterListener transporterListener;

    public TranspoterAdapter(List<Transporter> transporters, Context context) {
        this.context = context;
        this.transporters = transporters;
    }

    public void setTransporterListener(TransporterListener transporterListener) {
        this.transporterListener = transporterListener;
    }

    public void setTransporters(List<Transporter> transporters) {
        this.transporters = transporters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_transpoter, viewGroup, false);
        return new TranspotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        final Transporter currentItem = transporters.get(i);
        TranspotViewHolder transpotViewHolder = (TranspotViewHolder) viewHolder;
        if (currentItem.isCheck()) {
            transpotViewHolder.root.setBackgroundColor(Color.parseColor("#1abc9c"));
            transpotViewHolder.imgCheck.setVisibility(View.VISIBLE);
        } else {
            transpotViewHolder.root.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            transpotViewHolder.imgCheck.setVisibility(View.GONE);
        }

        transpotViewHolder.tvTrans.setText(currentItem.getName());
        Glide.with(context).load(currentItem.getLogoUrl()).into(transpotViewHolder.imgTrans);
        transpotViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change colors
                currentItem.setCheck(true);
                uncheckAll(transporters, currentItem);
                transporterListener.onClickTranspoter(currentItem);
            }
        });
    }


    public interface TransporterListener {
        void onClickTranspoter(Transporter transporter);
    }


    @Override
    public int getItemCount() {
        return transporters.size();
    }

    class TranspotViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout root;
        private ImageView imgTrans;
        private TextView tvTrans;
        private ImageView imgCheck;

        public TranspotViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (RelativeLayout) itemView.findViewById(R.id.layoutRootTranspoter);
            imgTrans = (ImageView) itemView.findViewById(R.id.imgTrans);
            tvTrans = (TextView) itemView.findViewById(R.id.tvTrans);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
        }
    }

    private void uncheckAll(List<Transporter> transporters, Transporter currentItem) {
        for (int i = 0; i < transporters.size(); i++) {
            if (transporters.get(i).getId() != currentItem.getId()) {
                transporters.get(i).setCheck(false);
            }
        }
        notifyDataSetChanged();
    }
}
