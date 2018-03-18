package de.schegar.alarmclock;

import de.schegar.alarmclock.model.Temperature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tools {

    private static Tools instance;

    public Tools() {
        this.instance = this;
    }

    public static Tools getInstance() {
        return instance;
    }

    public static String getFormattedTimePart(int time) {
        if (time < 10) return "0" + time;
        return String.valueOf(time);
    }

    public static Temperature getRoomTemp() {
        String s;
        Process processCompile = null;

        try {
            processCompile = Runtime.getRuntime().exec("sudo ./loldht 0");


            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(processCompile .getInputStream()));

            String humidity = "";
            String temperature = "";

            Temperature temp = new Temperature();

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                if (s.startsWith("Humidity")) {
                    String[] substring = s.split("% ");
                    humidity = substring[0].replace("Humidity = ", "");
                    temperature = substring[1].replace("Temperature = ", "").replace(" *C", "");
                }
            }
            temp.setHumidity(Double.valueOf(humidity));
            temp.setTemp(Double.valueOf(temperature));

            return temp;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Calendar resetDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static ArrayList<Appointment> getAppointments() {
        HashMap<Long, Appointment> bak = new HashMap<>();
        ArrayList<Appointment> apps = new ArrayList<>();

        ExchangeService serviceETL = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentialsETL = new WebCredentials("pw@datev", "iIw1WnigAhu");
        serviceETL.setCredentials(credentialsETL);

        /*ExchangeService serviceEXCO = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentialsEXCO = new WebCredentials("patrick.wiese@exco-ft", "r0ck$t4rPa1n");
        serviceEXCO.setCredentials(credentialsEXCO);*/

        try {

            serviceETL.setUrl(new URI("https://webmail.numrich-collegen.de/EWS/Exchange.asmx"));
            //serviceEXCO.setUrl(new URI("https://mail.exco.de/EWS/Exchange.asmx"));
            //serviceEXCO.autodiscoverUrl("patrick.wiese@exco.de");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = new Date();//formatter.parse("2016-01-01 12:00:00");
            Date endDate = formatter.parse("2017-08-30 13:00:00");

            CalendarFolder cfETL=CalendarFolder.bind(serviceETL, WellKnownFolderName.Calendar);
            FindItemsResults<Appointment> findResultsETL = cfETL.findAppointments(new CalendarView(startDate, endDate));

            //CalendarFolder cfEXCO=CalendarFolder.bind(serviceEXCO, WellKnownFolderName.Calendar);
            //FindItemsResults<Appointment> findResultsEXCO = cfEXCO.findAppointments(new CalendarView(startDate, endDate));

            ArrayList<Appointment> appointmentsETL = findResultsETL.getItems();
            //ArrayList<Appointment> appointmentsEXCO = findResultsEXCO.getItems();

            for (Appointment appt:appointmentsETL) {
                bak.put(appt.getStart().getTime(), appt);
            }
            //for (Appointment appt:appointmentsEXCO) {
            //    bak.put(appt.getStart().getTime(), appt);
            //}

            Long[] dates = bak.keySet().toArray(new Long[bak.keySet().size()]);
            Arrays.sort(dates);
            //System.out.println(apps.size());

            for (Long date : dates) {
                //System.out.println(format1.format(cal.getTime()) + "  " + bak.get(date).getSubject());
                apps.add(bak.get(date));
            }
            //System.out.println(apps.size());


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /*for (Appointment appt:apps) {
                System.out.println(appt.getStart() + "  " + appt.getSubject());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (Map.Entry<Long, Appointment> entry : apps.entrySet()) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm - yyyy-MM-dd");
            try {
                cal.setTimeInMillis(entry.getValue().getStart().getTime());
                //System.out.println(format1.format(entry.getValue().getStart()) + "  " + entry.getValue().getSubject());
                System.out.println(entry.getKey() + "  " + entry.getValue().getSubject());
            } catch (ServiceLocalException e) {
                e.printStackTrace();
            }


        }*/
        //System.out.println(" ");

        return apps;

    }
}
