package com.fuzzy.crypt;

import com.fuzzy.config.AppConfig;
import com.fuzzy.model.CsvFile;
import com.fuzzy.utils.FileUtils;
import com.fuzzy.windows.DatEditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class L2CryptUtil {

    private static final String DDF_FILES = "data\\l2asm-disasm\\DAT_defs\\H5pt\\";
    private static final File ENCODER = new File(AppConfig.DATA_DIRECTORY + "l2encdec\\l2encdec_old.exe");
    private static final File DISASM = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2disasm.exe");
    private static final File ASM = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2asm.exe");

    public static void decryptDat(File file) throws Exception {
        String fileName = file.getName().toLowerCase();
        final Path tempPath = Path.of(AppConfig.TEMP_DIRECTORY + fileName);
        FileUtils.copyFile(file.toPath(), tempPath);
        ProcessBuilder decodeProcess = new ProcessBuilder();
        decodeProcess.command(ENCODER.getAbsolutePath(), "-s", fileName, "dec-" + fileName);
        decodeProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = decodeProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static CsvFile disasmDat(File file) throws FileNotFoundException {
        String fileName = file.getName().toLowerCase();
        File ddfFile = findDdfFile(file);
        File csvFile = new File(AppConfig.TEMP_DIRECTORY + file.getName().replace(".dat", ".txt").toLowerCase());
        FileUtils.copyFile(ddfFile.toPath(), Path.of(AppConfig.TEMP_DIRECTORY + ddfFile.getName().toLowerCase()));
        ProcessBuilder disasmProcess = new ProcessBuilder();
        disasmProcess.command(DISASM.getAbsolutePath(),
                "-d",
                ddfFile.getName(),
                "-e",
                ddfFile.getName().replace(".ddf", "-new.ddf").toLowerCase(),
                "dec-" + file.getName().toLowerCase(), csvFile.getName().toLowerCase()
        );
        disasmProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = disasmProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new CsvFile(csvFile);
    }

    public static void asmDat(File file){
        File ddfFile = new File(AppConfig.TEMP_DIRECTORY + file.getName().replace(".dat", "-new.ddf").toLowerCase());
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(ASM.getAbsolutePath(),
                "-d",
                ddfFile.getName(),
                file.getName().replace(".dat", ".txt").toLowerCase() ,
                "dec-" + file.getName().toLowerCase()
        );
        processBuilder.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = processBuilder.start();
            System.out.println("Процесс запущен! 🚀");
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void encryptDat(File file){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(ENCODER.getAbsolutePath(),
                "-h",
                "413",
                "dec-" + file.getName().toLowerCase(),
                file.getName().toLowerCase()
        );
        processBuilder.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = processBuilder.start();
            System.out.println("Процесс запущен! 🚀");
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
