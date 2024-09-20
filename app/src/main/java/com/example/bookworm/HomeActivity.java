package com.example.bookworm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bookworm.AllBookFragment;
import com.example.bookworm.IssuedFragment;
import com.example.bookworm.UserFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class HomeActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView main_nav;
    private AllBookFragment allBookFragment;
    private UserFragment userFragment;
    private IssuedFragment issuedFragment;
    private ShimmerFrameLayout mShimmerViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        //  mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        allBookFragment = new AllBookFragment();
        userFragment = new UserFragment();
        issuedFragment = new IssuedFragment();
        main_nav = (BottomNavigationView) findViewById(R.id.main_nav);
            setFragment(allBookFragment);
        main_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.all:
                        setFragment(allBookFragment);
                        return true;

                    case R.id.issued:
                        setFragment(issuedFragment);
                        return true;

                    case R.id.user:
                        setFragment(userFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                String token="";
                if(task.isSuccessful())
                {
                    token= task.getResult().getToken();

                }
                else
                {

                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                String User = "";
                int i = 0;
                String email = mAuth.getCurrentUser().getEmail();
                while (!email.substring(i, i + 1).equals("@")) {
                    User = User + email.substring(i, i + 1);
                    i = i + 1;
                }
                ref.child("Users").child(User).child("token").setValue(token);
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);


    }
/*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) searchItem.getActionView();
        searchView.setMaxWidth(1700);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;

            }
        });
        return true;
    }
*/


}