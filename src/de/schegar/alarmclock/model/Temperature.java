package de.schegar.alarmclock.model;

public class Temperature {

    private double humidity;
    private double temp;

    public Temperature() {}

    public Temperature(double humidity, double temp) {
        this.humidity = humidity;
        this.temp = temp;
    }


    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getTempString() {
        return temp + " °C";
    }

    public String getHumidityString() {
        return humidity + " %";
    }
}
