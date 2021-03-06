package com.example.khaln.coursemanager;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.NoteRepo;

public class AssessmentActivity extends TermActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        /*Set Instance Variables*/
        intent = getIntent();
        repoId = intent.getStringExtra(AssessmentRepo.ID);
        repoTitleLocation = AssessmentRepo.TITLE;
        repoTitle = intent.getStringExtra(AssessmentRepo.TITLE);
        repoTableName = AssessmentRepo.TABLE_NAME;
        uri = intent.getParcelableExtra(repoTableName);
        childRepoTitle = NoteRepo.TITLE;
        childRepoID = NoteRepo.ID;
        childRepoTableName = NoteRepo.TABLE_NAME;
        childsParentId = NoteRepo.ASSESSMENT_ID;
        childUri = MyContentProvider.NOTE_URI;
        childClass = NoteDetailsActivity.class;
        childClassDetails = NoteDetailsActivity.class;
        detailsIntent = new Intent(this, AssessmentDetailsActivity.class);
        childIntent = new Intent(this, childClassDetails);

        /*set cursor adapter*/
        String[] from = {childRepoTitle, childRepoID};
        int[] to = {R.id.textViewItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.course_list_item, null, from, to, 0);

        /*set list adapter*/
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        //get term title and set as text for titleTextView
        titleTextView = (TextView) findViewById(R.id.textViewAssessmentTitle);
        titleTextView.setText(repoTitle);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String loaderId = intent.getStringExtra(AssessmentRepo.ID);
        Uri loaderChildUri = MyContentProvider.NOTE_URI;
        String loaderChildsParentId = NoteRepo.ASSESSMENT_ID;
        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
    }
}