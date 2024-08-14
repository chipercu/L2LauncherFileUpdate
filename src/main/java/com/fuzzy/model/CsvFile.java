package com.fuzzy.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvFile {

    private final File csvFile;

    private String[] headers;

    private List<String[]> objects;

    public CsvFile(File csvFile) {
        this.csvFile = csvFile;
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
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] headers = reader.readNext();
            setHeaders(headers);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                getObjects().add(nextLine);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

}
