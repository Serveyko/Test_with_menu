package com.example.test_with_menu.ui.plus;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    private static final String AUDIO_RECORDER_FOLDER = "AudioRec";

    public Log() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        String p = file.getAbsolutePath() + "/logs.txt";
        File f = new File(p);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static public void write_to_file(String text) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        String p = file.getAbsolutePath() + "/logs.txt";
        try {
            FileWriter writer = new FileWriter(p, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(text + "\n");
            bufferWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
