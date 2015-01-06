package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Attachment extends BaseModel {

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

    @Override
    public void save(JSONObject object) throws JSONException {
        object.put("fileName", fileName);
        object.put("contentType", contentType);
        object.put("data", data);
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        fileName = getString(object, "fileName");
        contentType = getString(object, "contentType");
        data = getString(object, "data");
    }
}