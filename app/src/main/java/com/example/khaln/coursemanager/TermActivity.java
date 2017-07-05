package com.example.khaln.coursemanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

public class TermActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>//, android.app.LoaderManager.LoaderCallbacks<Object>
{
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
    protected TextView titleTextView;
    protected String repoTitleLocation;
    protected ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        /*Set Instance Variables*/

        intent = getIntent();
        repoId = intent.getStringExtra(TermRepo.ID);
        repoTitleLocation = TermRepo.TITLE;
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
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        //get term title and set as text for titleTextView
        titleTextView = (TextView) findViewById(R.id.textViewTermTitle);
        titleTextView.setText(repoTitle);

        getLoaderManager().initLoader(0, null, this);
    }


    protected void viewDetailsAction() {
        Intent viewDetailsIntent = detailsIntent;//new Intent(this, detailsActivityClass);
        viewDetailsIntent.putExtra(repoTableName, uri);
        viewDetailsIntent.putExtra("isNew", false);
        viewDetailsIntent.putExtra("hasChildren", (list.getChildCount() > 0 ));
        startActivityForResult(viewDetailsIntent, EDITOR_REQUEST_CODE);
    }

    public void openEditorForNewItem(View view) {
        Intent intent = new Intent(this, childClassDetails);
        intent.putExtra(repoTableName, uri);
        intent.putExtra("isNew", true);
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
                childIntent.putExtra(childRepoTitle, (String) view.getTag(R.string.item_title_tag));
                startActivityForResult(childIntent, CHILD_REQUEST_CODE);
            }
        };
        return listener;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            //Get new title from edit event and insert to intent for create method
            if (data != null){
                if (data.hasExtra("newTitle")){
                String title = data.getStringExtra("newTitle");
                intent.putExtra(repoTitleLocation, title);
                titleTextView.setText(title);
                } else if (data.getBooleanExtra("itemDeleted", false)){
                    finish();
                }
            }
            restartLoader();
        }
        else if (requestCode == CHILD_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    /*Handle cursorLoader and cursorAdapter*/

    protected void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String loaderId = intent.getStringExtra(TermRepo.ID);
        Uri loaderChildUri = MyContentProvider.COURSE_URI;
        String loaderChildsParentId = CourseRepo.TERM_ID;
        return new CursorLoader(this, loaderChildUri, null, loaderChildsParentId + "=" + loaderId, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    protected void finishEditing() {
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
            case R.id.action_view_details:
                viewDetailsAction();
                break;
            case android.R.id.home:
                finishEditing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        finishEditing();
    }



}

