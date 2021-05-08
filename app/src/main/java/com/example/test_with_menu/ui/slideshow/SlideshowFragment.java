package com.example.test_with_menu.ui.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.test_with_menu.R;
import com.example.test_with_menu.ui.home.HomeFragment;
import com.example.test_with_menu.ui.plus.FFT;
import com.example.test_with_menu.ui.plus.readWAV2Array;
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
import java.util.ArrayList;
import java.util.Arrays;

public class SlideshowFragment extends Fragment {
    private TextView warning_text, diagnos;
    private ImageView img, smile;
    static private Button btnStart;
    static private final String AUDIO_RECORDER_FOLDER = "AudioRec";
    static ImageView myImage;
    Bitmap btmp;
    String NAME = "";
    double min = 999999999;
    private MediaPlayer mediaPlayer;
    private ArrayList<ArrayList<Double>> ratioArray;
    private Context context;
    final boolean[] me_check = {true};
    long check = 0;
    long check2 = 0;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    static private TextView first_img_text;
    static private TextView second_img_text;
    static private TextView third_img_text;
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static int RECORDER_SAMPLERATE = 44000;
    static private ImageView ethalon_img, img3;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private static String TAG = "PermissionDemo";
    private boolean running;
    final boolean[] me_check2 = {true};
    final boolean[] me_check3 = {true};

