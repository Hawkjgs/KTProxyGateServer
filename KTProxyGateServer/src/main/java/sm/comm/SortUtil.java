package sm.comm;

import java.util.*;
public class SortUtil {
	
	
	
	public static List sortMap(List mList,String sort_field) {
		Collections.sort(mList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Integer  idx1 = StringUtil.parseInt((String) o1.get(sort_field));
				Integer  idx2 =  StringUtil.parseInt((String) o2.get(sort_field));
				//return name1.compareTo(name2);
				return idx1.compareTo(idx2);
			}
		});
		
//		for(int i=0; i<mList.size(); i++) 
//			System.out.println(mList.get(i));
		
		return mList;
		
	}
	
}
