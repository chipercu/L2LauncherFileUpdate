package com.fuzzy.services;

import com.fuzzy.utils.HexConverter;
import com.fuzzy.config.AppConfig;
import com.fuzzy.model.Fxml.FX_ImmutableFile;
import com.fuzzy.model.ImmutableFile;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutableFilesService {

    private Map<String, ImmutableFile> immutableFileList = new HashMap<>();

    public ImmutableFilesService() {}

    public void addImmutableFiles(VBox filesBox){
        FileChooser fileChooser = new FileChooser();
        File configPath = new File(AppConfig.CLIENT_DIRECTORY);
        if (!AppConfig.CONFIG_DIRECTORY.isEmpty() && configPath.exists()){
            fileChooser.setInitialDirectory(configPath);
        }
        List<File> fileList = fileChooser.showOpenMultipleDialog(filesBox.getScene().getWindow());
        if (fileList != null && !fileList.isEmpty()){
            fileList.forEach(file ->{
                String fileName = file.getParentFile().getName() + "/" + file.getName();
                ImmutableFile immutableFile = new ImmutableFile(fileName, getMd5Hash(getByteFile(file)));
                if (!immutableFileList.containsKey(fileName)){
                    immutableFileList.put(fileName, immutableFile);
                    FX_ImmutableFile fxImmutableFile = new FX_ImmutableFile(fileName);
                    fxImmutableFile.setDeleteHandler(mouseEvent -> {
                        filesBox.getChildren().remove(fxImmutableFile);
                        immutableFileList.remove(fileName);
                    });
                    filesBox.getChildren().add(fxImmutableFile);
                }else {
                    ImmutableFile mappedImmutableFile = immutableFileList.get(fileName);
                    if (!mappedImmutableFile.equals(immutableFile)){
                        immutableFileList.put(fileName, immutableFile);
                    }
                }
            });
        }
    }

    public static String getMd5Hash(byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String digestHex = HexConverter.bytesToHex(md.digest(bytes));
        return digestHex.toUpperCase();
    }

    public static byte[] getByteFile(File file) {
        byte[] fileBytes = new byte[0];
        try (InputStream inputStream = new FileInputStream(file); ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteOutput.write(buffer, 0, bytesRead);
            }
            fileBytes = byteOutput.toByteArray();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла в массив байтов: " + e.getMessage());
        }
        return fileBytes;
    }


}