    final boolean[] me_check4 = {true};

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        RECORDER_SAMPLERATE = HomeFragment.s.get_Frequency();
        context = root.getContext();
        warning_text = root.findViewById(R.id.warning_text);
        btnStart = root.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(btnClick);
        img = root.findViewById(R.id.img2);
        btnStart = root.findViewById(R.id.btnStart);
        ethalon_img = root.findViewById(R.id.img1);
        first_img_text = root.findViewById(R.id.first_img_text);
        second_img_text = root.findViewById(R.id.second_img_text);
        img3 = root.findViewById(R.id.img3);
        TextView count = root.findViewById(R.id.count);
        smile = root.findViewById(R.id.smile);
        diagnos = root.findViewById(R.id.diagnos);
        third_img_text = root.findViewById(R.id.third_img_text);
        btnStart.setOnClickListener(btnClick);
        myImage = (ImageView) root.findViewById(R.id.img2);
        bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (!check_warning() || !checkEtalonFile()) {
            warning_text.setText("Анализ недоступен, пока у вас не будет построен эталон");
            warning_text.setVisibility(View.VISIBLE);
            enableButton(btnStart, false);
        } else {
            warning_text.setVisibility(View.GONE);
            first_img_text.setText("Эталонная фоноспирограмма");
            img.setVisibility(View.VISIBLE);
            showEtalon();
        }
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showEtalon() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        ArrayList<String> file_data = new ArrayList<>();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/ethalons.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                String[] split_line = line.split(":");
                line = reader.readLine();
                for (int i = 0; i < split_line.length; i++)
                    file_data.add(split_line[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<Double>> ethalon = new ArrayList<>();
        f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/" + file_data.get(0) + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                ArrayList<Double> na2 = new ArrayList<>();
                String[] s = line.split(",");
                for (int i = 0; i < s.length; i++)
                    na2.add(Double.parseDouble(s[i]));
                ethalon.add(na2);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        btmp = Bitmap.createBitmap(ethalon.size(), ethalon.get(0).size(), conf);
        for (int i = 0; i < ethalon.size(); i++) {
            for (int j = 0; j < ethalon.get(0).size(); j++)
                btmp.setPixel(i, j, getColor(1.0 - ethalon.get(i).get(j)));
        }
        ethalon_img.setImageBitmap(btmp);
        ethalon_img.setScaleType(ImageView.ScaleType.FIT_XY);
        ethalon_img.setVisibility(View.VISIBLE);
        myImage.setVisibility(View.VISIBLE);
        myImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void equalWithEtalon() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        ArrayList<String> file_data = new ArrayList<>();
        File f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/ethalons.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                String[] split_line = line.split(":");
                line = reader.readLine();
                for (int i = 0; i < split_line.length; i++)
                    file_data.add(split_line[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<Double>> ethalon = new ArrayList<>();
        f = new File(filepath, AUDIO_RECORDER_FOLDER);
        try {
            File file = new File(f.getAbsolutePath() + "/" + file_data.get(0) + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                ArrayList<Double> na2 = new ArrayList<>();
                String[] s = line.split(",");
                for (int i = 0; i < s.length; i++)
                    na2.add(Double.parseDouble(s[i]));
                ethalon.add(na2);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int min_h = Math.min(ethalon.size(), ratioArray.size());
        int min_w = Math.min(ethalon.get(0).size(), ratioArray.get(0).size());
        ArrayList<Double> summArr2 = new ArrayList<>();
        for (int shift = 0; shift < 1; shift++) {
            double summ = 0;
            for (int i = 0; i < min_h; i++) {
                for (int t = 0; t < shift; t++) {
                    double last_elem = ratioArray.get(i).get(ratioArray.get(i).size() - t - 1);
                    double curr_elem = ratioArray.get(i).get(t);
                    ratioArray.get(i).set(t, last_elem);
                    ratioArray.get(i).set(ratioArray.get(i).size() - t - 1, curr_elem);
                }
                for (int j = 0; j < min_w; j++) {
                    double a = ratioArray.get(i).get(j);
                    double b = ethalon.get(i).get(j);
                    if (a < 0.5)
                        a = 0;
                    if (b < 0.5)
                        b = 0;
                    summ += Math.abs(a - b);
                }
            }
            summArr2.add(summ);
        }
        min = 999999999999.0;
        for (int i = 0; i < summArr2.size(); i++) {
            if (min > summArr2.get(i))
                min = summArr2.get(i);
        }
        double s = min / 10000 * 10;
        double st = Double.parseDouble(file_data.get(1)) / 100.0 * HomeFragment.s.get_Koeff() * 5;
        double top = Double.parseDouble(file_data.get(1)) + st;
        if (s > top) {
            MediaPlayer.create(context, R.raw.sad).start();
            diagnos.setText("Есть отклонения");
            smile.setImageResource(R.drawable.sad);
        } else {
            MediaPlayer.create(context, R.raw.norm).start();
            diagnos.setText("Это ваша норма");
            smile.setImageResource(R.drawable.smile);
        }
        smile.setVisibility(View.VISIBLE);
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

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart: {
                    final int[] ost = {0};
                    final double[] pip = {10000000};
                    me_check4[0] = true;
                    final boolean[] me_check_ost = {
                            true
                    };
                    new CountDownTimer(HomeFragment.s.get_Time() * 1000 + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale() * 1000 + 3000, 100) {
                        public void onTick(long millisUntilFinished) {
                            check2 = millisUntilFinished;
                            check = millisUntilFinished / 1000;
                            if (check <= HomeFragment.s.get_Time() + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale()) {
                                if (pip[0] - check2 / 100 > 10)
                                    me_check2[0] = true;
                                if (check == HomeFragment.s.get_Time() + HomeFragment.s.get_Time() / HomeFragment.s.get_Inhale() - 1 && me_check4[0]) {
                                    int o = (int) (check % HomeFragment.s.get_Time());
                                    ost[0] = o;
                                    MediaPlayer.create(context, R.raw.pip).start();
                                    me_check4[0] = false;
                                } else {
                                    if (check % HomeFragment.s.get_Inhale() == ost[0] && me_check[0] && check2 / 100 > 10) {
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
                            stopRecording();
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
            }
        }
    };

    private static void enableButton(Button butt, boolean isEnable) {
        butt.setEnabled(isEnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() {
        smile.setVisibility(View.GONE);
        diagnos.setVisibility(View.GONE);
        me_check3[0] = true;
        img3.setVisibility(View.VISIBLE);
        img.setVisibility(View.GONE);
        second_img_text.setVisibility(View.GONE);
        enableButton(btnStart, false);
        long pauseOffset = 0;
        if (!running)
            running = true;
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
    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists())
            file.mkdirs();
        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);
        if (tempFile.exists())
            tempFile.delete();
        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read;

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
    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists())
            file.mkdirs();
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopRecording() {
        if (running)
            running = false;
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
            paint();
            equalWithEtalon();
        });
        third_img_text.setVisibility(View.GONE);
        third_img_text.setText("Построение фоноспирограммы");
        new Thread(runnable2).start();
    }

    private boolean checkEtalonFile() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists())
            file.mkdirs();
        File f1 = new File(file.getAbsolutePath() + "/" + "ethalons.txt");
        if (!f1.exists() && !f1.isDirectory())
            return false;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
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
            while (in.read(data) != -1)
                out.write(data);
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
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        NAME = outFile.split("/")[5].split("\\.")[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {
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


    private void playAudio() throws IOException {
        mediaPlayer = new MediaPlayer();
        enableButton(btnStart, false);
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
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(f.getAbsolutePath() + "/" + file_name + ".wav");
        mediaPlayer.prepare();
        mediaPlayer.start();
        Runnable runnable3 = () -> {
            doFakeWorkPlay(HomeFragment.s.get_Time());
            handler.post(() -> enableButton(btnStart, true));
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

        third_img_text.setVisibility(View.VISIBLE);
        String file_name = NAME;
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + file_name + ".wav";
        try {
            readWAV2Array audioTest = new readWAV2Array(filepath, true);
            double[] rawData = audioTest.getByteArray();
            int length = rawData.length;
            int WS = 4096;
            int OF = 8;
            int windowStep = WS / OF;
            double SR = audioTest.getSR();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
            });
            double min = 0;
            double max = 0;
            for (int i = 0; i < rawData.length; i++) {
                if (rawData[i] > max)
                    max = rawData[i];
                if (rawData[i] < min)
                    min = rawData[i];
                series.appendData(new DataPoint(Double.parseDouble(String.valueOf(i)) / Double.parseDouble(String.valueOf(RECORDER_SAMPLERATE)), rawData[i]), true, rawData.length);
            }
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
            first_img_text.setVisibility(View.VISIBLE);
            second_img_text.setVisibility(View.VISIBLE);
            first_img_text.setText("Эталонная фоноспирограмма");
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
                for (int x = 0; x < nX && sx < (step_x < nX ? nX / (int) (Math.ceil(stepCeil)) : nX); x += nX < step_x ? 1 : (int) (Math.ceil(stepCeil))) {
                    int sy = 0;
                    ArrayList<Double> ra = new ArrayList<>();
                    for (int y = nY / 100 * 50, yy = 0; y < nY; y += (nY - nY / 100 * 50) / step_y + 1, yy++) {
                        double ratio = plotData[x][y];
                        ra.add(ratio);
                        int newColor = getColor(1.0 - ratio);
                        btmp.setPixel(sx, sy++, newColor);
                        writer.write(ratio + ",");
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
            enableButton(btnStart, true);
            third_img_text.setVisibility(View.GONE);
            second_img_text.setText("Записанная фоноспирограмма");
            second_img_text.setText("Записанная фоноспирограмма");
            img3.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}