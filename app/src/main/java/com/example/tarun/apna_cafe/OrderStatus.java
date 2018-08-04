package com.example.tarun.apna_cafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.tarun.apna_cafe.Common.Common;
import com.example.tarun.apna_cafe.Model.Request;
import com.example.tarun.apna_cafe.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_order_status );

        //Firebase
        database = FirebaseDatabase.getInstance ();
        requests = database.getReference ("Requests");

        recyclerView =findViewById ( R.id.listOrders );
        recyclerView.setHasFixedSize ( true );
        layoutManager = new LinearLayoutManager ( this );
        recyclerView.setLayoutManager ( layoutManager );
        
        if(getIntent () == null)
        loadOrders( Common.currentUser.getPhone ());
        else
            loadOrders ( getIntent ().getStringExtra ( "userPhone" ) );

    }

    private void loadOrders ( String phone ) {

        adapter = new FirebaseRecyclerAdapter <Request, OrderViewHolder> (Request.class,R.layout.order_layout,OrderViewHolder.class,requests.orderByChild ( "phone" ).equalTo ( phone ) ) {
            @Override
            protected void populateViewHolder ( OrderViewHolder viewHolder , Request model , int position ) {

                viewHolder.txtOrderId.setText ( adapter.getRef ( position ).getKey () );
                viewHolder.txtOrderStatus.setText (Common.ConvertCodeToStatus(model.getStatus ()) );
                viewHolder.txtOrderAddress.setText ( model.getAddress () );
                viewHolder.txtOrderPhone.setText ( model.getPhone () );
            }
        };
        recyclerView.setAdapter ( adapter );
    }


}
