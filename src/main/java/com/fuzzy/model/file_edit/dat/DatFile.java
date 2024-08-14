package com.fuzzy.model.file_edit.dat;

import com.fuzzy.config.AppConfig;
import com.fuzzy.utils.FileUtils;
import com.fuzzy.windows.DatEditor;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.fuzzy.utils.StringUtils.containsSubstring;

public class DatFile{

    private final File file;
    private List<String> fileLines = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();
    private final List<Data> data = new ArrayList<>();
    private Column idColumn;
    private Column nameColumn;
    private String [] cols;
    private final ProgressBar fileManagerProggBar;

    public DatFile(File file, ProgressBar progressBar) {
        this.file = file;
        this.fileManagerProggBar = progressBar;
    }

    public List<Data> filterDataByName(String filterText){
        if (nameColumn != null){
           return data.stream().filter(d -> containsSubstring(d.getData()[nameColumn.index], filterText) ).toList();
        }
        return data;
    }

    public List<Data> filterDataById(String filterId){
        if (idColumn != null){
            return data.stream().filter(d -> containsSubstring(d.getData()[idColumn.index], filterId) ).toList();
        }
        return data;
    }

    public void parseFile(){
        try {
            this.fileLines = Files.readAllLines(Path.of(AppConfig.TEMP_DIRECTORY + file.getName().replace(".dat", ".txt")));
            this.cols = fileLines.get(0).split("\t");
        } catch (IOException e){
            throw new RuntimeException("Не удалось прочесть файл");
        }
    }

    private void duplicateData(){}

    public void decodeL2DatFile() {
        final Path tempPath = Path.of(AppConfig.TEMP_DIRECTORY + file.getName());

        FileUtils.copyFile(file.toPath(), tempPath);
        File ddfFile = new File("data\\l2asm-disasm\\DAT_defs\\H5pt\\" + file.getName().replace("-ru", "-e").replace(".dat", ".ddf"));
        FileUtils.copyFile(ddfFile.toPath(), Path.of(AppConfig.TEMP_DIRECTORY + ddfFile.getName()) );

        File encoder = new File(AppConfig.DATA_DIRECTORY + "l2encdec\\l2encdec_old.exe");
        ProcessBuilder decodeProcess = new ProcessBuilder();
        decodeProcess.command(encoder.getAbsolutePath(), "-s", file.getName(), "dec-" + file.getName());
        decodeProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = decodeProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        File disasm = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2disasm.exe");
        String absolutePath = disasm.getAbsolutePath();
        ProcessBuilder disasmProcess = new ProcessBuilder();
        disasmProcess.command(absolutePath, "-d", ddfFile.getName(),"-e", ddfFile.getName().replace(".ddf", "-new.ddf"), "dec-" + file.getName(), file.getName().replace(".dat", ".txt"));
        disasmProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = disasmProcess.start();
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(File file){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String columns = String.join("\t", getCols());
            writer.write(columns); // Записываем строку в файл
            writer.newLine(); // Нужен, если хотите добавить новую строку
            getData().forEach(data -> {
                String join = String.join("\t", data.getData());
                try {
                    writer.write(join); // Записываем строку в файл
                    writer.newLine(); // Нужен, если хотите добавить новую строку
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключений
        }
        asmFile(file);
        encodeFile(file);
    }

    private void asmFile(File file){
        File ddfFile = new File(AppConfig.TEMP_DIRECTORY + getFile().getName().replace(".dat", "-new.ddf"));

        File asm = new File(AppConfig.DATA_DIRECTORY + "l2asm-disasm\\l2asm.exe");
        String absolutePath = asm.getAbsolutePath();
        ProcessBuilder disasmProcess = new ProcessBuilder();
        disasmProcess.command(absolutePath,
                "-d",
                ddfFile.getName(),
                file.getName().replace(".dat", ".txt") ,
                "dec-" + file.getName().replace(".txt", ".dat")
        );

        disasmProcess.directory(new File(AppConfig.TEMP_DIRECTORY));
        try {
            Process process = disasmProcess.start();
            System.out.println("Процесс запущен! 🚀");
            int exitCode = process.waitFor();
            System.out.println("Процесс завершился с кодом: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void encodeFile(File file){
        File encoder = new File(AppConfig.DATA_DIRECTORY + "l2encdec\\l2encdec_old.exe");
        ProcessBuilder decodeProcess = new ProcessBuilder();
        decodeProcess.command(encoder.getAbsolutePath(),
                "-h",
                "413",
                "dec-" + file.getName().replace(".txt", ".dat"),
                file.getName().replace(".txt", ".dat")
        );
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

    public void parseColumns(){
        String[] split = fileLines.get(0).split("\t");
        for (int i = 0; i < split.length; i++) {
            String stringColumn = split[i];
            if (stringColumn.equalsIgnoreCase("id")){
                Column column = new Column(i, stringColumn);
                this.idColumn = column;
                columns.add(column);
            } else if (stringColumn.equalsIgnoreCase("name")) {
                Column column = new Column(i, stringColumn);
                this.nameColumn = column;
                columns.add(column);
            } else {
                columns.add(new Column(i, stringColumn));
            }
        }
    }

    public void parseData(){
        for (int i = 1; i < fileLines.size(); i++) {
            String[] split = fileLines.get(i).split("\t");
            Data data1 = new Data(i, split);
            data.add(data1);
        }
    }


    public List<Data> getData() {
        return data;
    }

    public Column getIdColumn() {
        return idColumn != null ? idColumn : null;
    }

    public Column getNameColumn() {
        return nameColumn != null ? nameColumn : null;
    }

    public String[] getCols() {
        return cols;
    }

    public File getFile() {
        return file;
    }

    public static class Data implements Cloneable{
        private int index;
        private String[] data;

        public Data(int index, String[] data) {
            this.index = index;
            this.data = data;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String[] getData() {
            return data;
        }

        public void setData(String[] data) {
            this.data = data;
        }

        @Override
        public Data clone() {
            String[] clone = data.clone();

            return new Data(this.index, clone);
        }
    }



    public static class Column{
        private int index;
        private String name;

        public Column(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }


}



