package com.example.test_with_menu.ui.plus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.test_with_menu.MyDialogFragment;
import com.example.test_with_menu.R;
import com.example.test_with_menu.ui.home.HomeFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class PlusFragment extends Fragment {
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRec";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static int RECORDER_SAMPLERATE = 44000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    static private ImageView iw, noise_img, ethalon_img, img_white;
    static private GraphView graph;
    Bitmap btmp;
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean running;
    private long pauseOffset;
    static private LinearLayout squares;
    static public int countsquares = 0;
    private ArrayList<ArrayList<ArrayList<Double>>> signArr;
    static ImageView myImage;
    static private Button btnStart, playAudio, clear_all;
    static private Button build_ethalon;
    static private TextView first_img_text;
    static private TextView second_img_text;
    static private TextView third_img_text;
    static Log log = new Log();
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    static private View line, line2;
    private MediaPlayer mediaPlayer;
    private ArrayList<ArrayList<Double>> ratioArray;
    private Context context;
    double min = 999999999;
    long check = 0;
    long check2 = 0;
    boolean isRunning = false;
    boolean ispaint = false;
    final boolean[] me_check = {true};
    final boolean[] me_check2 = {true};
    final boolean[] me_check3 = {true};
    final boolean[] me_check4 = {true};
    final boolean[] me_check5 = {true};
    String playAudioFile = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    log.write_to_file("Permission has been denied by user");
                } else {
                    log.write_to_file("Permission has been granted by user");
                }
            }
        }
    }

    public void onStop() {
        super.onStop();
        isRunning = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        log.write_to_file("construct app");
        log.write_to_file(String.valueOf(HomeFragment.s.get_Frequency()));
        RECORDER_SAMPLERATE = HomeFragment.s.get_Frequency();
        View root = inflater.inflate(R.layout.fragment_plus, container, false);
        System.out.println(ContextCompat.checkSelfPermission((Activity) root.getContext(),
                Manifest.permission.RECORD_AUDIO));
        int permission = ContextCompat.checkSelfPermission((Activity) root.getContext(),
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            log.write_to_file("Permission to record denied");
            ActivityCompat.requestPermissions((Activity) root.getContext(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission((Activity) root.getContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((Activity) root.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) root.getContext(), new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);
        }
        context = root.getContext();
        if (ContextCompat.checkSelfPermission((Activity) root.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) root.getContext(), new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        btnStart = root.findViewById(R.id.btnStart);
        playAudio = root.findViewById(R.id.playAudio);
        clear_all = root.findViewById(R.id.clear_all);
        ;
        build_ethalon = root.findViewById(R.id.build_ethalon);
        ethalon_img = root.findViewById(R.id.img1);
        squares = root.findViewById(R.id.squares);
        img_white = root.findViewById(R.id.img_white);
        noise_img = root.findViewById(R.id.noise_img);
        enableButton(playAudio, false);
        line = root.findViewById(R.id.line);
        line2 = root.findViewById(R.id.line2);
        first_img_text = root.findViewById(R.id.first_img_text);
        second_img_text = root.findViewById(R.id.second_img_text);
        TextView count = root.findViewById(R.id.count);
        third_img_text = root.findViewById(R.id.third_img_text);
        btnStart.setOnClickListener(btnClick);
        clear_all.setOnClickListener(btnClick);
        playAudio.setOnClickListener(btnClick);
        build_ethalon.setOnClickListener(btnClick);
        myImage = (ImageView) root.findViewById(R.id.img2);
        iw = (ImageView) root.findViewById(R.id.img2);
        log.write_to_file("5");
        graph = (GraphView) root.findViewById(R.id.graph);
        bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        log.write_to_file("6");
        line.bringToFront();
        if (check_warning()) {
            enableButton(build_ethalon, true);
        } else {
            enableButton(build_ethalon, false);
        }
        log.write_to_file("7");
        ArrayList<String> names = new ArrayList<>();
        log.write_to_file("8");
        getName(names);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        countsquares = count_rows();
        for (int i = 0; i < count_rows(); i++) {
            addSquare(names.get(i), i + 1, false, false);
        }
        img_white.setVisibility(View.VISIBLE);
        log.write_to_file("9");
        return root;
    }

    private void getName(ArrayList<String> arr) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        int count = 0;
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                arr.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private int currentSignal = 0;
    static private ArrayList<TextView> squaresAll = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addSquare(String name, int i, boolean isGrey, boolean changeColor) {
        TextView image;
        if (i < 1)
            image = new TextView(this.getContext());
        else
            image = changeColor ? squaresAll.get(i - 1) : new TextView(this.getContext());
        image.setBackgroundResource(isGrey ? R.drawable.square_grey : R.drawable.square_blue);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);

        image.setText(String.valueOf(i));
        image.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        image.setTextColor(Color.parseColor("#FFFFFF"));
        image.setTextSize(20);
        if (!changeColor)
            squaresAll.add(image);
        System.out.println(isGrey + " = isGrey");
        if (!isGrey) {
            image.setOnClickListener(v -> {
                Runnable runnable2 = () -> handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        myImage.setVisibility(View.GONE);
                        graph.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                        first_img_text.setVisibility(View.GONE);
                        second_img_text.setVisibility(View.GONE);
                    }
                });
                new Thread(runnable2);
                Runnable runnable3 = () -> handler2.post(() -> {
                    myImage.setVisibility(View.VISIBLE);
                    graph.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    first_img_text.setVisibility(View.VISIBLE);
                    second_img_text.setVisibility(View.VISIBLE);
                });

                System.out.println(currentSignal + " = curr");
                if (currentSignal != 0) {
                    squaresAll.get(currentSignal - 1).setBackgroundResource(R.drawable.square_blue);
                    enableButton(squaresAll.get(currentSignal - 1), true);
                }
                currentSignal = i;
                image.setBackgroundResource(R.drawable.square_blue_border);
                paintGraph(name);
                playAudioFile = name;
                enableButton(squaresAll.get(currentSignal - 1), false);
                first_img_text.setText("Фоноспирограмма № " + i);
                MediaPlayer.create(context, R.raw.pip).start();
                enableButton(playAudio, true);

                String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + playAudioFile + ".wav";
                System.out.println(filepath);
                try {
                    readWAV2Array audioTest = new readWAV2Array(filepath, true);
                    double[] rawData = audioTest.getByteArray();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
                    double min = 0;
                    double max = 0;
                    for (int i1 = 0; i1 < rawData.length; i1++) {
                        if (rawData[i1] > max)
                            max = rawData[i1];
                        if (rawData[i1] < min)
                            min = rawData[i1];
                        series.appendData(new DataPoint(Double.parseDouble(String.valueOf(i1)) / Double.parseDouble(String.valueOf(RECORDER_SAMPLERATE)), rawData[i1]), true, rawData.length);
                    }
                    graph.addSeries(series);
                    graph.setVisibility(View.VISIBLE);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setMinY(min);
                    graph.getViewport().setMaxY(max);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setScalable(true);
                    second_img_text.setText("Исходный сигнал");
                    line.setVisibility(View.VISIBLE);
                    ethalon_img.setVisibility(View.GONE);
                    second_img_text.setVisibility(View.VISIBLE);
                    myImage.setVisibility(View.VISIBLE);
                    graph.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    myImage.setVisibility(View.VISIBLE);
                    first_img_text.setVisibility(View.VISIBLE);
                    second_img_text.setVisibility(View.VISIBLE);
                    new Thread(runnable3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        image.setLayoutParams(layoutParams);
        if (!changeColor) {
            squares.addView(image);
            TextView tv = new TextView(this.getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(15, 10);
            tv.setLayoutParams(layoutParams1);
            squares.addView(tv);
        } else {
            squaresAll.get(i - 1).setBackgroundResource(R.drawable.square_blue);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void paintGraph(String name) {
        img_white.setVisibility(View.GONE);
        String filepath = Environment.getExternalStorageDirectory().getPath();
        ArrayList<String> file_data = new ArrayList<>();
        ArrayList<ArrayList<Double>> gr = new ArrayList<>();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/" + name + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                ArrayList<Double> na2 = new ArrayList<>();
                String[] s = line.split(",");
                for (int i = 0; i < s.length; i++) {
                    na2.add(Double.parseDouble(s[i]));
                }
                gr.add(na2);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        btmp = Bitmap.createBitmap(gr.size(), gr.get(0).size(), conf);
        for (int i = 0; i < gr.size(); i++) {
            for (int j = 0; j < gr.get(0).size(); j++) {
                int newColor = getColor(1.0 - gr.get(i).get(j));
                btmp.setPixel(i, j, newColor);
            }
        }

        myImage.setImageBitmap(btmp);
        myImage.setScaleType(ImageView.ScaleType.FIT_XY);
        myImage.setVisibility(View.VISIBLE);
        graph.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        second_img_text.setVisibility(View.GONE);
    }

    private static void enableButton(Button butt, boolean isEnable) {
        butt.setEnabled(isEnable);
    }

    private static void enableButton(TextView butt, boolean isEnable) {
        butt.setEnabled(isEnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFilename() {
        log.write_to_file("get Filename ");
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTempFilename() {
        log.write_to_file("get Temp Filename ");
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);
        if (tempFile.exists())
            tempFile.delete();
        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() {
        for (int i = 0; i < squaresAll.size(); i++) {
            System.out.println(i + " = i");
            squaresAll.get(i).setBackgroundResource(R.drawable.square_grey);
            enableButton(squaresAll.get(i), false);
        }
        me_check3[0] = true;
        ethalon_img.setVisibility(View.GONE);
        first_img_text.setVisibility(View.GONE);
        myImage.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        second_img_text.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        log.write_to_file("start recording ");
        enableButton(btnStart, false);
        enableButton(playAudio, false);
        enableButton(build_ethalon, false);
        enableButton(clear_all, false);
        pauseOffset = 0;
        if (!running) {
            running = true;
        }
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        int i = recorder.getState();
        if (i == 1)
            recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(() -> {
            writeAudioDataToFile();
            MediaPlayer.create(context, R.raw.long_pip).start();
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeAudioDataToFile() {
        log.write_to_file("write audio data to file ");
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        int read = 0;
        if (null != os) {
            while (isRecording) {
                if (me_check3[0]) {
                    MediaPlayer.create(context, R.raw.long_pip).start();
                    me_check3[0] = false;
                }

                if (check < HomeFragment.s.get_Time() + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale() - 1 && !me_check4[0]) {
                    if ((check == HomeFragment.s.get_Time())) {
                        read = recorder.read(data, 0, bufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (me_check2[0]) {
                            read = recorder.read(data, 0, bufferSize);
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                try {
                                    os.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopRecording() throws IOException {
        log.write_to_file("stop recording ");
        if (running) {
            running = false;
        }
        if (null != recorder) {
            isRecording = false;
            int i = recorder.getState();
            if (i == 1)
                recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(), getFilename());
        deleteTempFile();
        Runnable runnable2 = () -> handler2.post(() -> {
            third_img_text.setVisibility(View.VISIBLE);
            paint();
            ispaint = false;
            for ( int i = 0; i < squaresAll.size(); i++ )
                squaresAll.get(i).setText(String.valueOf(i+1));
        });
        paintgraph();
        third_img_text.setText("Построение фоноспирограммы");
        new Thread(runnable2).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void paintgraph() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String file_name = null;
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                System.out.println("line = " + line);
                file_name = line;
                System.out.println(file_name);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        filepath = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + file_name + ".wav";
        System.out.println(filepath);
        try {
            readWAV2Array audioTest = new readWAV2Array(filepath, true);
            double[] rawData = audioTest.getByteArray();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
            double min = 0;
            double max = 0;
            for (int i = 0; i < rawData.length; i++) {
                if (rawData[i] > max)
                    max = rawData[i];
                if (rawData[i] < min)
                    min = rawData[i];
                series.appendData(new DataPoint(Double.parseDouble(String.valueOf(i)) / Double.parseDouble(String.valueOf(RECORDER_SAMPLERATE)), rawData[i]), true, rawData.length);
            }
            graph.addSeries(series);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(min);
            graph.getViewport().setMaxY(max);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
            graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
            graph.setVisibility(View.VISIBLE);
            second_img_text.setText("Исходный сигнал");
            line.setVisibility(View.VISIBLE);
            second_img_text.setVisibility(View.VISIBLE);
            third_img_text.setText("Построение фоноспирограммы");
            third_img_text.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int count_rows() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        int count = 0;
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteTempFile() throws IOException {
        log.write_to_file("delete temp file ");
        File file = new File(getTempFilename());
        file.delete();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void copyWaveFile(String inFilename, String outFilename) {
        log.write_to_file("copy Wave File ");
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
            writeNameAudio(outFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeNameAudio(String outFile) {
        log.write_to_file("write Name Audio ");
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file.getAbsolutePath() + "/savedAudio.txt", true)) {
            writer.write(outFile.split("/")[5].split("\\.")[0]);
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {
        log.write_to_file("Write Wave File Header ");
        byte[] header = new byte[44];
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        header[34] = RECORDER_BPP;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart: {
                    final int[] ost = {0};
                    final double[] pip = {10000000};
                    me_check4[0] = true;
                    me_check5[0] = true;
                    final boolean[] me_check_ost = {
                            true
                    };
                    new CountDownTimer(HomeFragment.s.get_Time() * 1000 + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale() * 1000 + 3000, 100) {
                        public void onTick(long millisUntilFinished) {
                            check2 = millisUntilFinished;
                            check = millisUntilFinished / 1000;
                            if (check <= HomeFragment.s.get_Time() + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale()) {
                                if (pip[0] - check2 / 100 > 10) {
                                    me_check2[0] = true;
                                }
                                if (check == 1 && me_check5[0]) {
                                    addSquare(getTempFilename(), countsquares + 1, true, false);
                                    me_check5[0] = false;
                                }
                                if (check == HomeFragment.s.get_Time() + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale() - 1 && me_check4[0]) {
                                    int o = (int) (check % HomeFragment.s.get_Time());
                                    ost[0] = o;
                                    MediaPlayer.create(context, R.raw.pip).start();
                                    me_check4[0] = false;
                                } else {
                                    if (check % HomeFragment.s.get_Inhale() == ost[0] && me_check[0] && check2 / 100 > 10) {
                                        System.out.println(check2 / 100);
                                        me_check_ost[0] = true;
                                        me_check[0] = false;
                                        if (check < HomeFragment.s.get_Time()) {
                                            pip[0] = check2 / 100;
                                            me_check4[0] = false;
                                            me_check2[0] = false;
                                            MediaPlayer.create(context, R.raw.pip).start();
                                        }
                                    }
                                    if (check % HomeFragment.s.get_Inhale() != ost[0]) {
                                        me_check[0] = true;
                                        if (check % HomeFragment.s.get_Inhale() != (ost[0] == 0 ? HomeFragment.s.get_Inhale() - 1 : ost[0] - 1) && me_check_ost[0]) {
                                            ost[0] = ost[0] == 0 ? HomeFragment.s.get_Inhale() - 1 : ost[0] - 1;
                                            me_check_ost[0] = false;
                                        }
                                    }
                                }
                            }
                        }

                        public void onFinish() {
                            try {
                                stopRecording();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    startRecording();
                    break;
                }
                case R.id.playAudio: {
                    try {
                        playAudio();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.clear_all: {
                    clearAll();
                    break;
                }
                case R.id.build_ethalon: {
                    read_bitmaps();
                    break;
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void read_bitmaps() {
        signArr = new ArrayList<>();
        enableButton(playAudio, false);
        noise_img.setVisibility(View.GONE);
        first_img_text.setVisibility(View.GONE);
        second_img_text.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        myImage.setVisibility(View.GONE);
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                fileRead(line);
                fileNames.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = getDistance2();
        try (FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + "ethalons" + ".txt", false)) {
            writer.write(String.valueOf(fileNames.get(index)) + ":" + min);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<Double>> arr = new ArrayList<>();
        try {
            File file = new File(f.getAbsolutePath() + "/" + fileNames.get(index) + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                ArrayList<Double> na2 = new ArrayList<>();
                String[] s = line.split(",");
                for (int i = 0; i < s.length; i++)
                    na2.add(Double.parseDouble(s[i]));
                arr.add(na2);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap btmp2 = Bitmap.createBitmap(arr.size(), arr.get(0).size(), conf);
        for (int i = 0; i < arr.size(); i++) {
            for (int j = 0; j < arr.get(i).size(); j++) {
                int newColor = getColor(1.0 - arr.get(i).get(j));
                btmp2.setPixel(i, j, newColor);
            }
        }
        ethalon_img.setImageBitmap(btmp2);
        img_white.setVisibility(View.GONE);
        ethalon_img.setScaleType(ImageView.ScaleType.FIT_XY);
        ethalon_img.setVisibility(View.VISIBLE);
        if (currentSignal != 0)
            squaresAll.get(currentSignal - 1).setBackgroundResource(R.drawable.square_blue);
        first_img_text.setText("Эталон");
        graph.setVisibility(View.GONE);
        int cycle = (index + 1);
        first_img_text.setText("Построение завершено, эталонным был выбран цикл " + cycle);
        first_img_text.setVisibility(View.VISIBLE);
    }

    private void fileRead(String path) {
        ArrayList<ArrayList<Double>> na = new ArrayList<>();
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/" + path + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                ArrayList<Double> na2 = new ArrayList<>();
                String[] s = line.split(",");
                for (int i = 0; i < s.length; i++)
                    na2.add(Double.parseDouble(s[i]));
                na.add(na2);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        signArr.add(na);
    }

    private int getDistance2() {
        min = 999999;
        ArrayList<ArrayList<Double>> summArr = new ArrayList<>();
        int min_width = signArr.get(0).get(0).size();
        int min_height = signArr.get(0).size();
        for (int i = 0; i < signArr.size(); i++) {
            if (signArr.get(i).get(0).size() < min_width)
                min_width = signArr.get(i).get(0).size();
            if (signArr.get(i).size() < min_height)
                min_height = signArr.get(i).size();
        }
        for (int i = 0; i < signArr.size(); i++) {
            ArrayList<Double> summArr2 = new ArrayList<>();
            for (int j = 0; j < signArr.size(); j++) {
                ArrayList<Double> summMins = new ArrayList<>();
                for (int shift = 0; shift < 1; shift++) {
                    double summ = 0;
                    for (int w = 0; w < min_height - shift; w++) {
                        for (int h = 0; h < min_width; h++) {
                            double a = signArr.get(i).get(w).get(h);
                            double b = signArr.get(j).get(w + shift).get(h);
                            if (a < 0.5)
                                a = 0;
                            if (b < 0.5)
                                b = 0;
                            summ += Math.abs(a - b);
                        }
                    }
                    summMins.add(summ);
                }
                double ms = summMins.get(0);
                for (int b = 1; b < summMins.size(); b++) {
                    if (ms > summMins.get(b))
                        ms = summMins.get(b);
                }
                summArr2.add(ms / 10000 * 10);
            }
            summArr.add(summArr2);
        }
        int index = 0;
        for (int i = 0; i < summArr.size(); i++) {
            double sum = 0;
            for (int j = 0; j < summArr.get(i).size(); j++)
                sum += summArr.get(i).get(j);
            double s = sum / (summArr.size() - 1);
            if (min > s) {
                min = s;
                index = i;
            }
        }
        return index;
    }

    static public void clearAllTrue() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(filepath, AUDIO_RECORDER_FOLDER);
        for (final File fileEntry : dir.listFiles()) {
            if ((getFileExtension(fileEntry).equals("wav") || getFileExtension(fileEntry).equals("jpg") || getFileExtension(fileEntry).equals("txt")) && !fileEntry.getName().equals("settings.txt"))
                fileEntry.delete();
        }
        clearTheFile();
        currentSignal = -1;
        countsquares = 0;
        squaresAll.clear();
        for ( int i = 0; i < squaresAll.size(); i++ )
            squaresAll.remove(i);
        enableButton(build_ethalon, false);
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
        ethalon_img.setVisibility(View.GONE);
        noise_img.setVisibility(View.GONE);
        first_img_text.setVisibility(View.GONE);
        second_img_text.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        myImage.setVisibility(View.GONE);
        squares.removeAllViews();
    }

    public void clearAll() {
        MyDialogFragment myDialogFragment = new MyDialogFragment("Все записи и эталоны будут сброшены", "Да", "Нет", "Сброс", "clearAll");
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        myDialogFragment.show(transaction, "dialog");

    }

    private void playAudio() throws IOException {
        enableButton(playAudio, false);
        mediaPlayer = new MediaPlayer();
        enableButton(clear_all, false);
        enableButton(build_ethalon, false);
        enableButton(btnStart, false);
        for (int i = 0; i < squaresAll.size(); i++)
            squaresAll.get(i).setBackgroundResource(R.drawable.square_grey);
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(f.getAbsolutePath() + "/" + playAudioFile + ".wav");
        mediaPlayer.prepare();
        mediaPlayer.start();
        float start = line.getX();
        TranslateAnimation animation = new TranslateAnimation(line.getX(), line2.getX() - 5, 0, 0);
        animation.setRepeatMode(0);
        animation.setDuration(HomeFragment.s.get_Time() * 1000);
        animation.setFillAfter(true);
        line.startAnimation(animation);
        Runnable runnable3 = () -> {
            doFakeWorkPlay(HomeFragment.s.get_Time());
            handler.post(() -> {
                enableButton(playAudio, true);
                enableButton(clear_all, true);
                if (check_warning())
                    enableButton(build_ethalon, true);
                enableButton(btnStart, true);
                for (int i = 0; i < squaresAll.size(); i++)
                    squaresAll.get(i).setBackgroundResource(R.drawable.square_blue);
                TranslateAnimation animation1 = new TranslateAnimation(start, line2.getX() - 5, 0, 0);
                animation1.setRepeatMode(0);
                animation1.setDuration(HomeFragment.s.get_Time() * 0);
                line.startAnimation(animation1);
            });
        };
        new Thread(runnable3).start();
    }

    private void doFakeWorkPlay(int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    public void paint() {
        ispaint = true;
        third_img_text.setVisibility(View.VISIBLE);
        third_img_text.setText("Построение фоноспирограммы");
        log.write_to_file("paint ");
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String file_name = null;
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                file_name = line;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playAudioFile = file_name;
        filepath = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + file_name + ".wav";
        try {
            readWAV2Array audioTest = new readWAV2Array(filepath, true);
            double[] rawData = audioTest.getByteArray();
            double min = 0;
            double max = 0;
            for (int i = 0; i < rawData.length; i++) {
                if (rawData[i] > max)
                    max = rawData[i];
                if (rawData[i] < min)
                    min = rawData[i];
            }
            int length = rawData.length;
            int WS = 4096;
            int OF = 8;
            int windowStep = WS / OF;
            double SR = audioTest.getSR();
            int nX = (length - WS) / windowStep;
            int nY = WS / 2 + 1;
            double[][] plotData = new double[nX][nY];
            double maxAmp = Double.MIN_VALUE;
            double minAmp = Double.MAX_VALUE;
            double amp_square;
            double[] inputImag = new double[length];
            for (int i = 0; i < nX; i++) {
                Arrays.fill(inputImag, 0.0);
                double[] WS_array = FFT.fft(Arrays.copyOfRange(rawData, i * windowStep, i * windowStep + WS), inputImag, true);
                for (int j = 0; j < nY; j++) {
                    amp_square = (WS_array[2 * j] * WS_array[2 * j]) + (WS_array[2 * j + 1] * WS_array[2 * j + 1]);
                    if (amp_square == 0.0)
                        plotData[i][j] = amp_square;
                    else
                        plotData[i][nY - j - 1] = 10 * Math.log10(amp_square);
                    if (plotData[i][j] > maxAmp)
                        maxAmp = plotData[i][j];
                    else if (plotData[i][j] < minAmp)
                        minAmp = plotData[i][j];
                }
            }
            maxAmp = 100.0;
            minAmp = -70.0;
            myImage.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            first_img_text.setVisibility(View.VISIBLE);
            second_img_text.setVisibility(View.VISIBLE);
            first_img_text.setText("Фоноспирограмма № " + (countsquares + 1));
            second_img_text.setText("Исходный сигнал");
            double diff = maxAmp - minAmp;
            for (int i = 0; i < nX; i++) {
                for (int j = 0; j < nY; j++)
                    plotData[i][j] = (plotData[i][j] - minAmp) / diff;
            }
            double l = length / SR;
            int step_x = (int) (l * 20) + 1;
            int step_y = 60;
            double stepCeil = Double.parseDouble(String.valueOf(Double.parseDouble(String.valueOf(nX)) / (Double.parseDouble(String.valueOf(step_x)))));
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            btmp = Bitmap.createBitmap(step_x < nX ? nX / (int) (Math.ceil(stepCeil)) : nX, step_y, conf);
            ratioArray = new ArrayList<>();
            try (FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + file_name + ".txt", true)) {
                int sx = 0;
                for (int x = 0; x < nX && sx < step_x; x += nX < step_x ? 1 : (int) (Math.ceil(stepCeil))) {
                    int sy = 0;
                    ArrayList<Double> ra = new ArrayList<>();
                    for (int y = nY / 100 * 50, yy = 0; y < nY; y += (nY - nY / 100 * 50) / step_y + 1, yy++) {
                        double ratio = plotData[x][y];
                        ra.add(ratio);
                        int newColor = getColor(1.0 - ratio);
                        try {
                            btmp.setPixel(sx, sy++, newColor);
                        } catch (Exception e) {
                            System.out.println(1);
                        }
                        writer.write(String.valueOf(ratio) + ",");
                        writer.flush();
                    }
                    ratioArray.add(ra);
                    sx++;

                    writer.append("\n");
                    writer.flush();
                }
            }
            myImage.setImageBitmap(btmp);
            myImage.setScaleType(ImageView.ScaleType.FIT_XY);
            img_white.setVisibility(View.GONE);
            enableButton(playAudio, true);
            enableButton(btnStart, true);
            enableButton(clear_all, true);
            third_img_text.setVisibility(View.GONE);
            squaresAll.get(countsquares).setBackgroundResource(R.drawable.square_blue_border);
            countsquares++;
            currentSignal = countsquares;
            for (int i = 0; i < squaresAll.size(); i++) {
                squaresAll.get(i).setBackgroundResource(R.drawable.square_blue);
                enableButton(squaresAll.get(i), true);
                if (i + 1 == squaresAll.size())
                    squaresAll.get(i).setBackgroundResource(R.drawable.square_blue_border);
            }
            addSquare(file_name, countsquares, false, true);
            squaresAll.get(squaresAll.size() - 1).setBackgroundResource(R.drawable.square_blue_border);
            ethalon_img.setVisibility(View.GONE);
            if (check_warning())
                enableButton(build_ethalon, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean check_warning() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        int count = 0;
        try {
            File file = new File(f.getAbsolutePath() + "/savedAudio.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (count < HomeFragment.s.get_Size())
            return false;
        else
            return true;
    }
}