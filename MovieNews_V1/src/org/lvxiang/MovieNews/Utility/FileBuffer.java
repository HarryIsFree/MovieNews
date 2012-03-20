package org.lvxiang.MovieNews.Utility;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.lvxiang.MovieNews.bean.Movie;



public class FileBuffer {

	private static Hashtable<String,ArrayList<String>> sentences;
	private static String[] names;
	private static String currentKey;
	private static List<Movie> newMovieList;
	private static List<Movie> topTenList;
	
	public static void setSentences(Hashtable<String,ArrayList<String>> s){
		sentences=s;
	}
	
	public static Hashtable<String,ArrayList<String>> getSentences(){
		return sentences;
	}
	
	public static void setNames(String[] ns){
		names=ns;
	}
	
	public static String[] getNames(){
		return names;
	}
	
	public static void setCurrentKey(String str){
		currentKey=str;
	}
	
	public static String getCurrentKey(){
		return currentKey;
	}
	
	public static void setNewMovieList(List<Movie> newMovieList2){
		newMovieList=newMovieList2;
	}
	
	public static List<Movie> getNewMovieList(){
		return newMovieList;
	}
	
	public static void setTopMovieList(List<Movie> list){
		topTenList=list;
	}
	
	public static List<Movie> getTopMovieList(){
		return topTenList;
	}
}
