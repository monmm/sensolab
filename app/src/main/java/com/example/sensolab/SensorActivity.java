package com.example.sensolab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SensorActivity extends AppBarActivity implements SensorDialogFragment.SensorDialogListener {

    enum EstadoCaptura {
        INACTIVO, CAPTURANDO, PAUSADO
    }

    EstadoCaptura estado = EstadoCaptura.INACTIVO;

    private static final String[] pathList = {"/temp-hum",
            "/distance", "/voltage", "/light"};
    private static final int[] itemList = {R.id.adjust_item,
            R.id.share_item, R.id.connect_item, R.id.about_item};

    private boolean actionStatus = true;

    private FloatingActionButton fabMain;
    private FloatingActionButton fabStop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        String sensorType = getIntent().getStringExtra(MainActivity.SENSOR_TYPE_KEY);
        getSupportActionBar().setTitle("Sensor de " + sensorType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabMain = findViewById(R.id.play_fab);
        fabStop = findViewById(R.id.stop_fab);

        SensorDialogFragment.clearSavedSensorPrefs(this);
        showSensorDialog();
        doFabAction();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        MenuItem actionItem;
        for (int j : itemList) {
            actionItem = menu.findItem(j);
            if (estado == EstadoCaptura.PAUSADO && actionItem.getItemId() == R.id.connect_item) {
                actionItem.setEnabled(false);
            } else {
                actionItem.setEnabled(actionStatus); // actualiza estado según flag
            }
        }
        return super.onPrepareOptionsMenu(menu);
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

    private void doFabAction() {
        fabMain.setOnClickListener(v -> {
            switch (estado) {
                case INACTIVO:
                    //iniciarCaptura();
                    fabMain.setImageResource(R.drawable.pause_24px);
                    fabStop.setEnabled(true);
                    estado = EstadoCaptura.CAPTURANDO;
                    actionStatus = false;
                    invalidateOptionsMenu();
                    break;
                case CAPTURANDO:
                    //pausarCaptura();
                    fabMain.setImageResource(R.drawable.resume_24px);
                    estado = EstadoCaptura.PAUSADO;
                    // no podemos cambiar la ip
                    actionStatus = true;
                    // ni la visualizacion, solo el ritmo
                    SharedPreferences prefs = this.getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("groupStatus", false).apply();
                    invalidateOptionsMenu();
                    break;
                case PAUSADO:
                    //reanudarCaptura();
                    fabMain.setImageResource(R.drawable.pause_24px);
                    estado = EstadoCaptura.CAPTURANDO;
                    actionStatus = false;
                    invalidateOptionsMenu();
                    break;
            }
        });
        fabStop.setOnClickListener(v -> {
            //detenerCaptura();
            fabMain.setImageResource(R.drawable.play_arrow_24px);
            fabStop.setEnabled(false);
            estado = EstadoCaptura.INACTIVO;
            actionStatus = true;
            invalidateOptionsMenu();
        });
    }

}
