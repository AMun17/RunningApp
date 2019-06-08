package com.gorun.kelompok7.runningapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gorun.kelompok7.runningapp.R;

public class LoginActivity extends AppCompatActivity {
    // a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    // Tag for the logs optional
    private static final String TAG = "countdistance";

    // creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    // and also a Firebase Auth object
    FirebaseAuth mAuth;

    static ProgressDialog locate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, DetailDiriActivity.class));
        }
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate = new ProgressDialog(LoginActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Otentikasi pengguna...");
                locate.show();
                signIn();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        locate.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if the user is already signed in
        //we will close this activity
        //and take the user to main activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //locate.dismiss();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);


        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                            locate.dismiss();
                            startActivity(new Intent(LoginActivity.this, DetailDiriActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
