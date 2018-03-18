package de.schegar.alarmclock;

import de.schegar.alarmclock.model.Temperature;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    @FXML private Text clockField;
    @FXML private Text dateField;
    @FXML private Text InRoomTemp;

    Timer timer = new Timer();

    Temperature indoor;

    public void initialize(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                String hours = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                String minutes = String.valueOf(cal.get(Calendar.MINUTE));
                String seconds = String.valueOf(cal.get(Calendar.SECOND));

                if (hours.length() == 1) hours = "0" + hours;
                if (minutes.length() == 1) minutes = "0" + minutes;
                if (seconds.length() == 1) seconds = "0" + seconds;

                String clock = hours + ":" + minutes + ":" + seconds;
                clockField.setText(clock);
            }
        }, 0, 500);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String date = sdf.format(new Date());
                dateField.setText(date);
            }
        }, 0, 1000 * 60 * 60 * 12);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        indoor = reloadIndoor();

                        InRoomTemp.setText(indoor.getTempString());
                    }
                }).start();
            }
        }, 0, 5000); //1 minute


    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
    }

    public Temperature reloadIndoor() {
        String s;
        Process processCompile = null;

        try {
            processCompile = Runtime.getRuntime().exec("sudo ./loldht 0");


            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(processCompile .getInputStream()));

            String humidity = "";
            String temperature = "";

            Temperature indoor = new Temperature();

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                if (s.startsWith("Humidity")) {
                    String[] substring = s.split("% ");
                    humidity = substring[0].replace("Humidity = ", "");
                    temperature = substring[1].replace("Temperature = ", "").replace(" *C", "");
                }
            }
            indoor.setHumidity(Double.valueOf(humidity));
            indoor.setTemp(Double.valueOf(temperature));

            return indoor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}