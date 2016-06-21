package com.dhavalmotghare.wikires;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FakeSearchResultActivity extends SearchResultActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
    }


}
