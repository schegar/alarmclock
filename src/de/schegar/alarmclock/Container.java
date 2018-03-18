package de.schegar.alarmclock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Container {

    private final static int WIDTH = 1024;
    private final static int HEIGHT = 600;

    private static Container instance;

    public ArrayList<Scene> scenes;
    public Stage primaryStage;

    public Container() {
        this.instance = this;
    }

    public static Container getInstance() {
        return instance;
    }

    public void loadScenes() {
        scenes = new ArrayList<>();
        try {

            scenes.add(new Scene(FXMLLoader.load(getClass().getResource("resources/home.fxml")), WIDTH, HEIGHT));
            scenes.add(new Scene(FXMLLoader.load(getClass().getResource("resources/alarm.fxml")), WIDTH, HEIGHT));
            scenes.get(0).getStylesheets().add(getClass().getResource("resources/css/home.css").toExternalForm());
            scenes.get(1).getStylesheets().add(getClass().getResource("resources/css/material-fx-v0_3.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
