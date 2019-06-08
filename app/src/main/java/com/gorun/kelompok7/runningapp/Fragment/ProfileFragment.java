package com.gorun.kelompok7.runningapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gorun.kelompok7.runningapp.LoginActivity;
import com.gorun.kelompok7.runningapp.MainActivity;
import com.gorun.kelompok7.runningapp.R;

public class ProfileFragment extends Fragment {
    ImageView imageViewFoto;
    TextView textViewName, textViewEmail;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button buttonSignOut;
    GoogleSignInClient mGoogleSignInClient;
    private static final String JUDUL = "Profil";
    static ProgressDialog locate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity()).setActionBar(JUDUL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        final View rootView = inflater.inflate(R.layout.fragment_profil, null);
        imageViewFoto = rootView.findViewById(R.id.imageViewFoto);
        textViewName = rootView.findViewById(R.id.textViewName);
        textViewEmail = rootView.findViewById(R.id.textViewEmail);
        buttonSignOut = rootView.findViewById(R.id.buttonSignOut);

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageViewFoto);
        textViewName.setText(user.getDisplayName());
        textViewEmail.setText(user.getEmail());

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locate = new ProgressDialog(getActivity());
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Sedang memuat...");
                locate.show();
                signOut();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();

        if (mAuth.getCurrentUser() == null){
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void signOut() {
        mAuth.signOut();

        // Google sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        locate.dismiss();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
    }
}
