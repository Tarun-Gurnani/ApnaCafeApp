package com.example.tarun.apna_cafe;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tarun.apna_cafe.Common.Common;
import com.example.tarun.apna_cafe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone,edtName,edtPassword,edtSecureCode;
    Button btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName =(MaterialEditText)findViewById(R.id.edtName);
        edtPhone=(MaterialEditText)findViewById(R.id.edtphone);
        edtPassword=(MaterialEditText)findViewById(R.id.edtpassword);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);
        edtSecureCode=findViewById ( R.id.edtSecureCode );

        //INIT FIREBASE


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("user");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick ( View v ) {

                if (Common.isConnectedInternet ( getBaseContext ( ) )) {
                    final ProgressDialog mDialog = new ProgressDialog ( SignUp.this );
                    mDialog.setMessage ( "PLEASE WAITING ....." );
                    mDialog.show ( );

                    table_user.addValueEventListener ( new ValueEventListener ( ) {
                        @Override
                        public void onDataChange ( DataSnapshot dataSnapshot ) {
                            //CHECK IF ALREADU USER PHONE

                            if (dataSnapshot.child ( edtPhone.getText ( ).toString ( ) ).exists ( )) {
                                mDialog.dismiss ( );
                                Toast.makeText ( SignUp.this , "PHONE NUMBER ALREADY REGISTERED.." , Toast.LENGTH_SHORT ).show ( );
                            } else {
                                mDialog.dismiss ( );
                                User user = new User ( edtName.getText ( ).toString ( ) ,
                                        edtPassword.getText ( ).toString ( ) ,
                                        edtSecureCode.getText ().toString ());
                                table_user.child ( edtPhone.getText ( ).toString ( ) ).setValue ( user );


                                Toast.makeText ( SignUp.this , "SIGNUP SUCCESSFULLY ...." , Toast.LENGTH_SHORT ).show ( );
                                finish ( );
                            }
                        }

                        @Override
                        public void onCancelled ( DatabaseError databaseError ) {

                        }
                    } );

                }
                else {
                    Toast.makeText ( SignUp.this , "Please Check your Internet Connectivity ......!!" , Toast.LENGTH_SHORT ).show ( );
                }
            }

        });
    }
}
