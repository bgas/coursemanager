package com.example.khaln.coursemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.CourseRepo;

import java.text.DateFormat;
import java.util.Calendar;

public class AssessmentDetailsActivity extends AppCompatActivity {

    private String action;
    private Switch switchDue;
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
    private Boolean hasChildren = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        titleText = (EditText) findViewById(R.id.editTitle);
        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        itemUri = MyContentProvider.ASSESSMENT_URI;
        switchDue = (Switch) findViewById(R.id.switchDueDate);


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
            hasChildren = intent.getBooleanExtra("hasChildren", false);
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
        if(!hasChildren) {
            getContentResolver().delete(itemUri, whereClause, null);
            Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
            Intent deleteIntent = new Intent();
            //TODO implement delete return to parents parent
            deleteIntent.putExtra("itemDeleted", true);
            setResult(RESULT_OK, deleteIntent);
            finish();
        }else{
            Toast.makeText(this, R.string.itemWithSubsNotDeletable, Toast.LENGTH_SHORT).show();
        }
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
        scheduleAlarm(switchDue);
        ContentValues values = new ContentValues();
        values.put(AssessmentRepo.TITLE, title);
        values.put(AssessmentRepo.END, endDate);
        values.put(AssessmentRepo.COURSE_ID, courseId);
        if (update) {
            getContentResolver().update(itemUri, values, whereClause/*AssessmentRepo.COURSE_ID+"="+courseId*/, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
            Intent updateIntent = new Intent();
            updateIntent.putExtra("newTitle", title);
            setResult(RESULT_OK, updateIntent);
        } else {
            getContentResolver().insert(itemUri, values);
            setResult(RESULT_OK);
        }
    }
    public void scheduleAlarm(View v) {
        Switch reminder = (Switch) v;
        if (reminder.isChecked()) {
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth());
            long dueDateMili = dueDate.getTimeInMillis() - 24*60*60*1000;
            // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
            Intent intentAlarm = new Intent(this, MyAlarmReceiver.class);
            intentAlarm.putExtra("alarmMessage", titleText.getText().toString().trim() + " due in 24 hours");
            // Get the Alarm Service.
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Set the alarm for a particular time.
            alarmManager.set(AlarmManager.RTC_WAKEUP, dueDateMili, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            Toast.makeText(this, "Alarm Scheduled", Toast.LENGTH_LONG).show();
        }
    }

/*
    public void scheduleAlarm(View v)
    {

        //Set a notification in 7 days
        Calendar sevendayalarm = Calendar.getInstance();
        sevendayalarm.add(Calendar.DATE, 7);
        Intent intent = new Intent(this, Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 001, intent, 0);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, sevendayalarm.getTimeInMillis(), pendingIntent);
        // The time at which the alarm will be scheduled. Here the alarm is scheduled for 1 day from the current time.
        // We fetch the current time in milliseconds and add 1 day's time
        // i.e. 24*60*60*1000 = 86,400,000 milliseconds in a day.

        Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;

        // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
        // specified MyAlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
        Intent intentAlarm = new Intent(this, MyAlarmReceiver.class);

        // Get the Alarm Service.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm for a particular time.
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled for Tomorrow", Toast.LENGTH_LONG).show();
    }
*/

    public void onBackPressed(){
        finishEditing();
    }
}

