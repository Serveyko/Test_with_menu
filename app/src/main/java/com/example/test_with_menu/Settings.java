package com.example.test_with_menu;

import java.io.Serializable;

public class Settings implements Serializable {
    private int time;
    private int size;
    private int frequency;
    private int inhale;
    private int koeff;

    public Settings(int _time, int _size, int _frequency, int _inhale, int _koeff) {
        time = _time;
        size = _size;
        frequency = _frequency;
        inhale = _inhale;
        koeff = _koeff;
    }

    public int get_Time() {
        return time;
    }

    public int get_Size() {
        return size;
    }

    public int get_Frequency() {
        return frequency;
    }

    public int get_Inhale() {
        return inhale;
    }

    public int get_Koeff() {
        return koeff;
    }
}
