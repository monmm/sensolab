package com.example.sensolab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class IpDialogFragment extends DialogFragment {

    /**
     * Interfaz para comunicar la dirección IP ingresada desde el diálogo
     * hacia la actividad o fragmento que implementa este listener.
     */
    public interface IpDialogListener {
        void onIpEntered(String ipAddress);
    }

    public IpDialogListener listener;
    private EditText ipEditText;

    /**
     * Se llama cuando el fragmento se adjunta a su contexto.
     * Este metodo asegura que el contexto implemente la interfaz IpDialogListener.
     * @param context Contexto al que se adjunta el fragmento, debe implementar IpDialogListener.
     * @throws ClassCastException si el contexto no implementa la interfaz requerida.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Escucha para enviar la IP ingresada.
            listener = (IpDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement IPDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_dialog_ip, null);
        ipEditText = view.findViewById(R.id.editTextIp);

        builder.setView(view)
                .setMessage("Ingresa la dirección IP del sensor:")
                .setTitle("Conexión IP")
                .setPositiveButton("Conectar", null) // Evitamos el cierre automatico
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(dlg -> {
            Button connectButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            connectButton.setOnClickListener(v -> {
                String ip = ipEditText.getText().toString();
                if (isValidIp(ip)) {
                    IpDialogListener listener = (IpDialogListener) getActivity();
                    if (listener != null) {
                        listener.onIpEntered(ip);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Dirección IP no válida", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }

    /**
     * Valida que el texto con la direccion IP recibida sea valida, es decir,
     * que siga un patron 000.000.00.00
     * @param ip La direccion ip a validar
     * @return true En caso de que la direccion sea valida,
     *         false En otro caso.
     */
    private boolean isValidIp(String ip) {
        return ip.matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");
    }
}
