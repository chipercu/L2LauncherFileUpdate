package com.fuzzy;

import com.fuzzy.utils.HexConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientFile {

    private String fileName;
    private String fileDirectory;
    private String sha;
    private boolean isEditable;


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

}
