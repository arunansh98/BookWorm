package com.example.bookworm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

public class IssuedAdpater extends RecyclerView.Adapter<IssuedAdpater.IssuedHolder>{

    private Context context;
    private List<Upload> issued_list;
    private ArrayList<Upload> up;
    private ArrayList<String> requested;
     View rootView;
    public IssuedAdpater(Context context, List<Upload> issued_list,View rootView) {
        this.context = context;
        this.issued_list = issued_list;
        this.rootView=rootView;
    }

    @NonNull
    @Override
    public IssuedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.list_issued_all_books,viewGroup,false);
        return new IssuedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuedHolder issuedHolder, int i)
    {
        final Upload details= issued_list.get(i);
        issuedHolder.name.setText(details.getName());
        String author= details.getAuthor();
        issuedHolder.author.setText(author);
        Picasso.get().load(details.getURL()).fit().centerInside().into(issuedHolder.imageView);
        issuedHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Cancel Issue Request")
                        .setMessage("Do you wish to cancel the issue request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(details);
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

    public void removeItem(Upload details)
            {
                mehdi.sakout.fancybuttons.FancyButton issuedButton;
                issuedButton=(mehdi.sakout.fancybuttons.FancyButton)rootView.findViewById(R.id.issueAll);
                int position= issued_list.indexOf(details);
                Gson gson= new Gson();
                SharedPreferences sharedPreferences= context.getSharedPreferences("sharedPreferences",MODE_PRIVATE);
                String json2=sharedPreferences.getString("requested string list",null);
                Type type2= new TypeToken<ArrayList<String>>() {}.getType();
                requested=gson.fromJson(json2,type2);
                requested.remove(issued_list.get(position).getName());
                issued_list.remove(position);
                notifyItemRemoved(position);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                String json1=gson.toJson(issued_list);
                if((requested.size()==0 || requested==null)||(issued_list.size()==0 || issued_list==null))
                {
                    requested = new ArrayList<>();
                    issuedButton.setEnabled(false);
                    issuedButton.setText("No issues here");
                    ImageView empty=(ImageView)rootView.findViewById(R.id.empty);
                    TextView emptyText=(TextView)rootView.findViewById(R.id.emptyText);
                    empty.setImageResource(R.drawable.empty);
                    emptyText.setText("Your list is empty");
                    empty.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.VISIBLE);

        }
        String json3=gson.toJson(requested);
        editor.putString("requested object list",json1);
        editor.putString("requested string list",json3);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return issued_list.size();
    }

    public class IssuedHolder extends RecyclerView.ViewHolder {

        ImageView imageView,remove;
        TextView name, author;
        public IssuedHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView= (ImageView)itemView.findViewById(R.id.recycle_image);
            name= (TextView)itemView.findViewById(R.id.name);
            author= (TextView)itemView.findViewById(R.id.authorName);
            remove= (ImageView)itemView.findViewById(R.id.removeBook);

        }
    }
}
