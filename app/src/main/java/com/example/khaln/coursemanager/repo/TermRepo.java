package com.example.khaln.coursemanager.repo;

/**
 * Created by khaln on 5/20/17.
 */

public class TermRepo {

    //Constants for identifying table and columns
    public static final String TABLE_NAME = "terms";
    public static final String ID = "_id";
    public static final String TITLE = "termTitle";
    public static final String START = "termStarted";
    public static final String END = "termEnded";

    public static final String[] COLUMNS =
            {ID, TITLE, START, END};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                START + " TEXT default CURRENT_TIMESTAMP," +
                END + " TEXT" +
                ")";
    }

}
