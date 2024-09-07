package com.fuzzy.model;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFile {
    private final File csvFile;
    private String[] headers;
    private List<String[]> objects;

    public CsvFile(File csvFile) {
        this.csvFile = csvFile;
        this.objects = new ArrayList<>();
        parse();
    }

    public File getCsvFile() {
        return csvFile;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<String[]> getObjects() {
        return objects;
    }

    public void setObjects(List<String[]> objects) {
        this.objects = objects;
    }

    private void parse(){
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build()) {
            String[] headers = reader.readNext();
            setHeaders(headers);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for (int i = 0; i < nextLine.length; i++) {
                    nextLine[i] = nextLine[i].replace("\\", "\\\\");
                }
                getObjects().add(nextLine);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        try (CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvFile)).withSeparator('\t').withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build()) {
            // Запись заголовков
            writer.writeNext(headers);
            // Запись данных
            for (String[] object : objects) {
                writer.writeNext(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
