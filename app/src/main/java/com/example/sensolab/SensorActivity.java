package com.example.sensolab;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;

public class SensorActivity extends AppBarActivity {
    private String sensorType;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorType = getIntent().getStringExtra(MainActivity.SENSOR_TYPE_KEY);
        getSupportActionBar().setTitle("Sensor de " + sensorType);
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
