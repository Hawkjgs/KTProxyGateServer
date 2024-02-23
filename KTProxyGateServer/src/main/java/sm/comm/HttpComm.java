package sm.comm;
 
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat; 
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse; 
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler; 
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate; 
 
@Component
public class HttpComm {
	   
	
	
	 
	public static HashMap HTTP_GET_URL(String url, String body)
	{
		
    	String ret="";
    	HashMap res = new HashMap();
	 	try {
			HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
			HttpGet getRequest = new HttpGet(url); //GET 메소드 URL 생성
			HttpResponse response = client.execute(getRequest);
			if (response.getStatusLine().getStatusCode() == 200) {
				ResponseHandler<String> handler = new BasicResponseHandler();
				body = handler.handleResponse(response);
				res.put("StatusCode", response.getStatusLine().getStatusCode());
				res.put("Data", body);
				
			} else {
				res.put("StatusCode", response.getStatusLine().getStatusCode());
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				res.put("Data", responseString);
			}
			} catch (Exception e){
			//System.err.println(e.toString());
		}

		return res;
	}
    
	
	public static HashMap HTTP_COMM_URL(String RequestMethod, String url, String body,String CLIENT_MODE,String ignoreSslYn,Logger logger)
  	 {
		 HashMap resultMap = null;
		 
		 String back_body=""+body;
		 
		 if("N".equals(CLIENT_MODE)) {
			 body = body.replaceAll("TOKEN_DATA", KTCommHeader.TOKEN);
	//		 body = body.replaceAll("SUB_NB_DATA", KTCommHeader.SUB_NB);
		 }
			 
  		 if("PUT".equals(RequestMethod)) {
  			 resultMap=HTTP_PUT_URL(url,body,ignoreSslYn);
  		
  		 }
  		 else if("POST".equals(RequestMethod)) {
  			resultMap=HTTP_POST_URL(url,body,ignoreSslYn);
  		 }
  		
  		 int iStatusCode = CommUtil.parseInt(CommUtil.NvlToBlank(""+resultMap.get("StatusCode")));
			 
		 if( iStatusCode ==200 ) {
			 
			 String res_json= CommUtil.NvlToBlank(resultMap.get("Data"));
			 HashMap hRet = JSONUtils.stringToJSON(res_json);
			 
			
			
			 int iResult =  (int) hRet.get("result");
			 
			 if(iResult==0) {
				 HashMap m = (HashMap) hRet.get("data");
				 if(m!=null) {
					 iResult =  CommUtil.parseInt(CommUtil.NvlToBlank(""+m.get("result"))); 
				 }
			 }
			 
			 logger.info("HTTP_COMM_URL 1 : res_json :"+res_json);
			 logger.info("HTTP_COMM_URL 2 : iResult : "+iResult );
			 
			 resultMap.put("result", iResult);
			 
			 if (iResult ==-8) {
					KTCommHeader.TOKEN_STATUS="TOKEN_401_ERROR";
					resultMap.put("StatusCode",iResult);
			 }
			 else {
				SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				KTCommHeader.TOKEN_UPDATE_DT=dateParser.format(new java.util.Date());
				KTCommHeader.TOKEN_STATUS="OK";
				resultMap.put("StatusCode",200);
			 }
		 }
		 else  if( iStatusCode ==401 ) {
			 KTCommHeader.TOKEN_STATUS="TOKEN_401_ERROR";
		 }
			 
  		 return resultMap;
  	 }
	
	
//	public static HashMap NHIS_TOKEN_REFLASH_URL(String ignoreSslYn,Logger logger)
// 	 {
//		 // Token오류로 Token을 갱신한다. 
//		 HashMap map = new HashMap();
//		  String reflash_token="{ \"token\": \""+KTCommHeader.TOKEN+"\"}";
//		  HashMap  resultMap=HTTP_PUT_URL(KTCommHeader.TOKEN_REFLASH_URL,reflash_token,ignoreSslYn);
//		  int iStatusCode= CommUtil.parseInt(CommUtil.NvlToBlank(resultMap.get("StatusCode")));
//		  
//		  if(iStatusCode == 200) {
//	    		String resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
//	    		HashMap hRet = JSONUtils.stringToJSON(resultJson);
//	    		//int iRet =  (int) hRet.get("result");
//	    	
//				 int iResult =  (int) hRet.get("result");
//					
//	    		 logger.info("NHIS_TOKEN_REFLASH_URL 1 : res_json :"+resultJson);
//				 
//				 
//				 if(iResult==0) {
//					 HashMap m = (HashMap) hRet.get("data");
//					 if(m!=null) {
//						 iResult =  CommUtil.parseInt(CommUtil.NvlToBlank(""+m.get("result"))); 
//					 }
//				 }
//				 
//				 logger.info("NHIS_TOKEN_REFLASH_URL 2: iResult : "+iResult +"\nhRet : " + hRet);
//				 //logger.info("NHIS_TOKEN_REFLASH_URL : hRet" +hRet);
//				 
//	    		//logger.info("NHIS_TOKEN_REFLASH_URL : iResult"+iResult + "==>" +hRet);
//	    		  
//				 
//	    		if(iResult==0) {
//	    			HashMap tmp = (HashMap) hRet.get("data");
//	    			String token  = CommUtil.NvlToBlank(tmp.get("token"));
//	    			KTCommHeader.TOKEN = token;
//	    			SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//					KTCommHeader.TOKEN_UPDATE_DT=dateParser.format(new java.util.Date());
//	    			KTCommHeader.TOKEN_STATUS="OK";
//	    			KTCommHeader.TOKEN_COMM_ERR_MSG="";
//	    			map.put("result",0);
//	    			map.put("res_body",resultJson);
//	    			map.put("req_body",reflash_token);
//	    			iStatusCode=iResult;
//	    			return map;
//	    		}
//	    		else {
//	    			//KTCommHeader.TOKEN="";
//	    			KTCommHeader.TOKEN_STATUS="TOKEN_401_ERROR";
//	    			KTCommHeader.TOKEN_COMM_ERR_MSG="로그인 인증 오류";
//	    			map.put("req_body",reflash_token);
//	    			map.put("result",iResult);
//	    			
//	    		}
//	    		iStatusCode=iResult;
//	    		
//		 }
//		 else  {
//			 	 map.put("req_body",reflash_token);
//				 map.put("res_body",""+resultMap.get("ErrMsg") + " Code : "+iStatusCode);
//		}
//		map.put("result",iStatusCode);
// 		return map;
// 	 }
	
