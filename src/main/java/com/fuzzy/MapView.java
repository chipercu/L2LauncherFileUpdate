package com.fuzzy;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.stage.Stage;


public class MapView extends Application {

    @Override
    public void start(Stage stage) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateX(10);
        camera.setTranslateZ(-100);
        camera.setFieldOfView(20);


        Box box = new Box(100, 100, 100);
        box.setTranslateX(150);
        box.setTranslateY(0);
        box.setTranslateZ(400);

        Group group = new Group(box);


        Scene scene = new Scene(group, 800, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LAVENDER);
        scene.setCamera(camera);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()){
                case UP -> camera.setTranslateY(camera.getTranslateY() - 10);
                case DOWN -> camera.setTranslateY(camera.getTranslateY() + 10);
                case RIGHT -> camera.setTranslateX(camera.getTranslateX() - 10);
                case LEFT -> camera.setTranslateX(camera.getTranslateX() + 10);
            }
        });

        stage.setTitle("3D Dragging");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
