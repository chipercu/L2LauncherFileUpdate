package com.fuzzy.windows;

import com.fuzzy.App;
import com.fuzzy.fx_controllers.FileManagerController;
import com.fuzzy.fx_controllers.TextEditorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TextEditor extends Stage {

    private final File file;
    private final TextEditorController controller;

    public TextEditor(File file)  {
        this.file = file;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("TextEditor.fxml"));
            setScene(new Scene(fxmlLoader.load(), 800, 800));
            setTitle(file.getName());
            controller = fxmlLoader.getController();

            StringBuilder stringBuilder = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, "UTF-16LE");
                 BufferedReader reader = new BufferedReader(isr)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String join = stringBuilder.toString().replace(">", ">\n");

//            List<String> strings = Files.readAllLines(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_16BE);
//            String join = String.join("\n", strings).replace(">", ">\n");



            controller.getTEXT_AREA().setText(join);
            controller.getTEXT_AREA().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
                controller.getWeb().getEngine().loadContent(controller.getTEXT_AREA().getText());
            });


            WebEngine engine = controller.getWeb().getEngine();
            engine.loadContent(join);


            show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
