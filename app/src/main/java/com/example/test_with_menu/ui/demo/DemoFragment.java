package com.example.test_with_menu.ui.demo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;


public class DemoFragment extends Fragment {

    private DemoViewModel demoViewModel;
    ArrayList<ArrayList<Double>> ethalon = new ArrayList<>();
    ArrayList<ArrayList<Double>> demo1 = new ArrayList<>();
    ArrayList<ArrayList<Double>> demo2 = new ArrayList<>();
    View root;
    Button original, mute, noise;
    Context context;
    ImageView img1, img2, smile;
    TextView second_img_text, diagnos;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static final String AUDIO_RECORDER_FOLDER = "AudioRec";
    int id = 0;
    private boolean isMute = false;
    Bitmap btmp, btmp2, btmp3;
    private Handler handler2 = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_demo, container, false);
        demoViewModel =
                new ViewModelProvider(this).get(DemoViewModel.class);
        root = inflater.inflate(R.layout.fragment_demo, container, false);
        context = root.getContext();
        Resources res = context.getResources();
        img1 = root.findViewById(R.id.img1);
        img2 = root.findViewById(R.id.img2);
        smile = root.findViewById(R.id.smile);
        original = root.findViewById(R.id.original);
        noise = root.findViewById(R.id.noise);
        mute = root.findViewById(R.id.mute);
        second_img_text = root.findViewById(R.id.second_img_text);
        diagnos = root.findViewById(R.id.diagnos);
        original.setOnClickListener(btnClick);
        noise.setOnClickListener(btnClick);
        mute.setOnClickListener(btnClick);
        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            getData1();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            getData2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showEtalon();
        showEtalon1();
        showEtalon2();
        return root;
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.noise: {
                    smile.setVisibility(View.GONE);
                    diagnos.setVisibility(View.GONE);
                    img2.setVisibility(View.GONE);
                    img2.setImageBitmap(btmp2);
                    img2.setScaleType(ImageView.ScaleType.FIT_XY);
                    img2.setVisibility(View.VISIBLE);
                    img2.setBackground(getResources().getDrawable(R.drawable.black_border));
                    second_img_text.setText("Патологический сигнал");
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler2.post(() -> {
                                id = 1;
                                if (!isMute)
                                    MediaPlayer.create(context, R.raw.sad).start();
                                diagnos.setText("Есть отклонения");
                                diagnos.setVisibility(View.VISIBLE);
                                smile.setImageResource(R.drawable.sad);
                                smile.setVisibility(View.VISIBLE);
                            });
                        }
                    };
                    new Thread(runnable2).start();
                    break;
                }
                case R.id.original: {
                    smile.setVisibility(View.GONE);
                    diagnos.setVisibility(View.GONE);
                    img2.setVisibility(View.GONE);
                    img2.setImageBitmap(btmp);
                    img2.setScaleType(ImageView.ScaleType.FIT_XY);
                    img2.setVisibility(View.VISIBLE);
                    img2.setBackground(getResources().getDrawable(R.drawable.black_border));
                    second_img_text.setText("Нормальный сигнал");
                    Runnable runnable2 = () -> {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler2.post(() -> {
                            smile.setVisibility(View.VISIBLE);
                            id = 2;
                            if (!isMute)
                                MediaPlayer.create(context, R.raw.norm).start();
                            diagnos.setText("Это ваша норма");
                            diagnos.setVisibility(View.VISIBLE);
                            smile.setImageResource(R.drawable.smile);
                        });
                    };
                    new Thread(runnable2).start();
                    break;
                }
                case R.id.mute: {
                    mute.setCompoundDrawablesWithIntrinsicBounds(!isMute ? R.drawable.ic_action_mute : R.drawable.ic_action_sound, 0, 0, 0);
                    isMute = !isMute;
                    break;
                }

            }
        }
    };

    private void getData() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.ethalon);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String eachline = null;
        try {
            eachline = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (eachline != null) {
            ArrayList<Double> na2 = new ArrayList<>();
            String[] s = eachline.split(",");
            for (int i = 0; i < s.length; i++)
                na2.add(Double.parseDouble(s[i]));
            ethalon.add(na2);
            eachline = bufferedReader.readLine();
        }
    }


    private void getData1() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.demo1);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String eachline = null;
        try {
            eachline = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (eachline != null) {
            ArrayList<Double> na2 = new ArrayList<>();
            String[] s = eachline.split(",");
            for (int i = 0; i < s.length; i++)
                na2.add(Double.parseDouble(s[i]));
            demo1.add(na2);
            eachline = bufferedReader.readLine();
        }

    }

    private void getData2() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.demo2);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String eachline = null;
        try {
            eachline = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (eachline != null) {
            ArrayList<Double> na2 = new ArrayList<>();
            String[] s = eachline.split(",");
            for (int i = 0; i < s.length; i++)
                na2.add(Double.parseDouble(s[i]));
            demo2.add(na2);
            eachline = bufferedReader.readLine();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static public int getColor(double power) {
        double H = power * 100;
        double S = 1.0;
        double B = 1.0;
        return Color.HSVToColor(new float[]{(float) H, (float) S, (float) B});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showEtalon1() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        btmp = Bitmap.createBitmap(demo1.size(), demo1.get(0).size(), conf);
        for (int i = 0; i < demo1.size(); i++) {
            for (int j = 0; j < demo1.get(0).size(); j++) {
                int newColor = getColor(1.0 - demo1.get(i).get(j));
                btmp.setPixel(i, j, newColor);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showEtalon2() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        btmp2 = Bitmap.createBitmap(demo2.size(), demo2.get(0).size(), conf);
        for (int i = 0; i < demo2.size(); i++) {
            for (int j = 0; j < demo2.get(0).size(); j++) {
                int newColor = getColor(1.0 - demo2.get(i).get(j));
                btmp2.setPixel(i, j, newColor);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showEtalon() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        btmp3 = Bitmap.createBitmap(ethalon.size(), ethalon.get(0).size(), conf);
        for (int i = 0; i < ethalon.size(); i++) {
            for (int j = 0; j < ethalon.get(0).size(); j++) {
                int newColor = getColor(1.0 - ethalon.get(i).get(j));
                btmp3.setPixel(i, j, newColor);
            }
        }

        img1.setImageBitmap(btmp3);
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        img1.setVisibility(View.VISIBLE);
    }
}