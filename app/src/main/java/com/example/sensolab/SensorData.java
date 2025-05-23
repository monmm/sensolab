package com.example.sensolab;

public class SensorData {
    private int time;
    private String value;

    public SensorData() {}

    public SensorData(int time, String value) {
        this.time = time;
        this.value = value;
    }

    public int getTime() { return time; }

    public String getValue() { return value; }

    public void setTime(int time) { this.time = time; }

    public void setValue() { this.value = value; }
}
