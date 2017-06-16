package com.example.khaln.coursemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToTermList(View view){
        Intent intent = new Intent(this, TermList.class);
        startActivity(intent);

    }

    public void goToCurrentTerm(View view){
        Intent intent = new Intent(this, TermDetailsActivity.class);
        startActivity(intent);

    }
}
