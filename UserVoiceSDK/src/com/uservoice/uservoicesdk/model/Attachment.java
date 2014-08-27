package com.uservoice.uservoicesdk.model;

public class Attachment {

    private String fileName;
    private String contentType;
    private String data;

    public Attachment(String fileName, String contentType, String data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getData() {
        return data;
    }
}