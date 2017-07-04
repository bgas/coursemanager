package com.example.khaln.coursemanager.repo;

import java.util.ArrayList;

/**
 * Created by khaln on 5/20/17.
 */

public class MentorRepo {
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "mentors";
    public static final String ID = "_id";
    public static final String TITLE = "mentorName";
    public static final String EMAIL = "mentorEmail";
    public static final String PHONE = "mentorPhone";

    public static final String[] COLUMNS =
            {ID, TITLE, PHONE, EMAIL};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                PHONE + " TEXT default CURRENT_TIMESTAMP," +
                EMAIL + " TEXT" +
                ")";
    }
    public static ArrayList createArrayList(){
        return new ArrayList();
    }
}