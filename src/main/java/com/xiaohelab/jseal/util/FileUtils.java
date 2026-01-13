package com.xiaohelab.jseal.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 文件工具类
 */
public class FileUtils {

    /**
     * 获取文件扩展名
     */
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * 判断是否为PDF文件
     */
    public static boolean isPdfFile(File file) {
        return file.isFile() && "pdf".equalsIgnoreCase(getExtension(file));
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    /**
     * 生成不冲突的输出文件名
     */
    public static File getUniqueFile(File file) {
        if (!file.exists()) {
            return file;
        }

        String name = file.getName();
        String baseName;
        String extension;
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        } else {
            baseName = name;
            extension = "";
        }

        int counter = 1;
        File newFile;
        do {
            newFile = new File(file.getParent(), baseName + "_" + counter + extension);
            counter++;
        } while (newFile.exists());

        return newFile;
    }

    /**
     * 备份文件
     */
    public static File backup(File file) throws IOException {
        String backupName = file.getName() + ".bak";
        File backupFile = new File(file.getParent(), backupName);
        Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return backupFile;
    }

    /**
     * 确保目录存在
     */
    public static void ensureDirectoryExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 获取用户数据目录
     */
    public static File getAppDataDir() {
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, ".jseal");
        ensureDirectoryExists(appDir);
        return appDir;
    }

    /**
     * 获取证书存储目录
     */
    public static File getCertificateDir() {
        File certDir = new File(getAppDataDir(), "certificates");
        ensureDirectoryExists(certDir);
        return certDir;
    }
}
