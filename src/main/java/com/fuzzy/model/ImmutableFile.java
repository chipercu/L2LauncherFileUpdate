package com.fuzzy.model;

import java.util.Objects;

public class ImmutableFile {

    private String fileName;
    private String sha;

    public ImmutableFile() {
    }

    public ImmutableFile(String fileName, String sha) {
        this.fileName = fileName;
        this.sha = sha;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableFile that = (ImmutableFile) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(sha, that.sha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, sha);
    }
}
