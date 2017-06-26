package com.example.khaln.coursemanager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

import java.util.HashMap;

public class TermActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>//, android.app.LoaderManager.LoaderCallbacks<Object>
{
    //TODO convert to display courses
    protected static final int EDITOR_REQUEST_CODE = 1001;
    protected static final int CHILD_REQUEST_CODE = 1002;
    protected CursorAdapter cursorAdapter;
    protected Intent intent;
    protected String repoId;
    protected String repoTitle;
    protected String repoTableName;
    protected Uri uri;
    protected String childRepoTitle;
    protected String childRepoID;
    protected String childRepoTableName;
    protected String childsParentId;
    protected Uri childUri;
    protected Class childClass;
    protected Class childClassDetails;
    protected Intent childIntent;
    protected Intent detailsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        /*Set Instance Variables*/


/*
        Intent intent = new Intent(TermList.this, TermActivity.class);
        Uri uri = Uri.parse(MyContentProvider.TERM_URI + "/" + id);
        intent.putExtra(TermRepo.TABLE_NAME, uri);
        intent.putExtra(TermRepo.ID, (String) view.getTag(R.string.item_id_tag));
        intent.putExtra(TermRepo.TITLE, (String) view.getTag(R.string.item_title_tag));
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
        */
        intent = getIntent();
        repoId = intent.getStringExtra(TermRepo.ID);
        repoTitle = intent.getStringExtra(TermRepo.TITLE);
        repoTableName = TermRepo.TABLE_NAME;
        uri = intent.getParcelableExtra(repoTableName);
        childRepoTitle = CourseRepo.TITLE;
        childRepoID = CourseRepo.ID;
        childRepoTableName = CourseRepo.TABLE_NAME;
        childUri = MyContentProvider.COURSE_URI;
        childClass = CourseActivity.class;
        childClassDetails = CourseDetailsActivity.class;
        childIntent = new Intent(this, childClass);
        detailsIntent = new Intent(this, TermDetailsActivity.class);

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



    protected void viewDetailsAction() {
        Intent viewDetailsIntent = detailsIntent;//new Intent(this, detailsActivityClass);
        //TODO fix this shit!
        //Uri uri = Uri.parse(MyContentProvider.TERM_URI + "/" + id);
        viewDetailsIntent.putExtra(repoTableName, uri);
        Log.d(this.getLocalClassName(), "repoTableName: " + repoTableName+ " uri: "+uri +" detailsIntent: "+ detailsIntent.toString());
        startActivityForResult(viewDetailsIntent, EDITOR_REQUEST_CODE);
    }

    public void openEditorForNewItem(View view) {
        Log.d(this.getLocalClassName(), "Create new item as child of this type with uri lastPath: " + uri.getLastPathSegment());
        Intent intent = new Intent(this, childClassDetails);
        intent.putExtra(childRepoTableName, uri.getLastPathSegment());
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    /* create and respond to child Activities */
    protected AdapterView.OnItemClickListener getChildActionClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
                Uri childIDUri = Uri.parse(childUri + "/" + id);
                childIntent.putExtra(childRepoTableName, childIDUri);
                childIntent.putExtra(childRepoID, (String) view.getTag(R.string.item_id_tag));
                Log.d(this.getClass().toString(), "tag1: "+ (String) view.getTag(R.string.item_id_tag) + " tag2: " + (String) view.getTag(R.string.item_title_tag) );
                childIntent.putExtra(childRepoTitle, (String) view.getTag(R.string.item_title_tag));
                Log.d(this.getClass().toString(), "childIDUri: " +childIDUri + " childRepoTableName : " + childRepoTableName + " childRepoID : " + childRepoID + " childRepoTitle: " + childRepoTitle);
                startActivityForResult(childIntent, CHILD_REQUEST_CODE);
            }
        };
        return listener;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(this.getLocalClassName(), "activity result requestCode: " + requestCode + "resultCode: " + resultCode /* + "data: " + data */ );
        Log.d(this.getLocalClassName(), " requestCode == EDITOR_REQUEST_CODE: " +(requestCode == EDITOR_REQUEST_CODE) + " resultCode == RESULT_OK: " +(resultCode == RESULT_OK));
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
        else if (requestCode == CHILD_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    /*Handle cursorLoader and cursorAdapter*/

    protected void restartLoader() {
        Log.d(this.getLocalClassName(),"restartLoader");
        getLoaderManager().restartLoader(0, null, this);
    }
    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String childsParentId = CourseRepo.TERM_ID;
        Log.d(this.getLocalClassName(), "childUri: " + childUri + " childsParentId: "+ childsParentId + " repoId: " + repoId);
        return new CursorLoader(this, childUri, null, childsParentId + "=" + repoId, null, null);
    }
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String loaderId = intent.getStringExtra(TermRepo.ID);
        Uri loaderChildUri = MyContentProvider.COURSE_URI;
        String loaderChildsParentId = CourseRepo.TERM_ID;
        Log.d(this.getLocalClassName(), "loaderChildUri: " + loaderChildUri + " loaderChildsParentId: "+ loaderChildsParentId + " loaderId: " + loaderId);
        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(this.getLocalClassName(), "onLoadFinished: ");
        Log.d(this.getLocalClassName(), DatabaseUtils.dumpCursorToString(data));
        cursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(this.getLocalClassName(), "onLoaderReset: ");
        cursorAdapter.swapCursor(null);
    }

    protected void finishEditing() {
        Log.d(this.getLocalClassName(), " finishEditing running");
        setResult(RESULT_OK);
        finish();
    }

    /* Handle menu items and navigation buttons */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
    @Override
    //Handle selection of menu items
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllItems();
                break;
            case R.id.action_view_details:
                viewDetailsAction();
                break;
            case android.R.id.home:
                Log.d(this.getLocalClassName(), "id.home selected");
                finishEditing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        finishEditing();
    }

//TODO these items only used for testing, perhaps repurpose insertCourse?

    protected void insertItem(String title, String start, String end, String status) {
        //ID, TITLE, MENTOR_ID, COURSE_TERM, START, END, STATUS
        //int repoId = termCursor.getInt(termCursor.getColumnIndex(TermRepo.ID));
        ContentValues values = new ContentValues();
        values.put(childRepoTitle, title);
        values.put(CourseRepo.TERM_ID, repoId);
        values.put(CourseRepo.START, start);
        values.put(CourseRepo.END, end);
        values.put(CourseRepo.STATUS, status);
        getContentResolver().insert(childUri, values);
        Log.d("MainActivity DL", "Into " + childUri + " inserted "+ values.toString());
    }
    protected void insertSampleData() {
        //TODO remove this at completion
        //TITLE, MENTOR_ID, COURSE_TERM, START, END, STATUS
        insertItem("course 1", "2001-11-21", "2001-11-21", "passing");
        insertItem("course 2\nMulti-line", "2001-11-21", "2001-11-21", "withdraw-passing");
        insertItem("course 3 very long very very very very very very very very very very long", "2001-11-21", "2001-11-21", "failing");
        restartLoader();
    }
    protected void deleteAllItems() {
        //TODO remove this at completion
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(
                                    childUri, null, null
                            );
                            restartLoader();
                            Toast.makeText(TermActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

}

