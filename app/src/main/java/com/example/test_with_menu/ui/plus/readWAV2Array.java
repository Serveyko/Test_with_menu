package com.example.test_with_menu.ui.plus;


import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class readWAV2Array {
    private byte[] entireFileData;

    public double getSR() {
        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(entireFileData, 24, 28)); // big-endian by default
        double SR = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        return SR;
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        ;
        try {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    public static byte[] read(File file) {
        FileInputStream input = null;
        if (file.exists()) try {
            input = new FileInputStream(file);
            int len = (int) file.length();
            byte[] data = new byte[len];
            int count, total = 0;
            while ((count = input.read(data, total, len - total)) > 0) total += count;
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) try {
                input.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    byte[] fileToBytes(File file) {
        byte[] bytes = new byte[0];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public readWAV2Array(String filepath, boolean print_info) throws IOException {
        Log.write_to_file("read ");
        byte[] bytes;
        RandomAccessFile f = null;
        File file = new File(filepath);
        try {
            Log.write_to_file("read 1");
            Path path = Paths.get(filepath);
            this.entireFileData = Files.readAllBytes(path);
        } catch (NoClassDefFoundError ex1) {
            try {
                Log.write_to_file("read 2");
                f = new RandomAccessFile(filepath, "r");
                byte[] b = new byte[(int) f.length()];
                f.readFully(b);
                this.entireFileData = b;
            } catch (NoClassDefFoundError ex2) {
                try {
                    Log.write_to_file("read 3");
                    File ff = new File(filepath);
                    byte[] fileContent = Files.readAllBytes(ff.toPath());
                    this.entireFileData = fileContent;
                } catch (NoClassDefFoundError ex3) {
                    try {
                        Log.write_to_file("read 4");
                        file = new File(filepath);
                        bytes = new byte[(int) file.length()];

                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(file);
                            fis.read(bytes);
                        } finally {
                            if (fis != null) {
                                fis.close();
                            }
                        }
                        this.entireFileData = bytes;
                    } catch (NoClassDefFoundError ex4) {
                        try {
                            Log.write_to_file("read 5");
                            int size = (int) file.length();
                            bytes = new byte[size];
                            try {
                                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                                buf.read(bytes, 0, bytes.length);
                                buf.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            this.entireFileData = bytes;
                        } catch (NoClassDefFoundError ex5) {
                            try {
                                Log.write_to_file("read 6");
                                bytes = FileUtils.readFileToByteArray(file);
                                this.entireFileData = bytes;
                            } catch (NoClassDefFoundError ex6) {
                                try {
                                    Log.write_to_file("read 7");
                                    bytes = fullyReadFileToBytes(file);
                                    this.entireFileData = bytes;
                                } catch (NoClassDefFoundError ex7) {
                                    try {
                                        Log.write_to_file("read 8");
                                        bytes = new byte[(int) file.length()];
                                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                                        DataInputStream dis = new DataInputStream(bis);
                                        dis.readFully(bytes);
                                        this.entireFileData = bytes;
                                    } catch (NoClassDefFoundError ex8) {
                                        try {
                                            Log.write_to_file("read 9");
                                            bytes = read(file);
                                            this.entireFileData = bytes;
                                        } catch (NoClassDefFoundError ex9) {
                                            try {
                                                Log.write_to_file("read 10");
                                                bytes = fileToBytes(file);
                                                this.entireFileData = bytes;
                                            } catch (NoClassDefFoundError ex10) {
                                                try {
                                                    Log.write_to_file("read 11");
                                                    bytes = FileUtils.readFileToByteArray(new File(filepath));
                                                    this.entireFileData = bytes;
                                                } catch (NoClassDefFoundError ex11) {
                                                    try {
                                                        Log.write_to_file("read 12");
                                                        bytes = IOUtils.toByteArray(new FileInputStream(filepath));
                                                        this.entireFileData = bytes;
                                                    } catch (NoClassDefFoundError ex12) {
                                                        try {
                                                            Log.write_to_file("read 13");
                                                            bytes = new byte[(int) f.length()];
                                                            FileInputStream is = new FileInputStream(file);
                                                            is.read(bytes);
                                                            is.close();
                                                            this.entireFileData = bytes;
                                                        } catch (NoClassDefFoundError ex13) {
                                                            RandomAccessFile fff = new RandomAccessFile(filepath, "rw");
                                                            bytes = new byte[(int) fff.length()];
                                                            f.readFully(bytes);
                                                            this.entireFileData = bytes;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


//        RandomAccessFile f = new RandomAccessFile(filepath, "r");
//        byte[] b = new byte[(int)f.length()];
//        f.readFully(b);
//        this.entireFileData = b;

        if (print_info) {

            //extract format
            String format = new String(Arrays.copyOfRange(entireFileData, 8, 12), "UTF-8");

            //extract number of channels
            int noOfChannels = entireFileData[22];
            String noOfChannels_str;
            if (noOfChannels == 2)
                noOfChannels_str = "2 (stereo)";
            else if (noOfChannels == 1)
                noOfChannels_str = "1 (mono)";
            else
                noOfChannels_str = noOfChannels + "(more than 2 channels)";

            //extract sampling rate (SR)
            int SR = (int) this.getSR();

            //extract Bit Per Second (BPS/Bit depth)
            int BPS = entireFileData[34];

            System.out.println("---------------------------------------------------");
            System.out.println("File path:          " + filepath);
            System.out.println("File format:        " + format);
            System.out.println("Number of channels: " + noOfChannels_str);
            System.out.println("Sampling rate:      " + SR);
            System.out.println("Bit depth:          " + BPS);
            System.out.println("---------------------------------------------------");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public double[] getByteArray() {
        Log.write_to_file("get Byte Array ");
        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
        int totalLength = data_raw.length;

        //declare double array for mono
        int new_length = totalLength / 4;
        double[] data_mono = new double[new_length];

        double left, right;
        for (int i = 0; 4 * i + 3 < totalLength; i++) {
            left = (short) ((data_raw[4 * i + 1] & 0xff) << 8) | (data_raw[4 * i] & 0xff);
            right = (short) ((data_raw[4 * i + 3] & 0xff) << 8) | (data_raw[4 * i + 2] & 0xff);
            data_mono[i] = (left + right) / 2.0;
        }
        return data_mono;
    }
}


//
//package com.example.test_with_menu.ui.plus;
//
//        import android.content.Context;
//        import android.os.Build;
//
//        import androidx.annotation.RequiresApi;
//
//        import org.apache.commons.io.FileUtils;
//        import org.apache.commons.io.IOUtils;
//
//        import java.io.BufferedInputStream;
//        import java.io.ByteArrayOutputStream;
//        import java.io.DataInputStream;
//        import java.io.File;
//        import java.io.FileInputStream;
//        import java.io.FileNotFoundException;
//        import java.io.IOException;
//        import java.io.InputStream;
//        import java.io.RandomAccessFile;
//        import java.nio.ByteBuffer;
//        import java.nio.file.Files;
//        import java.nio.file.Path;
//        import java.nio.file.Paths;
//        import java.util.Arrays;
//
//public class readWAV2Array {
//
//    private byte[] entireFileData;
//
//    //SR = sampling rate
//    public double getSR(){
//        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(entireFileData, 24, 28)); // big-endian by default
//        double SR = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
//        return SR;
//    }
//
//    byte[] fullyReadFileToBytes(File f) throws IOException {
//        int size = (int) f.length();
//        byte bytes[] = new byte[size];
//        byte tmpBuff[] = new byte[size];
//        FileInputStream fis= new FileInputStream(f);;
//        try {
//
//            int read = fis.read(bytes, 0, size);
//            if (read < size) {
//                int remain = size - read;
//                while (remain > 0) {
//                    read = fis.read(tmpBuff, 0, remain);
//                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
//                    remain -= read;
//                }
//            }
//        }  catch (IOException e){
//            throw e;
//        } finally {
//            fis.close();
//        }
//
//        return bytes;
//    }
//
//    byte[] fileToBytes(File file){
//        byte[] bytes = new byte[0];
//        try(FileInputStream inputStream = new FileInputStream(file)) {
//            bytes = new byte[inputStream.available()];
//            //noinspection ResultOfMethodCallIgnored
//            inputStream.read(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bytes;
//    }
//
//    public static byte[] read(File file) throws IOException {
//        FileInputStream input = null;
//        if (file.exists()) try
//        {
//            input = new FileInputStream (file);
//            int len = (int) file.length();
//            byte[] data = new byte[len];
//            int count, total = 0;
//            while ((count = input.read (data, total, len - total)) > 0) total += count;
//            return data;
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        finally
//        {
//            if (input != null) try
//            {
//                input.close();
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public readWAV2Array(String filepath, boolean print_info) throws IOException{
//        Path path = Paths.get(filepath);
//        this.entireFileData = Files.readAllBytes(path);
//        System.out.println("length1 = " + entireFileData.length);
//
////
////        RandomAccessFile f = new RandomAccessFile(filepath, "r");
////        byte[] b = new byte[(int)f.length()];
////        f.readFully(b);
////        this.entireFileData = b;
////        System.out.println("length2 = " + entireFileData.length);
//
//
////        File ff = new File(filepath);
////        byte[] fileContent = Files.readAllBytes(ff.toPath());
////        this.entireFileData = fileContent;
////        System.out.println("length3 = " + entireFileData.length);
//
////
////        File file = new File(filepath);
////        byte[] bytes = new byte[(int) file.length()];
////
////        FileInputStream fis = null;
////        try {
////
////            fis = new FileInputStream(file);
////
////            //read file into bytes[]
////            fis.read(bytes);
////
////        } finally {
////            if (fis != null) {
////                fis.close();
////            }
////        }
////        this.entireFileData = bytes;
////        System.out.println("length4 = " + entireFileData.length);
////
////        File file = new File(filepath);
////        int size = (int) file.length();
////        byte[] bytes = new byte[size];
////        try {
////            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
////            buf.read(bytes, 0, bytes.length);
////            buf.close();
////        } catch (FileNotFoundException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        } catch (IOException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
////        this.entireFileData = bytes;
////        System.out.println("length5 = " + entireFileData.length);
//
//
////        bytes = FileUtils.readFileToByteArray(file);
////        this.entireFileData = bytes;
////        System.out.println("length6 = " + entireFileData.length);
//
//
////        bytes = fullyReadFileToBytes(file);
////        this.entireFileData = bytes;
////        System.out.println("length7 = " + entireFileData.length);
//
////
////        bytes = new byte[(int) file.length()];
////        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
////        DataInputStream dis = new DataInputStream(bis);
////        dis.readFully(bytes);
////        this.entireFileData = bytes;
////        System.out.println("length8 = " + entireFileData.length);
//
////        bytes = read(file);
////        this.entireFileData = bytes;
////        System.out.println("length9 = " + entireFileData.length);
//
////        bytes = fileToBytes(file);
////        this.entireFileData = bytes;
////        System.out.println("length10 = " + entireFileData.length);
//
//
////        bytes = FileUtils.readFileToByteArray(new File(filepath));
////        this.entireFileData = bytes;
////        System.out.println("length11 = " + entireFileData.length);
//
//
////        bytes = IOUtils.toByteArray(new FileInputStream(filepath));
////        this.entireFileData = bytes;
////        System.out.println("length12 = " + entireFileData.length);
//
//
//
////        bytes = new byte[(int)f.length()];
////
////        FileInputStream is = new FileInputStream(file);
////
////        is.read(bytes);
////
////        is.close();
////        this.entireFileData = bytes;
////        System.out.println("length13 = " + entireFileData.length);
//
//
////        RandomAccessFile fff = new RandomAccessFile(filepath, "rw");
////
////        bytes = new byte[(int)fff.length()];
////
////        f.readFully(bytes);
////        this.entireFileData = bytes;
////        System.out.println("length14 = " + entireFileData.length);
//
//
//
////CHECKCHECKCHECK
////        try {
////            FileInputStream fiStream = new FileInputStream(inputFile_name);
////            Scanner sc = null;
////            try {
////                sc = new Scanner(fiStream);
////                while (sc.hasNextLine()) {
////                    String line = sc.nextLine();
////                    byte[] buf = line.getBytes();
////                }
////            } finally {
////                if (fiStream != null) {
////                    fiStream.close();
////                }
////
////                if (sc != null) {
////                    sc.close();
////                }
////            }
////        }catch (Exception e){
////            Log.e(TAG, "Exception: " + e.toString());
////        }
//
//
//
//        if (print_info){
//
//            //extract format
//            String format = new String(Arrays.copyOfRange(entireFileData, 8, 12), "UTF-8");
//
//            //extract number of channels
//            int noOfChannels = entireFileData[22];
//            String noOfChannels_str;
//            if (noOfChannels == 2)
//                noOfChannels_str = "2 (stereo)";
//            else if (noOfChannels == 1)
//                noOfChannels_str = "1 (mono)";
//            else
//                noOfChannels_str = noOfChannels + "(more than 2 channels)";
//
//            //extract sampling rate (SR)
//            int SR = (int) this.getSR();
//
//            //extract Bit Per Second (BPS/Bit depth)
//            int BPS = entireFileData[34];
//
//            System.out.println("---------------------------------------------------");
//            System.out.println("File path:          " + filepath);
//            System.out.println("File format:        " + format);
//            System.out.println("Number of channels: " + noOfChannels_str);
//            System.out.println("Sampling rate:      " + SR);
//            System.out.println("Bit depth:          " + BPS);
//            System.out.println("---------------------------------------------------");
//
//        }
//    }
//
//    public double[] getByteArray (){
//        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
//        int totalLength = data_raw.length;
//
//        //declare double array for mono
//        int new_length = totalLength/4;
//        double[] data_mono = new double[new_length];
//
//        double left, right;
//        for (int i = 0; 4*i+3 < totalLength; i++){
//            left = (short)((data_raw[4*i+1] & 0xff) << 8) | (data_raw[4*i] & 0xff);
//            right = (short)((data_raw[4*i+3] & 0xff) << 8) | (data_raw[4*i+2] & 0xff);
//            data_mono[i] = (left+right)/2.0;
//        }
//        return data_mono;
//    }
//}