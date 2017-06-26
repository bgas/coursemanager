package com.example.khaln.coursemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.AssessmentRepo;

public class AssessmentDetailsActivity extends AppCompatActivity {

    private String action;
    private EditText titleText;
    private DatePicker endDate;
    private String whereClause;
    private String oldText;
    private String[] oldEnd;
    protected Uri itemUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        titleText = (EditText) findViewById(R.id.editTitle);

        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        itemUri = MyContentProvider.ASSESSMENT_URI;

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(AssessmentRepo.TABLE_NAME /*MyContentProvider.CONTENT_ITEM_TYPE*/);
//        Log.d(this.getLocalClassName() + "UI", "URI null? " + (uri == null) );
        if (uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_assessment));
        } else {
            action = Intent.ACTION_EDIT;
            whereClause = AssessmentRepo.ID + "=" +uri.getLastPathSegment();
           //Get item values
            Cursor cursor = getContentResolver().query(uri, AssessmentRepo.COLUMNS, "", null, null);
            cursor.moveToFirst();
            //Get existing values
//            Log.d(this.getLocalClassName(), "fetching old values");
            oldText = cursor.getString(cursor.getColumnIndex(AssessmentRepo.TITLE));
            oldEnd = cursor.getString(cursor.getColumnIndex(AssessmentRepo.END)).split("-");
            //Set values for various fields
//            Log.d("AssessmentDetailsActivity", "oldText: " + oldText + " oldStart: " + oldStart + " oldEnd: " + oldEnd );
            titleText.setText(oldText);
//            startDate.updateDate(Integer.parseInt(oldStart[0]), Integer.parseInt(oldStart[1]), Integer.parseInt(oldStart[2]));
            endDate.updateDate(Integer.parseInt(oldEnd[0]), Integer.parseInt(oldEnd[1]), Integer.parseInt(oldEnd[2]));
            titleText.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        switch (item.getItemId()){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteItem();
                break;
        }
        return true;
    }


    private void deleteItem() {
        getContentResolver().delete(itemUri, whereClause, null);
        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = titleText.getText().toString().trim();
        //String newStart = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDayOfMonth();
        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
        int termId = Integer.parseInt(getIntent().getStringExtra(AssessmentRepo.TABLE_NAME));
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                } else{
                    addUpdateItem(newText, newEnd, termId, false);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0){
                    deleteItem();
                } else if (oldText.equals(newText) && oldEnd.equals(newEnd) ){
                    setResult(RESULT_CANCELED);
                } else {
                    addUpdateItem(newText, newEnd, termId, true);
                }
        }
        finish();
    }

    private void addUpdateItem(String title, String endDate, int termId, Boolean update) {
        ContentValues values = new ContentValues();
        values.put(AssessmentRepo.TITLE, title);
        values.put(AssessmentRepo.END, endDate);
        values.put(AssessmentRepo.COURSE_ID, termId);
        if (update) {
            getContentResolver().update(itemUri, values, whereClause, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        } else {
            getContentResolver().insert(itemUri, values);
        }
        setResult(RESULT_OK);
        Log.d(this.getLocalClassName(), "update: " + update);
        Log.d(this.getLocalClassName(), "into uri: " + itemUri + /*" whereClause: " + whereClauseCourse + */" insert values: "+ values);

    }

    public void onBackPressed(){
        finishEditing();
    }

}





//package com.example.khaln.coursemanager;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.khaln.coursemanager.repo.AssessmentRepo;
//
//public class AssessmentDetailsActivity extends AppCompatActivity {
//
//    private String action;
//    private EditText titleText;
//    private DatePicker endDate;
//    private String whereClause;
//    private String oldText;
//    private String[] oldEnd;
//    protected Uri itemUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_assessment_details);
//
//        titleText = (EditText) findViewById(R.id.editTitle);
//        endDate = (DatePicker) findViewById(R.id.endDatePicker);
//        itemUri = MyContentProvider.ASSESSMENT_URI;
//
//        Intent intent = getIntent();
//        Uri uri = intent.getParcelableExtra(AssessmentRepo.TABLE_NAME /*MyContentProvider.CONTENT_ITEM_TYPE*/);
////        Log.d(this.getLocalClassName() + "UI", "URI null? " + (uri == null) );
//        if (uri == null){
//            action = Intent.ACTION_INSERT;
//            setTitle(getString(R.string.new_assessment));
//        } else {
//            action = Intent.ACTION_EDIT;
//            whereClause = AssessmentRepo.ID + "=" +uri.getLastPathSegment();
////            Log.d("TActivity dl ui", "uri " + uri + " uri last path segment "+ uri.getLastPathSegment() + " whereClause " + whereClause);
//            //Get item values
//            Cursor cursor = getContentResolver().query(uri, AssessmentRepo.COLUMNS, "", null, null);
//            cursor.moveToFirst();
//            //Get existing values
////            Log.d(this.getLocalClassName(), "fetching old values");
//            oldText = cursor.getString(cursor.getColumnIndex(AssessmentRepo.TITLE));
//            oldEnd = cursor.getString(cursor.getColumnIndex(AssessmentRepo.END)).split("-");
//            //Set values for various fields
////            Log.d("AssessmentDetailsActivity", "oldText: " + oldText + " oldStart: " + oldStart + " oldEnd: " + oldEnd );
//            titleText.setText(oldText);
////            startDate.updateDate(Integer.parseInt(oldStart[0]), Integer.parseInt(oldStart[1]), Integer.parseInt(oldStart[2]));
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
//
//    private void deleteItem() {
//        getContentResolver().delete(itemUri, whereClause, null);
//        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    private void finishEditing() {
//        String newText = titleText.getText().toString().trim();
//        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
//        switch (action){
//            case Intent.ACTION_INSERT:
//                if (newText.length() == 0){
//                    setResult(RESULT_CANCELED);
//                } else{
//                    addUpdateItem(newText, newEnd, false);
//                }
//                break;
//            case Intent.ACTION_EDIT:
//                if (newText.length() == 0){
//                   deleteItem();
//                } else if (oldText.equals(newText) && oldEnd.equals(newEnd) ){
//                    setResult(RESULT_CANCELED);
//                } else {
//                    addUpdateItem(newText, newEnd, true);
//                }
//        }
//        finish();
//    }
//
//    public static final String COURSE_ID = "courseID";
//
//
//
//    private void addUpdateItem(String title, String endDate, Boolean update) {
//        ContentValues values = new ContentValues();
//        values.put(AssessmentRepo.TITLE, title);
//        values.put(AssessmentRepo.END, endDate);
//        if (update) {
//            getContentResolver().update(itemUri, values, whereClause, null);
//            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
//        } else {
//            getContentResolver().insert(itemUri, values);
//        }
//        setResult(RESULT_OK);
//    }
//
//    public void onBackPressed(){
//        finishEditing();
//    }
//
//}
//
//
