package com.uservoice.uservoicesdk.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.RestMethod;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;


public class BaseModel {
	
	private static final String TAG = "com.uservoice.uservoicesdk.model.BaseModel";
	
	protected int id;
	
	public void load(JSONObject object) throws JSONException {
		id = object.getInt("id");
	}
	
	public int getId() {
		return id;
	}
	
	protected String getString(JSONObject object, String key) throws JSONException {
		return object.isNull(key) ? null : object.getString(key);
	}
	
	protected Date getDate(JSONObject object, String key) throws JSONException {
		String dateString = getString(object, key);
		try {
			return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString).toGregorianCalendar().getTime();
		} catch (DatatypeConfigurationException e) {
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
	
	protected static void doGet(String path, RestTaskCallback callback) {
		doGet(path, null, callback);
	}
	
	protected static void doPost(String path, RestTaskCallback callback) {
		doPost(path, null, callback);
	}
	
	protected static void doDelete(String path, RestTaskCallback callback) {
		doDelete(path, null, callback);
	}
	
	protected static void doPut(String path, RestTaskCallback callback) {
		doPut(path, null, callback);
	}
	
	protected static void doGet(String path, Map<String,String> params, RestTaskCallback callback) {
		new RestTask(RestMethod.GET, path, params, callback).execute();
	}
	
	protected static void doPost(String path, Map<String,String> params, RestTaskCallback callback) {
		new RestTask(RestMethod.POST, path, params, callback).execute();
	}
	
	protected static void doDelete(String path, Map<String,String> params, RestTaskCallback callback) {
		new RestTask(RestMethod.DELETE, path, params, callback).execute();
	}
	
	protected static void doPut(String path, Map<String,String> params, RestTaskCallback callback) {
		new RestTask(RestMethod.PUT, path, params, callback).execute();
	}
	
	protected static <T extends BaseModel> List<T> deserializeList(JSONObject object, String rootKey, Class<T> modelClass) {
		try {
			JSONArray array = object.getJSONArray(rootKey);
			List<T> list = new ArrayList<T>(array.length());
			for (int i = 0; i < array.length(); i++) {
				T model = modelClass.newInstance();
				model.load(array.getJSONObject(i));
				list.add(model);
			}
			return list;
		} catch (JSONException e) {
			Log.e(TAG, "JSON deserialization failure for " + modelClass + " " + e.getMessage() + " JSON: " + object.toString());
			return null;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			Log.e(TAG, "Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
			return null;
		} catch (InstantiationException e) {
			Log.e(TAG, "Reflection failed trying to instantiate " + modelClass + " " + e.getMessage());
			return null;
		}
	}
	
	protected static <T extends BaseModel> T deserializeObject(JSONObject object, String rootKey, Class<T> modelClass) {
		try {
			JSONObject singleObject = object.getJSONObject(rootKey);
			T model = modelClass.newInstance();
			model.load(singleObject);
			return modelClass.cast(model);
		} catch (JSONException e) {
			Log.e(TAG, "JSON deserialization failure for "  + modelClass + " " + e.getMessage() + " JSON: " + object.toString());
			return null;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			Log.e(TAG, "Reflection failed trying to call load on " + modelClass + " " + e.getMessage());
			return null;
		} catch (InstantiationException e) {
			Log.e(TAG, "Reflection failed trying to instantiate " + modelClass + " " + e.getMessage());
			return null;
		}
	}
	
	protected static List<BaseModel> deserializeHeterogenousList(JSONObject object, String rootKey) {
		try {
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
		} catch (JSONException e) {
			Log.e(TAG, "JSON deserialization failure " + e.getMessage() + " JSON: " + object.toString());
			return null;
		}
	}

}
