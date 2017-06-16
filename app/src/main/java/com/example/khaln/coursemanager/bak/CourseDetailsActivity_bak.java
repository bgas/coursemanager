//package com.example.khaln.coursemanager.bak;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.khaln.coursemanager.MyContentProvider;
//import com.example.khaln.coursemanager.R;
//import com.example.khaln.coursemanager.repo.TermRepo;
//
//public class CourseDetailsActivity_bak extends AppCompatActivity {
//
//    private String action;
//    private EditText titleText;
//    private DatePicker startDate;
//    private DatePicker endDate;
//    private String whereClause;
//    private String oldText;
//    private String[] oldStart;
//    private String[] oldEnd;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_term_details);
//
//        titleText = (EditText) findViewById(R.id.editTitle);
//        startDate = (DatePicker) findViewById(R.id.startDatePicker);
//        endDate = (DatePicker) findViewById(R.id.endDatePicker);
//
//        Intent intent = getIntent();
//        Uri uri = intent.getParcelableExtra(TermRepo.TABLE_NAME /*MyContentProvider.CONTENT_ITEM_TYPE*/);
//        Log.d(this.getLocalClassName() + "UI", "URI null? " + (uri == null) );
//        if (uri == null){
//            action = Intent.ACTION_INSERT;
//            setTitle(getString(R.string.new_term));
//        } else {
//            action = Intent.ACTION_EDIT;
//            whereClause = TermRepo.ID + "=" +uri.getLastPathSegment();
//            Log.d("TActivity dl ui", "uri " + uri + " uri last path segment "+ uri.getLastPathSegment() + " whereClause " + whereClause);
//            //Get item values
//            Cursor cursor = getContentResolver().query(uri, TermRepo.COLUMNS, "", null, null);
//            cursor.moveToFirst();
//            //Get existing values
//            Log.d(this.getLocalClassName(), "fetching old values");
//            oldText = cursor.getString(cursor.getColumnIndex(TermRepo.TITLE));
//            oldStart = cursor.getString(cursor.getColumnIndex(TermRepo.START)).split("-");
//            oldEnd = cursor.getString(cursor.getColumnIndex(TermRepo.END)).split("-");
//            //Set values for various fields
//            Log.d("TermDetailsActivity", "oldText: " + oldText + " oldStart: " + oldStart + " oldEnd: " + oldEnd );
//            titleText.setText(oldText);
//            startDate.updateDate(Integer.parseInt(oldStart[0]), Integer.parseInt(oldStart[1]), Integer.parseInt(oldStart[2]));
//            endDate.updateDate(Integer.parseInt(oldEnd[0]), Integer.parseInt(oldEnd[1]), Integer.parseInt(oldEnd[2]));
//            titleText.requestFocus();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        if (action.equals(Intent.ACTION_EDIT)){
//            getMenuInflater().inflate(R.menu.menu_editor, menu);
//        }
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item){
//        int id=item.getItemId();
//        switch (item.getItemId()){
//            case android.R.id.home:
//                finishEditing();
//                break;
//            case R.id.action_delete:
//                deleteItem();
//                break;
//        }
//        return true;
//    }
//
//    private void deleteItem() {
//        getContentResolver().delete(MyContentProvider.TERM_URI, whereClause, null);
//        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    private void finishEditing() {
//        String newText = titleText.getText().toString().trim();
//        String newStart = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDayOfMonth();
//        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
//        Log.d(this.getLocalClassName() + "UI", "newText: "+ newText + " newStart: + "+ newStart + " newEnd: "+newEnd);
//        switch (action){
//            case Intent.ACTION_INSERT:
//                if (newText.length() == 0){
//                    setResult(RESULT_CANCELED);
//                } else{
//                    insertTerm(newText, newStart, newEnd);
//                }
//                break;
//            case Intent.ACTION_EDIT:
//                if (newText.length() == 0){
//                   deleteItem();
//                } else if (oldText.equals(newText) && oldStart.equals(newStart) && oldEnd.equals(newEnd) ){
//                    setResult(RESULT_CANCELED);
//                } else {
//                    updateNote(newText, newStart, newEnd);
//                }
//        }
//        finish();
//    }
//
//    private void updateNote(String title, String startDate, String endDate) {
//        Log.d("TermDetailsActivity UI", "title: " + title + " startDate: " + startDate + " endDate: " + endDate );
//        ContentValues values = new ContentValues();
//        values.put(TermRepo.TITLE, title);
//        values.put(TermRepo.START, startDate);
//        values.put(TermRepo.END, endDate);
//        getContentResolver().update(MyContentProvider.TERM_URI, values, whereClause, null);
//        Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//    }
//
//    private void insertTerm(String title, String startDate, String endDate) {
//        //TODO repeats code from Main Activity be+ DRY
//        ContentValues values = new ContentValues();
//        values.put(TermRepo.TITLE, title);
//        values.put(TermRepo.START, startDate);
//        values.put(TermRepo.END, endDate);
//        getContentResolver().insert(MyContentProvider.TERM_URI, values);
//        setResult(RESULT_OK);
//    }
//
//    public void onBackPressed(){
//        finishEditing();
//    }
//
//}
