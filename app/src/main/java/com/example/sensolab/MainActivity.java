package com.example.sensolab;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity
        implements IpDialogFragment.IpDialogListener {

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
    }
}