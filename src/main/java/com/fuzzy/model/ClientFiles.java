package com.fuzzy.model;

import java.util.ArrayList;
import java.util.List;


public class ClientFiles {
    private List<String> clientFileList = new ArrayList<>();
    private List<String> blockedFiles = new ArrayList<>();


    public List<String> getClientFileList() {
        return clientFileList;
    }

    public void setClientFileList(List<String> clientFileList) {
        this.clientFileList = clientFileList;
    }

    public List<String> getBlockedFiles() {
        return blockedFiles;
    }

    public void setBlockedFiles(List<String> blockedFiles) {
        this.blockedFiles = blockedFiles;
    }
}
