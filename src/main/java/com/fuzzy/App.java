package com.fuzzy;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.util.Animations;
import com.fuzzy.config.AppConfig;
import com.fuzzy.config.configsystem.ConfigBuilder;
import com.fuzzy.fx_controllers.AppController;
import com.fuzzy.model.Fxml.ExtTreeItem;
import com.fuzzy.model.Fxml.PatchFilePane;
import com.fuzzy.utils.StringUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static atlantafx.base.util.Animations.EASE;

public class App extends Application {



    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("App.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 550);
        stage.setTitle("LauncherFileUpdate");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("background/icons/mipmap-hdpi/ic_launcher.png")).toString()));
        stage.setScene(scene);
        stage.setResizable(false);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        AppController controller = fxmlLoader.getController();

        stage.titleProperty().bind(Bindings.createStringBinding(() ->
                (controller.projectNameProperty().get() != null ? "Project - " + controller.projectNameProperty().get() : ""), controller.projectNameProperty()));

        stage.show();
    }

    public static void main(String[] args) throws IOException {
        ConfigBuilder.load(AppConfig.class, false);
        launch();
    }


    public static Timeline fileListViewAnimation(Node node, Duration duration){
        var t = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.translateXProperty(), 0, EASE)
                ),
                new KeyFrame(duration.divide(2),
                        new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2 , EASE)
                ),
                new KeyFrame(duration.divide(2),
                        new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2 , EASE)
                ),
                new KeyFrame(duration.divide(2),
                        new KeyValue(node.translateXProperty(), 0, EASE)
                )
        );

        t.statusProperty().addListener((obs, old, val) -> {
            if (val == Animation.Status.STOPPED) {
                node.setTranslateX(0);
            }
        });
        return t;
    }


}
