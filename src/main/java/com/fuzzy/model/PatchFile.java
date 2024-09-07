package com.fuzzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuzzy.services.GitService;
import com.fuzzy.utils.HexConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PatchFile {

    @JsonIgnore
    private final BooleanProperty lockProperty = new SimpleBooleanProperty();
    @JsonIgnore
    private File file;
    private String folder;
    private String fileName;
    private String sha;
    private long size;
    private boolean isImmutable;
    private boolean patch_include;

    public PatchFile() {
    }

    public PatchFile(File file, String folder) {
        this.file = file;
        this.folder = folder;
        this.fileName = file.getName();
        this.size = file.length();
        this.isImmutable = false;
        this.patch_include = false;
    }

    public File getFile() {
        return new File(folder + fileName);
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

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setImmutable(boolean immutable) {
        this.isImmutable = immutable;
        lockProperty.set(immutable);
    }

    public boolean isPatch_include() {
        return patch_include;
    }

    public void setPatch_include(boolean patch_include) {
        this.patch_include = patch_include;
    }

    public void include(Project project){
        setPatch_include(true);
        GitService.addFile(project, getFile());
    }

    public void exclude(Project project){
        setPatch_include(false);
        GitService.removeFile(project, getFile());
    }


    public void lock(Project project) {
        this.sha = getSHA(new File(project.getPath() + File.separator + folder + fileName));
        setImmutable(true);
    }

    public void unlock() {
        this.sha = null;
        setImmutable(false);
    }

    public BooleanProperty lockProperty() {
        return lockProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatchFile patchFile = (PatchFile) o;
        return size == patchFile.size && isImmutable == patchFile.isImmutable && Objects.equals(folder, patchFile.folder) && Objects.equals(fileName, patchFile.fileName) && Objects.equals(sha, patchFile.sha);
    }


    public static String getMd5Hash(byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        String digestHex = HexConverter.bytesToHex(md.digest(bytes));
        return digestHex.toUpperCase();
    }

    public static String getSHA(File file) {
        return getMd5Hash(getByteFile(file));
    }

    public static byte[] getByteFile(File file) {
        byte[] fileBytes = new byte[0];
        try (InputStream inputStream = new FileInputStream(file); ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteOutput.write(buffer, 0, bytesRead);
            }
            fileBytes = byteOutput.toByteArray();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла в массив байтов: " + e.getMessage());
        }
        return fileBytes;
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
