package org.lvxiang.MovieNews.Utility;

import java.util.HashMap;

import android.graphics.Bitmap;

public class ImageBuffer {

	private static HashMap<Integer,Bitmap[]> images;
	private static HashMap<Integer,String[]> pic_ids;
	
	public static void addImageArray(Integer id,Bitmap []pics){
		if(images!=null){
			if(!contains(id)){
				images.put(id, pics);
			}
		}
	}
	
	public static Bitmap[] getImageArray(Integer id){
		if(contains(id)){
			return images.get(id);
		}
		return null;
	}
	
	public static void addPicIds(int id,String[] ids){
		if(pic_ids==null){
			pic_ids=new HashMap<Integer,String[]>();
		}
		pic_ids.put(id, ids);
	}
	
	public static String[] getPicIds(int id){
		return pic_ids.get(id);
	}
	
	/**
	 * check if pics of the film have been stored in the buffer
	 * poor designed, kind of reliable on how others will call this function
	 * @param id
	 * @return
	 */
	public static boolean contains(Integer id){
		if(images!=null){
			return images.containsKey(id);
		}
		
		images=new HashMap<Integer,Bitmap[]>();
		return false;
	}
	
	public static void remove(int id){
		images.remove(id);
	}
	
}
