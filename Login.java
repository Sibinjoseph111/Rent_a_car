package com.example.hp.rent_a_car;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.example.hp.rent_a_car.R.id.Code_auth;


public class Login extends AppCompatActivity {
    EditText phoneNum;
    EditText Code;           //// two edit text one for enter phone number other for enter OTP code
    Button sent_,Verify;                    // sent_ button to request for verification and verify is for to verify code
    Integer authstate;
    public FirebaseAuth mAuth;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public String mVerificationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNum =(EditText) findViewById(R.id.Mobile_no);
        Code =(EditText) findViewById(Code_auth);

        sent_ =(Button)findViewById(R.id.Signin_button);
        Verify =(Button)findViewById(R.id.Code_auth_button);

        callback_verification();


        mAuth = FirebaseAuth.getInstance();




        sent_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num=phoneNum.getText().toString();
                startPhoneNumberVerification(num);                  // call function for receive OTP 6 digit code

            }
        });


        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=Code.getText().toString();
                verifyPhoneNumberWithCode(mVerificationId,code);                 //call function for verify code

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent newintent= new Intent(Login.this,MainActivity.class);
            startActivity(newintent);
        } else {
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]


    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getApplicationContext(), "sign in successfull", Toast.LENGTH_SHORT).show();
                            // [START_EXCLUDE]
                            authstate=1;
                            Intent getnewintent = new Intent(Login.this,MainActivity.class);
                            startActivity(getnewintent);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]

                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI

                            // [END_EXCLUDE]
                        }
                    }
                });
    }



    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void callback_verification() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
                Toast.makeText(Login.this, "Login successfull", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(Login.this, "CODE SENT", Toast.LENGTH_SHORT).show();



            }
        };
    }
}