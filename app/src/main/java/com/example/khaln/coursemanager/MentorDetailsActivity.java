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
import android.widget.EditText;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.CourseRepo;
import com.example.khaln.coursemanager.repo.MentorRepo;

import java.util.ArrayList;

public class MentorDetailsActivity extends AppCompatActivity {


    private String action;
    private EditText editNameText;
    private EditText editEmailText;
    private EditText editPhoneText;
    private String whereClause;
    private ArrayList parentIDsList;
    private String oldName;
    private String oldEmail;
    private String oldPhone;
    private Uri itemUri;
    private Uri uri = MyContentProvider.MENTOR_URI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);

        editNameText = (EditText) findViewById(R.id.editName);
        editEmailText = (EditText) findViewById(R.id.editEmail);
        editPhoneText = (EditText) findViewById(R.id.editPhone);

        parentIDsList = MentorRepo.createArrayList();

        Intent intent = getIntent();
        itemUri = intent.getParcelableExtra(MentorRepo.TABLE_NAME);
//        Log.d(this.getLocalClassName(), "itemUri: "+ itemUri);

        if (itemUri == null/*itemUri.getLastPathSegment().contains("mentors")*/){
            action = Intent.ACTION_INSERT;
        } else {
            action = Intent.ACTION_EDIT;
            whereClause = MentorRepo.ID + "=" +itemUri.getLastPathSegment();
//            Log.d(this.getLocalClassName(), "edit mentor uri: " + itemUri + " and whereclause:  " +whereClause);
            //Get item values
            Cursor cursor = getContentResolver().query(itemUri, MentorRepo.COLUMNS, "", null, null);
            cursor.moveToFirst();
//            Log.d(this.getLocalClassName(), "cursor: " + cursor.getCount());
            //Get existing values from DB
            oldName = cursor.getString(cursor.getColumnIndex(MentorRepo.TITLE));
            oldEmail = cursor.getString(cursor.getColumnIndex(MentorRepo.EMAIL));
            oldPhone = cursor.getString(cursor.getColumnIndex(MentorRepo.PHONE));


            //Set values for various fields
            editNameText.setText(oldName);
            editEmailText.setText(oldEmail);
            editPhoneText.setText(oldPhone);
            //focus on title
            editNameText.requestFocus();
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
        String newText = editNameText.getText().toString().trim();
        String emailText = editEmailText.getText().toString().trim();
        String phoneText = editPhoneText.getText().toString().trim();
//        Log.d(this.getLocalClassName(), "finish edit running action: "+action);
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                } else{
//                    Log.d(this.getLocalClassName(), "action insert newText: "+ newText + " emailText: " + emailText + " phoneText: "+ phoneText);
                    addUpdateItem(newText, emailText, phoneText, false);
                }
                break;
            case Intent.ACTION_EDIT:
//                if (oldName.equals(newText) && oldEmail.equals(emailText) && oldPhone.equals(phoneText) ){
//                    setResult(RESULT_CANCELED);
//                } else {
//                    Log.d(this.getLocalClassName(), "action edit newText: "+ newText + " emailText: "+ emailText + " phoneText: "+ phoneText);
                    addUpdateItem(newText, emailText, phoneText, true);
//                }
                break;
        }
        finish();
    }

    private void addUpdateItem(String name, String email, String phone, Boolean update) {
        int mentorId;
        Intent parentIntent = new Intent(this, CourseDetailsActivity.class);

        ContentValues values = new ContentValues();
        values.put(MentorRepo.TITLE, name);
        values.put(MentorRepo.EMAIL, email);
        values.put(MentorRepo.PHONE, phone);

        if (update) {
            getContentResolver().update(uri, values, whereClause, null);
            mentorId = Integer.parseInt(itemUri.getLastPathSegment());
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
        } else {
            Uri newMentorUri = getContentResolver().insert(uri, values);
            mentorId = Integer.parseInt(newMentorUri.getLastPathSegment());
        }
        parentIntent.putExtra(CourseRepo.MENTOR_ID, mentorId);
        setResult(RESULT_OK, parentIntent);
    }

    public void onBackPressed(){
        finishEditing();
    }

}
