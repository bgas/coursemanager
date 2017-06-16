package com.example.khaln.coursemanager.repo;

/**
 * Created by khaln on 5/20/17.
 */

public class MentorRepo {
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "mentors";
    public static final String ID = "_id";
    public static final String NAME = "mentorName";
    public static final String PHONE = "mentorEmail";
    public static final String EMAIL = "mentorPhone";

    public static final String[] COLUMNS =
            {ID, NAME, PHONE, EMAIL};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                PHONE + " TEXT default CURRENT_TIMESTAMP," +
                EMAIL + " TEXT" +
                ")";
    }
}
