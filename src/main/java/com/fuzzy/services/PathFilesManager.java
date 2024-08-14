package com.fuzzy.services;

import com.fuzzy.utils.FileUtils;
import com.fuzzy.utils.HexConverter;
import com.fuzzy.config.AppConfig;
import com.fuzzy.model.PatchFile;
import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PathFilesManager {

    private static PathFilesManager INSTANCE = new PathFilesManager();

    private CopyOnWriteArrayList<PatchFile> patchFiles = new CopyOnWriteArrayList<>();

    public static PathFilesManager getInstance() {
        return INSTANCE;
    }






    public void fullInspectClient(ProgressBar progressBar) {
        getPatchFiles().clear();
        System.out.println("Start analize " + Calendar.getInstance().getTime());
        File clientFolder = new File(AppConfig.CLIENT_DIRECTORY);

        List<File> files = new ArrayList<>();
        inspect(clientFolder, files);

        Button button = (Button) progressBar.getScene().lookup("#FULL_ANALIZE_BUTTON");
        button.setDisable(true);

        progressBar.progressProperty().unbind();
        final Task<Void> testTask = fullInspectTask(files);
        progressBar.progressProperty().bind(testTask.progressProperty());

        testTask.setOnSucceeded(workerStateEvent -> {
            System.out.println("Finish analize " + Calendar.getInstance().getTime());
            System.out.println(getPatchFiles().size());
            for (PatchFile patchFile : getPatchFiles()) {
                System.out.println(patchFile);
            }
            button.setDisable(false);
        });

        Thread thread = new Thread(testTask);
        thread.setDaemon(true);
        thread.start(); // Запускаем задачу
//        generateUpdaterFileList();
    }


    public Map<String, List<PatchFile>> groupByFolder() {
        Map<String, List<PatchFile>> groupedMap = new HashMap<>();
        for (PatchFile patchFile : patchFiles) {
            groupedMap
                    .computeIfAbsent(patchFile.getFolder(), k -> new ArrayList<>())
                    .add(patchFile);
        }
        return groupedMap;
    }

    private void inspect(File folder, List<File> collectList) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    inspect(file, collectList);
                } else {
                    collectList.add(file);
                }
            }
        }
    }


    private Task<Void> fullInspectTask(List<File> files) {
        return new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    PatchFile patchFile = new PatchFile(file);
                    PathFilesManager.getInstance().getPatchFiles().add(patchFile);
                    updateProgress(i + 1, files.size());
                }
                updateProgress(0, files.size());
                return null;
            }
        };
    }


    public List<PatchFile> getImmutableFiles() {
        return patchFiles.stream().filter(PatchFile::isImmutable).toList();
    }

    public List<PatchFile> getMutableFiles() {
        return patchFiles.stream().filter(patchFile -> !patchFile.isImmutable()).toList();
    }

    public List<PatchFile> getPatchFiles() {
        return patchFiles;
    }

    public void generateUpdaterFileList() {
        Gson gson = new Gson();
        String json = gson.toJson(patchFiles);
        FileUtils.saveToFile(json.getBytes(), Path.of(AppConfig.CONFIG_DIRECTORY + ".json"));
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

    public static String getSHA(File file) {
        return getMd5Hash(getByteFile(file));
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
