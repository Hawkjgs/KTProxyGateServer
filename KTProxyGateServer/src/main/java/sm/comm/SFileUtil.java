package sm.comm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;


class UtilsFileNameSort {
	public static void sort(File[] filterResult){
		// 파일명으로 정렬한다. 
		Arrays.sort(filterResult, new Comparator() {
			public int compare(Object arg0, Object arg1) {
			File file1 = (File)arg0;
			File file2 = (File)arg1;
			//return file1.getName().compareToIgnoreCase(file2.getName());
			//return file2.getName().compareToIgnoreCase(file1.getName());
			//return file2.getName().compareToIgnoreCase(file1.getfile));
		
			return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
			}
		});
	}
}

public class SFileUtil {
	
	

	public static long  FileLen(String soruceFile) {
		File file = new File(soruceFile);
		return file.length();
 	}
	
	
	public static String byteCalculation(String bytes) {
        String retFormat = "0";
        
        long l_long = StringUtil.parseLong(bytes);
        
        if(l_long ==0) return "-";
       Double size = Double.parseDouble(bytes);

        String[] s = { "bytes", "KB", "MB", "GB", "TB", "PB" };
        

        if (size  >0) {
              int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
              DecimalFormat df = new DecimalFormat("#,###.##");
              double ret = ((size / Math.pow(1024, Math.floor(idx))));
              retFormat = df.format(ret) + " " + s[idx];
         } else {
              retFormat += " " + s[0];
         }

         return retFormat;
}
	
	
	public static String renderPagination(int iCurPage , int iPageSize,  int iTotalCnt) {
		StringBuffer str = new StringBuffer();
		int iTotalBlk= (iTotalCnt + iPageSize -1)/ iPageSize ;
		
		int iCurBlk  = ((iCurPage - 1 ) / 5 ) *5;
		String acive="active";
		if( iCurBlk > 1) {
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage(1);'>&lt;&lt;</a>\n");
            str.append(" </li>\n");
		}
		if( iCurBlk > 1) {
			 
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+iCurBlk+");'>&lt;</a>\n");
            str.append(" </li>\n");
		}
		
		int iTmpBlock = ((iCurPage-1) / 5 ) * 5  +1 ;
		if(iTmpBlock <=0) iTmpBlock=1;
		for( int i=iTmpBlock;i<(iTmpBlock+5)  && (i  <=iTotalBlk) ;i++) {
			if(i == iCurPage) str.append("<li class='active'>\n");
			else  str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+i+");'>"+ i+"</a>\n");
            str.append(" </li>\n");
		}
