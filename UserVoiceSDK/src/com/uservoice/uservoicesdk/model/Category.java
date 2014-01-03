package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Category extends BaseModel {
    private String name;

    @Override
    public void load(JSONObject object) throws JSONException {
        super.load(object);
        name = getString(object, "name");
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
