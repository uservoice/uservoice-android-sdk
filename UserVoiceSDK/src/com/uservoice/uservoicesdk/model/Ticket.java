package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;
import com.uservoice.uservoicesdk.babayaga.Babayaga;

public class Ticket extends BaseModel {

    public static void createTicket(String message, Map<String, String> customFields, final Callback<Ticket> callback) {
        createTicket(message, null, null, customFields, callback);
    }

    public static void createTicket(String message, String email, String name, Map<String, String> customFields, final Callback<Ticket> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ticket[message]", message);

        if (email != null)
            params.put("email", email);

        if (name != null)
            params.put("display_name", name);

        if (Babayaga.getUvts() != null)
            params.put("uvts", Babayaga.getUvts());

        for (Map.Entry<String, String> entry : getSession().getExternalIds().entrySet()) {
            params.put(String.format("created_by[external_ids][%s]", entry.getKey()), entry.getValue());
        }

        if (getConfig().getCustomFields() != null) {
            for (Map.Entry<String, String> entry : getConfig().getCustomFields().entrySet()) {
                params.put(String.format("ticket[custom_field_values][%s]", entry.getKey()), entry.getValue());
            }
        }

        if (customFields != null) {
            for (Map.Entry<String, String> entry : customFields.entrySet()) {
                params.put(String.format("ticket[custom_field_values][%s]", entry.getKey()), entry.getValue());
            }
        }

        List<Attachment> attachmentList = getSession().getConfig().getAttachmentList();
        if (attachmentList != null) {
            for (int i = 0; i < attachmentList.size(); i++) {
                Attachment attachment = attachmentList.get(i);
                params.put(String.format("ticket[attachments][%d][name]", i), attachment.getFileName());
                params.put(String.format("ticket[attachments][%d][data]", i), attachment.getData());
                params.put(String.format("ticket[attachments][%d][content_type]", i), attachment.getContentType());
            }
        }

        doPost(apiPath("/tickets.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeObject(result, "ticket", Ticket.class));
            }
        });
    }
}
