package com.example.sushantattada.pdfdemoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itextpdf.text.DocumentException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SelfNoteFragment noteFragment = null;
        try {
            noteFragment = SelfNoteFragment.newInstance();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentTransaction fragTransaction= this.getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.container, noteFragment);
        fragTransaction.commit();
    }


}
