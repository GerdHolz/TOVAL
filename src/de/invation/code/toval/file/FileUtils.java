package de.invation.code.toval.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.invation.code.toval.misc.SystemUtils;
import de.invation.code.toval.misc.SystemUtils.OperatingSystemType;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

    public static List<File> getFilesInDirectory(String directory) throws IOException {
        return getFilesInDirectory(directory, null);
    }

    public static List<File> getFilesInDirectory(String directory, String acceptedEnding) throws IOException {
        return getFilesInDirectory(directory, true, true, acceptedEnding);
    }

    public static List<File> getFilesInDirectory(String directory, boolean onlyFiles, boolean onlyVisibleFiles, final String acceptedEnding) throws IOException {
        return getFilesInDirectory(directory, onlyFiles, onlyVisibleFiles, new HashSet<>(Arrays.asList(acceptedEnding)));
    }

    public static List<File> getFilesInDirectory(String directory, boolean onlyFiles, boolean onlyVisibleFiles, final Set<String> acceptedEndings) throws IOException {
//		if(!dir.exists())
//			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Invalid or non-existing file path.");
//		if(!dir.isDirectory())
//			throw new ParameterException(ErrorCode.INCOMPATIBILITY, "File is not a directory.");

        File dir = Validate.directory(directory);

        List<File> result = new ArrayList<File>();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                if (acceptedEndings != null && !acceptedEndings.isEmpty()) {
                    boolean hasAcceptedEnding = false;
                    for (String acceptedEnding : acceptedEndings) {
                        if (fileName.endsWith(".".concat(acceptedEnding))) {
                            hasAcceptedEnding = true;
                            break;
                        }
                    }
                    return hasAcceptedEnding;
                } else {
                    return true;
                }
            }
        });
        for (int i = 0; files != null && i < files.length; i++) {
            if (onlyFiles && !files[i].isFile()) {
                continue;
            }
            if (onlyVisibleFiles && files[i].isHidden()) {
                continue;
            }
            result.add(files[i]);
        }
        return result;
    }

    public static List<String> getFileNamesInDirectory(String directory) throws IOException {
        return getFileNamesInDirectory(directory, false);
    }

    public static List<String> getFileNamesInDirectory(String directory, Set<String> acceptedEndings) throws IOException {
        return getFileNamesInDirectory(directory, false, acceptedEndings);
    }

    public static List<String> getFileNamesInDirectory(String directory, boolean absolutePath) throws IOException {
        return getFileNamesInDirectory(directory, true, true, absolutePath, null);
    }

    public static List<String> getFileNamesInDirectory(String directory, boolean absolutePath, Set<String> acceptedEndings) throws IOException {
        return getFileNamesInDirectory(directory, true, true, absolutePath, acceptedEndings);
    }

    public static List<String> getFileNamesInDirectory(String directory, boolean onlyFiles, boolean onlyVisibleFiles, boolean absolutePath, Set<String> acceptedEndings) throws IOException {
        List<File> files = getFilesInDirectory(directory, onlyFiles, onlyVisibleFiles, acceptedEndings);
        List<String> result = new ArrayList<String>();
        for (File file : files) {
            if (absolutePath) {
                result.add(file.getAbsolutePath());
            } else {
                result.add(file.getName());
            }
        }
        return result;
    }

    public static List<File> getSubdirectories(String directory) throws IOException {
        Validate.directory(directory);
        File dir = new File(directory);
        if (!dir.exists()) {
            throw new ParameterException(ErrorCode.INCOMPATIBILITY, "Invalid or non-existing directory.");
        }

        List<File> result = new ArrayList<File>();
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            if (!files[i].isDirectory()) {
                continue;
            }
            result.add(files[i]);
        }
        return result;
    }

    public static void deleteFile(String fileName) throws Exception {
        deleteFile(new File(fileName));
    }

    public static void deleteFile(File file) throws Exception {
        deleteFile(file, false);
    }

    public static void deleteFile(String fileName, boolean followLinks) throws Exception {
        deleteFile(new File(fileName), followLinks);
    }

    public static void deleteFile(File file, boolean followLinks) throws Exception {

        if (!file.exists()) {
            throw new IllegalArgumentException("No such file or directory: " + file.getAbsolutePath());
        }

        if (!file.canWrite()) {
            throw new IllegalArgumentException("Write protection: " + file.getAbsolutePath());
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException("File is a directory: " + file.getAbsolutePath());
        }

        boolean success = false;
        if (followLinks) {
            success = file.delete();
        } else {
            success = removeLinkOnly(file);
        }

        if (!success) {
            throw new IllegalArgumentException("Unspecified deletion error: " + file.getAbsolutePath());
        }
    }

    public static boolean removeLinkOnly(File file) throws Exception {
        if (file == null) {
            return false;
        }

        OperatingSystemType os = SystemUtils.getOperatingSystem();

        String[] command = new String[3];
        String path = file.getPath();
        switch (os) {
            case win:
                command[0] = "cmd";
                command[1] = "/C";
                command[2] = "del \"" + path + "\"";
                break;
            case mac:
                command[0] = "/bin/sh";
                command[1] = "-c";
                command[2] = "rm \"" + path + "\"";
                break;
            default:
                command[0] = "/bin/sh";
                command[1] = "-c";
                command[2] = "rm \"" + path + "\"";
                break;
        }
        SystemUtils.runCommand(command);
        return true;
    }

    public static void deleteDirectory(String dirName, boolean recursive) throws Exception {
        deleteDirectory(dirName, recursive, false);
    }

    public static void deleteDirectory(String dirName, boolean recursive, boolean followLinks) throws Exception {
        File file = new File(dirName);

        if (!file.exists()) {
            throw new IllegalArgumentException("No such file or directory: " + dirName);
        }

        if (!file.canWrite()) {
            throw new IllegalArgumentException("Write protection: " + dirName);
        }

        if (!file.isDirectory()) {
            throw new IllegalArgumentException("No directory: " + dirName);
        }

        String[] files = file.list();
        if (files != null && files.length > 0) {
            if (!recursive) {
                throw new IllegalArgumentException("Cannot delete non-empty directory in non-recursive mode: " + dirName);
            }

            for (int i = 0; i < files.length; i++) {
                File childFile = new File(file.getPath() + "/" + files[i]);
                if (childFile.isDirectory()) {
                    deleteDirectory(childFile.getAbsolutePath(), recursive, followLinks);
                } else {
                    deleteFile(childFile.getAbsolutePath(), followLinks);
                }
            }
        }

        boolean success = file.delete();

        if (!success) {
            throw new IllegalArgumentException("Unspecified deletion error: " + dirName);
        }
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getPath(File f) {
        return getPath(f.getAbsolutePath());
    }

    public static String getPath(String absolutePath) {
        return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator) + 1);
    }

    public static String getFile(File f) {
        return getFile(f.getAbsolutePath());
    }

    public static String getFile(String absolutePath) {
        if (absolutePath.endsWith(File.separator)) {
            return "";
        }
        return absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1, absolutePath.length());
    }

    public static String getFileWithoutEnding(File f) {
        return getFileWithoutEnding(f.getAbsolutePath());
    }

    public static String getFileWithoutEnding(String absolutePath) {
        return separateFileNameFromEnding(getFile(absolutePath));
    }

    public static String separateFileNameFromEnding(File f) {
        String name = f.getName();
        if (name.contains(".")) {
            return name.substring(0, name.lastIndexOf('.'));
        }
        return name;
    }

    public static String separateFileNameFromEnding(String file) {
        if (file.contains(".")) {
            return file.substring(0, file.lastIndexOf('.'));
        }
        return file;
    }

    public static String getDirName(File dir) {
        return getDirName(dir.getAbsolutePath());
    }

    public static String getDirName(String file) {
        File dir = new File(file);
        Validate.directory(dir);

        String sep = System.getProperty("file.separator");
        if (file.endsWith(sep)) {
            if (file.length() == 1) {
                return "";
            }

            char[] chars = file.toCharArray();
            int index = file.length() - 2;
            while (index >= 0) {
                if (chars[index] == sep.charAt(0)) {
                    break;
                }
                index--;
            }
            if (index == 0 && chars[0] != sep.charAt(0)) {
                return file.substring(0, file.length() - 1);
            }
            return file.substring(index + 1, file.length() - 1);
        } else {
            return file.substring(file.lastIndexOf(sep) + 1);
        }
    }

    public static void copy(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static File writeFile(String path, String fileName, String content) throws IOException {
        FileWriter writer = new FileWriter(path, fileName);
        writer.write(content);
        writer.closeFile();
        return writer.getFile();
    }

    public static String readStringFromFile(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line).append(FileWriter.DEFAULT_EOL_STRING);
        }
        reader.closeFile();
        return stringBuffer.toString();
    }

    public static List<String> readLinesFromFile(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.closeFile();
        return lines;
    }

    public static long getLineCount(String fileName, String encodingName) throws IOException {
        long linesCount = 0;
        File file = new File(fileName);
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(file);
            Charset encoding = Charset.forName(encodingName);
            Reader fileReader = new InputStreamReader(fileIn, encoding);
            int bufferSize = 4096;
            Reader reader = new BufferedReader(fileReader, bufferSize);
            char[] buffer = new char[bufferSize];
            int prevChar = -1;
            int readCount = reader.read(buffer);
            while (readCount != -1) {
                for (int i = 0; i < readCount; i++) {
                    int nextChar = buffer[i];
                    switch (nextChar) {
                        case '\r': {
                            linesCount++;
                            break;
                        }
                        case '\n': {
                            if (prevChar == '\r') {
                            } else {
                                linesCount++;
                            }
                            break;
                        }
                    }
                    prevChar = nextChar;
                }
                readCount = reader.read(buffer);
            }
            if (prevChar != -1) {
                switch (prevChar) {
                    case '\r':
                    case '\n': {
                        break;
                    }
                    default: {
                        linesCount++;
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fileIn.close();
        }
        return linesCount;
    }
}
