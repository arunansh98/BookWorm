package com.example.bookworm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class PastIssues_Adapter extends RecyclerView.Adapter<PastIssues_Adapter.PastHolder>{

    private Context context;
    private ArrayList<ArrayList<String>> past_list;
    ArrayList<String> numberList;
    public PastIssues_Adapter(Context context, ArrayList<ArrayList<String>> past_list,ArrayList<String> numberList) {
        this.context = context;
        this.past_list = past_list;
        this.numberList=numberList;
    }

    @NonNull
    @Override
    public PastHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.list_past_issues,viewGroup,false);
        return new PastHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastHolder pastHolder, int i)
    {
        pastHolder.ordernumber.setText("Order Number : "+numberList.get(i));
        ArrayList<String> bookList=past_list.get(i);
        //Toast.makeText(context,bookList.get(i),Toast.LENGTH_LONG).show();
        pastHolder.listView.setAdapter(new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,bookList));
    }



    @Override
    public int getItemCount() {
        return numberList.size();
    }

    public class PastHolder extends RecyclerView.ViewHolder {

        TextView ordernumber;
        ListView listView;
        public PastHolder(@NonNull View itemView)
        {
            super(itemView);
            ordernumber= (TextView)itemView.findViewById(R.id.orderNumber);
            listView=(ListView)itemView.findViewById(R.id.listView);
        }
    }
}
