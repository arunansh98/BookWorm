package com.example.bookworm;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookworm.Upload;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewBookActivity extends AppCompatActivity implements Serializable {

    private ImageView imageView;
    private TextView bookName,author;
    mehdi.sakout.fancybuttons.FancyButton issueButton;
    private String quantity;
    private String title;
    private TextView description;
    private FirebaseAuth mAuth;
    private ArrayList<String> requested;
    private ProgressBar loadingCover;
    private ArrayList<String> ISBN;
    private DatabaseReference databaseReference;
    private DatabaseReference d;
    private ArrayList<Upload> up;
    int a;
    Upload u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);
        loadingCover= (ProgressBar)findViewById(R.id.loadingCover);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadData();
        a=0;
        imageView = (ImageView) findViewById(R.id.bookCover);
        description= (TextView) findViewById(R.id.desc);
        bookName = (TextView) findViewById(R.id.bookTitle);
        author = (TextView) findViewById(R.id.author);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        issueButton = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.issueButton);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        u = (Upload) getIntent().getSerializableExtra("Object");
        title = u.getName();
        String url = u.getURL();
        quantity = u.getQuantity();
        checkIfRequested();
        ISBN = new ArrayList<>();
        bookName.setText(title);
        description.setText(u.getDescription());
        author.setText("By "+u.getAuthor());
        Picasso.get().load(url).fit().placeholder(R.drawable.loading).centerInside().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                loadingCover.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
       /* Glide.with(this)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.loading).centerInside())
                .into(imageView);
*/
    }

    public void issue(View view) {
        if (view.getTag().equals("1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Book Selected")
                    .setMessage("Do you want to issue this book?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            issueButton.setBackgroundColor(ContextCompat.getColor(ViewBookActivity.this, R.color.darkGrey));
                            issueButton.setFocusBackgroundColor(ContextCompat.getColor(ViewBookActivity.this, R.color.lightGrey));
                            issueButton.setText("Requested");
                            issueButton.setTag("0");
                            requested.add(title);
                            up.add(u);

                            saveData();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).show();
        } else if (view.getTag().equals("0")) {
            Toast.makeText(ViewBookActivity.this, "Book already issued", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ViewBookActivity.this, "Currently out of stock", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkIfRequested() {
        ArrayList list = new ArrayList();
        if (quantity.equals("0")) {
            issueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey));
            issueButton.setFocusBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey));
            issueButton.setText("Unavailable");
            issueButton.setTag("2");
        } else if (requested.contains(title) || isIssued()) {
            if(a==0)
            req();
        } else {
            issueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonGreen));
            issueButton.setFocusBackgroundColor(ContextCompat.getColor(this, R.color.ButtonPressedGreen));
            issueButton.setText("Issue Book");
            issueButton.setTag("1");
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(requested);
        String json1 = gson.toJson(up);
        editor.putString("requested string list", json);
        editor.putString("requested object list", json1);
        editor.apply();

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("requested string list", null);
        String json1 = sharedPreferences.getString("requested object list", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Type type1 = new TypeToken<ArrayList<Upload>>() {
        }.getType();
        requested = gson.fromJson(json, type);
        up = gson.fromJson(json1, type1);
        if (up == null) {
            up = new ArrayList<>();
        }
        if (requested == null) {
            requested = new ArrayList<>();
        }

    }

    public boolean isIssued() {
        a=1;
        String email = mAuth.getCurrentUser().getEmail();
        String User = "";
        int i = 0;
        while (!email.substring(i, i + 1).equals("@")) {
            User = User + email.substring(i, i + 1);
            i = i + 1;
        }
        //Toast.makeText(this,User,Toast.LENGTH_LONG).show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order List").child(User);
        final String o = User;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    d = FirebaseDatabase.getInstance().getReference().child("Order List").child(o).child(key);
                    d.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ISBN.add(snapshot.getValue(String.class));

                                //Toast.makeText(getApplicationContext(),snapshot.getValue(String.class),Toast.LENGTH_LONG).show();

                            }
                            if (ISBN.contains(title)) {
                                req();
                                //Toast.makeText(getApplicationContext(),u.getISBN(),Toast.LENGTH_LONG).show();
                            }}

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
            }
        });
        //Toast.makeText(this,ISBN.get(0),Toast.LENGTH_LONG).show();
        if (issueButton.getTag().equals("0"))
            return true;
        else
            return false;
    }


    void req() {
        issueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey));
        issueButton.setFocusBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey));
        issueButton.setText("Requested");
        issueButton.setTag("0");
}
}