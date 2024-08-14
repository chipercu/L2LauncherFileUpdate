package com.fuzzy.model.file_edit.dat;

import com.fuzzy.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ItemNameHandler {


    private List<ItemNameDat> itemNameDatList = new ArrayList<>();


    public void decode(File file) throws IOException {
        final Path tempPath = Path.of(AppConfig.TEMP_DIRECTORY + ItemNameDat.ru);
        Files.copy(file.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);

        File ddfFile = new File("data\\l2asm-disasm\\DAT_defs\\H5pt\\" + ItemNameDat.ddf);
        Files.copy(ddfFile.toPath(), Path.of(AppConfig.TEMP_DIRECTORY + ddfFile.getName()) , StandardCopyOption.REPLACE_EXISTING);

        l2encdec(ItemNameDat.ru);
        l2asm(ItemNameDat.ddf, ItemNameDat.ru);
        parseData(ItemNameDat.ru);


    }

    private void parseData(String fileName) throws IOException {
        List<String> strings = Files.readAllLines(Path.of(AppConfig.TEMP_DIRECTORY + fileName.replace(".dat", ".txt")));

        for (int i = 1; i < strings.size(); i++) {
            String[] split = strings.get(i).split("\t");

            ItemNameDat itemNameDat = new ItemNameDat();
            itemNameDat.setId(Integer.parseInt(split[0]));
            itemNameDat.setName(split[1]);
            itemNameDat.setAdd_name(split[2]);



            itemNameDatList.add(itemNameDat);
        }
    }


    private void l2encdec(String fileName){
        File encoder = new File(AppConfig.DATA_DIRECTORY + "l2encdec\\l2encdec_old.exe");
        ProcessBuilder decodeProcess = new ProcessBuilder();
        decodeProcess.command(encoder.getAbsolutePath(), "-s", fileName, "dec-" + fileName);
        decodeProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = decodeProcess.start();
            System.out.println("Процесс запущен! 🚀");
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void l2asm(String ddf, String fileName){
        File disasm = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2disasm.exe");
        String absolutePath = disasm.getAbsolutePath();
        ProcessBuilder disasmProcess = new ProcessBuilder();
        disasmProcess.command(absolutePath, "-d", ddf,"-e", ddf.replace(".ddf", "-new.ddf"), "dec-" + fileName, fileName.replace(".dat", ".txt"));
        disasmProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            // Запускаем процесс
            Process process = disasmProcess.start();
            System.out.println("Процесс запущен! 🚀");
            // Ожидаем его завершения
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
