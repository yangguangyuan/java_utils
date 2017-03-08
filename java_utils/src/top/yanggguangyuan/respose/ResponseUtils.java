package top.yanggguangyuan.respose;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.MimetypesFileTypeMap;

public class ResponseUtils {
	
	/**
	 * @Title: sendPostForm 
	 * @Description: TODO(post提交form-data格式数据) 
	 * @param @param url
	 * @param @param params 
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public static String sendPostForm(String url, Map<String, Object> params) {  
        URL u = null;  
        HttpURLConnection con = null;  
        // 构建请求参数  
        StringBuffer sb = new StringBuffer();  
        if (params != null) {  
            for (Entry<String, Object> e : params.entrySet()) {  
                sb.append(e.getKey());  
                sb.append("=");  
                sb.append(e.getValue());  
                sb.append("&");  
            }  
            sb.substring(0, sb.length() - 1);  
        }  
        System.out.println("send_url:" + url);  
        System.out.println("send_data:" + sb.toString());  
        // 尝试发送请求  
        try {  
            u = new URL(url);  
            con = (HttpURLConnection) u.openConnection();  
            //// POST 只能为大写，严格限制，post会不识别  
            con.setRequestMethod("POST");  
            con.setDoOutput(true);  
            con.setDoInput(true);  
            con.setUseCaches(false);  
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
          //  con.se
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");  
            osw.write(sb.toString());  
            osw.flush();  
            osw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (con != null) {  
                con.disconnect();  
            }  
        }  
  
        // 读取返回内容  
        StringBuffer buffer = new StringBuffer();  
        try {  
            //一定要有返回值，否则无法把请求发送给server端。  
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));  
            String temp;  
            while ((temp = br.readLine()) != null) {  
                buffer.append(temp);  
                buffer.append("\n");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return buffer.toString();  
    }  
	
	/**
	 * @Title: sendPostFormFile 
	 * @Description: TODO(提交有文件的form表单) 
	 * @param @param urlStr
	 * @param @param textMap
	 * @param @param fileMap
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public static String sendPostFormFile(String urlStr, Map<String, Object> textMap,
            Map<String, Object> fileMap) {
        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716"; 
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    if ("fileTypes".equals(inputName)||"fileSuffixes".equals(inputName)) {
                    	String[] inputValue = (String[]) entry.getValue();
						strBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
						strBuf.append("Content-Disposition: form-data; name=\""
								+ inputName + "\"\r\n\r\n");
						for (int i = 0; i < inputValue.length; i++) {
							if (i==inputValue.length-1) {
								strBuf.append(inputValue[i]);
							}else {
								strBuf.append(inputValue[i]+",");
							}
						}

					}else {
						String inputValue = (String) entry.getValue();
						strBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
						strBuf.append("Content-Disposition: form-data; name=\""
								+ inputName + "\"\r\n\r\n");
						strBuf.append(inputValue);
					}
                }
                System.out.println(strBuf.toString());
                out.write(strBuf.toString().getBytes());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    File[] inputValue = (File[]) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    for(int i=0;i<inputValue.length;i++){
                  
                    File file = inputValue[i];
                    String filename = file.getName();
                    
                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
                    String contentType = new MimetypesFileTypeMap().getContentType(file);
                    //contentType非空采用filename匹配默认的图片类型
                    if(!"".equals(contentType)){
                        if (filename.endsWith(".png")) {
                            contentType = "image/png"; 
                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        }else if (filename.endsWith(".ico")) {
                            contentType = "image/image/x-icon";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                    	.append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                    			+ inputName + "\"; filename=\"" + filename
                    			+ "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    
                    out.write(strBuf.toString().getBytes("utf-8"));
                    System.err.println(strBuf);
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024*5];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    if(i<inputValue.length-1){ 
                    	String endData = ("\r\n--" + BOUNDARY + "--\r\n");   
                        out.write( endData.getBytes("utf-8") );  
                    } 
                    in.close();
                    }
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }
	
	
	/**
	 * @Title: sendPostFormFile 
	 * @Description: TODO(提交有文件的form表单) 
	 * @param @param urlStr
	 * @param @param textMap
	 * @param @param fileMap
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public static String sendPostFormFile1(String urlStr, Map<String, Object> textMap,
            Map<String, Object> fileMap) {
        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716"; 
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    if ("fileTypes".equals(inputName)||"fileSuffixes".equals(inputName)) {
                    	String[] inputValue = (String[]) entry.getValue();
                    	for (int i = 0; i < inputValue.length; i++) {
						strBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
						strBuf.append("Content-Disposition: form-data; name=\""
								+ inputName + "\"\r\n\r\n");
								strBuf.append(inputValue[i]);
						}

					}else {
						String inputValue = (String) entry.getValue();
						strBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
						strBuf.append("Content-Disposition: form-data; name=\""
								+ inputName + "\"\r\n\r\n");
						strBuf.append(inputValue);
					}
                }
                System.out.println(strBuf.toString());
                out.write(strBuf.toString().getBytes());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    File[] inputValue = (File[]) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    for(int i=0;i<inputValue.length;i++){
                  
                    File file = inputValue[i];
                    String filename = file.getName();
                    
                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
                    String contentType = new MimetypesFileTypeMap().getContentType(file);
                    //contentType非空采用filename匹配默认的图片类型
                    if(!"".equals(contentType)){
                        if (filename.endsWith(".png")) {
                            contentType = "image/png"; 
                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        }else if (filename.endsWith(".ico")) {
                            contentType = "image/image/x-icon";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                    	.append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                    			+ inputName + "\"; filename=\"" + filename
                    			+ "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    
                    out.write(strBuf.toString().getBytes("utf-8"));
                    System.err.println(strBuf);
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024*5];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    if(i<inputValue.length-1){ 
                    	String endData = ("\r\n--" + BOUNDARY + "--\r\n");   
                        out.write( endData.getBytes("utf-8") );  
                    } 
                    in.close();
                    }
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }
}
