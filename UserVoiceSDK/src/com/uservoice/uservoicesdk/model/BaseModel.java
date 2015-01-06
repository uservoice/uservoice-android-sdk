package com.uservoice.uservoicesdk.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.RestMethod;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;


public class BaseModel {

    protected int id;

    public void load(JSONObject object) throws JSONException {
        id = object.getInt("id");
    }

    public int getId() {
        return id;
    }

    public boolean persist(SharedPreferences prefs, String prefsKey, String rootKey) {
        JSONObject object = new JSONObject();
        JSONObject container = new JSONObject();
        try {
            save(object);
            container.put(rootKey, object);
        } catch (JSONException e) {
            return false;
        }
        Editor edit = prefs.edit();
        edit.putString(prefsKey, container.toString());
        return edit.commit();
    }

    public static <T extends BaseModel> T load(SharedPreferences prefs, String prefsKey, String rootKey, Class<T> modelClass) {
        try {
            JSONObject container = new JSONObject(prefs.getString(prefsKey, "{}"));
            return deserializeObject(container, rootKey, modelClass);
        } catch (JSONException e) {
            return null;
        }
    }

    public void save(JSONObject object) throws JSONException {
        object.put("id", id);
    }

    protected String getString(JSONObject object, String key) throws JSONException {
        return object.isNull(key) ? null : Html.fromHtml(object.getString(key)).toString().trim();
    }

    protected String getHtml(JSONObject object, String key) throws JSONException {
        return object.isNull(key) ? null : object.getString(key);
    }

    protected Map<String, String> deserializeStringMap(JSONObject object) throws JSONException {
        Iterator<String> keyIter = object.keys();
        Map<String, String> map = new HashMap<String,String>(object.length());
        while (keyIter.hasNext()) {
            String fieldKey = keyIter.next();
            String value = object.getString(fieldKey);
            map.put(fieldKey, value);
        }
        return map;
    }

    protected JSONObject serializeStringMap(Map<String, String> map) throws JSONException {
        JSONObject object = new JSONObject();
        for (String key : map.keySet()) {
            object.put(key, map.get(key));
        }
        return object;
    }

    protected Map<String, Object> deserializeMap(JSONObject object) throws JSONException {
        Iterator<String> keyIter = object.keys();
        Map<String, Object> map = new HashMap<String, Object>(object.length());
        while (keyIter.hasNext()) {
            String fieldKey = keyIter.next();
            Object value = object.get(fieldKey);
            if (value instanceof JSONObject) {
                value = deserializeMap((JSONObject) value);
            }
            map.put(fieldKey, value);
        }
        return map;
    }

    protected JSONObject serializeMap(Map<String, Object> map) throws JSONException {
        JSONObject object = new JSONObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map) {
                value = serializeMap((Map<String, Object>) value);
            }
            object.put(key, value);
        }
        return object;
    }

    protected <T extends BaseModel> JSONArray serializeList(List<T> list) throws JSONException {
        JSONArray array = new JSONArray();
        for (T object : list) {
            array.put(serializeObject(object));
        }
        return array;
    }

    protected <T extends BaseModel> JSONObject serializeObject(T object) throws JSONException {
        JSONObject obj = new JSONObject();
        object.save(obj);
        return obj;
    }

    @SuppressLint("SimpleDateFormat")
    protected Date getDate(JSONObject object, String key) throws JSONException {
        String dateString = getString(object, key);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        try {
            return format.parse(dateString);
        } catch (ParseException e1) {
            throw new JSONException("Could not parse date: " + dateString);
        }
    }

    protected static Session getSession() {
        return Session.getInstance();
    }

    protected static Config getConfig() {
        return getSession().getConfig();
    }

    protected static ClientConfig getClientConfig() {
        return getSession().getClientConfig();
    }

    protected static String apiPath(String path, Object... args) {
        return "/api/v1" + String.format(path, args);
    }

    protected static RestTask doGet(String path, RestTaskCallback callback) {
        return doGet(path, null, callback);
    }

    protected static RestTask doPost(String path, RestTaskCallback callback) {
        return doPost(path, null, callback);
    }

    protected static RestTask doDelete(String path, RestTaskCallback callback) {
        return doDelete(path, null, callback);
    }

    protected static RestTask doPut(String path, RestTaskCallback callback) {
        return doPut(path, null, callback);
    }

    protected static RestTask doGet(String path, Map<String, String> params, RestTaskCallback callback) {
        RestTask task = new RestTask(RestMethod.GET, path, params, callback);
        task.execute();
        return task;
    }

    protected static RestTask doPost(String path, Map<String, String> params, RestTaskCallback callback) {
        RestTask task = new RestTask(RestMethod.POST, path, params, callback);
        task.execute();
        return task;
    }

    protected static RestTask doDelete(String path, Map<String, String> params, RestTaskCallback callback) {
        RestTask task = new RestTask(RestMethod.DELETE, path, params, callback);
        task.execute();
        return task;
    }

    protected static RestTask doPut(String path, Map<String, String> params, RestTaskCallback callback) {
        RestTask task = new RestTask(RestMethod.PUT, path, params, callback);
        task.execute();
        return task;
    }

    protected static <T extends BaseModel> List<T> deserializeList(JSONObject object, String rootKey, Class<T> modelClass) throws JSONException {
        if (!object.has(rootKey))
            return null;
        try {
            JSONArray array = object.getJSONArray(rootKey);
            List<T> list = new ArrayList<T>(array.length());
            for (int i = 0; i < array.length(); i++) {
                T model = modelClass.newInstance();
                model.load(array.getJSONObject(i));
                list.add(model);
            }
            return list;
        } catch (IllegalArgumentException e) {
            throw new JSONException("Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new JSONException("Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
        } catch (InstantiationException e) {
            throw new JSONException("Reflection failed trying to instantiate " + modelClass + " " + e.getMessage());
        }
    }

    protected static <T extends BaseModel> T deserializeObject(JSONObject object, String rootKey, Class<T> modelClass) throws JSONException {
        if (!object.has(rootKey))
            return null;
        try {
            JSONObject singleObject = object.getJSONObject(rootKey);
            T model = modelClass.newInstance();
            model.load(singleObject);
            return modelClass.cast(model);
        } catch (JSONException e) {
            throw new JSONException("JSON deserialization failure for " + modelClass + " " + e.getMessage() + " JSON: " + object.toString());
        } catch (IllegalArgumentException e) {
            throw new JSONException("Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new JSONException("Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
        } catch (InstantiationException e) {
            throw new JSONException("Reflection failed trying to instantiate " + modelClass + " " + e.getMessage());
        }
    }

    protected static List<BaseModel> deserializeHeterogenousList(JSONObject object, String rootKey) throws JSONException {
        if (!object.has(rootKey))
            return null;
        JSONArray array = object.getJSONArray(rootKey);
        List<BaseModel> list = new ArrayList<BaseModel>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            String type = o.getString("type");
            BaseModel model;

            if (type.equals("suggestion"))
                model = new Suggestion();
            else if (type.equals("article"))
                model = new Article();
            else
                continue;

            model.load(o);
            list.add(model);
        }
        return list;
    }

}
