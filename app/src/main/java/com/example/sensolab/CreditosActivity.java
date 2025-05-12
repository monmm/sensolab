package com.example.sensolab;

import android.os.Bundle;
import android.view.MenuItem;

public class CreditosActivity extends AppBarActivity {

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // No inflar el menú para ocultar el overflow (tres puntos)
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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