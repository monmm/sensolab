package com.example.sensolab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SensorActivity extends AppBarActivity implements IpDialogFragment.IpDialogListener,
        SensorDialogFragment.SensorDialogListener, AlertDialogFragment.AlertDialogListener {

    enum EstadoCaptura { INACTIVO, CAPTURANDO, PAUSADO }

    private EstadoCaptura estado = EstadoCaptura.INACTIVO;
    private static final int[] itemList = {R.id.adjust_item, R.id.share_item, R.id.connect_item, R.id.about_item};
    private static final String[] pathList = {"/humidity", "/temperature", "/distance", "/voltage", "/light"};

    private String path = "";
    private String unity = "";
    private String sensorType = "";
    private String ipAddress = "";
    private String visual;

    private DataAdapter adapter;
    private FloatingActionButton fabMain, fabStop;
    private Handler handler;
    private Runnable dataFetchRunnable;
    private int interval = 5;
    private int time = 0;
    private boolean actionStatus = true;

    private List<SensorData> dataList;
    // FrameLayout

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ipAddress = getIntent().getStringExtra("host");
        sensorType = getIntent().getStringExtra(MainActivity.SENSOR_TYPE_KEY);
        if (sensorType != null) {
            setSensorType(sensorType);
            getSupportActionBar().setTitle("Sensor de " + sensorType);
        }

        fabMain = findViewById(R.id.play_fab);
        fabStop = findViewById(R.id.stop_fab);
        // fragment container
        handler = new Handler();

        SensorDialogFragment.clearSavedSensorPrefs(this);
        showSensorDialog();
        doFabAction();
    }

    private void setSensorType(String sensorType) {
        switch (sensorType) {
            case "Humedad": path = pathList[0]; unity = " (%)"; break;
            case "Temperatura": path = pathList[1]; unity = " (°C)"; break;
            case "Distancia": path = pathList[2]; unity = " (cm)"; break;
            case "Voltage": path = pathList[3]; unity = " (V)"; break;
            case "Luz": path = pathList[4]; unity = " (LDR)"; break;
        }
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

    // CHECK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "Perderás cualquier dato capturado\n¿Deseas continuar?";

        if (item.getItemId() == R.id.share_item) {
            CsvUtils csv = new CsvUtils();
            String fileName = "Sensor_" + sensorType + "_data.csv";
            File file = null;
            try {
                file = csv.exportToCsv(getApplicationContext(), dataList, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (file != null) {
                Toast.makeText(getApplicationContext(), "CSV exportado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.connect_item) {
            showAlertDialog(message, "changeIP");
        }

        if (item.getItemId() == android.R.id.home) {
            stopDataCollection(false);
            showAlertDialog(message, "goHome");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showIpDialog() {
        DialogFragment dialog = new IpDialogFragment();
        dialog.show(getSupportFragmentManager(), "IpDialogFragment");
    }

    @Override
    public void onIpEntered(String ipAddress) {
        changeConnection(ipAddress);
    }

    public void showSensorDialog() {
        DialogFragment dialog = new SensorDialogFragment();
        dialog.show(getSupportFragmentManager(), "SensorDialogFragment");
    }

    @Override
    public void onDialogSave(String interval, String selectedOption) {
        if (!interval.isEmpty()) this.interval = Integer.parseInt(interval);
        this.visual = selectedOption;

        // Frame
        if (visual.equals("Tabla")) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            dataList = new ArrayList<>();
            adapter = new DataAdapter(dataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            TextView headerValue = findViewById(R.id.valueHeader);
            String headerVal = sensorType + unity;
            headerValue.setText(headerVal);
        }
    }

    // CHECK
    public void showAlertDialog(String mensaje, String nextAction) {
        Bundle args = new Bundle();
        args.putString("mensaje", mensaje);
        args.putString("action", nextAction);

        DialogFragment dialog = new AlertDialogFragment();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    @Override
    public void onNeutralSelected() {
        this.time = 0;
        this.dataList.clear();
        startDataCollection();
    }

    // TO-DO
    @Override
    public void onPositiveSelected(String action) {
        switch (action) {
            case "goHome":
                finish();
                break;
            case "restart":
                startDataCollection();
                break;
            case "changeIP":
                this.dataList.clear();
                showIpDialog();
                break;
        }
    }

    private void changeConnection(String ipAddress) {
        Log.d("MainActivity", "IP recibida: " + ipAddress);
        // 10.0.2.2 en el emulador
        SensorService sensorService = new SensorService();
        sensorService.requestData(ipAddress, "", new SensorService.SensorServiceInterface() {
            @Override
            public void onSuccess(String data) {
                Log.d("SensorData", data);
                runOnUiThread(() ->
                        setIpAddress(ipAddress)
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
                                    showIpDialog();
                                }).show()
                );
            }
        });
    }

    private void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        this.time = 0;
        Toast.makeText(getApplicationContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show();
        SensorDialogFragment.clearSavedSensorPrefs(this);
        showSensorDialog();
    }

    private void doFabAction() {
        fabMain.setOnClickListener(v -> {
            switch (estado) {
                case INACTIVO:
                    if (ipAddress != null) {
                        if (dataList.isEmpty()) {
                            startDataCollection();
                        } else {
                            showAlertDialog("¿Deseas limpiar la tabla o continuar capturando?", "restart");
                        }
                    }
                    break;
                case CAPTURANDO: pauseDataCollection(); break;
                case PAUSADO: resumeDataCollection(); break;
            }
        });
        fabStop.setOnClickListener(v -> stopDataCollection(false));
    }

    private void stopDataCollection(boolean showError) {
        if (dataFetchRunnable != null) handler.removeCallbacks(dataFetchRunnable);
        if (showError) Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();

        estado = EstadoCaptura.INACTIVO;
        fabMain.setImageResource(R.drawable.play_arrow_24px);
        fabStop.setEnabled(false);
        actionStatus = true;
        invalidateOptionsMenu();
        // delete:
        SharedPreferences prefs = this.getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("groupStatus", true).apply();
    }

    public void pauseDataCollection() {
        estado = EstadoCaptura.PAUSADO;
        fabMain.setImageResource(R.drawable.resume_24px);
        actionStatus = true;
        invalidateOptionsMenu();
        // delete:
        SharedPreferences prefs = this.getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("groupStatus", false).apply();
    }

    public void resumeDataCollection() {
        estado = EstadoCaptura.CAPTURANDO;
        fabMain.setImageResource(R.drawable.pause_24px);
        actionStatus = false;
        invalidateOptionsMenu();
    }

    private void startDataCollection() {
        estado = EstadoCaptura.CAPTURANDO;
        fabMain.setImageResource(R.drawable.pause_24px);
        fabStop.setEnabled(true);
        actionStatus = false;
        invalidateOptionsMenu();

        if (dataFetchRunnable != null) handler.removeCallbacks(dataFetchRunnable);

        dataFetchRunnable = () -> {
            if (estado == EstadoCaptura.CAPTURANDO) collectData(path);
            handler.postDelayed(dataFetchRunnable, interval * 1000L);
            time += interval;
        };

        handler.post(dataFetchRunnable);
    }

    private void collectData(String path) {
        SensorService sensorService = new SensorService();
        sensorService.requestData(ipAddress, path, new SensorService.SensorServiceInterface() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String value = jsonObject.getString(path.replace("/", ""));
                    runOnUiThread(() -> display(time, value));
                } catch (JSONException e) {
                    runOnUiThread(() -> stopDataCollection(true));
                    Log.e("JSONError", e.getMessage());
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> stopDataCollection(true));
            }
        });
    }

    private void display(int time, String value) {
        SensorData data = new SensorData(time, value);
        dataList.add(data);
        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        //if (fragment instanceof TableFragment) ((TableFragment) fragment).updateData();
        //else if (fragment instanceof ChartFragment) ((ChartFragment) fragment).updateData();
        adapter.notifyDataSetChanged(); // delete
    }
}
