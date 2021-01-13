package io.github.teccheck.bluechat;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    public String sender;
    public String text;
    public long time;

    public Message(String sender, String text, long time) {
        this.text = text;
        this.time = time;
        this.sender = sender;
    }

    public static Message fromJson(JSONObject jsonObject) throws JSONException {
        return new Message(jsonObject.getString("sender"), jsonObject.getString("text"), jsonObject.getLong("time"));
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", sender);
        jsonObject.put("text", text);
        jsonObject.put("time", time);
        return jsonObject;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", sender, text);
    }
}