package com.example.bookworm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.welcomeActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {

    Button logout,callNow;
    DatabaseReference databaseReference, databaseReference1;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference rootRef, roofref1;
    TextView user_name,fine,user_currentOrder;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        logout= (Button)view.findViewById(R.id.logout);
        FirebaseApp.initializeApp(getActivity());
        database= FirebaseDatabase.getInstance();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        rootRef= database.getReference("Users");
        roofref1= database.getReference("Issue List");
        mAuth = FirebaseAuth.getInstance();
        //String email=mAuth.getCurrentUser().getEmail();
        String email=SaveSharedPreference.getUserName(getActivity());
        if(email!=null)
        {
            email= email.substring(0,7);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(email);
        databaseReference1= FirebaseDatabase.getInstance().getReference("Issue List").child(email);


        user_name= (TextView)view.findViewById(R.id.user_name);
        fine= (TextView)view.findViewById(R.id.fine);
        user_currentOrder= (TextView)view.findViewById(R.id.user_currentOrder);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Do you wish to log out?")
                        .setMessage("Your Issues will be cleared")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                SaveSharedPreference.setUserName(getActivity(),"");
                                Intent intent= new Intent(getActivity(), welcomeActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();


            }
        });
        callNow= (Button)view.findViewById(R.id.callNow);
        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone= "+919598894735";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        userDetails();
        return view;
    }
    public void userDetails()
    {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userProfile = dataSnapshot.getValue(Users.class);

                user_name.setText(userProfile.getName());
                if(userProfile.getCurrentOrder().equals("")) {
                    user_currentOrder.setText("Current Issue Number : None");
                }
                else
                {
                    user_currentOrder.setText("Current Issue Number : "+userProfile.getCurrentOrder());
                }
                fine.setText("Pending Fine : Rs. "+userProfile.getFine());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            //activity.getSupportActionBar().hide();
        }
    }
}
