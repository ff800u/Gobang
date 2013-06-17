package servlet;

import net.sf.json.JSONObject;

public class JsonUtil {
	public static String toJsonString(Object obj) {
		JSONObject json = JSONObject.fromObject(obj);
		return json.toString();
	}
}
