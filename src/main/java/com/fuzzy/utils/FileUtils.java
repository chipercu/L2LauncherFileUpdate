package com.fuzzy.utils;

import com.fuzzy.model.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;

public final class FileUtils {

    private FileUtils() {}

    public static void copyFile(Path source, Path target){
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteDirectoryIfExists(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return false;
        }

        IOException[] exception = new IOException[]{null};
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                if (exc != null && exception[0] == null) {
                    exception[0] = exc;
                }

                try {
                    Files.delete(dir);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (exception[0] != null) {
            throw exception[0];
        }
        return true;
    }

    public static boolean deleteDirectoryIfExists(Path dir, int attempts, Duration attemptTimeout) throws IOException {
        int attemptCount = 0;
        while (true) {
            try {
                return deleteDirectoryIfExists(dir);
            } catch (IOException e) {
                if ((attemptCount++) >= attempts) {
                    throw e;
                }
                try {
                    Thread.sleep(attemptTimeout.toMillis());
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
        }
    }

    public static long sizeOfDirectory(final Path directory) {
        long size = 0;
        try (Stream<Path> walk = Files.walk(directory)) {
            size = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    /**
     * Makes a directory. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * @param dir
     * @throws IOException
     * @throws  SecurityException
     */
    public static void ensureDirectory(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            Files.createDirectory(dir);
        }
    }

    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * @param dir
     * @throws IOException
     * @throws SecurityException
     */
    public static void ensureDirectories(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            FileUtils.ensureDirectories(dir);
//            ensureDirectories(dir.getParent());
            Files.createDirectory(dir);
        }
    }

    public static void copyDirectory(Path source, Path target) throws IOException {
        String sourceDirectoryLocation = source.toAbsolutePath().toString();
        String destinationDirectoryLocation = target.toAbsolutePath().toString();
        try (Stream<Path> stream = Files.walk(Paths.get(sourceDirectoryLocation))) {
            stream.forEach(sourcePathFile -> {
                Path destination = Paths.get(destinationDirectoryLocation, sourcePathFile.toString()
                        .substring(sourceDirectoryLocation.length()));
                try {
                    Files.copy(sourcePathFile, destination);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static boolean isLocked(Path filePath) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE);
             FileLock lock = channel.tryLock()) {
            return lock == null;
        } catch (OverlappingFileLockException e) {
            return true;
        }
    }

    public static void deleteQuietly(Collection<Path> pathes) {
        if (pathes == null) {
            return;
        }

        for (Path path : pathes) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignore) {}
        }
    }


    /**
     * Создает временный файл который забираем из uri.
     * <br>
     * Вернет готовый Path для работы.
     * <br>
     * За удаление временных файлов отвечает ОС или вызывающая данный метод сторона.
     *
     * Адрес файла в файловой системе.
     * @return вернет Path файла который обработан для работы при многосерверной развертке системы.
     *  Может вернуть {@link (IOException)} если файл в процессе работы был удален или перемещен.
     */
//    public static Path copyFileToTempDir(@NonNull Component component, @NonNull URI uri) throws PlatformException {
//        try {
//            final Path temp = Files.createTempFile("temp_", "");
//            final ClusterFile clusterFile = new ClusterFile(component, uri);
//            clusterFile.copyTo(temp, StandardCopyOption.REPLACE_EXISTING);
//            return temp;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void saveToFile(byte[] data, Path path) {
        if (data == null) throw new IllegalArgumentException();
        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path saveToFileIntoSubsystemsTempDir(byte[] data, String fileName, Path path) {
        saveToFile(data, path);
        return path;
    }

    public static Path saveToFileIntoSubsystemsTempDir(byte[] data, Path tempDir) {
        Path path = null;
//        do {
//            String fileName = RandomStringUtils.randomAlphanumeric(20, 21);
//            path = tempDir.resolve(fileName);
//        } while (Files.exists(path));
//        saveToFile(data, path);
        return path;
    }

    public static byte[] readBytesFromFile(Path path){
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            inputStream.transferTo(output);
            return output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        // Проверяем, есть ли точка в имени файла
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1); // Возвращаем расширение
        }
        return ""; // Если расширение не найдено
    }

    public static String relativePath(Project project, File file){
        return file.getAbsolutePath().replace(project.getPath() + File.separator, "");
    }

    public static String getMd5Hash(byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
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
}
