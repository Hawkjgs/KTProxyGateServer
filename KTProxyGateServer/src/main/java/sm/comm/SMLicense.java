package sm.comm;

import java.util.*;
public class SMLicense {
	 
	public static boolean LICENSE=false;
	public static String MakeLic(String product,String mac) {
	    String licdata="";
	
	    List mList = new ArrayList();
	    
	    mList.add(mac);
		licdata ="BEGIN";
		licdata+="\n" +"PRODUCT-REQUEST-KEY:"+product;
		licdata+="\n" +Seed256.Encrypt("PRODUCT:"+product);
		for (int i=0;i<mList.size();i++) {
			licdata+="\n" +Seed256.Encrypt(""+mList.get(i)); 
		}
		licdata+="\nEND\n";
		
		 
		return licdata;
    }
	
	public static boolean MakeLicGenerator(String REQ_LIC,String LIC_FILE)	    
    {
    
		if("".equals(REQ_LIC)) return false;
		REQ_LIC= REQ_LIC.replaceAll("\r", "");
		
		
		List mList = new ArrayList();
		String[] splitStr = REQ_LIC.split("\n");
		String REQ_GUBUN="";
		String gubun="";
		String sData="";
		
		for(int i=0; i<splitStr.length; i++){
			gubun = splitStr[i].trim();
			if(gubun.indexOf("PRODUCT-REQUEST-KEY") >= 0) {
				REQ_GUBUN= gubun.replaceAll("PRODUCT-REQUEST-KEY:", "");
			}
			else if(gubun.indexOf("PRODUCT-KEY") >= 0) {
				REQ_GUBUN= gubun.replaceAll("PRODUCT-KEY:", "");
			}
			else if(gubun.indexOf("BEGIN") >= 0) {
				
			}
			else  if(gubun.indexOf("END") >= 0) {
				
			}
			else if(!"".equals(gubun) ) {
				
				if("SCHDULER2.0".equals(REQ_GUBUN)
					|| "SM-SFTP_SYNC".equals(REQ_GUBUN)	
					|| "YWIS_KTGATEWAY".equals(REQ_GUBUN)	
				) 
				{
					try {
						String mac= Seed256.Decrypt(gubun);
						if(mac.indexOf("PRODUCT")>=0) {
							
						}
						else mList.add(mac);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if("NFT_DESKTOP".equals(REQ_GUBUN)) {
					sData =sData+"\r"+gubun;
				}
				else if("NFT_MET_REFLASH".equals(REQ_GUBUN)) {
					try {
						String mac= Seed256.Decrypt(gubun);
						if(mac.indexOf("PRODUCT")>=0) {
							
						}
						else mList.add(mac);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if("KSToWinDriver2.0".equals(REQ_GUBUN))
				{
					sData =sData+ SEED_KISA.Decrypt(gubun);
				}
			}
			
		}
		String licdata2="";
		
		if("YWIS_KTGATEWAY".equals(REQ_GUBUN)){ 
			licdata2 ="BEGIN";
			licdata2+="\r\n" +"PRODUCT-KEY:"+REQ_GUBUN;
			licdata2+="\r\n" +Seed256.Encrypt("PRODUCT:"+REQ_GUBUN);
			for (int i=0;i<mList.size();i++) {
				//licdata2+="\n"+ Seed256.Encrypt("PRODUCT:LIC-"+mList.get(i)+"-MAC");
				licdata2+="\r\n"+ Seed256.Encrypt("LIC-"+mList.get(i)+"-MAC");
			}
			licdata2+="\r\nEND";
		}
		 
		if(!licdata2.isEmpty()) {
			SFileUtil.writeFile(LIC_FILE, licdata2.toUpperCase(), "");
			 return true;
		}
		return false;
	}
	
	public static boolean MakeLicCheck(String product,String key,String macInfo)	    
    {
    
		if("".equals(key)) return false;
		List mList = new ArrayList();
		mList.add(macInfo);
		key= key.replaceAll("\r", "");
	    String [] listKey = key.split("\n"); 
	    String mac="";
	    String proc="";
	    boolean bProduct=false;
	    for(int i=0;i< mList.size();i++) {
	    		mac = "LIC-"+ mList.get(i)+"-MAC";
	    		//mac = ""+ mList.get(i);
	    		//System.out.println(mac);
	    		for (int j=0;j< listKey.length;j++) {
	    			if("BEGIN".equals(listKey[j])) {
	    				
	    			}
	    			else if(listKey[j].indexOf("PRODUCT-KEY")>=0) {
	    				
	    			}
	    			else if("END".equals(listKey[j])) {
	    				
	    			}
	    			else  {
	    				if(!"".equals(listKey[j])) {
		    				if(bProduct==false ) {
		    					try {
		    						String lickey =listKey[j];
		    						proc = Seed256.Decrypt(listKey[j]);
			    					//System.out.println("proc:"+proc);
			    					if(proc.indexOf("PRODUCT:"+product)>=0) {
			    						bProduct=true;
			    					}
		    					}
		    					catch(Exception e) {
		    						
		    					}
		    							
		    				}
		    				else {
			    				try {
			    					
			    					
			    					String licdata = Seed256.Decrypt(listKey[j]);
			    					
			    					StringBuilder licBuffer= new StringBuilder(); 
			    					
			    					licdata=licdata.trim();
			    					
			    					byte[] bytes = licdata.getBytes();
			    					for(int k=0;k<licdata.length();k++) {
			    						//System.out.println("licdata:"+bytes[k]);
			    						if(bytes[k] > 0x20) licBuffer.append((char) bytes[k]);
			    						
			    					}
//			    					 
//			    					
//			    					System.out.println("licdata:"+licBuffer+ ":"+licBuffer.length());
//			    					System.out.println("mac    :"+mac+ ":"+mac.length());
	//		    					
			    					if(mac.equals(licBuffer.toString()) && bProduct) {
			    						return true;
			    					}
			    				}
		    					catch(Exception e) {
		    						
		    					}
		    				}
	    				}
	    			}
	    		}
	    		
	    }
	   LICENSE=false;
	   return false;
	}
	
}
