package com.example.bookworm;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.Serializable;

import com.example.bookworm.R;
import com.example.bookworm.ViewBookActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class All_Book_Adapter extends RecyclerView.Adapter<All_Book_Adapter.ViewHolder> implements Filterable
{
    private Context context;
    public RecyclerView recyclerView;
    private List<Upload> uploads;
    private List<Upload> uploadsFull;

    All_Book_Adapter(Context context, List<Upload> uploads)
    {
        this.context= context;
        this.uploads= uploads;
        uploadsFull=new ArrayList<>(uploads);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater= LayoutInflater.from(viewGroup.getContext());
        View view;
        view= inflater.from(context).inflate(R.layout.list_item_all_books,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        Upload upload= uploads.get(i);
        viewHolder.u=upload;
        Picasso.get().load(upload.getURL()).fit().centerInside().into(viewHolder.imageView);
    }



    @Override
    public int getItemCount() {
        return uploads.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter=new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Upload> filteredList=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filteredList.addAll(uploadsFull);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(Upload item:uploadsFull)
                {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            uploads.clear();
            uploads.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Upload u;
        //ShimmerFrameLayout mShimmerViewContainer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView= (ImageView)itemView.findViewById(R.id.recycle_image);
           // mShimmerViewContainer =(ShimmerFrameLayout) itemView.findViewById(R.id.shimmer_view_container);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptionsCompat v=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)itemView.getContext(),imageView,"imageTransition");
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), ViewBookActivity.class).putExtra("Object",u),v.toBundle());
                }
            });
        }
    }
}