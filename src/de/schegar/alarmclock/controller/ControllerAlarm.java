package de.schegar.alarmclock.controller;

import de.schegar.alarmclock.Tools;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ControllerAlarm {

    @FXML private Text hour;
    @FXML private Text min;

    int hourVal = 0;
    int minVal = 0;

    @FXML
    public void hourUp() {
        hourVal++;
        checkRanges();
        updateTimer();
    }

    @FXML
    public void hourDown() {
        hourVal--;
        checkRanges();
        updateTimer();
    }

    @FXML
    public void minUp() {
        minVal++;
        checkRanges();
        updateTimer();
    }

    @FXML
    public void minDown() {
        minVal--;
        checkRanges();
        updateTimer();
    }

    private void updateTimer() {

        hour.setText(Tools.getFormattedTimePart(hourVal));
        min.setText(Tools.getFormattedTimePart(minVal));
    }

    private void checkRanges() {
        if (hourVal < 0) hourVal = 23;
        else if (hourVal >= 24) hourVal = 0;
        if (minVal < 0) {
            minVal = 59;
            hourVal--;
            checkRanges();
        } else if (minVal > 59) {
            minVal = 0;
            hourVal++;
            checkRanges();
        }
    }

}