	public static HashMap NHIS_LOGIN_URL(String ignoreSslYn,Logger logger)
	 {
		 // Token오류로 Token을 갱신한다. 
		 HashMap map = new HashMap();
		 String login_json="";
		 login_json +="{";
		 login_json +=" \"addr\": \""+KTCommHeader.SERVER_IP_ADDR+"\" ,";
		 login_json +=" \"mac\": \""+KTCommHeader.SERVER_MAC+"\" ,";
		 login_json +=" \"system\": \""+KTCommHeader.SYSTEM_NM+"\" ,";
		 login_json +=" \"custNo\": \""+KTCommHeader.SYSTEM_CUSTNO+"\"";
		
		 login_json +="}";
		 
		 map.put("req_body",login_json);
		 
		  HashMap  resultMap=HTTP_POST_URL(KTCommHeader.KT_ACTIVE_SERVER_IP+KTCommHeader.LOGIN_URL,login_json,ignoreSslYn);
		  int iStatusCode= CommUtil.parseInt(CommUtil.NvlToBlank(resultMap.get("StatusCode")));
		  if(iStatusCode == 200) {
	    		String resultJson = CommUtil.NvlToBlank(resultMap.get("Data"));
	    		HashMap hRet = JSONUtils.stringToJSON(resultJson);
	    		 
	    		 int iResult =  (int) hRet.get("result");
					
	    		
				 if(iResult==0) {
					 HashMap m = (HashMap) hRet.get("data");
					 if(m!=null) {
						 iResult =  CommUtil.parseInt(CommUtil.NvlToBlank(""+m.get("result"))); 
					 }
				 }
				 
				 logger.info("NHIS_LOGIN_URL   : req_json :"+login_json);
				 logger.info("NHIS_LOGIN_URL 1 : hRet" +hRet);
				 
	    		 logger.info("NHIS_LOGIN_URL 2 : iResult : "+iResult + "==>" +hRet);
	    		 map.put("req_body",login_json);
	    		 map.put("res_body",resultJson);
	    		
	    		if(iResult==0) {
	    			HashMap mapResult = (HashMap) hRet.get("data") ;
	    			String token  = CommUtil.NvlToBlank(mapResult.get("token"));
	    			KTCommHeader.TOKEN = token;
	    			SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
					KTCommHeader.TOKEN_UPDATE_DT=dateParser.format(new java.util.Date());
					KTCommHeader.TOKEN_STATUS="OK";
					KTCommHeader.TOKEN_COMM_ERR_MSG="";
					map.put("result",0);
					 
					 
					return map;
	    		}
	    		else {
	    			KTCommHeader.TOKEN="";
	    			KTCommHeader.TOKEN_COMM_ERR_MSG="로그인 인증 오류";
	    			//map.put("req_body","로그인 인증 오류");
	    			map.put("result",iResult);
	    			return map;
	    		}
	    		
		 }
		 else  {
			 map.put("res_body",""+resultMap.get("ErrMsg") + " Code : "+iStatusCode);
			 KTCommHeader.TOKEN_COMM_ERR_MSG=""+resultMap.get("ErrMsg") + " Code : "+iStatusCode;
		 }
		map.put("result",iStatusCode);
		return map;
	 }
	
	
	
