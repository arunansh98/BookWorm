package com.example.bookworm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    EditText forgotEmail;
    Button reset;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        forgotEmail= (EditText)findViewById(R.id.forgotEmail);
        reset= (Button)findViewById(R.id.resetPassword);
        firebaseAuth= FirebaseAuth.getInstance();
    }
    public void resetPassword(View view)
    {
        String mail= forgotEmail.getText().toString();
        if(TextUtils.isEmpty(mail))
        {
            forgotEmail.setError("Email is required");
        }
        else {
            firebaseAuth.sendPasswordResetEmail(forgotEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Password sent to your email", Toast.LENGTH_LONG).show();
                        reset.setClickable(false);
                        reset.setText("Mail sent!");
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void goBack(View view)
    {
        Intent intent= new Intent(getApplicationContext(), welcomeActivity.class);
        startActivity(intent);
    }
}
