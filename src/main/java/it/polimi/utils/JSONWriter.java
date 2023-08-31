package it.polimi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;

public class JSONWriter {
    public static <T> void write(T obj, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // create file fileName if not exists
        // write jsonSolution to fileName
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            gson.toJson(obj, writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