	public static RestTemplate makeRestTemplate(boolean ignoreSsl) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		  if(ignoreSsl) {
		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		      .loadTrustMaterial(null, acceptingTrustStrategy)
		      .build();

		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
		    CloseableHttpClient httpClient = HttpClients.custom()
		        .setSSLSocketFactory(csf)
		        .build();

		    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		    requestFactory.setHttpClient(httpClient);
		    requestFactory.setConnectTimeout(3 * 1000);
		    requestFactory.setReadTimeout(3 * 1000);
		    
		    return new RestTemplate(requestFactory);
		  }
		  else {
		    return new RestTemplate();
		  }
		}
	
	 public static HashMap HTTP_POST_URL(String url, String body , String ignoreSslYn )
   	 {
   		 String resData="";
   		 HashMap res = new HashMap();
   		 //System.out.println("HTTP_POST_URL:"+url+"\n"+body);
   		 try {
   			 
   			 if("Y".equals(ignoreSslYn))  {
	   			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			      .loadTrustMaterial(null, acceptingTrustStrategy)
			      .build();
	
			    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
			    CloseableHttpClient client = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();
	
			    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			    requestFactory.setHttpClient(client);
			    requestFactory.setConnectTimeout(3 * 1000);
			    requestFactory.setReadTimeout(3 * 1000);
		    	HttpPost postRequest = new HttpPost(url); //GET 메소드 URL 생성
   				postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
   		    	HttpResponse response = client.execute(postRequest);
   				
   				//Response 출력
   				if (response.getStatusLine().getStatusCode() == 200) {
   					ResponseHandler<String> handler = new BasicResponseHandler();
   					resData = handler.handleResponse(response);
   					//System.out.println(resData);
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("Data", resData);
   					res.put("body", body);
   					
   				} else {
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("ErrMsg", "HTTP Error");
   					res.put("body", body);
   					HttpEntity entity = response.getEntity();
   					String responseString = EntityUtils.toString(entity, "UTF-8");
   					//res.put("Data", responseString);
   					//System.err.println(responseString);
   					 
   				}
   			 }
   			 else {
   				 
   				HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
   				HttpPost postRequest = new HttpPost(url); //GET 메소드 URL 생성
   				postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
   		        HttpResponse response = client.execute(postRequest);
   				//Response 출력
   				if (response.getStatusLine().getStatusCode() == 200) {
   					ResponseHandler<String> handler = new BasicResponseHandler();
   					resData = handler.handleResponse(response);
   					//System.out.println(resData);
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("Data", resData);
   					res.put("body", body);
   					
   				} else {
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("ErrMsg", "HTTP Error");
   					res.put("body", body);
   					HttpEntity entity = response.getEntity();
   					String responseString = EntityUtils.toString(entity, "UTF-8");
   					//res.put("Data", responseString);
   					//System.err.println(responseString);
   					 
   				}
   				 
   			 }
   				 
   		 } catch (ClientProtocolException e) {
   				String errString = e.getCause().toString();
   				
   				if(errString.indexOf("Connection timed out") >=0) {
   					
   					if("OK".equals(KTCommHeader.KT_ACTIVE_SERVER_CHG)) {
   						KTCommHeader.KT_ACTIVE_SERVER_CHG="REQ";
   					}
   					res.put("StatusCode", 9900);
   				}
   				else {
   					res.put("StatusCode", 9995);
   				}
   				
   				//System.err.println(e.getCause());
   				res.put("StatusCode", 9995);
				res.put("ErrMsg", e.getCause());
				res.put("body", body);
				System.err.println("errString 1 :"+ errString);
			
   			} catch (Exception e){
   				
   				String errString = e.getCause().toString();
   				
   				
   				//if("Connection timed out".indexOf(errString) >=0) {
   				if(errString.indexOf("Connection timed out") >=0) {
   					if("OK".equals(KTCommHeader.KT_ACTIVE_SERVER_CHG)) {
   						KTCommHeader.KT_ACTIVE_SERVER_CHG="REQ";
   					}
   					res.put("StatusCode", 9900);
   				}
   				else {
   					res.put("StatusCode", 9995);
   				}
   				
   				res.put("ErrMsg", e.getCause());
   				res.put("body", body);
   				System.err.println("errString 2:"+ errString);
   			}

   		 return res;
   	 }
	 
	 
