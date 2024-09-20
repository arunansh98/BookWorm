package com.example.bookworm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class PastIssues extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PastIssues_Adapter past_adapter;
    private FirebaseAuth mAuth;
    private ArrayList<String> OrderNumberList;
    private ArrayList<ArrayList<String>> OrderList;
    private ArrayList<String> BookNameList;
    private DatabaseReference databaseReference;
    String orderNum;
    String User="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_issues2);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        //recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        past_issues();
    }
    public void past_issues()
    {
        getUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Order List").child(User);
        OrderNumberList =new ArrayList<String>();
        Toast.makeText(PastIssues.this, User, Toast.LENGTH_LONG).show();
        OrderList=new ArrayList<ArrayList<String>>();
        BookNameList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    //OrderList.add(NameList);
                    //Toast.makeText(getActivity(),NameList.get(0),Toast.LENGTH_LONG).show();
                    ArrayList<String> td=(ArrayList<String>)snapshot.getValue();
                    Toast.makeText(PastIssues.this, td.get(0), Toast.LENGTH_LONG).show();
                    OrderList.add(td);
                    OrderNumberList.add(snapshot.getKey());
                }
//                Toast.makeText(PastIssues.this, OrderList.get(0).get(0), Toast.LENGTH_LONG).show();
                past_adapter= new PastIssues_Adapter(PastIssues.this,OrderList,OrderNumberList);
                recyclerView.setAdapter(past_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getUser()
    {
        String email = mAuth.getCurrentUser().getEmail();
        orderNum = orderNumber();
        int i = 0;
        while (!email.substring(i, i + 1).equals("@")) {
            User = User + email.substring(i, i + 1);
            i = i + 1;
        }
    }

    private String orderNumber() {
        Random random = new Random();
        String lastFour = Long.toString(random.nextInt(10000));
        Calendar c = Calendar.getInstance();
        String firstEight = Integer.toString(c.get(Calendar.YEAR)) + Integer.toString(c.get(Calendar.MONTH) + 1) + Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        return firstEight + lastFour;
    }
}
