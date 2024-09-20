package com.example.bookworm;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookworm.All_Book_Adapter;
import com.example.bookworm.Upload;
import com.example.bookworm.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.GridLayoutManager.*;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class AllBookFragment extends Fragment {


    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private All_Book_Adapter adapter;
    private List<Upload> uploads;
    private com.victor.loading.book.BookLoading bookLoading;
    private TextView fetching;
  // private ShimmerFrameLayout mShimmerViewContainer;
    public AllBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_all_book, container, false);
        FirebaseApp.initializeApp(getActivity());
        recyclerView= (RecyclerView)rootView.findViewById(R.id.recyclerView);
        fetching= (TextView)rootView.findViewById(R.id.fetching);
        bookLoading= (com.victor.loading.book.BookLoading)rootView.findViewById(R.id.bookloading);
        bookLoading.setVisibility(View.VISIBLE);
        bookLoading.start();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        setHasOptionsMenu(true);
     //   mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        uploads= new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("BOOKS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Upload upload= snapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                if(getActivity()!=null)
                adapter= new All_Book_Adapter(getActivity(),uploads);
                recyclerView.setAdapter(adapter);
                bookLoading.setVisibility(View.GONE);
                fetching.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().show();
          //  mShimmerViewContainer.startShimmerAnimation();
        }
    }
    @Override
    public void onPause() {
        //mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
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
    }
}