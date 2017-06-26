package com.example.khaln.coursemanager;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khaln.coursemanager.model.Assessment;
import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.CourseRepo;

public class CourseActivity extends TermActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        /*Set Instance Variables*/
        intent = getIntent();
        repoId = intent.getStringExtra(CourseRepo.ID);
        repoTitle = intent.getStringExtra(CourseRepo.TITLE);
        repoTableName = CourseRepo.TABLE_NAME;
        uri = intent.getParcelableExtra(repoTableName);
        childRepoTitle = AssessmentRepo.TITLE;
        childRepoID = AssessmentRepo.ID;
        childRepoTableName = AssessmentRepo.TABLE_NAME;
        //childsParentId = AssessmentRepo.COURSE_ID; childsParentId = CourseRepo.TERM_ID;
        childUri = MyContentProvider.ASSESSMENT_URI;
        Log.d(this.getLocalClassName(), "extra from intent: " +uri);
        Log.d(this.getLocalClassName(), "childUri: " +childUri);
        childClass = AssessmentActivity.class;
        childClassDetails = AssessmentDetailsActivity.class;
        childIntent = new Intent(this, childClass);
        detailsIntent = new Intent(this, CourseDetailsActivity.class);

        /*set cursor adapter*/
        String[] from = {childRepoTitle, childRepoID};
        int[] to = {R.id.textViewItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.course_list_item, null, from, to, 0);

        /*set list adapter*/
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        //get term title and set as text for titleTextView
        TextView titleTextView = (TextView) findViewById(R.id.textViewCourseTitle);
        Log.d(this.getLocalClassName(), "repoTitle: " +repoTitle);
        titleTextView.setText(repoTitle);

        getLoaderManager().initLoader(0, null, this);
    }
/*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String childUr = "";
        Uri loaderChildUri = MyContentProvider.ASSESSMENT_URI;
        String loaderChildsParentId = AssessmentRepo.COURSE_ID;
        Log.d(this.getLocalClassName(), "loaderChildUri: " + loaderChildUri + " loaderChildsParentId: "+ loaderChildsParentId + " repoId: " + repoId);
        return new CursorLoader(this, childUri, null, childsParentId + "=" + repoId, null, null);
    }
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String loaderId = intent.getStringExtra(CourseRepo.ID);
        Uri loaderChildUri = MyContentProvider.ASSESSMENT_URI;
        String loaderChildsParentId = AssessmentRepo.COURSE_ID;
        Log.d(this.getLocalClassName(), "loaderChildUri: " + loaderChildUri + " loaderChildsParentId: "+ loaderChildsParentId + " loaderId: " + loaderId);
        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
    }

}