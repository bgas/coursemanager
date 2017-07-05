package com.example.khaln.coursemanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by khaln on 5/25/17.
 */

public class ManagerCursorAdapter extends CursorAdapter {
    String[] from; //[0]Text column int, [1]id column int, [2] optional item to give "Selected" status
//    int[] where;
    int[] to;
    int layout;
//    int i =0;
    public ManagerCursorAdapter(Context context, int layout, Cursor c, String[] from, /*int[] where,*/ int[] to, int flags) {
        super(context, c, flags);
        this.to = to;
        this.from = from;
        this.layout = layout;
    }

    public ManagerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //get text and id values
        String itemText = cursor.getString(cursor.getColumnIndex(from[0]));
        String itemId = "" + cursor.getInt(cursor.getColumnIndex(from[1]));

        //if itemText contains newline character insert ...
        final String titleText = (-1 != itemText.indexOf(10)) ? itemText.substring(0,itemText.indexOf(10))+ "..." : itemText;

        //pin values to view item to be passed as extra
        view.setTag(R.string.item_id_tag, itemId);
        view.setTag(R.string.item_title_tag, titleText);
        if (from.length > 2){
            view.setBackgroundColor(Color.WHITE);
            if (from[2].equals(itemId)){
                view.setBackgroundColor(Color.CYAN);
            }
        }

        //bind title to view
        TextView tv = (TextView) view.findViewById(to[0]);
        tv.setText(titleText);
    }
}
