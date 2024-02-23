package sm.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FTPUtil {
	
	public static Session sFTPSession(String ipaddrss, int iPort , String userNm ,String pwd, String pkey)
	{
		Session session = null; //com.jcraft.jsch.Session 참조함
		JSch jsch = new JSch();
	 	try {
//	 		pkey ="/Volumes/COMM_DISK/Project/lilliusApp/aws/openssh_rsa.pem";
//	 		String key = SFileUtil.readFile(pkey);
	 		
	 		if(!"".equals(pkey)) jsch.addIdentity(pkey);
	 		session = jsch.getSession(userNm, ipaddrss, iPort);
			// 3. 패스워드를 설정한다.
	 		
	 		if("".equals(pkey))  {
	 			session.setPassword(pwd);
	 		}
	        // 4. 세션과 관련된 정보를 설정한다.
	        java.util.Properties config = new java.util.Properties();
	        // 4-1. 호스트 정보를 검사하지 않는다.
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
             //5. 접속한다.       
	         session.connect();
		     
	        }catch (JSchException e1) {
			  e1.printStackTrace();
	        }
	        return session;
	}
//	
//	public static Session sFTPSession(String ipaddrss, int iPort , String userNm ,String userPwd)
//	{
//		Session session = null; //com.jcraft.jsch.Session 참조함
//		JSch jsch = new JSch();
//	 	try {
//			session = jsch.getSession(userNm, ipaddrss, iPort);
//			// 3. 패스워드를 설정한다.
//	        session.setPassword(userPwd);
//	        // 4. 세션과 관련된 정보를 설정한다.
//	        java.util.Properties config = new java.util.Properties();
//	        // 4-1. 호스트 정보를 검사하지 않는다.
//	        config.put("StrictHostKeyChecking", "no");
//	        session.setConfig(config);
//             //5. 접속한다.       
//	         session.connect();
//		     
//	        }catch (JSchException e1) {
//			  e1.printStackTrace();
//	        }
//	        return session;
//	}
//	
	public static ChannelSftp sFTPConnect(Session session)
	{
	 
		ChannelSftp channelSftp = null;
		try {
			 // 6. sftp 채널을 연다.
	         Channel channel = session.openChannel("sftp");
	         channelSftp = (ChannelSftp) channel;
	         channelSftp.connect();
	         
	        }catch (JSchException e1) {
			  e1.printStackTrace();
	        }
	        return channelSftp;
	}
	

	
	public static HashMap FtpFileDelete(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile,String pkey)
	{
		HashMap result = null;
		if(bSFTP)  
			result= SFtpFileDelete(path,ipaddrss,iPort,userNm,userPwd,srcFile,pkey);
		else 	result= StdFtpFileDelete(path,ipaddrss,iPort,userNm,userPwd,srcFile);
		 
		return result;

	}
	

	public static HashMap StdFtpFileDelete(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile)
	{
		
		
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 
		try {
			client.deleteFile(srcFile);
			client.disconnect();
			result.put("RESULT", 1); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
		}
 		 
		 
		return result;
		
	}
	
	
	public static HashMap SFtpFileDelete(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
		 
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		try {
			channelSftp.rm(srcFile);
			result.put("RESULT", 1); 
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		 
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	
	public static HashMap StdFTPConnect(String ipaddrss,int iPort,String userNm,String userPwd)
	{
		HashMap result = new HashMap();
		
		 
		FTPClient client = new FTPClient();
 		try {
 			client.setConnectTimeout(5000);
 			client.connect(ipaddrss, iPort);
			
			int resultCode = client.getReplyCode();
			//System.out.println("getReplyCode:"+resultCode);
			if (!FTPReply.isPositiveCompletion(resultCode)) {
				result.put("RESULT", -1);
				result.put("RESULT_MSG", "FTP server refused connection.!");
				
				
				
				result.put("RESULT", -1);
				result.put("client",client);
				
				return result;
			} else {
				
				client.setSoTimeout(1000);
				// 로그인을 한다.
				if (!client.login(userNm, userPwd)) {
					// 로그인을 실패하면 프로그램을 종료한다.
					result.put("RESULT", -1);
					result.put("RESULT_MSG", "Login Error!");
					client.disconnect();
					
					return result;
				}
				
				result.put("RESULT", 1);
				result.put("client", client);
				
				return result;
				
			}
	
		} catch (SocketException e1) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", e1.toString());
			 
		} catch (IOException e1) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", e1.toString());
		}
 		
 		
 		return result;
 
	}
	
	
	
	
	
	public static HashMap FtpMkDir(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd,String pkey)
	{
		HashMap result = null;
		if(bSFTP) result= SFtpMkDir(path,ipaddrss,iPort,userNm,userPwd,pkey);
		else  result= StdFtpMkDir(path,ipaddrss,iPort,userNm,userPwd);
		return result;

	}
	
	public static HashMap StdFtpMkDir(String path ,String ipaddrss, int iPort , String userNm ,String userPwd)
	{
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		
		//System.out.println("StdFtpMkDir:"+result);
		
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 
		try {
			client.mkd(path);
			client.disconnect();
			
			result.put("RESULT", 1); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return result;
		   
		
	}
	
	
	public static HashMap FtpChDir(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd,String pkey)
	{
		HashMap result = null;
		if(bSFTP) result= SFtpChDir(path,ipaddrss,iPort,userNm,userPwd,pkey);
		else  result= StdFtpChDir(path,ipaddrss,iPort,userNm,userPwd);
		return result;

	}
	
	public static HashMap StdFtpChDir(String path ,String ipaddrss, int iPort , String userNm ,String userPwd)
	{
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 
		try {
			boolean bRet= client.changeWorkingDirectory(path);
			
			client.disconnect();
			if(bRet) result.put("RESULT", 1); 
			else {
				result.put("RESULT", -1); 
				result.put("RESULT_MSG","StdFtpChDir Error:"+ path); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return result;
		   
		
	}
	
	
	public static HashMap SFtpChDir(String path ,String ipaddrss, int iPort , String userNm ,String userPwd,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		StringBuffer sb = new StringBuffer();
		Vector<ChannelSftp.LsEntry> flist;
		String lastModif = "";
		String strDate="";
		try {
			channelSftp.cd(path);
			result.put("RESULT", 1); 
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	
	public static HashMap SFtpMkDir(String path ,String ipaddrss, int iPort , String userNm ,String userPwd,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		StringBuffer sb = new StringBuffer();
		Vector<ChannelSftp.LsEntry> flist;
		String lastModif = "";
		String strDate="";
		try {
			channelSftp.mkdir(path);
			result.put("RESULT", 1); 
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	
	
	
	
	public static HashMap FtpFileMove(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile,String destFile,String pkey)
	{
		HashMap result = null;
		if(bSFTP)  
			result= SFtpFileMove(path,ipaddrss,iPort,userNm,userPwd,srcFile,destFile,pkey);
		else 	result= StdFtpFileMove(path,ipaddrss,iPort,userNm,userPwd,srcFile,destFile);
		 
		return result;

	}
	
	
	public static HashMap StdFtpFileMove(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile,String destFile)
	{
		
		
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 

		
 		try {
			client.deleteFile(destFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 		
 		
		try {
			client.rename(srcFile, destFile);
			client.disconnect();
			
			result.put("RESULT", 1); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	
	
	public static HashMap SFtpFileMove(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String srcFile,String destFile,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
		 
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		try {
			channelSftp.rm(destFile);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		try {
			
			channelSftp.rename(srcFile, destFile);
			result.put("RESULT", 1); 
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	
	public static HashMap FtpFileUpload(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String downDir,String fnName, String sPassMode,String pkey)
	{
		HashMap result = null;
	//	System.out.println("FtpFileUpload start" );
		if(bSFTP)  
			result= SFtpFileUpload(path,ipaddrss,iPort,userNm,userPwd,downDir,fnName,pkey);
		else result= StdFtpFileUpload(path,ipaddrss,iPort,userNm,userPwd,downDir,fnName, sPassMode);
		
	//	System.out.println("FtpFileUpload End:"+ result);
		return result;

	}
	
	public static HashMap StdFtpFileUpload(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String downDir,String fnName,String sPassMode)
	{
		
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 		try {
 			client.mkd(path);
		} catch (IOException e) {
			System.out.println(e);
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
			

		try {
			
			File uFile = new File(downDir+File.separator+fnName);
			FileInputStream fis = new FileInputStream(uFile);
			if("TRUE".equals(sPassMode.toUpperCase())) {
				client.enterLocalPassiveMode();
			}
			client.setFileType(FTP.BINARY_FILE_TYPE);
			//client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			//System.out.println("client.storeFile Start");
			boolean done = client.storeFile(path+"/"+fnName, fis);
			//System.out.println("client.storeFile End");
			 if (done) {
				 result.put("RESULT", 1);
				 result.put("RESULT_MSG","Send OK");
	            }
			 else {
				 result.put("RESULT", -1);
				 result.put("RESULT_MSG","Send Fail");
				 
			 }
			 
	            
			fis.close();
			
		} catch (IOException e) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG",e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			client.disconnect();
		} catch (IOException e) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG",e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 
		return result;
		   
		
	}
	
	
	public static HashMap SFtpFileUpload(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String downDir,String fnName,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
		 
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		StringBuffer sb = new StringBuffer();
		Vector<ChannelSftp.LsEntry> flist;
		String lastModif = "";
		String strDate="";
		try {
			
			try { 
				channelSftp.mkdir(path);
			} catch (SftpException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	        
			channelSftp.put( downDir+File.separator+fnName,path+"/"+fnName);
			result.put("RESULT", 1); 
			result.put("RESULT_MSG","Send OK");
		        
			
		} catch (SftpException e) {
			result.put("RESULT",-1); 
			result.put("RESULT_MSG",e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	
	
	public static HashMap FtpFileDn(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd,
			String dnDir,String fnName,String pkey)
	{
		HashMap result = null;
		if(bSFTP) result= SFtpFileDown(path,ipaddrss,iPort,userNm,userPwd,dnDir,fnName,pkey);
		else result= StdFtpFileDown(path,ipaddrss,iPort,userNm,userPwd,dnDir,fnName);
		return result;

	}
	
	public static HashMap StdFtpFileDown(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String dnDir,String fnName)
	{
		
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 
		File getFile;
		FileOutputStream outputstream;
		try {
			getFile = new File(dnDir+File.separator+fnName);
			outputstream = new FileOutputStream(getFile);
			client.retrieveFile(path+"/"+fnName, outputstream);
			client.disconnect();
			result.put("RESULT", 1); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
 
		
	}
	
	public static HashMap SFtpFileDown(String path ,String ipaddrss, int iPort , String userNm ,String userPwd
			,String dnDir,String fnName,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
//		log.debug("FtpFileList");
//		log.debug("path:"+path);
//		log.debug("ipaddrss:"+ipaddrss);
//		log.debug("iPort:"+iPort);
//		log.debug("userNm:"+userNm);
//		log.debug("userPwd:"+userPwd);
//		
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		StringBuffer sb = new StringBuffer();
		Vector<ChannelSftp.LsEntry> flist;
		String lastModif = "";
		String strDate="";
		try {
			String srcFile = path +"/"+fnName;
			channelSftp.get(srcFile, dnDir+File.separator + fnName);
			result.put("RESULT", 1); 
			
		        
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	

	public static HashMap FtpFileList(Boolean bSFTP , String path ,String ipaddrss, int iPort , String userNm ,String userPwd,int iTime,String pkey)
	{
		HashMap result = null;
		if(bSFTP) result= SFtpFileList(path,ipaddrss,iPort,userNm,userPwd,iTime,pkey);
		else result= StdFtpFileList(path,ipaddrss,iPort,userNm,userPwd,iTime);
		return result;

	}
	
	public static HashMap StdFtpFileList(String path ,String ipaddrss, int iPort , String userNm ,String userPwd,int iTime)
	{
		HashMap result = StdFTPConnect(ipaddrss,iPort,userNm , userPwd);
		List fileList = new ArrayList();
		String ret= ""+ result.get("RESULT"); 
		if(!"1".equals(ret)) {
			return result;
		}
		FTPClient client = (FTPClient) result.get("client");
 
		String lastModif = "";
		String strDate="";
		try {
			boolean bret= client.changeWorkingDirectory(path);
			//System.out.println("changeWorkingDirectory:"+bret);
			FTPFile[] files =client.listFiles();
			FTPFile file = null;
			//System.out.println("files:"+files.length);
			for( int i=0;i<files.length;i++) {
				file = files[i];
				if(!file.getName().equals(".") && !file.getName().equals("..")
		       			 && file.getName().indexOf(".") != 0
		       			 && !file.isDirectory()
		       			 && file.getName().indexOf(".xls") > 0
		       			 ){
					
					Calendar cal =file.getTimestamp();
					Date f_date = cal.getTime();  
					Date cur_date = new Date();
					
					 long diff = cur_date.getTime() - f_date.getTime();
	                 long diffMinutes = diff / (60 * 1000);   
	                 
					 SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					
					 
					
					 HashMap m = new HashMap();
		       		 m.put("FILE_NM", file.getName());
	       		 	 m.put("FILE_SZ", file.getSize());
	       		 	 m.put("FILE_C_DT", tranSimpleFormat.format(f_date));
	       		 	 m.put("FILE_M_DT", tranSimpleFormat.format(f_date));
	       		 	 m.put("CAP_TIME", (int) diffMinutes);
	       		 	 
	       		 	 fileList.add(m);
	       		 	 
				}
			}
			result.put("RESULT", 1);
			result.put("FILE_LIST", fileList);
			
			client.disconnect();
			
		} catch (IOException e) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
 
		
		return result;
		   
		
	}
	
	
	public static HashMap SFtpFileList(String path ,String ipaddrss, int iPort , String userNm ,String userPwd,int iTime,String pkey)
	{
		List fileList = new ArrayList();
		HashMap result = new HashMap();
//		log.debug("FtpFileList");
//		log.debug("path:"+path);
//		log.debug("ipaddrss:"+ipaddrss);
//		log.debug("iPort:"+iPort);
//		log.debug("userNm:"+userNm);
//		log.debug("userPwd:"+userPwd);
		
		Session session =sFTPSession( ipaddrss,  iPort ,  userNm , userPwd,pkey);
		if(session==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Connection Fail");
			 
			return result;
		}
		ChannelSftp channelSftp = sFTPConnect( session);
		
		if(channelSftp ==null) {
			result.put("RESULT", -1);
			result.put("RESULT_MSG", "SFTP Channel Fail");
			return result;
		}
		
		StringBuffer sb = new StringBuffer();
		Vector<ChannelSftp.LsEntry> flist;
		String lastModif = "";
		String strDate="";
		try {
			flist = channelSftp.ls(path);
			for(ChannelSftp.LsEntry entry : flist){
		       	 //log.info("파일명 :"+entry.getAttrs().isDir());    
		       	 if(!entry.getFilename().equals(".") && !entry.getFilename().equals("..")
		       			 && entry.getFilename().indexOf(".") != 0
		       			 && !entry.getAttrs().isDir()
		       			 && entry.getFilename().indexOf(".xls") > 0
		       			 ){
		       		 
		       		 	lastModif = entry.getAttrs().getMtimeString();
		       		 	
		       		 	SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		       		 
		       		 	// 여기에 원하는 포맷을 넣어주면 된다
		       		 	SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		      
			             try {
			                 Date f_date = recvSimpleFormat.parse(lastModif);
			                 
			                 Date cur_date = new Date();
			                 
			                 long diff = cur_date.getTime() - f_date.getTime();
			                 long diffMinutes = diff / (60 * 1000);   
			                 
			                 //if(diffMinutes >= iTime) {
			                	 HashMap m = new HashMap();
					       		 m.put("FILE_NM", entry.getFilename());
				       		 	 m.put("FILE_SZ", entry.getAttrs().getSize());
				       		 	 m.put("FILE_C_DT", entry.getAttrs().getAtimeString());
				       		 	 m.put("FILE_M_DT_KST", lastModif);
				       		 	 m.put("FILE_M_DT", tranSimpleFormat.format(f_date));
				       		 	 m.put("CAP_TIME", (int) diffMinutes);
				       		 	 //m.put("CAP_TIME", (int) diffMinutes);
				       		 	 
				       		 	 fileList.add(m);
					       	 //}
			                 
			               
			             } catch (ParseException e) {
			                 e.printStackTrace();
			             }
  
		       		 	//log.info("파일명 :"+entry.getFilename());  
		       		 	
		       	 }
		    }
			result.put("RESULT", 1);
			result.put("FILE_LIST", fileList);
			
		        
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		channelSftp.quit();
		session.disconnect();
		return result;
		   
		
	}
	
	 
 
}
