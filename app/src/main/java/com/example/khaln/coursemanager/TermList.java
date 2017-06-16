package com.example.khaln.coursemanager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.TermRepo;

import java.util.HashMap;

public class TermList extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        //Now being handled by cursorLoader Cursor cursor = getContentResolver().query(MyContentProvider.TERM_URI, TermRepo.COLUMNS, null, null, null, null);
        String[] from = {TermRepo.TITLE, TermRepo.ID};
        int[] to = {R.id.textViewItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.term_list_item, null, from, to, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        //Open list-item when clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
                Intent intent = new Intent(TermList.this, TermActivity.class);
                Uri uri = Uri.parse(MyContentProvider.TERM_URI + "/" + id);
                intent.putExtra(TermRepo.TABLE_NAME, uri);

                HashMap tag = (HashMap) view.getTag();
                intent.putExtra(TermRepo.ID, (String) tag.get("id"));
                intent.putExtra(TermRepo.TITLE, (String) tag.get("titleText"));

                //intent.putExtra(TermRepo.TITLE, termTitle);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.getLocalClassName(), "isdestroyed: " + isDestroyed());
    }

    private void insertTerm(String title, String start, String end) {
        ContentValues values = new ContentValues();
        values.put(TermRepo.TITLE, title);
        values.put(TermRepo.START, start);
        values.put(TermRepo.END, end);
        Uri termUri = getContentResolver().insert(MyContentProvider.TERM_URI, values);   //getContentResolver().insert(MyContentProvider.getTableUri(TermRepo.TABLE_NAME), values);
        Log.d(this.getLocalClassName() +" DL", "Inserted Note " + termUri.getLastPathSegment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(this.getLocalClassName()+" activity result", "requestCode: " + requestCode + "resultCode: " + resultCode + "data: " + data  );
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MyContentProvider.TERM_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewTerm(View view) {
        Intent intent = new Intent(this, TermDetailsActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);;
    }

    private void deleteAllItems() {
        //TODO remove this at completion
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(
                                    MyContentProvider.TERM_URI, null, null
                            );
                            restartLoader();
                            Toast.makeText(TermList.this,
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
    private void insertSampleData() {
        //TODO remove this at completion
        insertTerm("TermActivity Title", "2001-11-21", "2001-11-21");
        insertTerm("TermActivity Title\nMulti-line", "2001-11-21", "2001-11-21");
        insertTerm("TermActivity Title very long very very very very very very very very very very long", "2001-11-21", "2001-11-21");
        restartLoader();
    }
}
