 
package sm.comm;
 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
 
public class SMEnv {
	
	
	
	
	
	public static  String getEnvAPIFileName() {
    	
    	String jarPath = SMEnv.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String envPath="";
		
    	jarPath=jarPath.replaceAll("!/WEB-INF/classes!/", "");
		jarPath=jarPath.replaceAll("file:", "");
		jarPath=jarPath.replaceAll("/classes/", "");
		jarPath=jarPath.replaceAll("%20", " ");
		
		Path path_phy = Paths.get(jarPath);
	 	envPath = path_phy.getParent().toString()+"/conf/lilliusAPI.properties";
		return envPath;
    }
	

	public static String getProperties(String fileName,String key) {	
		String value="";
		try{
			Properties p = new Properties();
			p.load(new FileInputStream(fileName));
			
			value= StringUtil.NullToBlank(p.getProperty(key));
		}
		catch (Exception e) {
		     //       System.out.println(e);
		}
		return value.trim();
 
	}
	
	public static String getProperties(String fileName,String key,String defValue) {	
		String value="defValue";
		try{
			Properties p = new Properties();
			p.load(new FileInputStream(fileName));
			
			value= StringUtil.NullToBlank(p.getProperty(key));
			if("".equals(value)) {
				return defValue;
			}
		}
		catch (Exception e) {
		     //       System.out.println(e);
		}
		return value.trim();
 
	}
	
	public static boolean SetProperties(String fileName,String key ,String value) {	
		try{
			Properties p = new Properties();
			p.load(new FileInputStream(fileName));
			
			p.setProperty(key, value);
			
			OutputStream os = null;
			os = new FileOutputStream(fileName); 
			p.store(os, null);
			return true;

		}
		catch (Exception e) {
		     //       System.out.println(e);
		}
		
		return false;
 
	} 
}
