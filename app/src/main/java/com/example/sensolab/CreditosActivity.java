package com.example.sensolab;

import android.os.Bundle;
import android.view.MenuItem;

public class CreditosActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finaliza esta actividad y vuelve atrás
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