//		<li class="active"><a href="#" onclick="gotoPage(1);">1</a>
		

		if( (iTmpBlock + 5) <= iTotalBlk) {
			int NextBlk =  iTmpBlock + 5;
			if(NextBlk < iTotalBlk ) {
				NextBlk=NextBlk;
			}
			else NextBlk =iTotalBlk;
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+NextBlk+");'>&gt;</a>\n");
            str.append(" </li>\n");
			
		}

		if( ( iTmpBlock + 5) <= iTotalBlk) {
			str.append("<li>\n");
			str.append("<a href = '#' onclick='gotoPage("+iTotalBlk+");'>&gt;&gt;</a>\n");
            str.append(" </li>\n");
			
		}
		return str.toString();		
	}
	
	
	public static String readJobInfo(String sourceFile )
	{
		String readData ="";
		String encoded="UTF8";
		
		try {
			File fileDir = new File(sourceFile);
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(fileDir), encoded));
			String str;
			if ((str = in.readLine()) != null) {
				readData=str;
			}
	        in.close();
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return readData;
	}
	
	public static int  getJobInfoUsed(HashMap map ,String FileName,Map searchMap) {
		
		String JOB_INFO = readJobInfo(FileName);
		String JOB[]= JOB_INFO.split("\\|");
		 
		boolean bJOB_STATUS=false;
		boolean bSearch=false; 
		boolean bERR_CD=false;
		String JOB_ID="";
		String JOB_URL="";
		String JOB_STATUS="";
		String REQ_IP="";
		String JOB_TY=""; 
		String ERR_CD="";
		for(int i=0; i< JOB.length;i++) {
			String tmp[] = JOB[i].split("=");
			
			if("JOB_ID".equals(tmp[0]))  {
				map.put("JOB_ID", tmp[1]);
				JOB_ID = CommUtil.NvlToBlank(map.get("JOB_ID")); 
			}
			else if("JOB_URL".equals(tmp[0])) {
				map.put("JOB_URL", tmp[1]);
				JOB_URL = CommUtil.NvlToBlank(map.get("JOB_URL"));
			}
			else if("JOB_STATUS".equals(tmp[0]))  {
				map.put("JOB_STATUS", tmp[1]);
				JOB_STATUS = CommUtil.NvlToBlank(map.get("JOB_STATUS"));
				 
			}
			
			else if("REQ_IP".equals(tmp[0]))  {
				map.put("REQ_IP", tmp[1]);
				REQ_IP = CommUtil.NvlToBlank(map.get("REQ_IP"));
			}
			else if("JOB_DT".equals(tmp[0]))  {
				map.put("JOB_DT", tmp[1]);
			}
			else if("JOB_TY".equals(tmp[0])) {
				map.put("JOB_TY", tmp[1]);
				JOB_TY = CommUtil.NvlToBlank(map.get("JOB_TY"));
			}
			else if("ERR_CD".equals(tmp[0])) {
				map.put("ERR_CD", tmp[1]);
				ERR_CD = CommUtil.NvlToBlank(map.get("ERR_CD"));
			}
			
		}
 
		return 1;	 
	}
	
	
	public static HashMap getJonInfo(String path, String jobFile) {
		 
		HashMap map = new HashMap();
		
		
		String JOB_INFO = readJobInfo(path+File.separator+jobFile);
		String JOB[]= JOB_INFO.split("\\|");
		
		String JOB_ID="";
		String JOB_URL="";
		String JOB_STATUS="";
		String REQ_IP="";
		String JOB_TY=""; 
	 
		for(int i=0; i< JOB.length ;i++) {
			String tmp[] = JOB[i].split("=");
			if("JOB_ID".equals(tmp[0]))  {
				map.put("JOB_ID", tmp[1]);
			}
			else if("JOB_URL".equals(tmp[0])) {
				map.put("JOB_URL", tmp[1]);
			}
			else if("JOB_STATUS".equals(tmp[0]))  {
				map.put("JOB_STATUS", tmp[1]);
			}
			
			else if("REQ_IP".equals(tmp[0]))  {
				map.put("REQ_IP", tmp[1]);
			}
			else if("JOB_DT".equals(tmp[0]))  {
				map.put("JOB_DT", tmp[1]);
			}
			else if("JOB_TY".equals(tmp[0])) {
				map.put("JOB_TY", tmp[1]);
			}
			else if("ERR_CD".equals(tmp[0])) {
				map.put("ERR_CD", tmp[1]);
			}
		}
		
		map.put("FILE_NM", jobFile);
		
		return map;
	  }
	
	public static HashMap getFolderFileList(String path, int s_idx , int iFileTotalCnt,Map searchMap) {
		File folder = new File(path);
		List listFolder = new ArrayList();
		HashMap mapFile = new HashMap();
	
		int icount=0;
		int idx=iFileTotalCnt - s_idx+1;
		//s_idx = (s_idx -1) *10 +1;
		int e_idx = s_idx+10;
		try {
		    if(folder.exists()) {
				File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
				
				UtilsFileNameSort.sort(folder_list);
				
				String fName="";
				for (int j = 0; j < folder_list.length; j++) {
					fName = folder_list[j].getName();
					String fext = FilenameUtils.getExtension(fName).toLowerCase();
				    if(folder_list[j].isFile() && fext.indexOf("json") >=0)  {
//				    	icount++;
//				    	if((s_idx <= icount)  &&  (e_idx > icount )) {
					    	HashMap  map  = new HashMap();
					    	int iret = getJobInfoUsed(map,path+File.separator +fName,searchMap);
					    	map.put("IDX", idx);
					    	map.put("FILE_NM", fName);
					    	map.put("JOB_DATE", CommUtil.NvlToBlank(searchMap.get("ST_DT_STR")));
					    	
					    	map.put("LENGTH", byteCalculation(""+ folder_list[j].length()));
					    	if(iret==1) {
					    		icount++;
					    		if((s_idx <= icount)  &&  (e_idx > icount )) {
					    			idx--;
						    		listFolder.add(map);
					    		
					    		}
//					    		iFileTotalCnt--;
//					    		
//					    	}
				    	}
				    	
				    	
				    }
				}
		    }	
		} catch (Exception e) {
				e.getStackTrace();
		}
		mapFile.put("FileList", listFolder);
		mapFile.put("FileTotalCnt", icount);
		
		return mapFile;
	  }
	
	
	
	public static List getFolderList(String path) {
		File folder = new File(path);
		//System.out.println("getFolderList:"+path);
		List listFolder = new ArrayList();
		
		try {
		    if(folder.exists()) {
				File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
				UtilsFileNameSort.sort(folder_list);
				String fName="";
				for (int j = 0; j < folder_list.length; j++) {
					fName = folder_list[j].getName();
					String fext = FilenameUtils.getExtension(fName).toLowerCase();
				    if(folder_list[j].isDirectory())  {
				    	HashMap  map  = new HashMap();
				    	map.put("PATH", fName);
				    	listFolder.add(map);
				    	//System.out.println(":::"+fName);
				    }
				}
		    }	
		} catch (Exception e) {
				e.getStackTrace();
		}
		return listFolder;
	  }
	
	
	public static int getFolderFileCount(String path , Map searchMap) {
		File folder = new File(path);
		int iCount=0;
		try {
		    if(folder.exists()) {
				File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
				String fName="";
				for (int j = 0; j < folder_list.length; j++) {
					fName = folder_list[j].getName();
					String fext = FilenameUtils.getExtension(fName).toLowerCase();
				    if(folder_list[j].isFile() && fext.indexOf("json") >=0)  {
				    	HashMap map = new HashMap();
				    	int iret = getJobInfoUsed(map,path+File.separator +fName,searchMap);
				    	if(iret==1) iCount++;
				    }
				}
		    }	
		} catch (Exception e) {
				e.getStackTrace();
		}
		return iCount;
	  }
	
	 public  static boolean isFile(String fName)
	    {
		    File f = new File(fName);
		  
	        // 파일 존재 여부 판단
	        if (f.exists())  return true;
	        else return false;
		}
	 
	
	public static String FileExtChg(String soruceFile,String sExt)
	{
		String sExtension = FilenameUtils.getExtension(soruceFile); 
		String sTargetFile = soruceFile.replaceAll("\\."+sExtension, sExt);
		return sTargetFile;
        
	}
	public static boolean DeleteFile(String sourceFile )
	{
		File file = new File(sourceFile);
		if( file.exists() ){
			if(file.delete())
			{ 
			//	System.out.println("파일삭제 성공"); 
			}
			else{
				//System.out.println("파일삭제 실패"); 
			} 
		}
		else{ 
			//System.out.println("파일이 존재하지 않습니다."); 
		} 
		
		return true;
		
	}

	
	public static String readFile(String sourceFile )
	{
		String readData ="";
		String encoded="UTF8";
		
		try {
			File fileDir = new File(sourceFile);
				
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(fileDir), encoded));
			        
			String str;
			    
			int iLineCount=0;
			while ((str = in.readLine()) != null) {
				if(iLineCount ==0) readData=str;
				else readData=readData+"\n"+str;
				iLineCount++;
			}
			        
	        in.close();
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return readData;
	}
	
	
	public static String readFile(String sourceFile , String encoded)
	{
		String readData ="";
		
		try {
			File fileDir = new File(sourceFile);
				
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(fileDir), encoded));
			        
			String str;
			    
			int iLineCount=0;
			while ((str = in.readLine()) != null) {
				if(iLineCount ==0) readData=str;
				else readData=readData+"\n"+str;
				iLineCount++;
			}
			        
	        in.close();
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return readData;
	}
	
	public static boolean writeFile(String destFile ,String data, String encoded)
	{
		boolean ret =false;
		if("".equals(encoded)) {
			encoded="UTF8";
		}
		
		try {
			 	
			File targetFile = new File(destFile);
			targetFile.createNewFile();
			
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), encoded));

			output.write(data);
			output.close();
			ret = true;
			 
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return ret;
	}
	
	public static boolean FileExits(String soruceFile) {
		boolean bret;
		File file = new File(soruceFile);
		bret= file.exists();;
		return bret;
 	}
	
	public static boolean FileMove(String sourceFile , String destFile)
	{
		
		System.out.println("sourceFile:"+sourceFile);
		System.out.println("destFile:"+destFile);
		
		File file = new File(sourceFile);

		File fileToMove = new File(destFile);

		boolean isMoved = file .renameTo(fileToMove);

			
		return true;
	}
	
	public static void makeDir(String path)
	{
		
		File targetDir = new File(path);  
		if(!targetDir.exists()) {    //디렉토리 없으면 생성.
	         targetDir.mkdirs();
	     }
		
	}
	
	public static List ReadToList(String sourceFile, String encoded)
	{
		
		List fList = new ArrayList();
		String readData ="";
		
		try {
			File fileDir = new File(sourceFile);
				
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(fileDir), encoded));
			        
			String str;
			    
			int iLineCount=0;
			while ((str = in.readLine()) != null) {
				fList.add(str);
			}
			        
	        in.close();
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return fList;
	}
	
	
	
	public static boolean FileCopy(String source, String dest) 
	{
		try {
			FileInputStream fis = new FileInputStream(source);
			FileOutputStream fos = new FileOutputStream(dest);
			
			byte[] buf = new byte[1024];
			int read;
			while((read = fis.read(buf)) != -1) 
			{ 
				fos.write(buf, 0, read);
			}
			fis.close();
			fos.close();
			 
		} 	
		catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} 
		catch (IOException e) {
		}
		return true;
	}	  
	
	public static HashMap getStrToProp(List propList, String val) {
		int iPropCount = propList.size();
		int iCheckPropCount =0;
		HashMap retMap = new HashMap();
		List listProp = new ArrayList();
		val= FilenameUtils.removeExtension(val);
		String str[]= val.split("#");
		String tmp="";
		String key="";
		String data="";
		HashMap mapData = new HashMap();
		HashMap mapPropData = new HashMap();
		HashMap tmpMap = null;
		
		String hashStr="\"attributes\":[";
		int index=0;
		boolean bNameVaild=false;
		for( int i=0;i< str.length;i++) {
			tmp = str[i].trim();
			String tmp_data[]= tmp.split("-");
			key = tmp_data[0].trim().toUpperCase();
			if(tmp_data.length>=2) {
				HashMap m = new HashMap();
				data = tmp_data[1].trim();
				mapData.put(key, data);
				m.put("key", key);
				m.put("val", data);
				
				if("NAME".equals(key)) {
					bNameVaild=true;
				}
				else {
					if(index==0)
						hashStr=hashStr + "{\"trait_type\":\""+key +"\",\"value\":\""+data +"\"}";
					else 
						hashStr=hashStr + ",{\"trait_type\":\""+key +"\",\"value\":\""+data +"\"}";
					index++;
				}
				listProp.add(m);
			}
			else data="";
		}
		hashStr=hashStr+"]";
		List mapList = new ArrayList();
		for( int i=0;i< propList.size();i++) {
			tmpMap =  (HashMap) propList.get(i);
			key = CommUtil.NvlToBlank(tmpMap.get("PROP_NM")).toUpperCase();
			
			data  = CommUtil.NvlToBlank(mapData.get(key));
			
			if("".equals(data)) {
				key = CommUtil.NvlToBlank(tmpMap.get("SH_PROP_NM")).toUpperCase();
				data  = CommUtil.NvlToBlank(mapData.get(key));
			}
			
			tmpMap.put("META_DATA", data);
			mapList.add(tmpMap);
			if(!"".equals(data)) {
				iCheckPropCount++;
				//System.out.println("["+key+">>"+data+"]");
			}
			else {
				//System.out.println("["+key+">>"+data+"]");
			}
			
		}
		
		if(iCheckPropCount==iPropCount && bNameVaild)  {
			retMap.put("result", true);
			retMap.put("HASH_STR", hashStr);
			retMap.put("propList", listProp);
			retMap.put("mapList", mapList);
			
			
		}
		else  {
			retMap.put("propList", listProp);
			retMap.put("HASH_STR", hashStr);
			retMap.put("result", false);  
		}
		
		return retMap;
		
		
	}
	
	public static String  getFileNameToProp(String propNm, String val) {
		boolean bRet=false; 
		
		String str[]= val.split("#");
		
		String tmp="";
		String key="";
		String data="";
		
		propNm=propNm.toUpperCase();
		for( int i=0;i< str.length;i++) {
			tmp = str[i].trim();
			String tmp_data[]= tmp.split("-");
			key = tmp_data[0].trim().toUpperCase();
			if(tmp_data.length>=2) {
				data = tmp_data[1].trim();
			}
			else data="";
			
			if(propNm.equals(key)) {
				return data;
			}
		}
		
		return "";
		
		
		
	}
	
	public static void WriteImgeFile(HttpServletResponse response,String path, String fileName) throws Exception{
    	
		File file = null;
		FileInputStream fis = null;
	
		BufferedInputStream in = null;
		ByteArrayOutputStream bStream = null;
		
		try {
			String szFileName=path+File.separator+fileName;
			File fileChk = new File(szFileName);
			if(!fileChk.exists()){
				//path=propertiesService.getString("defImg");
				fileName="no_image_def.gif";
				fileChk = new File(szFileName);
			}
			
			
			if(fileChk.exists()){
	        	file = new File(path, fileName);
			    fis = new FileInputStream(file);
		
			    in = new BufferedInputStream(fis);
			    bStream = new ByteArrayOutputStream();
		
			    int imgByte;
			    while ((imgByte = in.read()) != -1) {
				bStream.write(imgByte);
			    }
		
			    String fileNames[]= fileName.split("\\.");
				String type = fileNames[fileNames.length-1];
			
				
			    if ("jpg".equals(type.toLowerCase())) {
			    	type = "image/jpeg";
			    } 
			    else if ("gif".equals(type.toLowerCase())) {
			    	type = "image/gif";
			    } 
			    else if ("png".equals(type.toLowerCase())) {
			    	type = "image/png";
			    } else {
			    	type = "image/" + type.toLowerCase();
			    }
			  
				response.setHeader("Content-Type", type);
				response.setContentLength(bStream.size());
			
				bStream.writeTo(response.getOutputStream());
			
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
		} finally {
			if (bStream != null) {
				try {
					bStream.close();
				} catch (Exception ignore) {
					//System.out.println("IGNORE: " + ignore);
					//log.debug("IGNORE: " + ignore.getMessage());
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignore) {
					//System.out.println("IGNORE: " + ignore);
					//log.debug("IGNORE: " + ignore.getMessage());
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ignore) {
					//System.out.println("IGNORE: " + ignore);
					//log.debug("IGNORE: " + ignore.getMessage());
				}
			}
		}

	}	  
 
 
}
