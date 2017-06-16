package com.example.khaln.coursemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by khaln on 5/25/17.
 */

public class ManagerCursorAdapter extends CursorAdapter {
    String[] from;
//    int[] where;
    int[] to;
    int layout;
    public ManagerCursorAdapter(Context context, int layout, Cursor c, String[] from, /*int[] where,*/ int[] to, int flags) {
        super(context, c, flags);
        this.to = to;
        this.from = from;
        this.layout = layout;
//        this.where = where;
//        Log.d(this.getClass().toString(), Arrays.toString(where));
        //Log.d(this.getClass().toString(), "constructor cursor dump: " + DatabaseUtils.dumpCursorToString(c));
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
        final String itemId = "" + cursor.getInt(cursor.getColumnIndex(from[1]));
        //if itemText contains newline character insert ...
        final String titleText = (-1 != itemText.indexOf(10)) ? itemText.substring(0,itemText.indexOf(10))+ "..." : itemText;
        //pin values to view item to be passed as extra
        view.setTag(new HashMap<String, String>(){{
            put("id",itemId);
            put("titleText",titleText);
        }});

        //bind title to view
        TextView tv = (TextView) view.findViewById(to[0]);
        tv.setText(titleText);
    }
}
