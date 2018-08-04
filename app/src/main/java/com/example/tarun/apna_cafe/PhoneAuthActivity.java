package com.example.tarun.apna_cafe;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tarun.apna_cafe.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton, mResendButton;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    ProgressBar pro;

    private static final String TAG = "PhoneAuthActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_popup);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        pro=findViewById(R.id.progress);

        mVerificationField.setVisibility(View.INVISIBLE);
        pro.setVisibility(View.INVISIBLE);
        mVerifyButton.setVisibility(View.INVISIBLE);
        mResendButton.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                           // startActivity(new Intent(PhoneAuthActivity.this, Set_New_Pass.class));
                            showChangePasswordDialogue();
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    private void showChangePasswordDialogue () {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder ( PhoneAuthActivity.this );
        alertDialog.setTitle ( "ChangePassword" );
        alertDialog.setMessage ( "Please fill All information" );

        LayoutInflater inflater = LayoutInflater.from ( this );
        View layout_pwd = inflater.inflate ( R.layout.change_password_layout, null );

        final MaterialEditText edtNewPassword = layout_pwd.findViewById ( R.id.edtNewPassword );
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById ( R.id.edtRepeatPassword );

        alertDialog.setView ( layout_pwd );

        alertDialog.setPositiveButton ( "Change" , new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick ( DialogInterface dialog , int which ) {
            //Change Password Here
                final AlertDialog waitingDialog = new SpotsDialog ( PhoneAuthActivity.this );
                waitingDialog.show ();

                if(edtNewPassword.getText ().toString ().equals ( edtRepeatPassword.getText ().toString () ))
                {
                    Map<String, Object> passwordUpdate = new HashMap <> (  );
                    passwordUpdate.put ( "password", edtNewPassword.getText ().toString () );
                    
                    DatabaseReference user = FirebaseDatabase.getInstance ().getReference ("user");
                    user.child ( Common.currentUser.getPhone () )
                            .updateChildren ( passwordUpdate )
                            .addOnCompleteListener ( new OnCompleteListener <Void> ( ) {
                                @Override
                                public void onComplete ( @NonNull Task <Void> task ) {
                                    waitingDialog.dismiss ();
                                    alertDialog.show ();
                                    Toast.makeText ( PhoneAuthActivity.this , "Passwod was Update" , Toast.LENGTH_SHORT ).show ( );
                                }
                            } )
                            .addOnFailureListener ( new OnFailureListener ( ) {
                                @Override
                                public void onFailure ( @NonNull Exception e ) {
                                    Toast.makeText ( PhoneAuthActivity.this , e.getMessage (), Toast.LENGTH_SHORT ).show ( );
                                }
                            } );


                }else {
                    waitingDialog.dismiss ();
                    Toast.makeText ( PhoneAuthActivity.this , "Password doesn't match" , Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );


        alertDialog.setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick ( DialogInterface dialog , int which ) {
                dialog.dismiss ();
            }
        } );

        alertDialog.show ();

    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }
   
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                mVerificationField.setVisibility(View.VISIBLE);
                pro.setVisibility(View.VISIBLE);
                mVerifyButton.setVisibility(View.VISIBLE);
                mResendButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                mPhoneNumberField.setVisibility(View.GONE);
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
        }
    }
}