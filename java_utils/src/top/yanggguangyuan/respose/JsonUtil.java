package top.yanggguangyuan.respose;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.util.Assert;
 

public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * 将对象转换为JSON字符串
	 * @param object 对象
	 */
	public static String toJson(Object object) {
		Assert.notNull(object);
		try {
			String test = mapper.writeValueAsString(object);
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将对象转换为JSON流
	 * @param response HttpServletResponse
	 * @param contentType contentType
	 * @param object 对象
	 */
	public static void toJson(HttpServletResponse response, String contentType, Object value) {
		Assert.notNull(response);
		Assert.notNull(contentType);
		Assert.notNull(value);
		try {
			response.setContentType(contentType);
			mapper.writeValue(response.getWriter(), value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将对象转换为JSON流
	 * @param response HttpServletResponse
	 * @param object 对象
	 */
	public static void toJson(HttpServletResponse response, Object value) {
		Assert.notNull(response);
		Assert.notNull(value);
		try {
			mapper.writeValue(response.getWriter(), value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将JSON字符串转换为对象
	 * @param json JSON字符串
	 * @param valueType 对象类型
	 */
	public static <T> T toObject(String json, Class<T> valueType) {
		Assert.notNull(json);
		Assert.notNull(valueType);
		try {
			return mapper.readValue(json, valueType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将JSON字符串转换为对象
	 * @param json JSON字符串
	 * @param typeReference 对象类型
	 */
	public static <T> T toObject(String json, TypeReference<T> typeReference) {
		Assert.notNull(json);
		Assert.notNull(typeReference);
		try {
			json = json.replaceAll("\\\\", "");
			return (T) mapper.readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将JSON字符串转换为对象
	 * @param json JSON字符串
	 * @param javaType 对象类型
	 */
	public static <T> T toObject(String json, JavaType javaType) {
		Assert.notNull(json);
		Assert.notNull(javaType);
		try {
			return (T) mapper.readValue(json, javaType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static ObjectMapper getMapper(){
		return mapper;
	}
	
	/** 
    * 将json格式的字符串解析成Map对象 <li> 
    * json格式：{"name":"admin","retries":"3fff","testname" 
    * :"ddd","testretries":"fffffffff"} 
    */  
   public static HashMap<String, String> toHashMap(String object){  
       HashMap<String, String> data = new HashMap<String, String>();  
       // 将json字符串转换成jsonObject  
       JSONObject jsonObject = JSONObject.fromObject(object);  
       Iterator it = jsonObject.keys();  
       // 遍历jsonObject数据，添加到Map对象  
       while (it.hasNext())  
       {  
           String key = String.valueOf(it.next());  
           String value = (String) jsonObject.get(key);  
           data.put(key, value);  
       }  
       return data;  
   }  
}