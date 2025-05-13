package com.example.sensolab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class SensorDialogFragment extends DialogFragment {

    public interface SensorDialogListener {
        void onDialogSave(String ipAddress, String selectedOption);
    }

    private SensorDialogListener listener;
    private EditText rhythmEditText;
    private RadioGroup viewFormatOptions;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SensorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implements SensorDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_dialog_sensor, null);
        rhythmEditText = view.findViewById(R.id.editTextRhythm);
        viewFormatOptions = view.findViewById(R.id.radioGroupOptions);

        builder.setView(view)
                .setTitle("Ajustes")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());


        SharedPreferences prefs = requireActivity().getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        int savedRadioId = prefs.getInt("selectedRadioId", -1);
        String savedRhythm = prefs.getString("rhythmValue", "");
        boolean enableGroup = prefs.getBoolean("groupStatus", true);

        if (savedRadioId != -1) {
            viewFormatOptions.check(savedRadioId);
        }
        rhythmEditText.setText(savedRhythm);

        if (!enableGroup) {
            for (int i = 0; i < viewFormatOptions.getChildCount(); i++) {
                View child = viewFormatOptions.getChildAt(i);
                child.setEnabled(false);
            }
        }

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false); // Inicialmente deshabilitado

            viewFormatOptions.setOnCheckedChangeListener((group, checkedId) -> {
                positiveButton.setEnabled(checkedId != -1); // Habilita si hay selección
            });
            if (savedRadioId != -1) {
                rhythmEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        positiveButton.setEnabled(true); // Habilita si hay modificación

                    }
                });
            }

            positiveButton.setOnClickListener(v -> {
                String rhythm = rhythmEditText.getText().toString();
                int selectedRadioId = viewFormatOptions.getCheckedRadioButtonId();
                RadioButton selectedRadio = view.findViewById(selectedRadioId);
                String selectedOption = selectedRadio.getText().toString();

                prefs.edit()
                        .putInt("selectedRadioId", selectedRadioId) // ID del RadioButton
                        .putString("rhythmValue", rhythm)
                        .apply();

                if (listener != null) {
                    listener.onDialogSave(rhythm, selectedOption);
                }
                dialog.dismiss();
            });
        });

        return dialog;
    }

    public static void clearSavedSensorPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}


