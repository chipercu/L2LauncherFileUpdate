package com.fuzzy.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientFiles {
    private List<String> clientFileList = new ArrayList<>();
    private Map<String, String> blockedFiles = new HashMap<>();


    public List<String> getClientFileList() {
        return clientFileList;
    }

    public void setClientFileList(List<String> clientFileList) {
        this.clientFileList = clientFileList;
    }

    public Map<String, String> getBlockedFiles() {
        return blockedFiles;
    }

    public void setBlockedFiles(Map<String, String> blockedFiles) {
        this.blockedFiles = blockedFiles;
    }
}
