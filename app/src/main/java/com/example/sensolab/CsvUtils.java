package com.example.sensolab;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvUtils {

    // Exporta los datos del sensor a un archivo CSV
    public File exportToCsv(Context context, List<SensorData> dataList, String filename) throws IOException {
        File csvFile = new File(context.getExternalFilesDir(null), filename);
        FileWriter writer = new FileWriter(csvFile);
        writer.append("Tiempo,Valor\n");

        for (SensorData data : dataList) {
            writer.append(String.valueOf(data.getTime())).append(",").append(data.getValue()).append("\n");
        }

        writer.flush();
        writer.close();

        return csvFile;
    }
}
