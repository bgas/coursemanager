package com.example.khaln.coursemanager.repo;

/**
 * Created by khaln on 5/17/17.
 */

public class NoteRepo {
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "notes";
    public static final String ID = "_id";
    public static final String ASSESSMENT_ID = "assessmentID";
    public static final String TITLE = "noteTitle";
    public static final String CREATED = "noteCreated";
    public static final String TEXT = "noteText";
    public static final String PHOTO = "notePhoto";

    public static final String[] COLUMNS =
            {ID, ASSESSMENT_ID, TITLE, CREATED, TEXT, PHOTO};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ASSESSMENT_ID + " INTEGER, " +
                TITLE + " TEXT, " +
                CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                TEXT + " TEXT, " +
                PHOTO + " STRING " +
                ")";
    }
}
