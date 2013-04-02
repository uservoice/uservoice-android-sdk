package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Ticket extends BaseModel {
	
	public static void createTicket(String message, Map<String,String> customFields, final Callback<Ticket> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("ticket[message]", message);

		// TODO name / email if not signed in
		// TODO external ids
		// TODO default custom fields
		
		if (customFields != null) {
			for (Map.Entry<String, String> entry : customFields.entrySet()) {
				params.put(String.format("ticket[custom_field_values][%s]", entry.getKey()), entry.getValue());
			}
		}
		
		doPost(apiPath("/tickets.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) {
				callback.onModel(deserializeObject(result, "ticket", Ticket.class));
			}
		});
	}

}
