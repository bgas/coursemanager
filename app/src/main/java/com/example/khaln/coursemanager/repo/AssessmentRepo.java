package com.example.khaln.coursemanager.repo;

/**
 * Created by khaln on 5/17/17.
 */

public class AssessmentRepo{
    //Constants for identifying table and columns
    public static final String TABLE_NAME = "assessments";
    public static final String ID = "_id";
    public static final String COURSE_ID = "courseID";
    public static final String TITLE = "assessmentTitle";
    public static final String END = "assessmentEnded";


    public static final String[] COLUMNS =
            {ID, COURSE_ID, TITLE, END};

    //SQL to create table
    public static final String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_ID + " INTEGER, " +
                TITLE + " TEXT, " +
                END + " TEXT" +
                ")";
    }
}
