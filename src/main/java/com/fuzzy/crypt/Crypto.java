package com.fuzzy.crypt;

import com.fuzzy.config.AppConfig;
import com.fuzzy.utils.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by a.kiperku
 * Date: 29.08.2023
 */

public class Crypto {

    private final Charset charset = StandardCharsets.UTF_8;
    private final int saltLength = 128;
    private final Path keyPath;
    private volatile byte[] secretKey;

    public Crypto(Path keyPath) {
        this.keyPath = keyPath;
        if (keyPath.getNameCount() == 0) {
            throw new InvalidParameterException();
        }
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public synchronized void createKey() throws NoSuchAlgorithmException, IOException {
        final Path parent = this.keyPath;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            keyGen.init(128, sr); // must be equal to 128, 192 or 256

            this.secretKey = keyGen.generateKey().getEncoded();

        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException();
        }
    }

    public synchronized void createKeyIfNotExists() throws NoSuchAlgorithmException, IOException {
        if (!Files.exists(keyPath)) {
            createKey();
            FileUtils.saveToFile(getSecretKey(), Path.of(AppConfig.CONFIG_DIRECTORY + "secret_key"));
        }
    }

    public byte[] encrypt(byte[] value) throws GeneralSecurityException {
        if (value == null) {
            return null;
        }
        byte[] salt = new byte[this.saltLength];
        CryptoRandom.secureRandom.nextBytes(salt);
        value = addAll(value, salt);
        return cryptoExec(value, Cipher.ENCRYPT_MODE);
    }


    public byte[] encrypt(String value) throws GeneralSecurityException {
        if (value == null) {
            return null;
        }
        byte[] bytes = value.getBytes(this.charset);
        return encrypt(bytes);
    }

    public byte[] decrypt(byte[] value) throws GeneralSecurityException {
        if (value == null) {
            return null;
        }
        byte[] result = cryptoExec(value, Cipher.DECRYPT_MODE);
        result = Arrays.copyOfRange(result, 0, result.length - saltLength);
        return result;
    }

    public String decryptAsString(byte[] value) throws GeneralSecurityException {
        if (value == null) {
            return null;
        }
        return new String(decrypt(value), charset);
    }

    private byte[] cryptoExec(byte[] value, int encryptMode) throws GeneralSecurityException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(encryptMode, secretKeySpec);
            return cipher.doFinal(value);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException(e);
        }
    }

    public byte[] getSecretKey() {
        if (this.secretKey == null) {
//            return null;
            synchronized (this) {
                if (this.secretKey == null) {
                    this.secretKey = FileUtils.readBytesFromFile(this.keyPath);
                }
            }
        }
        return this.secretKey;
    }

    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        } else {
            Class<?> type1 = array1.getClass().getComponentType();
            byte[] joinedArray = (byte[]) Array.newInstance(type1, array1.length + array2.length);
            System.arraycopy(array1, 0, joinedArray, 0, array1.length);
            try {
                System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
                return joinedArray;
            } catch (ArrayStoreException var6) {
                Class<?> type2 = array2.getClass().getComponentType();
                if (!type1.isAssignableFrom(type2)) {
                    throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), var6);
                } else {
                    throw var6;
                }
            }
        }
    }
    public static byte[] clone(byte[] array) {
        return array == null ? null : array.clone();
    }

}
