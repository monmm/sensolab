package com.example.sensolab;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SensorActivity extends AppBarActivity implements SensorDialogFragment.SensorDialogListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        String sensorType = getIntent().getStringExtra(MainActivity.SENSOR_TYPE_KEY);
        getSupportActionBar().setTitle("Sensor de " + sensorType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SensorDialogFragment.clearSavedSensorPrefs(this);
        showSensorDialog();
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

    public void showSensorDialog() {
        // Creamos y mostramos un dialogo para realizar la conexion IP
        DialogFragment dialog = new SensorDialogFragment();
        dialog.show(getSupportFragmentManager(), "SensorDialogFragment");
    }

    @Override
    public void onDialogSave(String interval, String selectedOption) {
        // Aquí haces lo que necesites con los datos:
        Toast.makeText(getApplicationContext(), "Ritmo: " + interval + ", Visualización: " + selectedOption, Toast.LENGTH_SHORT).show();
    }

}
