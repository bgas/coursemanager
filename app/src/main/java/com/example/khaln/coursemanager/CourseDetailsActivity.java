package com.example.khaln.coursemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.MentorRepo;

public class CourseDetailsActivity extends AppCompatActivity {

    private String action;
    private EditText titleText;
    private Button mentorButton;
    private DatePicker startDate;
    private DatePicker endDate;
    private String whereClause;
    private String oldText;
    private String[] oldStart;
    private String[] oldEnd;
    private int oldMentorId;
    private String oldMentorName;
    private Uri itemUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        titleText = (EditText) findViewById(R.id.editTitle);
        mentorButton = (Button) findViewById(R.id.mentorButton);
        startDate = (DatePicker) findViewById(R.id.startDatePicker);
        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        itemUri = MyContentProvider.COURSE_URI;

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(CourseRepo.TABLE_NAME /*MyContentProvider.CONTENT_ITEM_TYPE*/);
        Log.d(this.getLocalClassName() + "UI", "URI null? " + (uri == null) );
        if (uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_course));
        } else {
            action = Intent.ACTION_EDIT;
            whereClause = CourseRepo.ID + "=" +uri.getLastPathSegment();
            //Get item values
            Cursor cursor = getContentResolver().query(uri, CourseRepo.COLUMNS, "", null, null);
            cursor.moveToFirst();
            //Get existing values from DB
            oldText = cursor.getString(cursor.getColumnIndex(CourseRepo.TITLE));
            oldStart = cursor.getString(cursor.getColumnIndex(CourseRepo.START)).split("-");
            oldEnd = cursor.getString(cursor.getColumnIndex(CourseRepo.END)).split("-");
            oldMentorId = cursor.getInt(cursor.getColumnIndex(CourseRepo.MENTOR_ID));
            //cursor.close();
            Log.d(this.getLocalClassName(), oldMentorId +"");
            if (oldMentorId != 0){
                Log.d(this.getLocalClassName(), "Mentor URI: "+ MyContentProvider.MENTOR_URI+ "/" + oldMentorId + " this URI: "+uri.toString());
                Cursor mentorCursor = getContentResolver().query(Uri.parse(MyContentProvider.MENTOR_URI + "/" + oldMentorId), MentorRepo.COLUMNS, null/*MentorRepo.ID + "=" + oldMentorId*/, null, null);
                mentorCursor.moveToFirst();
                oldMentorName = mentorCursor.getString(cursor.getColumnIndex(MentorRepo.NAME));
                mentorButton.setText(oldMentorName);
            }
                    //getContentResolver().query(MyContentProvider.MENTOR_URI, MentorRepo.COLUMNS, "", null, null);
            //getContentResolver().update(itemUri, values, whereClause, null);

            //Set values for various fields
            titleText.setText(oldText);
            startDate.updateDate(Integer.parseInt(oldStart[0]), Integer.parseInt(oldStart[1]), Integer.parseInt(oldStart[2]));
            endDate.updateDate(Integer.parseInt(oldEnd[0]), Integer.parseInt(oldEnd[1]), Integer.parseInt(oldEnd[2]));

            //focus on title
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
        String newStart = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDayOfMonth();
        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
        Log.d(this.getLocalClassName() + "UI", "newText: "+ newText + " newStart: + "+ newStart + " newEnd: "+newEnd);
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                } else{
                    addUpdateItem(newText, newStart, newEnd, false);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0){
                   deleteItem();
                } else if (oldText.equals(newText) && oldStart.equals(newStart) && oldEnd.equals(newEnd) ){
                    setResult(RESULT_CANCELED);
                } else {
                    addUpdateItem(newText, newStart, newEnd, true);
                }
        }
        finish();
    }

    private void addUpdateItem(String title, String startDate, String endDate, Boolean update) {
        ContentValues values = new ContentValues();
        values.put(CourseRepo.TITLE, title);
        values.put(CourseRepo.START, startDate);
        values.put(CourseRepo.END, endDate);
        if (update) {
            getContentResolver().update(itemUri, values, whereClause, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        } else {
            getContentResolver().insert(itemUri, values);
        }
        setResult(RESULT_OK);
    }

    public void onBackPressed(){
        finishEditing();
    }

}
