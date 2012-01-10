package com.challengeearth.cedroid.helpers;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJSONParsable {

	public void parse(JSONObject json) throws JSONException;
	public JSONObject getJson() throws JSONException;
}
