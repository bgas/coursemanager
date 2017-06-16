package com.example.khaln.coursemanager;

import android.content.Intent;
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
        setContentView(R.layout.activity_term);

        /*Set Instance Variables*/
        intent = getIntent();
        repoId = intent.getStringExtra(CourseRepo.ID);
        repoTitle = intent.getStringExtra(CourseRepo.TITLE);
        repoTableName = CourseRepo.TABLE_NAME;
        uri = intent.getParcelableExtra(repoTableName);
        childRepoTitle = AssessmentRepo.TITLE;
        childRepoID = AssessmentRepo.ID;
        childRepoTableName = AssessmentRepo.TABLE_NAME;
        childsParentId = AssessmentRepo.COURSE_ID;
        childUri = MyContentProvider.ASSESSMENT_URI;
        childIntent = new Intent(this, AssessmentActivity.class);
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
        TextView titleTextView = (TextView) findViewById(R.id.textViewTermTitle);
        titleTextView.setText(repoTitle);

        getLoaderManager().initLoader(0, null, this);
    }
    /*
    protected void viewDetailsAction() {
        Log.d(this.getLocalClassName(), "Edit existing item of this type");
        Intent viewDetailsIntent = detailsIntent;//new Intent(this, detailsActivityClass);
        viewDetailsIntent.putExtra(repoTableName, uri);
        startActivityForResult(viewDetailsIntent, EDITOR_REQUEST_CODE);
    }
    */
}