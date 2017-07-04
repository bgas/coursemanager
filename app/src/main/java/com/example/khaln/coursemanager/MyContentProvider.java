package com.example.khaln.coursemanager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.MentorRepo;
import com.example.khaln.coursemanager.repo.NoteRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

import java.net.URI;
import java.util.Arrays;

/**
 * Created by khaln on 5/11/17.
 */

public class MyContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.khaln.coursemanager.courseprovider";
    static final Uri TERM_URI = Uri.parse("content://" + AUTHORITY + "/" + TermRepo.TABLE_NAME);
    static final Uri COURSE_URI = Uri.parse("content://" + AUTHORITY + "/" + CourseRepo.TABLE_NAME);
    static final Uri ASSESSMENT_URI = Uri.parse("content://" + AUTHORITY + "/" + AssessmentRepo.TABLE_NAME);
    static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/" + NoteRepo.TABLE_NAME);
    static final Uri MENTOR_URI = Uri.parse("content://" + AUTHORITY + "/" + MentorRepo.TABLE_NAME);



    public static Uri getTableUri(String repoName) {
        Uri result = Uri.parse("content://" + AUTHORITY + "/" + repoName);
        return result;
    }

    public static final String CONTENT_ITEM_TYPE = "term";
/*
    private static final String TERM_PATH= "terms";
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERM_PATH);
    private static final String COURSE_PATH= "coursess";
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_PATH);
    private static final String ASSESSMENT_PATH= "assessment";
    public static final Uri ASSESSSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_PATH);
    private static final String NOTE_PATH= "terms";
    public static final Uri NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTE_PATH);
*/
    // Constant to identify the requested operation
    private static final int TERM = 1;
    private static final int TERM_ID = 2;
    private static final int COURSE = 3;
    private static final int COURSE_ID = 4;
    private static final int ASSESSMENT = 5;
    private static final int ASSESSMENT_ID = 6;
    private static final int NOTE = 7;
    private static final int NOTE_ID = 8;
    private static final int MENTOR = 9;
    private static final int MENTOR_ID = 10;

    private SQLiteDatabase database;

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // addURI() calls for multi row and single-row
        sUriMatcher.addURI(AUTHORITY, TermRepo.TABLE_NAME, TERM);
        sUriMatcher.addURI(AUTHORITY, TermRepo.TABLE_NAME + "/#", TERM_ID);
        sUriMatcher.addURI(AUTHORITY, CourseRepo.TABLE_NAME, COURSE);
        sUriMatcher.addURI(AUTHORITY, CourseRepo.TABLE_NAME + "/#", COURSE_ID);
        sUriMatcher.addURI(AUTHORITY, AssessmentRepo.TABLE_NAME, ASSESSMENT);
        sUriMatcher.addURI(AUTHORITY, AssessmentRepo.TABLE_NAME + "/#", ASSESSMENT_ID);
        sUriMatcher.addURI(AUTHORITY, NoteRepo.TABLE_NAME, NOTE);
        sUriMatcher.addURI(AUTHORITY, NoteRepo.TABLE_NAME + "/#", NOTE_ID);
        sUriMatcher.addURI(AUTHORITY, MentorRepo.TABLE_NAME, MENTOR);
        sUriMatcher.addURI(AUTHORITY, MentorRepo.TABLE_NAME + "/#", MENTOR_ID);
    }

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //select
        String table;
        String[] columns;
        switch (sUriMatcher.match(uri)) {
            case TERM:
                table = TermRepo.TABLE_NAME;
                columns = TermRepo.COLUMNS;
                break;
            case TERM_ID:
                table = TermRepo.TABLE_NAME;
                columns = TermRepo.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case COURSE:
                table = CourseRepo.TABLE_NAME;
                columns = CourseRepo.COLUMNS;
                break;
            case COURSE_ID:
                table = CourseRepo.TABLE_NAME;
                columns = CourseRepo.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case ASSESSMENT:
                table = AssessmentRepo.TABLE_NAME;
                columns = AssessmentRepo.COLUMNS;
                break;
            case ASSESSMENT_ID:
                table = AssessmentRepo.TABLE_NAME;
                columns = AssessmentRepo.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case NOTE:
                table = NoteRepo.TABLE_NAME;
                columns = NoteRepo.COLUMNS;
                break;
            case NOTE_ID:
                table = NoteRepo.TABLE_NAME;
                columns = NoteRepo.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case MENTOR:
                table = MentorRepo.TABLE_NAME;
                columns = MentorRepo.COLUMNS;
                break;
            case MENTOR_ID:
                table = MentorRepo.TABLE_NAME;
                columns = MentorRepo.COLUMNS;
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default: throw new SQLException("Failed to query " + uri);
        }
        //Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
        String columnStretch = "";
        for (String column : columns){ columnStretch += column + ", "; }
        return database.query(table, columns, selection, null, null, null, null);
    }

/*
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.ALL_COLUMNS, selection, null, null, null, DBOpenHelper.TERM_START_DATE + " DESC" );
    }
    */

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String table;
        switch (sUriMatcher.match(uri)) {
            case TERM:
                table = TermRepo.TABLE_NAME;
                break;
            case COURSE:
                table = CourseRepo.TABLE_NAME;
                break;
            case MENTOR:
                table = MentorRepo.TABLE_NAME;
                break;
            case ASSESSMENT:
                table = AssessmentRepo.TABLE_NAME;
                break;
            case NOTE:
                table = NoteRepo.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to insert " + uri);
        }

        long id = database.insert(table, null, values);
        return Uri.parse(table + "/" + id);
    }



    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table;
        switch (sUriMatcher.match(uri)) {
            case TERM:
                table = TermRepo.TABLE_NAME;
                break;
            case COURSE:
                table = CourseRepo.TABLE_NAME;
                break;
            case ASSESSMENT:
                table = AssessmentRepo.TABLE_NAME;
                break;
            case NOTE:
                table = NoteRepo.TABLE_NAME;
                break;
            case MENTOR:
                table = MentorRepo.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to delete " + uri);
        }
        return database.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table;
        switch (sUriMatcher.match(uri)) {
            case TERM:
                table = TermRepo.TABLE_NAME;
                break;
            case COURSE:
                table = CourseRepo.TABLE_NAME;
                break;
            case ASSESSMENT:
                table = AssessmentRepo.TABLE_NAME;
                break;
            case NOTE:
                table = NoteRepo.TABLE_NAME;
                break;
            case MENTOR:
                table = MentorRepo.TABLE_NAME;
                break;
            default: throw new SQLException("Failed to update: " + uri +" case: "+sUriMatcher.match(uri));
        }
        return database.update(table, values, selection, selectionArgs);
    }
}
