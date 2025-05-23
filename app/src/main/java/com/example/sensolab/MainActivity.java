package com.example.sensolab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppBarActivity
        implements IpDialogFragment.IpDialogListener, View.OnClickListener{

    private RelativeLayout[] sensorListRL = new RelativeLayout[sensorListID.length];
    private static final int[] sensorListID = {R.id.sensor1_rl, R.id.sensor2_rl,
            R.id.sensor3_rl, R.id.sensor4_rl, R.id.sensor5_rl};
    public static final String SENSOR_TEMP = "Temperatura";
    public static final String SENSOR_HUM = "Humedad";
    public static final String SENSOR_DISTANCE = "Distancia";
    public static final String SENSOR_VOLTAGE = "Voltaje";
    public static final String SENSOR_LIGHT = "Luz";

    public static final String SENSOR_TYPE_KEY = "mx.unam.icat.esie.dcide.SENSOR_TYPE_KEY";

    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showIpDialog();
        requestSelectedSensor();
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        menu.findItem(R.id.adjust_item).setVisible(false);
        menu.findItem(R.id.share_item).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    // CHECK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()  == R.id.connect_item) {
            new IpDialogFragment().show(getSupportFragmentManager(), "IpDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showIpDialog() {
        // Creamos y mostramos un dialogo para realizar la conexion IP
        DialogFragment dialog = new IpDialogFragment();
        dialog.show(getSupportFragmentManager(), "IpDialogFragment");
    }

    /**
     * Función que realiza la conecion con el servidor del sensor
     * una vez que se obtiene la IP ingresada por el usuario.
     * @param ipAddress La direccion IP del sensor a conectar
     */
    @Override
    public void onIpEntered(String ipAddress) {
        // Recibimos la IP para conectarnos al servidor
        this.ipAddress = ipAddress;
        Log.d("MainActivity", "IP recibida: " + ipAddress);
        // 10.0.2.2 en el emulador
        SensorService sensorService = new SensorService();
        sensorService.requestData(ipAddress, "", new SensorService.SensorServiceInterface() {
            @Override
            public void onSuccess(String data) {
                Log.d("SensorData", data);
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(Exception e) {
                Log.e("SensorError", "Error al obtener datos: " + e.getMessage());
                runOnUiThread(() ->
                    Snackbar.make(findViewById(android.R.id.content),
                                    "No se pudo establecer la conexión",
                                    Snackbar.LENGTH_INDEFINITE)
                            .setAction("Reintentar", v -> {
                                onIpEntered(ipAddress);
                            }).show()
                );
            }
        });
    }

    public void requestSelectedSensor() {
        for (int i = 0; i < sensorListID.length; i++) {
            sensorListRL[i] = findViewById(sensorListID[i]);
            sensorListRL[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Intent sensor = new Intent(this, SensorActivity.class);
        String sensorType;
        if (view.getId() == R.id.sensor1_rl) {
            sensorType = SENSOR_TEMP;
        } else if (view.getId() == R.id.sensor2_rl) {
            sensorType = SENSOR_HUM;
        } else if (view.getId() == R.id.sensor3_rl) {
            sensorType = SENSOR_DISTANCE;
        } else if (view.getId() == R.id.sensor4_rl) {
            sensorType = SENSOR_VOLTAGE;
        } else if (view.getId() == R.id.sensor5_rl) {
            sensorType = SENSOR_LIGHT;
        } else {
            throw new UnsupportedOperationException("Sensor \"" +
                    getResources().getResourceName(view.getId()) + "\" no soportado");
        }//indica el tipo de sensor seleccionado
        sensor.putExtra(SENSOR_TYPE_KEY, sensorType);
        if (ipAddress == null) {
            showIpDialog();
        } else {
            sensor.putExtra("host", this.ipAddress);
            startActivity(sensor);
        }
    }
}