package com.example.tarun.apna_cafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.tarun.apna_cafe.Common.Common;
import com.example.tarun.apna_cafe.Database.Database;
import com.example.tarun.apna_cafe.Interface.ItemClickListener;
import com.example.tarun.apna_cafe.Model.Food;
import com.example.tarun.apna_cafe.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId="";
    
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;


    //Favorites
    Database localDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase initialization
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        //Local DB
        localDB = new Database ( this );


        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent() != null)
        {
            categoryId = getIntent().getStringExtra("CategoryId");
            if(!categoryId.isEmpty() && categoryId != null)
            {
                if(Common.isConnectedInternet ( getBaseContext () ))
                         loadListFood(categoryId);
                else {
                    Toast.makeText ( this , "Please , check your Internet connection...!!" , Toast.LENGTH_SHORT ).show ( );
                    return;
                }
            }
        }
    }
    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder( final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                //Add Favorites
                if(localDB.isFavorite ( adapter.getRef ( position ).getKey () ))
                    viewHolder.fav_image.setImageResource ( R.drawable.ic_favorite_black_24dp );
                //Click to chnage status to Favorites
                viewHolder.fav_image.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick ( View v ) {
                        if(!localDB.isFavorite ( adapter.getRef ( position ).getKey () ))
                        {
                            localDB.addToFavourites ( adapter.getRef ( position ).getKey ());
                            viewHolder.fav_image.setImageResource ( R.drawable.ic_favorite_black_24dp );
                            Toast.makeText ( FoodList.this , ""+model.getName ()+"was added to Favorites" , Toast.LENGTH_SHORT ).show ( );
                        }
                        else {

                            localDB.removeFromFavorites ( adapter.getRef ( position ).getKey ());
                            viewHolder.fav_image.setImageResource ( R.drawable.ic_favorite_border_black_24dp );
                            Toast.makeText ( FoodList.this , ""+model.getName ()+"was removed from Favorites" , Toast.LENGTH_SHORT ).show ( );

                        }
                    }
                } );

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }
}
