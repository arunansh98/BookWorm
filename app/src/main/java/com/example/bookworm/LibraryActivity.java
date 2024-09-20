package com.example.bookworm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;


public class LibraryActivity extends AppCompatActivity {
    ImageView logo;
    CountDownTimer count;
    boolean connected = false;
    ProgressBar progress;
    private Button button;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor;
    int a=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        button=(Button) findViewById(R.id.button);
        //prefs = getSharedPreferences("com.example.bookworm", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("com.example.bookworm", MODE_PRIVATE);
getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
     //   checkFirstRun();
        button.setVisibility(View.INVISIBLE);
        logo=findViewById(R.id.Logo);
        progress=(ProgressBar) findViewById(R.id.progress);

        logoTimer();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void button(View view)
    {
        if(!isNetworkAvailable())
        {
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
        else
        {  button.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
            timer();
        }
    }
    public void timer()

    {  progress.setVisibility(View.VISIBLE);
        count = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent;
                if(isNetworkAvailable()) {
                //checkFirstRun();
                    if (SaveSharedPreference.getUserName(LibraryActivity.this).length() == 0||a==1 ) {
                        intent = new Intent(LibraryActivity.this, welcomeActivity.class);

                    } else {
                        intent = new Intent(LibraryActivity.this, HomeActivity.class).putExtra("message","from library activity");
                    }
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        this.finishAffinity();
        super.onBackPressed();
    }
    public void logoTimer()
    { new CountDownTimer(800,100)
    {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            logo.animate().translationYBy(-500).setDuration(1200).withEndAction(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.VISIBLE);
                    timer();
                }
            }); }
    }.start();

    }
    private boolean checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;
        Intent intent;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return false;

        } else if (savedVersionCode == DOESNT_EXIST) {
            intent = new Intent(LibraryActivity.this, welcomeActivity.class);
            // TODO This is a new install (or the user cleared the shared preferences)
            startActivity(intent);
            return true;

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            return true;
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPreferences.getBoolean("firstRun", true)) {
            //You can perform anything over here. This will call only first time
            editor = sharedPreferences.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
            a=1;

        }
    }
}