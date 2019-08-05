package com.momodupi.piggybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Node;

public class GridViewAdatper extends BaseAdapter {
    private Context mContext;
    private final String[] gridViewString;
    private final int[] gridViewImageId;

    public GridViewAdatper(Context context, String[] gridViewString, int[] gridViewImageId) {
        mContext = context;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
    }

    @Override
    public int getCount() {
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View gridView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.type_grid, null);
        } else {
            gridView = convertView;
        }
        TextView textView = (TextView) gridView.findViewById(R.id.gridtext);
        textView.setText(gridViewString[pos]);

        ImageView imageView = (ImageView) gridView.findViewById(R.id.gridimage);
        imageView.setImageResource(gridViewImageId[pos]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return gridView;
    }
}
