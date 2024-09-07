package com.fuzzy.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzzy.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private int version;
    private String gitUrl;
    private String name;
    private String path;
    private List<String> blockedFiles = new ArrayList<>();

    public Project() {
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public List<String> getBlockedFiles() {
        return blockedFiles;
    }

    public void setBlockedFiles(List<String> blockedFiles) {
        this.blockedFiles = blockedFiles;
    }

    public void addBockedFile(File file){
        getBlockedFiles().add(relativePath(file));
    }

    public void removeBlockedFile(File file){
        getBlockedFiles().remove(relativePath(file));
    }

    public boolean containBockedFile(File file){
        return getBlockedFiles().contains(relativePath(file));
    }

    private String relativePath(File file){
        return file.getAbsolutePath().replace(path + File.separator, "");
    }

    public void saveProject(){
        try {
            new ObjectMapper().writeValue(new File(AppConfig.PROJECTS_DIRECTORY + getName() + ".json"), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
