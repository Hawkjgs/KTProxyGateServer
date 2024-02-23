package sm.comm;

public class StringUtil {
	
	
	public static int ParserIntDef(String sValue,int iDefValue) {
		int ivalue=0;
	 	try {
	 		ivalue = Integer.parseInt(sValue);
		}
		catch(NumberFormatException e) {
			ivalue=iDefValue;	 
		}
		 
		return ivalue;
	  }
	 
	public static String NullToBlankDef(String str,String def)
	{
		if (str == null)return def;
		else if("null".equals(str)) return def;
		else if("".equals(str)) return def;
		else return str.trim();
	
	}
	    
	    public static String NullToBlank(Object  str,String def)
		{
			if (str == null)return def;
			else if("null".equals(str)) return def;
			else if("".equals(str)) return def;
			else return str.toString().trim();
		}
	    
	    public static String NullToBlank(Object  str)
		{
			if (str == null)return "";
			else if("null".equals(str)) return "";
			else return str.toString().trim();

		}
	    
	public static String trimTrailingZeros(String number) {
	    if(!number.contains(".")) {
	        return number;
	    }
	    return number.replaceAll("\\.?0*$", "");
	}
	
	public static String ExpFloatToString(Object expStr)
	{
		if (expStr==null) return "0.0";
		String s = ""+expStr;
		if(s.indexOf("E") <0) {
		 
			return trimTrailingZeros(s); 
			 
		}
		else if (expStr instanceof Double ) {
			String str ="";
			String s1;
			s = s.substring(s.indexOf("E")+1);
			s1 =""+ Math.abs(StringUtil.parseInt(s));
			str  =  String.format("%."+s1+"f", expStr); 
		 
       	 	return  str;
		}
		else if (expStr instanceof Float ) {
			String str ="";
			String s1;
			s = s.substring(s.indexOf("E")+1);
			s1 =""+ Math.abs(StringUtil.parseInt(s));
			str  =  String.format("%."+s1+"f", expStr); 
		
       	 	return  str;
		  
		}
		else return "0.0";
   	 
	}
	
	
	public static int parseInt(Object s) {
		if (s==null) return 0;
		if (s instanceof String ) {
			int number = 0;
			try {
				 number = Integer.parseInt((String) s);
	        } catch (NumberFormatException exception) {
	        	number=0;
	        }
			return number;
		}
		return 0;
	}
	
	public static int parseInt(Object s,int idef) {
		if (s==null) return 0;
		if (s instanceof String ) {
			int number = 0;
			try {
				 number = Integer.parseInt((String) s);
	        } catch (NumberFormatException exception) {
	        	number=idef;
	        }
			return number;
		}
		return idef;
	}
	
	public static long parseLong(Object s) {
		if (s==null) return 0;
		if (s instanceof String ) {
			long number = 0;
			try {
				 number = Long.parseLong((String) s);
	        } catch (NumberFormatException exception) {
	        	number=0;
	        }
			return number;
		}
		return 0;
	}
	
	
	public static float parseFloat(Object s) {
		if (s==null) return 0;
		if (s instanceof String ) {
			float number = 0;
			try {
				 number = Float.parseFloat((String) s);
	        } catch (NumberFormatException exception) {
	        	number=0;
	        }
			return number;
		}
		return 0;
	}
	
	
	public static String NvlToStr(Object s) {
		if (s==null) return "";
		if (s instanceof String ) {
			return (String) s;
		}
		return "";
	}
	
	public static String addZero (String str, int length) {
		String temp = "";
		for (int i = str.length(); i < length; i++)
			temp += "0";
		temp += str;
		return temp;
	}

	public static boolean isEmpty(String str) {
		if(str == null || str.length() == 0) {
			return true;
		}else {
			return false;
		}
	}
	
}
