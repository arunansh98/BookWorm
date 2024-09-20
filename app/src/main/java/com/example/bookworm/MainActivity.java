package com.example.bookworm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    mehdi.sakout.fancybuttons.FancyButton btn_signup,btn_signin;
    RelativeLayout l1, l2;
    EditText etname,etphone,etemail,etpass,etUsername,etpassword;
    FirebaseDatabase database;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        sp= getSharedPreferences("login",MODE_PRIVATE);
        FirebaseApp.initializeApp(this);
        l1= (RelativeLayout)findViewById(R.id.l1);
        l2= (RelativeLayout)findViewById(R.id.l2);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        Intent intent= getIntent();
        int i= intent.getIntExtra("type",1);
        if(i==2)
        {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);
        }
        else if(i==1)
        {
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        }
        //</editor-fold>
        etname= (EditText)findViewById(R.id.etname);
        etphone=(EditText)findViewById(R.id.etphone);
        etemail=(EditText)findViewById(R.id.etemail);
        etpass= (EditText)findViewById(R.id.etpass);
        etUsername= (EditText)findViewById(R.id.etUsername);
        etpassword= (EditText)findViewById(R.id.etpassword);
        database= FirebaseDatabase.getInstance();
        rootRef= database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        users= new Users();
        btn_signup= findViewById(R.id.btn_signup);
        btn_signin= findViewById(R.id.btn_signin);

        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();

        }
    private void setValues()
    {
        users.setName(etname.getText().toString());
        users.setPhone(etphone.getText().toString());
        users.setEmail(etemail.getText().toString());
        users.setPassword(etpass.getText().toString());
        users.setFine("0");
        users.setCurrentOrder("");

    }

    public void hide(View view)
    {
        InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void signup(View view)
    {

        String name= etname.getText().toString().trim();
        String phone= etphone.getText().toString().trim();
        final String mail= etemail.getText().toString().trim();
        String password= etpass.getText().toString().trim();
        if(TextUtils.isEmpty(name))
        {
            etname.setError( "Name is required!" );
        }
        else if(TextUtils.isEmpty(phone))
        {
            etphone.setError( "Phone is required!" );
        }
        else if(TextUtils.isEmpty(mail))
        {
            etemail.setError( "Email is required!" );
        }
        else if(TextUtils.isEmpty(password))
        {
            etpass.setError( "Password is required!" );
        }
        else {
            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if(task1.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Sign Up Successful! Please verify the mail sent to you to sign in.", Toast.LENGTH_LONG).show();
                                            l1.setVisibility(View.VISIBLE);
                                            l2.setVisibility(View.GONE);
                                            setValues();
                                            String roll = etemail.getText().toString().substring(0, 7);
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                                                rootRef.child("Users").child(roll).setValue(users);
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(MainActivity.this,task1.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });


                    } else {
                        Exception exception = task.getException();
                        TastyToast.makeText(MainActivity.this, exception.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    }
                }
            });
        }
    }

    public void signin(View view)
    {
        final String username= etUsername.getText().toString().trim();
        String password= etpassword.getText().toString().trim();

        if(TextUtils.isEmpty(username))
        {
            etUsername.setError("Email is required");
        }
        else if(TextUtils.isEmpty(password))
        {
            etpassword.setError("Password is required");
        }
        else {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        if(mAuth.getCurrentUser().isEmailVerified()) {
                            TastyToast.makeText(MainActivity.this, "Successful!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            SaveSharedPreference.setUserName(MainActivity.this, username);
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Cannot Login Until Email Verified.",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        TastyToast.makeText(MainActivity.this, task.getException().getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    }
                }
            });

        }
    }
    public void forgot(View view)
    {
        Intent intent= new Intent(getApplicationContext(),ForgotActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent= new Intent(getApplicationContext(),welcomeActivity.class);
        startActivity(intent);
    }
}