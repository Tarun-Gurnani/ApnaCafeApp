package com.example.tarun.apna_cafe.ViewHolder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarun.apna_cafe.Interface.ItemClickListener;
import com.example.tarun.apna_cafe.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;

    private ItemClickListener itemClickListener;


    public OrderViewHolder ( View itemView ) {
        super ( itemView );

        txtOrderId = (TextView)itemView.findViewById ( R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById ( R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById ( R.id.order_status);
        txtOrderAddress = (TextView)itemView.findViewById ( R.id.order_address);

        itemView.setOnClickListener ( this );

    }

    public void setItemClickListener ( ItemClickListener itemClickListener ) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick ( View v ) {

        itemClickListener.onClick ( v,getAdapterPosition (),false );

    }
}
