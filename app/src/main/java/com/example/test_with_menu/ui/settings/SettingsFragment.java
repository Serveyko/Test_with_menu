package com.example.test_with_menu.ui.settings;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_with_menu.MyDialogFragment;
import com.example.test_with_menu.R;
import com.example.test_with_menu.Settings;
import com.example.test_with_menu.ui.home.HomeFragment;
import com.example.test_with_menu.ui.plus.Log;
import com.example.test_with_menu.ui.plus.PlusFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;


@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsFragment extends Fragment {
    private Button btnReset, btnSave;
    static private EditText time, size, inhale, koeff, frequency;
    private SettingsViewModel settingsViewModel;
    private Spinner spinner;
    private int selectitem = 0;
    static private final String AUDIO_RECORDER_FOLDER = "AudioRec";
    static Log log = new Log();
    View root;
    String[] choose;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        time = root.findViewById(R.id.time);
        size = root.findViewById(R.id.size);
        inhale = root.findViewById(R.id.inhale);
//        frequency = root.findViewById(R.id.frequency);
        koeff = root.findViewById(R.id.koeff);
        btnReset = root.findViewById(R.id.reset);
        btnSave = root.findViewById(R.id.save);
        spinner = root.findViewById(R.id.spinner);
        btnReset.setOnClickListener(btnClick);
        btnSave.setOnClickListener(btnClick);
        choose = getResources().getStringArray(R.array.freq);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                selectitem = selectedItemPosition;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        time.setInputType(InputType.TYPE_CLASS_NUMBER);
        size.setInputType(InputType.TYPE_CLASS_NUMBER);
        inhale.setInputType(InputType.TYPE_CLASS_NUMBER);
//        frequency.setInputType(InputType.TYPE_CLASS_NUMBER);
        koeff.setInputType(InputType.TYPE_CLASS_NUMBER);
        time.setText(String.valueOf(HomeFragment.s.get_Time()));
        size.setText(String.valueOf(HomeFragment.s.get_Size()));
//        frequency.setText(String.valueOf(HomeFragment.s.get_Frequency()));
        inhale.setText(String.valueOf(HomeFragment.s.get_Inhale()));
        koeff.setText(String.valueOf(HomeFragment.s.get_Koeff()));
        for ( int i = 0; i < choose.length; i++ ) {
            if ( Integer.parseInt(choose[i]) == HomeFragment.s.get_Frequency() )
                spinner.setSelection(i);
        }
        return root;
    }


    private View.OnClickListener btnClick = v -> {
        switch (v.getId()) {
            case R.id.reset: {
                resetAll();
                break;
            }
            case R.id.save: {
                try {
                    saveAll();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    };

    private void changeFreq () {
        System.out.println(Integer.parseInt(spinner.getSelectedItem().toString()));
    }

    static public void reserAllTrue() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        HomeFragment.s = new Settings(10, 5, 22000, 5, 3);
        time.setText("20");
        size.setText("5");
        inhale.setText("5");
        frequency.setText("22000");
        koeff.setText("3");
        try {
            FileOutputStream f = new FileOutputStream(new File(file.getAbsolutePath() + "/" + "settings.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(HomeFragment.s);
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }
    }

    public void resetAll() {
        MyDialogFragment myDialogFragment = new MyDialogFragment("Все настройки будут сброшены", "Да", "Нет", "Сброс настроек", "resetAll");
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        myDialogFragment.show(transaction, "dialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveAll() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        int t = HomeFragment.s.get_Time();
        int s = HomeFragment.s.get_Size();
        int fr = HomeFragment.s.get_Frequency();
        int i = HomeFragment.s.get_Inhale();
        int k = HomeFragment.s.get_Koeff();
        if (!time.getText().equals(""))
            t = Integer.parseInt(String.valueOf(time.getText()));
        if (!size.getText().equals(""))
            s = Integer.parseInt(String.valueOf(size.getText()));
//        if (!frequency.getText().equals(""))
            fr = Integer.parseInt(String.valueOf(choose[selectitem]));
        if (!inhale.getText().equals(""))
            i = Integer.parseInt(String.valueOf(inhale.getText()));
        ;
        if (!koeff.getText().equals(""))
            k = Integer.parseInt(String.valueOf(koeff.getText()));
        time.setText(String.valueOf(t));
        size.setText(String.valueOf(s));
        inhale.setText(String.valueOf(i));
        HomeFragment.s = new Settings(t, s, fr, i, k);
        try {
            FileOutputStream f = new FileOutputStream(new File(file.getAbsolutePath() + "/" + "settings.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(HomeFragment.s);
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            log.write_to_file("File not found");
        } catch (IOException e) {
            log.write_to_file("Error initializing stream");
        }
        MyDialogFragment myDialogFragment = new MyDialogFragment("Сохранение прошло успешно", "", "Ок", "Сохранение", "save");
        FragmentManager manager = getFragmentManager();
        clearAllTrue();
        FragmentTransaction transaction = manager.beginTransaction();
        myDialogFragment.show(transaction, "dialog");
    }

    static public void clearAllTrue() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(filepath, AUDIO_RECORDER_FOLDER);
        for (final File fileEntry : dir.listFiles()) {
            if ((getFileExtension(fileEntry).equals("wav") || getFileExtension(fileEntry).equals("jpg") || getFileExtension(fileEntry).equals("txt")) && !fileEntry.getName().equals("settings.txt"))
                fileEntry.delete();
        }
        PlusFragment.squaresAll.clear();
        for ( int i = 0; i < PlusFragment.squaresAll.size(); i++ )
            PlusFragment.squaresAll.remove(i);
        clearTheFile();
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public static void clearTheFile() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(filepath, AUDIO_RECORDER_FOLDER);
        FileWriter fwOb = new FileWriter(dir.getAbsolutePath() + "/" + "savedAudio.txt", false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
}