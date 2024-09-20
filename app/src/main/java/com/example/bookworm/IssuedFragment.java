package com.example.bookworm;


import android.app.ActionBar;
import android.app.FragmentBreadCrumbs;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.bookworm.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class IssuedFragment extends Fragment {

    private ArrayList<Upload> up;
    private ArrayList<String> OrderNumberList;
    private ArrayList<ArrayList<String>> OrderList;
    private ArrayList<String> requested;
    private ArrayList<String> ISBN;
    private ArrayList<String> BookName;
    private RecyclerView recyclerView;
    private IssuedAdpater adapter;
    String orderNum;
    private PastIssues_Adapter past_adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private mehdi.sakout.fancybuttons.FancyButton issueButton;
    FirebaseDatabase database;
    private DatabaseReference rootRef;
    private TextView emptyText;
    private int notificationId = 1;
    private String channelId = "channel-01";
    private String channelName = "Channel Name";
    String User;
    int importance = NotificationManager.IMPORTANCE_HIGH;
    private NotificationCompat.Builder notification;
    private static final int uniqueID = 6169;
    ImageView empty;
    View rootView;
    ArrayList<String> BookNameList;
    public IssuedFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_issued, container, false);
        empty = (ImageView) rootView.findViewById(R.id.empty);
        setHasOptionsMenu(true);
        loadData();
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseDatabase.getInstance();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        notification = new NotificationCompat.Builder(getActivity(), channelId);
        notification.setAutoCancel(true);
        rootRef = database.getReference("Issue List");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        fragment_issued();
        return rootView;

    }

    public void fragment_issued() {
        adapter = new IssuedAdpater(getActivity(), up, rootView);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        issueButton = (mehdi.sakout.fancybuttons.FancyButton) rootView.findViewById(R.id.issueAll);
        visible();
        if (up.size() == 0) {
            invisible();
        }
        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Request")
                        .setMessage("Are you sure?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                invisible();
                                issued();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
            }
        });

    }
    public void getUser()
    {
        String email = mAuth.getCurrentUser().getEmail();
        User = "";
        orderNum = orderNumber();
        int i = 0;
        while (!email.substring(i, i + 1).equals("@")) {
            User = User + email.substring(i, i + 1);
            i = i + 1;
        }
    }

    public void issued() {
        getUser();
        ISBN = new ArrayList<>();
        BookName = new ArrayList<>();
        int i;
        for (i = 0; i < up.size(); i++) { //ISBN.add(Long.toString(up.get(i).getISBN()));
            BookName.add(up.get(i).getName());
            ISBN.add(up.get(i).getISBN());
        }
        databaseReference.child("Order List").child(User).child(orderNum).setValue(BookName);
        databaseReference.child("Users").child(User).child("currentOrder").setValue(orderNum);
        decQuantity();
        up.clear();
        //requested.clear();
        saveData();
        adapter.notifyDataSetChanged();
        issueButton.setVisibility(View.INVISIBLE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        applyNotification(orderNum);
    }

    public void applyNotification(String orderNum) {
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String title = "Issue no. " + orderNum + " successful";
        String description = "Kindly visit the Library and show this issue number to pick up your books.";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, title, importance);
            manager.createNotificationChannel(mChannel);
        }
        notification.setSmallIcon(R.drawable.logo)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Issue Number : " + orderNum)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your requested books have been issued. " + description))
                .build();
        Intent intent = new Intent(getActivity(), LibraryActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        manager.notify(uniqueID, notification.build());
        Toast.makeText(getActivity(), "Successful!", Toast.LENGTH_LONG).show();

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json1 = gson.toJson(up);
        String json2 = gson.toJson(requested);
        editor.putString("requested object list", json1);
        editor.putString("requested string list", json2);
        editor.apply();

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("requested object list", null);
        Type type = new TypeToken<ArrayList<Upload>>() {
        }.getType();
        up = gson.fromJson(json, type);
        if (up == null) {
            up = new ArrayList<>();
        }
        String json1 = sharedPreferences.getString("requested string list", null);
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        requested = gson.fromJson(json1, type1);
        if (requested == null) {
            requested = new ArrayList<>();
        }
    }

    private String orderNumber() {
        Random random = new Random();
        String lastFour = Long.toString(random.nextInt(10000));
        Calendar c = Calendar.getInstance();
        String firstEight = Integer.toString(c.get(Calendar.YEAR)) + Integer.toString(c.get(Calendar.MONTH) + 1) + Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        return firstEight + lastFour;
    }

    public void decQuantity() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BOOKS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Upload upload = snapshot.getValue(Upload.class);
                    if (ISBN.contains(snapshot.getKey())) {
                        Integer x = Integer.parseInt(upload.getQuantity());
                        x = x - 1;
                        // Toast.makeText(getActivity(),"Happy Reading!",Toast.LENGTH_LONG).show();
                        databaseReference.child(snapshot.getKey()).child("Quantity").setValue(Integer.toString(x));
                        ISBN.remove(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void invisible() {
        issueButton.setEnabled(false);
        issueButton.setText("Time to get some!");
        empty.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void visible() {
        issueButton.setEnabled(true);
        empty.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            //activity.getSupportActionBar().hide();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.itemselected, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pastIssues:
                Intent i =new Intent(getActivity(),PastIssues.class);
                //i.putExtra("Fragment",IssuedFragment.uniqueID);
                startActivity(i);
                //issueButton.setEnabled(false);
                //issueButton.setVisibility(View.INVISIBLE);
                //recyclerView.setAdapter(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
