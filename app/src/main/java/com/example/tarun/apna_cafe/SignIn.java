package com.example.tarun.apna_cafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarun.apna_cafe.Common.Common;
import com.example.tarun.apna_cafe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText edtphone,edtpassword;
    Button btnSignin;
    CheckBox ckbRemmember;
    TextView txtForgotPwd;
    PopupWindow popupWindow;
    FirebaseDatabase database;
     DatabaseReference table_user;

     LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtphone=(EditText)findViewById(R.id.edtphone);
        edtpassword=(EditText)findViewById(R.id.edtpassword);
        btnSignin=(Button)findViewById(R.id.btnSignIN);
        ckbRemmember=findViewById ( R.id.ckbRemember );
        txtForgotPwd = findViewById ( R.id.txtForgotPassword );


        txtForgotPwd.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
            Intent forgot_view =new Intent ( SignIn.this, PhoneAuthActivity .class );
            startActivity ( forgot_view );

               }
        } );


        //Init paper

        Paper.init ( this );

        //INIT FIREBASE


         database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedInternet ( getBaseContext () )) {

                    if (edtphone == null | edtpassword == null) {
                        Toast.makeText ( SignIn.this , "Please Enter your Mobile No. and Password carefully" , Toast.LENGTH_SHORT ).show ( );
                    } else {

                        //Save username & password here

                        if(ckbRemmember.isChecked ())
                        {
                            Paper.book ().write ( Common.USER_KEY,edtphone.getText ().toString () );
                            Paper.book ().write ( Common.PWD_KEY,edtpassword.getText ().toString () );
                        }



                        final ProgressDialog mDialog = new ProgressDialog ( SignIn.this );
                        mDialog.setMessage ( "PLEASE WAITING ....." );
                        mDialog.show ( );

                        table_user.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
                            @Override
                            public void onDataChange ( DataSnapshot dataSnapshot ) {

                                //CHECK IF USER NOT EXIST IN DATABASE

                                if (edtphone == null) {
                                    Toast.makeText ( SignIn.this , "Please Enter Your Phone No." , Toast.LENGTH_SHORT ).show ( );
                                    if (edtpassword == null) {
                                        Toast.makeText ( SignIn.this , "Please Enter Your Password" , Toast.LENGTH_SHORT ).show ( );
                                    }
                                } else {
                                    if (dataSnapshot.child ( edtphone.getText ( ).toString ( ) ).exists ( )) {
                                        //GET USER INFORMATION
                                        mDialog.dismiss ( );
                                        User user = dataSnapshot.child ( edtphone.getText ( ).toString ( ) ).getValue ( User.class );
                                        user.setPhone ( edtphone.getText ( ).toString ( ) );
                                        if (user.getPassword ( ).equals ( edtpassword.getText ( ).toString ( ) )) {

                                            Intent homeintent = new Intent ( SignIn.this , Home.class );
                                            Common.currentUser = user;
                                            startActivity ( homeintent );
                                            finish ( );

                                            table_user.removeEventListener ( this );
                                        } else {
                                            Toast.makeText ( SignIn.this , "WRONG PASSWORD !!!!!!" , Toast.LENGTH_SHORT ).show ( );
                                        }
                                    } else {

                                        mDialog.dismiss ( );
                                        Toast.makeText ( SignIn.this , "USER NOT EXIST IN DATABASE ...??." , Toast.LENGTH_SHORT ).show ( );
                                    }

                                }
                            }

                            @Override
                            public void onCancelled ( DatabaseError databaseError ) {

                            }
                        } );

                    }


                }
                else
                {
                    Toast.makeText ( SignIn.this , "Please , Check your Internet connectivity ...!!" , Toast.LENGTH_SHORT ).show ( );
                    return;
                }

            }
        });

    }



}

