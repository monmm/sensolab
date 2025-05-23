package com.example.sensolab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {

    private AlertDialogListener listener;

    public interface AlertDialogListener {
        void onPositiveSelected(String action);
        void onNeutralSelected();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Escucha para enviar la IP ingresada.
            listener = (AlertDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " OnNeutralSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = "¿Está seguro de continuar?";
        String action;

        if (getArguments() != null) {
            message = getArguments().getString("mensaje", message);
            action = getArguments().getString("action", "continue");
        } else {
            action = "continue";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Atención")
                .setMessage(message)
                .setPositiveButton("Continuar", (dialog, id) -> listener.onPositiveSelected(action))
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());

        if (action.equals("restart")) {
            builder.setNeutralButton("Limpiar", (dialog, id) -> listener.onNeutralSelected());
        }

        return builder.create();
    }
}
