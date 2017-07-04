package com.example.khaln.coursemanager;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.MentorRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class CourseDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String action;
    private EditText titleText;
    private EditText courseStatus;
    private DatePicker startDate;
    private DatePicker endDate;
    private String whereClauseCourse;
    private String oldText;
    private String oldStatus;
    private String[] oldStart;
    private String[] oldEnd;
    private int oldMentorId;
    private Uri itemUri;
    protected String childRepoTitle;
    protected CursorAdapter cursorAdapter;
    protected String childRepoID;
    protected Uri childUri;
    protected static final int EDITOR_REQUEST_CODE = 1001;
    protected static final int CHILD_REQUEST_CODE = 1002;
    protected String childRepoTableName;
    protected Intent childIntent;
    private String[] from = new String[3];
    private Uri courseUri;
    private int termId;
    private Boolean hasChildren = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        //Activity elements
        titleText = (EditText) findViewById(R.id.editTitle);
        startDate = (DatePicker) findViewById(R.id.startDatePicker);
        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        courseStatus = (EditText) findViewById(R.id.editStatus);
        itemUri = MyContentProvider.COURSE_URI;
        childRepoTitle = MentorRepo.TITLE;
        childRepoID = MentorRepo.ID;
        childUri = MyContentProvider.MENTOR_URI;
        childIntent = new Intent(this, MentorDetailsActivity.class);
        childRepoTableName = MentorRepo.TABLE_NAME;


        Cursor mentorCursor = getContentResolver().query(MyContentProvider.MENTOR_URI, MentorRepo.COLUMNS, null, null, null);
        mentorCursor.moveToFirst();

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(CourseRepo.TABLE_NAME);

        if (courseUri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_course));
            Uri termUri = intent.getParcelableExtra(TermRepo.TABLE_NAME);
            termId = Integer.parseInt(termUri.getLastPathSegment());
        } else {
            action = Intent.ACTION_EDIT;
            whereClauseCourse = CourseRepo.ID + "=" + courseUri.getLastPathSegment();
            hasChildren = intent.getBooleanExtra("hasChildren", false);
            courseUri = intent.getParcelableExtra(CourseRepo.TABLE_NAME);
            //Get item values
            Cursor courseCursor = getContentResolver().query(courseUri, CourseRepo.COLUMNS, "", null, null);
            courseCursor.moveToFirst();
            //Get existing values from DB
            oldText = courseCursor.getString(courseCursor.getColumnIndex(CourseRepo.TITLE));
            oldStart = courseCursor.getString(courseCursor.getColumnIndex(CourseRepo.START)).split("-");
            oldEnd = courseCursor.getString(courseCursor.getColumnIndex(CourseRepo.END)).split("-");
            oldMentorId = courseCursor.getInt(courseCursor.getColumnIndex(CourseRepo.MENTOR_ID));
            oldStatus = courseCursor.getString(courseCursor.getColumnIndex(CourseRepo.STATUS));
            termId = courseCursor.getInt(courseCursor.getColumnIndex(CourseRepo.TERM_ID));
 //courseCursor.close();
            //Set values for course fields
            titleText.setText(oldText);
            courseStatus.setText(oldStatus);
            startDate.updateDate(Integer.parseInt(oldStart[0]), Integer.parseInt(oldStart[1]), Integer.parseInt(oldStart[2]));
            endDate.updateDate(Integer.parseInt(oldEnd[0]), Integer.parseInt(oldEnd[1]), Integer.parseInt(oldEnd[2]));
            //Set value for mentor name
            //oldMentorName = mentorCursor.getString(mentorCursor.getColumnIndex(MentorRepo.TITLE));

        }

        /*set cursor adapter*/
        from[0] = childRepoTitle;
        from[1] = childRepoID;
        from[2] = ""+oldMentorId;
        //from = {childRepoTitle, childRepoID, ""+oldMentorId};
        int[] to = {R.id.textViewItem};
        cursorAdapter = new ManagerCursorAdapter(this, R.layout.mentor_list_item, null, from, to, 0);
        /*set list adapter*/
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Set list-item listener to open item on click
        list.setOnItemClickListener(getChildActionClickListener());

        titleText.requestFocus();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
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
            getContentResolver().delete(itemUri, whereClauseCourse, null);
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
        String newStart = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDayOfMonth();
        String newEnd = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDayOfMonth();
        String newStatus = courseStatus.getText().toString().trim();
        int newMentorId = oldMentorId;
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                } else{
                    addUpdateItem(newText, newStart, newEnd, newMentorId, newStatus, termId, false);
                }
                break;
            case Intent.ACTION_EDIT:
                if (oldText.equals(newText) && oldStart.equals(newStart) && oldEnd.equals(newEnd) && oldMentorId == newMentorId && oldStatus.equals(newStatus)){
                    setResult(RESULT_CANCELED);
                } else {
                    addUpdateItem(newText, newStart, newEnd, newMentorId, newStatus, termId, true);
                }
        }
        finish();
    }

    private void addUpdateItem(String title, String start, String end, int mentorId, String status, int termId, Boolean update) {
        scheduleAlarmEnd(findViewById(R.id.switchEndReminder));
        scheduleAlarmStart(findViewById(R.id.switchStartReminder));
        ContentValues values = new ContentValues();
        values.put(CourseRepo.TITLE, title);
        values.put(CourseRepo.START, start);
        values.put(CourseRepo.END, end);
        values.put(CourseRepo.MENTOR_ID, mentorId);
        values.put(CourseRepo.STATUS, status);
        values.put(CourseRepo.TERM_ID, termId);
        if (update) {
            getContentResolver().update(itemUri, values, whereClauseCourse, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
            Intent updateIntent = new Intent();
            updateIntent.putExtra("newTitle", title);
            setResult(RESULT_OK, updateIntent);
        } else {
            getContentResolver().insert(itemUri, values);
            setResult(RESULT_OK);
        }
//        setResult(RESULT_OK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            int dataResult = data.getIntExtra(CourseRepo.MENTOR_ID, 0);
            //If dataResult has value, assign value as new mentor ID
            if (0 != dataResult){
                oldMentorId = dataResult;
                from[2] = ""+dataResult;
                ContentValues values = new ContentValues();
                values.put(CourseRepo.MENTOR_ID, dataResult);
                getContentResolver().update(itemUri, values, whereClauseCourse, null);


            }
            restartLoader();
        }
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

    public void openEditorForNewItem(View view) {
        startActivityForResult(new Intent(this, MentorDetailsActivity.class), EDITOR_REQUEST_CODE);
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public void onBackPressed(){
        finishEditing();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, childUri, null, "", null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void scheduleAlarmStart(View v) {
        Switch startReminder = (Switch) v;
        if (startReminder.isChecked()) {
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth());
            long dueDateMili = dueDate.getTimeInMillis() - 24 * 60 * 60 * 1000;
            // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
            Intent intentAlarm = new Intent(this, MyAlarmReceiver.class);
            intentAlarm.putExtra("alarmMessage", titleText.getText().toString().trim() + " starts in 24 hours");
            // Get the Alarm Service.
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Set the alarm for a particular time.
            alarmManager.set(AlarmManager.RTC_WAKEUP, dueDateMili, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            Toast.makeText(this, "Alarm Scheduled", Toast.LENGTH_LONG).show();
        }
    }
    public void scheduleAlarmEnd(View v) {
        Switch endReminder = (Switch) v;
        if (endReminder.isChecked()) {
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth());
            long dueDateMili = dueDate.getTimeInMillis() - 24*60*60*1000;
            // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
            Intent intentAlarm = new Intent(this, MyAlarmReceiver.class);
            intentAlarm.putExtra("alarmMessage", titleText.getText().toString().trim() + " ends in 24 hours");
            // Get the Alarm Service.
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Set the alarm for a particular time.
            alarmManager.set(AlarmManager.RTC_WAKEUP, dueDateMili, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            Toast.makeText(this, "Alarm Scheduled", Toast.LENGTH_LONG).show();
        }
    }

}
