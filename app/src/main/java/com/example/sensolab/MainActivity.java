package com.example.sensolab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppBarActivity
        implements IpDialogFragment.IpDialogListener, View.OnClickListener{

    private static final int PORT = 8080;
    private static final String SCHEMA = "http";
    private String ipAddress;
    private final OkHttpClient client = new OkHttpClient();

    private boolean isConnected = false;

    private RelativeLayout[] sensorListRL = new RelativeLayout[sensorListID.length];
    private static final int[] sensorListID = {R.id.sensor1_rl, R.id.sensor2_rl,
            R.id.sensor3_rl, R.id.sensor4_rl, R.id.sensor5_rl};
    public static final String SENSOR_TEMP = "Temperatura";
    public static final String SENSOR_HUM = "Humedad";
    public static final String SENSOR_DISTANCE = "Distancia";
    public static final String SENSOR_VOLTAGE = "Voltaje";
    public static final String SENSOR_LIGHT = "Luz";

    public static final String SENSOR_TYPE_KEY = "mx.unam.icat.esie.dcide.SENSOR_TYPE_KEY";

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
        Log.d("MainActivity", "IP recibida: " + ipAddress);
        // TO-DO: conectarServidor(ip);
        // 10.0.2.2 en el emulador
        this.ipAddress = ipAddress;
        testConnection(ipAddress);
    }

    public void testConnection(String host) {
        Request request = new Request.Builder()
                .url(SCHEMA + "://" + host + ":" + PORT)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                isConnected = false;
                runOnUiThread(() ->
                        Snackbar.make(findViewById(android.R.id.content),
                                        "No se pudo establecer la conexión",
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction("Reintentar", v -> {
                                    showIpDialog();
                                }).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        isConnected = true;
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        isConnected = false;
                    }
                } finally {
                    response.close();
                }
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
        if (!isConnected) {
            Snackbar.make(findViewById(android.R.id.content),
                            "No se pudo establecer la conexión",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar", v -> {
                        showIpDialog();
                    }).show();
        } else {
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
            startActivity(sensor);
        }
    }
}