//package com.example.khaln.coursemanager.bak;
//
//import android.app.LoaderManager;
//import android.app.TaskStackBuilder;
//import android.content.ContentValues;
//import android.content.CursorLoader;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.CursorAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.khaln.coursemanager.AssessmentActivity;
//import com.example.khaln.coursemanager.CourseDetailsActivity;
//import com.example.khaln.coursemanager.ManagerCursorAdapter;
//import com.example.khaln.coursemanager.MyContentProvider;
//import com.example.khaln.coursemanager.R;
//import com.example.khaln.coursemanager.TermDetailsActivity;
//import com.example.khaln.coursemanager.repo.AssessmentRepo;
//import com.example.khaln.coursemanager.repo.CourseRepo;
//
//import java.util.HashMap;
//
//public class CourseActivity_bak extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<Cursor>//, android.app.LoaderManager.LoaderCallbacks<Object>
//{
//    //TODO convert to display courses
//    private static final int EDITOR_REQUEST_CODE = 1001;
//    private CursorAdapter cursorAdapter;
//    private Intent intent;
//    private Uri courseUri;
//    private String courseId;
//    private String courseTitle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_term);
//Log.d(this.getLocalClassName(), "running");
//        intent = getIntent();
//        courseUri = intent.getParcelableExtra(CourseRepo.TABLE_NAME);
//        courseId = intent.getStringExtra(CourseRepo.ID);
//        courseTitle = intent.getStringExtra(CourseRepo.TITLE);
//
//        String[] from = {AssessmentRepo.TITLE, AssessmentRepo.ID};
//        int[] to = {R.id.textViewItem};
//
//        cursorAdapter = new ManagerCursorAdapter(this, R.layout.course_list_item, null, from, to, 0);
//        ListView list = (ListView) findViewById(android.R.id.list);
//        list.setAdapter(cursorAdapter);
//
//        //Open list-item when clicked
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int Position, long id){
//                //Intent intent = new Intent(TermList.this, TermActivity.class);
//                Intent intent = new Intent(CourseActivity_bak.this, AssessmentActivity.class);
//                Uri courseUri = Uri.parse(MyContentProvider.ASSESSMENT_URI + "/" + id);
//                intent.putExtra(AssessmentRepo.TABLE_NAME, courseUri);
//                HashMap tag = (HashMap) view.getTag();
//                intent.putExtra(AssessmentRepo.ID, (String) tag.get("id"));
//                intent.putExtra(AssessmentRepo.TITLE, (String) tag.get("titleText"));
//                startActivityForResult(intent, EDITOR_REQUEST_CODE);
//            }
//        });
//
//
//        //get term title and set as text for titleTextView
//        TextView titleTextView = (TextView) findViewById(R.id.textViewTermTitle);
//        titleTextView.setText(courseTitle);
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
//    @Override
//    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder){}
//
//    private void viewDetailsAction() {
//        Intent intent = new Intent(CourseActivity_bak.this, CourseDetailsActivity.class);
//        intent.putExtra(CourseRepo.TABLE_NAME, courseUri);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        Log.d(this.getLocalClassName()+"activity result", "requestCode: " + requestCode + "resultCode: " + resultCode + "data: " + data  );
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
//        return new CursorLoader(this, MyContentProvider.ASSESSMENT_URI, null, AssessmentRepo.COURSE_ID + "=" + courseId, null, null);
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
//
//    private void finishEditing() {
//        Log.d(this.getLocalClassName(), "finishEditing running");
//
//        setResult(RESULT_OK);//, new Intent().putExtra("termID", "69"));
//        finish();
//    }
//    @Override
//    public void onBackPressed(){
//        Log.d("back", "pressed");
//        finishEditing();
//    }
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        Log.d(this.getLocalClassName(), "is destroyed: " +isDestroyed());
//    }
//
//
//
//    private void insertAssessment(String title, String end) {
//        //ID, TITLE, ASSESSMENT_MENTOR_ID, ASSESSMENT_COURSE, ASSESSMENT_START, END, ASSESSMENT_STATUS
//        //int courseId = termCursor.getInt(termCursor.getColumnIndex(CourseRepo.ID));
//        ContentValues values = new ContentValues();
//        values.put(AssessmentRepo.TITLE, title);
//        values.put(AssessmentRepo.COURSE_ID, courseId);
//        values.put(AssessmentRepo.END, end);
//        Uri courseUri = getContentResolver().insert(MyContentProvider.ASSESSMENT_URI, values);   //getContentResolver().insert(MyContentProvider.getTableUri(CourseRepo.TABLE_NAME), values);
////        Log.d("MainActivity DL", "Inserted Note " + courseUri.getLastPathSegment());
//    }
//    private void deleteAllItems() {
//        //TODO remove this at completion
//        DialogInterface.OnClickListener dialogClickListener =
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int button) {
//                        if (button == DialogInterface.BUTTON_POSITIVE) {
//                            getContentResolver().delete(
//                                    MyContentProvider.ASSESSMENT_URI, null, null
//                            );
//                            restartLoader();
//                            Toast.makeText(CourseActivity_bak.this,
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
//        //TITLE, ASSESSMENT_MENTOR_ID, ASSESSMENT_COURSE, ASSESSMENT_START, END, ASSESSMENT_STATUS
//        insertAssessment("assessment 1", "2001-11-21");
//        insertAssessment("assessment 2\nMulti-line", "2001-11-21");
//        insertAssessment("assessment 3 very long very very very very very very very very very very long", "2001-11-21");
//        restartLoader();
//    }
//}
