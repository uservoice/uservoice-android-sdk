package com.uservoice.uservoicesdk.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomField extends BaseModel {
	private String name;
	private List<String> predefinedValues;
	private boolean required;
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		name = getString(object, "name");
		required = !object.getBoolean("allow_blank");
		predefinedValues = new ArrayList<String>();
		if (object.has("possible_values")) {
			JSONArray values = object.getJSONArray("possible_values");
			for (int i = 0; i < values.length(); i++) {
				JSONObject value = values.getJSONObject(i);
				predefinedValues.add(getString(value, "value"));
			}
		}
	}

    @Override
    public void save(JSONObject object) throws JSONException {
        super.save(object);
        object.put("name", name);
        object.put("allow_blank", !required);
        JSONArray jsonPredefinedValues = new JSONArray();
        for (String value : predefinedValues) {
            JSONObject predefinedValue = new JSONObject();
            predefinedValue.put("value", value);
            jsonPredefinedValues.put(predefinedValue);
        }
        object.put("possible_values", jsonPredefinedValues);
    }

    public boolean isRequired() {
		return required;
	}
	
	public boolean isPredefined() {
		return predefinedValues.size() > 0;
	}
	
	public List<String> getPredefinedValues() {
		return predefinedValues;
	}
	
	public String getName() {
		return name;
	}
}
