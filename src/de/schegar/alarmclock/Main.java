package de.schegar.alarmclock;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Container cont = new Container();
        cont.loadScenes();

        primaryStage.setTitle("AlarmClock");
        primaryStage.setScene(cont.scenes.get(Scenes.HOME.getId()));
        primaryStage.show();

        cont.primaryStage = primaryStage;

    }


    public static void main(String[] args) {
        launch(args);
    }


}
