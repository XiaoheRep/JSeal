package com.xiaohelab.jseal.model;

/**
 * PDF 文件信息
 */
public class PdfInfo {
    private String fileName;
    private int pageCount;
    private boolean encrypted;
    private String pdfVersion;
    private String author;
    private String title;

    public PdfInfo() {}

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    public String getPdfVersion() { return pdfVersion; }
    public void setPdfVersion(String pdfVersion) { this.pdfVersion = pdfVersion; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PdfInfo info = new PdfInfo();

        public Builder fileName(String fileName) { info.fileName = fileName; return this; }
        public Builder pageCount(int pageCount) { info.pageCount = pageCount; return this; }
        public Builder encrypted(boolean encrypted) { info.encrypted = encrypted; return this; }
        public Builder pdfVersion(String pdfVersion) { info.pdfVersion = pdfVersion; return this; }
        public Builder author(String author) { info.author = author; return this; }
        public Builder title(String title) { info.title = title; return this; }
        public PdfInfo build() { return info; }
    }
}
