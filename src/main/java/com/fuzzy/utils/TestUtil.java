package com.fuzzy.utils;

import com.fuzzy.model.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtil {

    public static void startGame(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("E:\\Games\\CLIENT_HIGH_FIVE_LOCAL\\system\\l2.exe");
            Process process = processBuilder.start();
            process.pid();
            process.info().user().ifPresent(System.out::println);
            int exitCode = process.waitFor();
            System.out.println("Notepad++ exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void downloadFile(String fileName) {
        String fileUrl = "https://example.com/path/to/" + fileName; // URL для скачивания
        //            Path targetPath = Path.of(DIRECTORY_PATH, fileName);
//            Files.copy(new URL(fileUrl).openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Файл скачан: " + fileName);
    }

    private static void deleteFile(String fileName) {
        File fileToDelete = new File(fileName);
//        if (fileToDelete.delete()) {
        if (fileToDelete.exists()) {
            System.out.println("Файл удалён: " + fileName);
        } else {
            System.out.println("Не удалось удалить файл: " + fileName);
        }
    }

}
