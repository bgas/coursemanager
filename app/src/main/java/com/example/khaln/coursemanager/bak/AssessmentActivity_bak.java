//package com.example.khaln.coursemanager.bak;
//
//import android.app.LoaderManager;
//import android.content.ContentValues;
//import android.content.CursorLoader;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.CursorAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.khaln.coursemanager.ManagerCursorAdapter;
//import com.example.khaln.coursemanager.MyContentProvider;
//import com.example.khaln.coursemanager.NoteDetailsActivity;
//import com.example.khaln.coursemanager.R;
//import com.example.khaln.coursemanager.TermDetailsActivity;
//import com.example.khaln.coursemanager.repo.AssessmentRepo;
//import com.example.khaln.coursemanager.repo.NoteRepo;
//
//public class AssessmentActivity_bak extends ActionBarActivity
//        implements LoaderManager.LoaderCallbacks<Cursor>//, android.app.LoaderManager.LoaderCallbacks<Object>
//{
//
//    private static final int EDITOR_REQUEST_CODE = 1001;
//    private CursorAdapter cursorAdapter;
//    private Intent intent;
//    private Uri assessmentUri;
//    private String assessmentId;
//    private String assessmentTitle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_course);
//
//        intent = getIntent();
//        assessmentUri = intent.getParcelableExtra(AssessmentRepo.TABLE_NAME);
//        assessmentId = intent.getStringExtra(AssessmentRepo.ID);
//        assessmentTitle = intent.getStringExtra(AssessmentRepo.TITLE);
//
//        String[] from = {NoteRepo.TITLE, NoteRepo.ID};
//        int[] to = {R.id.textViewItem};
//
//        cursorAdapter = new ManagerCursorAdapter(this, R.layout.course_list_item, null, from, to, 0);
//        ListView list = (ListView) findViewById(android.R.id.list);
//        list.setAdapter(cursorAdapter);
//
//        //Open list-item when clicked
//        /*
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
//                //Intent intent = new Intent(TermList.this, TermActivity.class);
//                Intent intent = new Intent(NoteActivity.this, Note.class);
//                Uri assessmentUri = Uri.parse(MyContentProvider.NOTE_URI + "/" + id);
//                intent.putExtra(NoteRepo.TABLE_NAME, assessmentUri);
//                HashMap tag = (HashMap) view.getTag();
//                intent.putExtra(NoteRepo.ID, (String) tag.get("id"));
//                intent.putExtra(NoteRepo.TITLE, (String) tag.get("titleText"));
//                startActivityForResult(intent, EDITOR_REQUEST_CODE);
//            }
//        });
//        */
//
//        //get course title and set as text for titleTextView
//        TextView titleTextView = (TextView) findViewById(R.id.textViewCourseTitle);
//        titleTextView.setText(assessmentTitle);
//
//        getLoaderManager().initLoader(0, null, this);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_create_sample:
//                insertSampleData();
//                break;
//            case R.id.action_delete_all:
//                deleteAllItems();
//                break;
//            case R.id.action_view_details:
//                viewDetailsAction();
//                break;
//            case android.R.id.home:
//                Log.d(this.getLocalClassName(), "id.home selected");
//                finishEditing();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    private void finishEditing() {
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    private void viewDetailsAction() {
//        Intent intent = new Intent(AssessmentActivity_bak.this, NoteDetailsActivity.class);
//        intent.putExtra(AssessmentRepo.TABLE_NAME, assessmentUri);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
//            restartLoader();
//        }
//    }
//
//    private void restartLoader() {
//        getLoaderManager().restartLoader(0, null, this);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(this, MyContentProvider.NOTE_URI, null, NoteRepo.ASSESSMENT_ID + "=" + assessmentId, null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        cursorAdapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        cursorAdapter.swapCursor(null);
//    }
//
//    public void openEditorForNewItem(View view) {
//        Log.d(this.getLocalClassName(), "open editor running");
//        Intent intent = new Intent(this, TermDetailsActivity.class);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);
//    }
//
//    private void insertNote(String title, String created, String text) {
//        //TODO add code for PHOTO
//        //ID, TITLE, NOTE_MENTOR_ID, NOTE_ASSESSMENT, NOTE_START, NOTE_END, NOTE_STATUS
//        //int assessmentId = courseCursor.getInt(courseCursor.getColumnIndex(AssessmentRepo.ID));
//        ContentValues values = new ContentValues();
//        values.put(NoteRepo.TITLE, title);
//        values.put(NoteRepo.ASSESSMENT_ID, assessmentId);
//        values.put(NoteRepo.CREATED, created);
//        values.put(NoteRepo.TEXT, text);
//        Uri assessmentUri = getContentResolver().insert(MyContentProvider.NOTE_URI, values);   //getContentResolver().insert(MyContentProvider.getTableUri(AssessmentRepo.TABLE_NAME), values);
//        Log.d("MainActivity DL", "Inserted Note " + assessmentUri.getLastPathSegment());
//    }
//    /*ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                ID + " INTEGER, " +
//                TITLE + " TEXT, " +
//                CREATED + " TEXT default CURRENT_TIMESTAMP, " +
//                TEXT + " TEXT, " +
//                PHOTO + " BLOB " +
//                ")";*/
//
//
//
//
//    private void deleteAllItems() {
//        //TODO remove this at completion
//        DialogInterface.OnClickListener dialogClickListener =
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int button) {
//                        if (button == DialogInterface.BUTTON_POSITIVE) {
//                            getContentResolver().delete(
//                                    MyContentProvider.NOTE_URI, null, null
//                            );
//                            restartLoader();
//                            Toast.makeText(AssessmentActivity_bak.this,
//                                    getString(R.string.all_deleted),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                };
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.are_you_sure))
//                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
//                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
//                .show();
//    }
//    private void insertSampleData() {
//        //TODO remove this at completion
//        //TITLE, NOTE_MENTOR_ID, NOTE_ASSESSMENT, NOTE_START, NOTE_END, NOTE_STATUS
//        insertNote("note 1", "2001-11-21", "internal note textarea");
//        insertNote("note 2\nMulti-line", "2001-11-21", "internal note textarea");
//        insertNote("note 3 very long very very very very very very very very very very long", "2001-11-21", "internal note textarea");
//        restartLoader();
//    }
//}
