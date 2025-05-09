package com.example.sensolab;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends MainMenuActivity
        implements IpDialogFragment.IpDialogListener {

    private static final int PORT = 8080;
    private static final String SCHEMA = "http";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showIpDialog();
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
        testConnection(ipAddress);
    }

    public void testConnection(String host) {
        Request request = new Request.Builder()
                .url(SCHEMA + "://" + host + ":" + PORT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                // Cerrar la respuesta al final
                try {
                    if (response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

}