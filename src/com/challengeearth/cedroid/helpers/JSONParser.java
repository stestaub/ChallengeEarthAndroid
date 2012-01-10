package com.challengeearth.cedroid.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This JSON Parser is used to parse a JSON String to a compatible {@link IJSONParsable} object
 * 
 * @author Stefan Staub
 *
 * @param <T>
 */
public class JSONParser<T extends IJSONParsable> {

	private String jsonString;
	private Class<T> clazz;
	
	public JSONParser(BufferedReader reader, Class<T> clazz) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.jsonString=sb.toString();
		this.clazz = clazz;
	}
	
	public List<T> parseList() throws JSONException, IllegalAccessException, InstantiationException {
		JSONArray jArray = new JSONArray(jsonString);
		ArrayList<T> list = new ArrayList<T>();
		for(int i = 0; i < jArray.length(); i++) {
			JSONObject jObject = jArray.getJSONObject(i);
			list.add(parseTopLevelElement(jObject));
		}
		return list;
	}
	
	public T parseElement() throws JSONException, IllegalAccessException, InstantiationException {
		return parseTopLevelElement(new JSONObject(jsonString));
	}
	
	private T parseTopLevelElement(JSONObject jsonString) throws JSONException, IllegalAccessException, InstantiationException {
		T object;

		object = clazz.newInstance();
		object.parse(jsonString);
		return object;

	}


}
