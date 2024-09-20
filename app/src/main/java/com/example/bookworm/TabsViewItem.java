package com.example.bookworm;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookworm.R;
import com.hold1.pagertabsindicator.TabView;

import static com.hold1.pagertabsindicator.Util.getColorWithOpacity;
import static com.hold1.pagertabsindicator.Util.mixTwoColors;

public class TabsViewItem extends TabView {

    private TextView tabName;
    private ImageView tabIcon;
    private int activeColor, tabColor;

    public TabsViewItem(Context context, String title, int imageId, int tabColor, int activeColor)
    {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.custom_layout,this);
        tabName= findViewById(R.id.tab_name);
        tabIcon= findViewById(R.id.tab_icon);
        tabName.setText(title);
        tabIcon.setImageResource(imageId);
        this.tabColor= tabColor;
        this.activeColor= activeColor;
        this.tabIcon.setColorFilter(tabColor);
        this.tabName.setTextColor(tabColor);
    }

    @Override
    public void onOffset(float offset) {
        this.tabIcon.setColorFilter(getColorWithOpacity(activeColor,(int)(100*offset)));
        this.tabName.setTextColor(mixTwoColors(activeColor,tabColor,offset));
    }
}