package com.example.test_with_menu.ui.home;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_with_menu.R;
import com.example.test_with_menu.Settings;
import com.example.test_with_menu.ui.plus.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private final String AUDIO_RECORDER_FOLDER = "AudioRec";
    public static Settings s;
    static Log log = new Log();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plus, container, false);
        ActivityCompat.requestPermissions((Activity) root.getContext(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions((Activity) root.getContext(),
                new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1);
        ActivityCompat.requestPermissions((Activity) root.getContext(),
                new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        s = new Settings(10, 5, 22000, 5, 3);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        try {
            createSettingsFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createSettingsFile() throws IOException, ClassNotFoundException {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        File f1 = new File(file.getAbsolutePath() + "/" + "settings.txt");
        if (!f1.exists() && !f1.isDirectory()) {
            s = new Settings(10, 5, 22000, 5, 3);
            try {
                FileOutputStream f = new FileOutputStream(new File(file.getAbsolutePath() + "/" + "settings.txt"));
                ObjectOutputStream o = new ObjectOutputStream(f);
                o.writeObject(s);
                o.close();
                f.close();
            } catch (FileNotFoundException e) {
                log.write_to_file("File not found");
            } catch (IOException e) {
                log.write_to_file("Error initializing stream");
            }
        } else {
            FileInputStream fi = new FileInputStream(new File(file.getAbsolutePath() + "/" + "settings.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            s = (Settings) oi.readObject();
        }
    }
}