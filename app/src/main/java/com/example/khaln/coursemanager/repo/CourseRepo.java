package com.example.khaln.coursemanager.repo;

/**
 * Created by khaln on 5/17/17.
 */

public class CourseRepo{
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "courses";
    public static final String ID = "_id";
    public static final String TITLE = "courseTitle";
    public static final String MENTOR_ID = "mentorID";
    public static final String TERM_ID = "termID";
    public static final String START = "courseStarted";
    public static final String END = "courseEnded";
    public static final String STATUS = "courseStatus";

    public static final String[] COLUMNS =
            {ID, TITLE, MENTOR_ID, TERM_ID, START, END, STATUS};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                MENTOR_ID + " INTEGER, " +
                TERM_ID + " INTEGER, " +
                START + " TEXT, " +
                END + " TEXT, " +
                STATUS + " TEXT" +
                ")";
    }
//    CourseRepo(){}
}
