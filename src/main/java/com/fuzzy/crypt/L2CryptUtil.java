package com.fuzzy.crypt;

import com.fuzzy.config.AppConfig;
import com.fuzzy.model.CsvFile;
import com.fuzzy.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class L2CryptUtil {

    private static final String DDF_FILES = "data\\l2asm-disasm\\DAT_defs\\H5pt\\";
    private static final File ENCODER = new File(AppConfig.DATA_DIRECTORY + "l2encdec\\l2encdec_old.exe");
    private static final File DISASM = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2disasm.exe");

    public static void decryptDat(File file) throws Exception {
        final Path tempPath = Path.of(AppConfig.TEMP_DIRECTORY + file.getName());
        File ddfFile = findDdfFile(file);
        File csvFile = new File(AppConfig.TEMP_DIRECTORY + file.getName().replace(".dat", ".csv"));
        FileUtils.copyFile(file.toPath(), tempPath);
        FileUtils.copyFile(ddfFile.toPath(), Path.of(AppConfig.TEMP_DIRECTORY + ddfFile.getName()));


        ProcessBuilder decodeProcess = new ProcessBuilder();
        decodeProcess.command(ENCODER.getAbsolutePath(), "-s", file.getName(), "dec-" + file.getName());
        decodeProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = decodeProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        ProcessBuilder disasmProcess = new ProcessBuilder();
        disasmProcess.command(DISASM.getAbsolutePath(), "-d", ddfFile.getName(),"-e", ddfFile.getName().replace(".ddf", "-new.ddf"), "dec-" + file.getName(), csvFile.getName());
        disasmProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = disasmProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        CsvFile csv = new CsvFile(csvFile);
    }

    private static File findDdfFile(File file) throws FileNotFoundException {
        String name = file.getName().replace("ru", "e").replace("dat", "ddf");
        File ddfFile = new File(DDF_FILES + name);

        if (!ddfFile.exists()){
            throw new FileNotFoundException("Схема " + name + " не найдена!");
        }
        return ddfFile;
    }








}
