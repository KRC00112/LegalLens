package org.kaustav.majorproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class landerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
    }
    public void openLoginForm(View v){

        Intent i=new Intent(this,loginActivity.class);
        startActivity(i);
        finish();


    }
    public void openRegisterForm(View v){

        Intent i=new Intent(this,registerActivity.class);
        startActivity(i);
        finish();


    }



}