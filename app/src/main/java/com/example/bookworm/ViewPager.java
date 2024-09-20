package com.example.bookworm;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookworm.Model;
import com.example.bookworm.TabsViewItem;
import com.example.bookworm.R;
import com.hold1.pagertabsindicator.TabViewProvider;

import java.util.List;

public class ViewPager extends PagerAdapter implements TabViewProvider.CustomView {

    List<Model> models;
    Context context;

//    @Override
//    public Uri getImageUri(int i) {
//        return null;
//    }
//
//    @Override
//    public int getImageResourceId(int i) {
//
//        return models.get(i).getId();
//    }

    public ViewPager(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewGroup)container).removeView((View)object);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return "Page "+(position+1);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        LayoutInflater inflater= LayoutInflater.from(context);
        View itemView= inflater.inflate(R.layout.layour_item,container,false);
        TextView textView= (TextView)itemView.findViewById(R.id.textVew1);
        textView.setText(models.get(position).getTitle());
        container.addView(itemView);
        return itemView;

    }

    @Override
    public View getView(int i) {
        return new TabsViewItem(context, models.get(i).getTitle(),models.get(i).getId(),0xFF363636,0xFFFF0000);
    }
}