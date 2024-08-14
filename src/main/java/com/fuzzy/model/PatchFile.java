package com.fuzzy.model;

import com.fuzzy.services.PathFilesManager;

import java.io.File;
import java.util.Objects;

public class PatchFile {

    private final File file;
    private final String folder;
    private final String fileName;
    private String sha;
    private final long size;
    private boolean isImmutable;

    public PatchFile(File file) {
        this.file = file;
        this.folder = file.getParentFile().getName();
        this.fileName = file.getName();
        this.size = file.length();
        this.isImmutable = false;
    }


    public String getFolder() {
        return folder;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSha() {
        return sha;
    }

    public long getSize() {
        return size;
    }

    public boolean isImmutable() {
        return isImmutable;
    }

    public void setImmutable(boolean immutable) {
        if (immutable){
            this.sha = PathFilesManager.getSHA(file);
        }
        isImmutable = immutable;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatchFile patchFile = (PatchFile) o;
        return size == patchFile.size && isImmutable == patchFile.isImmutable && Objects.equals(folder, patchFile.folder) && Objects.equals(fileName, patchFile.fileName) && Objects.equals(sha, patchFile.sha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folder, fileName, sha, size, isImmutable);
    }

    @Override
    public String toString() {
        return "PatchFile{" +
                "folder='" + folder + '\'' +
                ", fileName='" + fileName + '\'' +
                ", sha='" + sha + '\'' +
                ", size=" + size +
                ", isImmutable=" + isImmutable +
                '}';
    }
}
