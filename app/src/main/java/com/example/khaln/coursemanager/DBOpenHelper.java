package com.example.khaln.coursemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.MentorRepo;
import com.example.khaln.coursemanager.repo.NoteRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

/**
 * Created by khaln on 5/11/17.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "coursemanager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DBOpenHelper.class.getSimpleName();

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(NoteRepo.createTable());
        db.execSQL(AssessmentRepo.createTable());
        db.execSQL(CourseRepo.createTable());
        db.execSQL(TermRepo.createTable());
        db.execSQL(MentorRepo.createTable());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + NoteRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AssessmentRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CourseRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TermRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MentorRepo.TABLE_NAME);
        onCreate(db);
    }
}