//	 public static HashMap HTTP_POST_URL_old(String url, String body)
//   	 {
//   		 String resData="";
//   		 HashMap res = new HashMap();
//   		 System.out.println("HTTP_POST_URL:"+url+"\n"+body);
//   		 try {
//   			 
//   			 
//   			 
//   				HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
//   				
//   				HttpPost postRequest = new HttpPost(url); //GET 메소드 URL 생성
//   				 
//   	   		       
//   				postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
//   		       
//   				HttpResponse response = client.execute(postRequest);
//   				
//   				//Response 출력
//   				if (response.getStatusLine().getStatusCode() == 200) {
//   					ResponseHandler<String> handler = new BasicResponseHandler();
//   					resData = handler.handleResponse(response);
//   					//System.out.println(resData);
//   					res.put("StatusCode", response.getStatusLine().getStatusCode());
//   					res.put("Data", resData);
//   					res.put("body", body);
//   					
//   				} else {
//   					res.put("StatusCode", response.getStatusLine().getStatusCode());
//   					res.put("ErrMsg", "HTTP Error");
//   					res.put("body", body);
//   					HttpEntity entity = response.getEntity();
//   					String responseString = EntityUtils.toString(entity, "UTF-8");
//   					//res.put("Data", responseString);
//   					//System.err.println(responseString);
//   					 
//   				}
//   				 
//   		 } catch (ClientProtocolException e) {
//   				//System.err.println(e.getCause());
//   				res.put("StatusCode", 9995);
//				res.put("ErrMsg", e.getCause());
//				res.put("body", body);
//			
//   			} catch (Exception e){
//   				res.put("StatusCode", 9995);
//   				res.put("ErrMsg", e.getCause());
//   				res.put("body", body);
//   				//System.err.println(e.fillInStackTrace());
//   			}
//
//   		 return res;
//   	 }
//	 
	 public static HashMap HTTP_PUT_URL(String url, String body, String ignoreSslYn )
   	 {
   		 String resData="";
   		 HashMap res = new HashMap();
    
   		 //System.out.println("HTTP_PUT_URL:"+url+"\n"+body);
   		 try {
   			  
   			 if("Y".equals(ignoreSslYn))  {
   				TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			      .loadTrustMaterial(null, acceptingTrustStrategy)
			      .build();
	
			    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
			    CloseableHttpClient client = HttpClients.custom()
			        .setSSLSocketFactory(csf)
			        .build();
	
			    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			    requestFactory.setHttpClient(client);
			    requestFactory.setConnectTimeout(3 * 1000);
			    requestFactory.setReadTimeout(3 * 1000);

			    HttpPut postRequest = new HttpPut(url); //GET 메소드 URL 생성
			    
   				postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
   		    	HttpResponse response = client.execute(postRequest);
   				
   				//Response 출력
   				if (response.getStatusLine().getStatusCode() == 200) {
   					ResponseHandler<String> handler = new BasicResponseHandler();
   					resData = handler.handleResponse(response);
   					//System.out.println(resData);
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("Data", resData);
   					res.put("body", body);
   					
   				} else {
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("ErrMsg", "HTTP Error");
   					res.put("body", body);
   					HttpEntity entity = response.getEntity();
   					String responseString = EntityUtils.toString(entity, "UTF-8");
   					//res.put("Data", responseString);
   					//System.err.println(responseString);
   					 
   				}
   				
   				 
   			 }
   			 else {
   				HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
   				
   				HttpPut postRequest = new HttpPut(url);  
   				 
   	   		       
   				postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
   		       
   				HttpResponse response = client.execute(postRequest);
   				
   				//Response 출력
   				if (response.getStatusLine().getStatusCode() == 200) {
   					ResponseHandler<String> handler = new BasicResponseHandler();
   					resData = handler.handleResponse(response);
   					//System.out.println(resData);
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					res.put("body", body);
   					res.put("Data", resData);
   					
   				} else {
   					res.put("StatusCode", response.getStatusLine().getStatusCode());
   					HttpEntity entity = response.getEntity();
   					String responseString = EntityUtils.toString(entity, "UTF-8");
   					res.put("Data", responseString);
   					res.put("body", body);
   					res.put("ErrMsg", responseString);
   					
   					//System.out.println("response is error : " + response.getStatusLine().getStatusCode());
   				}
   			 }
   		 	 }	 
   			 catch (ClientProtocolException e) {
    				//System.err.println(e.getCause());
   				 	String errString = e.getCause().toString();
   				
	   				if(errString.indexOf("Connection timed out") >=0) {
	   					KTCommHeader.KT_ACTIVE_SERVER_CHG="REQ";
	   					res.put("StatusCode", 9900);
	   				}
	   				else {
	   					res.put("StatusCode", 9995);
	   				}
    			 
    				res.put("ErrMsg", e.getCause());
    				res.put("body", body);
 			
    			} catch (Exception e){
    				
    				String errString = e.getCause().toString();
       				
       				if(errString.indexOf("Connection timed out") >=0) {
       					KTCommHeader.KT_ACTIVE_SERVER_CHG="REQ";
       					res.put("StatusCode", 9900);
       				}
       				else {
       					res.put("StatusCode", 9995);
       				}
       				 
    				res.put("ErrMsg", e.getCause());
    				res.put("body", body);
    				//System.err.println(e.fillInStackTrace());
    			}
   			

   		 return res;
   	 }
	 
	     
}
