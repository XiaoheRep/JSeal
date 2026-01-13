package com.xiaohelab.jseal.service;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

/**
 * PDF解密服务
 */
public class PdfDecryptionService {

    /**
     * 解密PDF文件（移除密码保护）
     *
     * @param inputFile  加密的PDF文件
     * @param outputFile 解密后的输出文件
     * @param password   密码（用户密码或所有者密码）
     * @throws IOException 解密失败时抛出
     */
    public void decrypt(File inputFile, File outputFile, String password) throws IOException {
        try (PDDocument document = PDDocument.load(inputFile, password)) {
            // 移除所有安全设置
            document.setAllSecurityToBeRemoved(true);
            
            // 保存解密后的文件
            document.save(outputFile);
        }
    }

    /**
     * 验证密码是否正确
     *
     * @param pdfFile  PDF文件
     * @param password 密码
     * @return true 如果密码正确或文件未加密
     */
    public boolean verifyPassword(File pdfFile, String password) {
        try (PDDocument document = PDDocument.load(pdfFile, password)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 检查PDF是否加密
     */
    public boolean isEncrypted(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.isEncrypted();
        } catch (IOException e) {
            // 如果无法打开，可能是需要密码
            return true;
        }
    }

    /**
     * 获取PDF基本信息
     */
    public PdfInfo getPdfInfo(File pdfFile, String password) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile, password)) {
            PdfInfo info = new PdfInfo();
            info.setPageCount(document.getNumberOfPages());
            info.setEncrypted(document.isEncrypted());
            if (document.getDocumentInformation() != null) {
                info.setTitle(document.getDocumentInformation().getTitle());
                info.setAuthor(document.getDocumentInformation().getAuthor());
            }
            return info;
        }
    }

    /**
     * PDF信息类
     */
    public static class PdfInfo {
        private int pageCount;
        private boolean encrypted;
        private String title;
        private String author;

        public int getPageCount() { return pageCount; }
        public void setPageCount(int pageCount) { this.pageCount = pageCount; }
        public boolean isEncrypted() { return encrypted; }
        public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
    }
}
