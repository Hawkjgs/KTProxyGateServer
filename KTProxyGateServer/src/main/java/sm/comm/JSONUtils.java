package sm.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.psd.internal.Exceptions.IO.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
 

/**

 * JSON 관련 Utility 메소드를 제공한다.  

 * @author 

 */

public abstract class JSONUtils {

	

	protected final static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

	public static HashMap stringToJSON(String json_str) {
		
		HashMap hRet = new HashMap();
		 
	    JSONParser parser = new JSONParser();
	    
		Map<String, String> map = null;
       try {
    	   map = new ObjectMapper().readValue(json_str, Map.class);
    	   hRet.put("result", 0);
	       hRet.put("result_msg", "OK");
	       hRet.put("data", map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 hRet.put("result", 9998);
				hRet.put("result_msg", "JSON Format Error");
		}
		    
	    return hRet;
	}
	
public static String MapToJSON(HashMap bodyMap) {
		
	ObjectMapper objectMapper = new ObjectMapper();
    String jsonStr;
	try {
		jsonStr = objectMapper.writeValueAsString(bodyMap);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "";
	}
    
	 return jsonStr;
}

} 