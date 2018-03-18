package de.schegar.alarmclock.controller;

import com.pi4j.system.SystemInfo;
import de.schegar.alarmclock.Container;
import de.schegar.alarmclock.Scenes;
import de.schegar.alarmclock.Tools;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.search.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.lang.management.PlatformLoggingMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static de.schegar.alarmclock.Tools.getFormattedTimePart;
import static de.schegar.alarmclock.Tools.getRoomTemp;

public class ControllerHome implements Initializable {

    @FXML private Text clock;
    @FXML private Text date;
    @FXML private Text temp_room;
    @FXML private Text temp_cpu;
    @FXML private TextFlow current_track;
    @FXML private ListView calendarList;

    private final int HOUR = 1000 * 60 * 60;
    private final int QUARTER_SECOND = 250;
    private final int FIVE_SECONDS = 5000;

    private TimerTask clockTask = new TimerTask() {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            //System.out.println("time updated");
            Random r = new Random();
            //clock.setText(getFormattedTimePart(hour) + ":" + getFormattedTimePart(minute) + ":" + getFormattedTimePart(second));
            //clock.setText(String.valueOf(r.nextInt()));
        }
    };

    private TimerTask dateTask = new TimerTask() {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            date.setText(dateFormat.format(cal.getTime()));
        }
    };

    private void updateRoomTemp() {
        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    temp_room.setText(getRoomTemp().getTempString());
                    temp_cpu.setText(SystemInfo.getCpuTemperature() + " °C");
                } catch (Exception e) {}
            });
        }).start();
    }

    private void updateCalendar() {
       new Thread(() -> {
           ObservableList<GridPane> items = FXCollections.observableArrayList ();
           ColumnConstraints cc1 = new ColumnConstraints();
           cc1.setPercentWidth(75);
           ColumnConstraints cc2 = new ColumnConstraints();
           cc2.setPercentWidth(25);

           ArrayList<Appointment> apps = Tools.getAppointments();

           int i = 0;
           long lastDay = 0;
           for (Appointment appt : apps) {
               try {
                   //System.out.println(appt.getSubject() + " "  + appt.getStart().toString());
                   if (i < 7) {
                       if (!appt.getIsAllDayEvent()) {// items.add(appt.getSubject());
                           //System.out.println("drinne");
                           GridPane grid = new GridPane();
                           grid.getColumnConstraints().addAll(cc1, cc2);


                           Calendar cal = Calendar.getInstance();
                           cal.setTime(appt.getStart());

                           Text title = new Text(appt.getSubject());
                           title.setTextAlignment(TextAlignment.LEFT);
                           title.setFill(Color.valueOf("#A6A6A6"));
                           title.setFont(Font.font(15));

                           TextFlow time = new TextFlow();
                           Text hour = new Text(String.valueOf(getFormattedTimePart(cal.get(Calendar.HOUR_OF_DAY))));
                           hour.setFill(Color.valueOf("#DF2940"));
                           hour.setFont(Font.font(15));
                           Text minute = new Text(String.valueOf(getFormattedTimePart(cal.get(Calendar.MINUTE))));
                           if (minute.getText().equals("0")) minute.setText("00");
                           minute.setFill(Color.valueOf("#A6A6A6"));
                           time.getChildren().addAll(hour, new Text(":"), minute);
                           ((Text) time.getChildren().get(1)).setFill(Color.valueOf("#A6A6A6"));
                           time.setTextAlignment(TextAlignment.RIGHT);
                           time.setPadding(new Insets(0, 5, 0, 0));

                           // Maybe event red and time grey

                           cal = Tools.resetDay(cal);

                           if (lastDay != cal.getTimeInMillis()) {
                               Text dateText = new Text(getFormattedTimePart(cal.get(Calendar.DAY_OF_MONTH)) + "." + getFormattedTimePart(cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));
                               dateText.setFill(Color.valueOf("#DF2940"));
                               dateText.setUnderline(true);
                               dateText.setFont(Font.font(13));
                               //date.setFill(Color.valueOf("#A6A6A6"));
                               GridPane dateGrid = new GridPane();
                               dateGrid.add(dateText, 0, 0);
                               items.add(dateGrid);
                               lastDay = cal.getTimeInMillis();
                           }
                           grid.add(title, 0, 0);
                           grid.add(time, 1, 0);

                           items.add(grid);
                           i++;
                       }
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
           Platform.runLater(() -> {
               calendarList.setItems(items);
               System.out.println("updated");

           });
       }).start();
    }

    @FXML public void click() {
        Container cont = Container.getInstance();
        cont.primaryStage.setScene(cont.scenes.get(Scenes.ALARM.getId()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timer timer = new Timer();

        timer.schedule(clockTask, 0, QUARTER_SECOND);
        timer.schedule(dateTask, 0, HOUR);

        Timeline timelineRoomTemp = new Timeline(new KeyFrame(Duration.millis(FIVE_SECONDS),
                ae -> updateRoomTemp()));
        timelineRoomTemp.setCycleCount(Animation.INDEFINITE);
        timelineRoomTemp.play();
        //timer.schedule(roomTempTask, 0, FIVE_SECONDS);
        //timer.schedule(calendarTask, 0, FIVE_SECONDS);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(FIVE_SECONDS),
                ae -> updateCalendar()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        Text text1 = new Text("Can't stop\n");
        text1.setFill(Color.valueOf("#DF2940"));
        text1.setFont(Font.font(36));
        text1.setTextAlignment(TextAlignment.CENTER);
        Text text2 = new Text("Red Hot Chilli Peppers");
        text2.setFill(Color.valueOf("#A6A6A6"));
        text2.setFont(Font.font(36));
        text2.setTextAlignment(TextAlignment.CENTER);
        current_track.getChildren().addAll(text1, text2);

    }

}
