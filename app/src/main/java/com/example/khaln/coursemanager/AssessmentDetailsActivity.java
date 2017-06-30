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
import com.example.khaln.coursemanager.repo.CourseRepo;

public class AssessmentDetailsActivity extends AppCompatActivity {

    private String action;
    private EditText titleText;
    private DatePicker endDate;
    private String whereClause;
    private String oldText;
    private String[] oldEnd;
    private Uri itemUri;
    private int parentId;
    private Uri assessmentUri;
    private int assessmentId;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        titleText = (EditText) findViewById(R.id.editTitle);
        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        itemUri = MyContentProvider.ASSESSMENT_URI;


//        assessmentUri = intent.hasExtra(CourseRepo.TABLE_NAME) ? (Uri) intent.getParcelableExtra(CourseRepo.TABLE_NAME) : (Uri) intent.getParcelableExtra(AssessmentRepo.TABLE_NAME); /*MyContentProvider.CONTENT_ITEM_TYPE*/
        Intent intent = getIntent();
        assessmentUri = intent.getParcelableExtra(AssessmentRepo.TABLE_NAME);

        if (assessmentUri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_assessment));
            Uri courseUri = intent.getParcelableExtra(CourseRepo.TABLE_NAME);
            courseId = Integer.parseInt(courseUri.getLastPathSegment());
        } else {
            action = Intent.ACTION_EDIT;
            assessmentId = Integer.parseInt(assessmentUri.getLastPathSegment());
            whereClause = AssessmentRepo.ID + "=" + assessmentUri.getLastPathSegment();
            Log.d(this.getLocalClassName(), "whereClause: "+whereClause);
           //Get item values
            Cursor cursor = getContentResolver().query(assessmentUri, AssessmentRepo.COLUMNS, "", null, null);
            cursor.moveToFirst();
            //Get existing values
            oldText = cursor.getString(cursor.getColumnIndex(AssessmentRepo.TITLE));
            oldEnd = cursor.getString(cursor.getColumnIndex(AssessmentRepo.END)).split("-");
            courseId = cursor.getInt(cursor.getColumnIndex(AssessmentRepo.COURSE_ID));
            //Set values for various fields
            titleText.setText(oldText);
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
        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                } else{
                    addUpdateItem(newText, newEnd, courseId, false);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0){
                    deleteItem();
                } else if (oldText.equals(newText) && oldEnd.equals(newEnd) ){
                    setResult(RESULT_CANCELED);
                } else {
                    addUpdateItem(newText, newEnd, courseId, true);
                }
        }
        finish();
    }

    private void addUpdateItem(String title, String endDate, int courseId, Boolean update) {
        ContentValues values = new ContentValues();
        values.put(AssessmentRepo.TITLE, title);
        values.put(AssessmentRepo.END, endDate);
        values.put(AssessmentRepo.COURSE_ID, courseId);
        Log.d(this.getLocalClassName()+"addUpdate", "assessmentUri: "+ assessmentUri);
        Log.d(this.getLocalClassName()+"addUpdate", "itemUri: "+ itemUri);
        Log.d(this.getLocalClassName()+"addUpdate", "values: " + values);
        Log.d(this.getLocalClassName()+"addUpdate", "courseID: "+ AssessmentRepo.COURSE_ID+"="+courseId);
        if (update) {
            Log.d(this.getLocalClassName(), "Update itemUri: "+ itemUri +" values: "+ values.toString() + " whereClauseCourse: " + whereClause);
            //getContentResolver().update(itemUri, values, whereClauseCourse, null);
            getContentResolver().update(itemUri, values, whereClause/*AssessmentRepo.COURSE_ID+"="+courseId*/, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(this.getLocalClassName(), "Insert itemUri: "+ itemUri +" values: "+ values.toString());
            getContentResolver().insert(itemUri, values);
        }
        setResult(RESULT_OK);

    }

    public void onBackPressed(){
        finishEditing();
    }
}

