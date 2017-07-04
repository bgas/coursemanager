package com.example.khaln.coursemanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.khaln.coursemanager.repo.AssessmentRepo;
import com.example.khaln.coursemanager.repo.NoteRepo;
import com.example.khaln.coursemanager.repo.TermRepo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private String action;
    private EditText titleText;
    private EditText bodyText;
    private ImageView myImageView;
    private String whereClause;
    private String oldTitleText;
    private String oldBodyText;
    private Uri oldPhotoUri;
    private String mCurrentPhotoPath;
    private Uri itemUri;
    private Intent intent;
    private int assessmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleText = (EditText) findViewById(R.id.editTitle);
        bodyText = (EditText) findViewById(R.id.bodyText);
        myImageView = (ImageView) findViewById(R.id.myImageView);
        itemUri = MyContentProvider.NOTE_URI;

        intent = getIntent();
        Uri noteUri = intent.getParcelableExtra(NoteRepo.TABLE_NAME);

        if (noteUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
            Uri assessmentUri = intent.getParcelableExtra(AssessmentRepo.TABLE_NAME);
            assessmentId = Integer.parseInt(assessmentUri.getLastPathSegment());
        } else {
            action = Intent.ACTION_EDIT;
            whereClause = AssessmentRepo.ID + "=" + noteUri.getLastPathSegment();
            Uri parentUri = getIntent().getParcelableExtra(NoteRepo.TABLE_NAME);
            assessmentId = Integer.parseInt(parentUri.getLastPathSegment());
            //Get item values
            Cursor cursor = getContentResolver().query(noteUri, NoteRepo.COLUMNS, "", null, null);
            cursor.moveToFirst();
            //Get existing values
            oldTitleText = cursor.getString(cursor.getColumnIndex(NoteRepo.TITLE));
            oldBodyText = cursor.getString(cursor.getColumnIndex(NoteRepo.TEXT));
            assessmentId = cursor.getInt(cursor.getColumnIndex(NoteRepo.ASSESSMENT_ID));
            String oldPhotoString = cursor.getString(cursor.getColumnIndex(NoteRepo.PHOTO));
            if (oldPhotoString != null) {
                oldPhotoUri = Uri.parse(oldPhotoString);
                myImageView.setImageURI(oldPhotoUri);
                mCurrentPhotoPath = oldPhotoString;
            }

            //Set values for various fields

            titleText.setText(oldTitleText);
            bodyText.setText(oldBodyText);
            titleText.requestFocus();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //TODO Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.khaln.coursemanager.fileprovider", photoFile);
                myImageView.setImageURI(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            myImageView.setImageURI(Uri.parse(mCurrentPhotoPath));
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteItem();
                break;
        }
        return true;
    }

    private void finishEditing() {
        String newTitle = titleText.getText().toString().trim();
        String newBodyText = bodyText.getText().toString().trim();
        switch (action) {
            case Intent.ACTION_INSERT:
                if (newTitle.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    addUpdateItem(newTitle, newBodyText, mCurrentPhotoPath, assessmentId, false);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newTitle.length() == 0) {
                    deleteItem();
                } else {
                    addUpdateItem(newTitle, newBodyText, mCurrentPhotoPath, assessmentId, true);
                }
        }
        finish();
    }


    private void addUpdateItem(String title, String text, String photo, int assessmentId, Boolean update) {
        ContentValues values = new ContentValues();
        values.put(NoteRepo.TITLE, title);
        values.put(NoteRepo.TEXT, text);
        values.put(NoteRepo.PHOTO, photo);
        values.put(NoteRepo.ASSESSMENT_ID, assessmentId);
        if (update) {
            getContentResolver().update(itemUri, values, whereClause, null);
            Toast.makeText(this, R.string.itemUpdated, Toast.LENGTH_SHORT).show();
            Intent updateIntent = new Intent();
            updateIntent.putExtra("newTitle", title);
            setResult(RESULT_OK, updateIntent);
        } else {
            getContentResolver().insert(itemUri, values);
        }
        setResult(RESULT_OK);


    }

    public void sendText(View view) {

        String text = titleText.getText().toString().trim() + ": " + bodyText.getText().toString().trim();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            Uri pictureUri = Uri.parse("file://" + mCurrentPhotoPath);
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
            shareIntent.setType("image/jpg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share image..."));
        } catch (NullPointerException e){
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share text..."));
        }

    }

    private void deleteItem() {
        getContentResolver().delete(MyContentProvider.NOTE_URI, whereClause, null);
        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


    public void onBackPressed(){
        finishEditing();
    }

}
