package org.lvxiang.MovieNews.Utility;


import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.lvxiang.MovieNews.bean.Movie;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.util.Log;


public class HtmlUtil {
	
	private static boolean loop;
	
	private final static String URL_PREFIX="http://movie.douban.com/subject/";
	private final static String URL_SUFFIX="/all_photos";
	
	
	public HtmlUtil(){
		
	}
	
	/**
	 * get search results
	 * @param url
	 * @return
	 */
	public static List<Movie> getSearchResult(Document doc){
		Elements elem=doc.getElementsByAttributeValue("class","article");
		Elements movies =elem.get(0).getElementsByTag("table");
		
		if(movies==null||movies.size()==0){
			return null;
		}
		
		ArrayList<Movie> results=new ArrayList<Movie>();
		
		for(int i=0;i<movies.size();i++){
			Movie m=new Movie();
			Elements tds=movies.get(i).getElementsByTag("td");
			
			String imgURL=tds.get(0).html();
			
			String doubanURL=imgURL.substring(imgURL.indexOf("http://movie.douban.com/subject/"));
			doubanURL=doubanURL.substring(0, doubanURL.indexOf("\""));
			m.setDoubanUrl(doubanURL);
			Log.d("doubanURL:", doubanURL);
			
			String mID=doubanURL.replace("http://movie.douban.com/subject/", "");
			mID=mID.replace("/", "");
			m.setID(Integer.parseInt(mID));
			Log.d("movie id", mID);
			
			imgURL=imgURL.substring(imgURL.indexOf("<img src=\""));
			imgURL=imgURL.replace("<img src=\"", "");
			imgURL=imgURL.substring(0, imgURL.indexOf("\""));
			m.setImgUrl(imgURL);
			Log.d("imgURL", imgURL);
			
			String name="";
			Elements title=tds.get(1).getElementsByAttributeValue("class", "pl2");
			List<Node> nodes=title.get(0).childNodes();
			List<Node> n=nodes.get(0).childNodes();

			name+=n.get(0).toString().toString().replace("/", "");

			Log.d("movie name", name);
			m.setMovieName(name);
			
			String intro="";
			Elements info=tds.get(1).getElementsByAttributeValue("class", "pl");
			intro=info.get(0).childNodes().get(0).toString();
			m.setDirActHtml(intro);
			
			results.add(m);
		}
		
		return results;
	}

	
	public static List<Movie> getIntroduction(String url){
		
		List<Movie> movieList=new ArrayList<Movie>();
		
		Document doc = null;
		

		try {
			doc=Jsoup.connect("http://movie.douban.com/").timeout(5000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(doc==null){
			try {
				doc=Jsoup.connect("http://movie.douban.com/").timeout(5000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(doc==null){
			return null;
		}
		
		Elements mainUL=doc.select("ul#newcontent1");

		Elements elem=mainUL.get(0).getElementsByTag("li");
		
		for(int i=0;i<elem.size();i++){
			//Log.i("li"+i, elem.get(i).html());
			Movie m=new Movie();
			Element tempElement=elem.get(i);
			Elements imgDiv=tempElement.getElementsByAttributeValue("class", "img");
			
			m.setImgHtml(imgDiv.get(0).html());
			//Log.i("img_url:", imgDiv.get(0).html());
			Elements elems=imgDiv.get(0).children();
			String imgURL=null;
			imgURL=elems.get(0).html().substring(elems.get(0).html().indexOf("\"")+1, elems.get(0).html().lastIndexOf("\""));
			//Log.d("Movie News", imgURL);
			m.setImgUrl(imgURL);
			
			//get the movie's index at movie.douban.com
			String doubanUrl=imgDiv.get(0).html();
			int start=doubanUrl.indexOf("href=\"")+6;
			int end=start;
			while(doubanUrl.charAt(++end)!='\"');
			doubanUrl=doubanUrl.substring(start, end);
			m.setDoubanUrl(doubanUrl);
			String id=doubanUrl.substring(doubanUrl.indexOf("subject/")+8, doubanUrl.length()-1);
			m.setID(Integer.parseInt(id));
			//Log.e(doubanUrl, id);
			//Log.e("Movie News", doubanUrl);
			Elements introDiv=tempElement.getElementsByAttributeValue("class", "intro");
			
			//save the rating node to extract this movie's rating
			Node rating=introDiv.get(0).child(1);
			List<Node> nodeList=rating.childNodes();
			//rank of the movie instance will be set to the number from the extracted node
			m.setDoubanRank(nodeList.get(2).toString().trim());
			
			//remove the rating node from the parent
			introDiv.get(0).child(1).remove();
			//remove the number of current movie
			introDiv.get(0).childNodes().get(1).childNode(0).remove();
			
			//get the movie's name
			List<Node> childList=introDiv.get(0).childNodes().get(1).childNodes().get(0).childNodes();
			String title=childList.get(0).toString();
			if(title.contains("&middot;")){
				Log.e("Movie News", "tag found");
				title=title.replace("&middot;",".");
			}
			m.setMovieName(title);
			
			m.setInfoHtml(introDiv.get(0).html());
			movieList.add(m);
		}
		//System.out.println("working");
		return movieList;
		
	}
	
	public static List<Movie> getTopTenMovieList(String url){
		
		List<Movie> movieList=new ArrayList<Movie>();
		Document doc=null;
		try {
			doc=Jsoup.connect(url).timeout(5000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(doc!=null){
			Elements tBody=doc.getElementsByTag("table");
			Element tableNode=tBody.get(5);
			//System.out.println(tableNode);
			Elements tableRows=tableNode.getElementsByTag("tr");
			
			
			for(int i=0;i<tableRows.size();i++){
				Elements tds=tableRows.get(i).getElementsByTag("td");
				int size=tds.size();
				if(size>4){
					
					Movie m=new Movie();
					String title=getTitle(tds.get(size-5));
					String producer=getOtherInfo(tds.get(size-4));
					String weekly=getOtherInfo(tds.get(size-3));
					String total=getOtherInfo(tds.get(size-2));
					
					m.setMovieName(title);
					m.setProducer(producer);
					m.setWeeklyBox(weekly);
					m.setTotalBox(total);
					
					movieList.add(m);
					//System.out.println(title+":"+producer+" "+weekly+" "+total);
				}
				else 
					continue;
			}
		}
		return movieList;
	}
	
	/**
	 * get detailed movie info from its index at movie.douban.com
	 * @param m
	 * @return
	 */
	public static Movie getDetailedMovieInfo(Movie m,Context context) throws SocketException{
		
		Movie movie=null;
		Document doc=null;
		loop=true;
		try {
			
			movie=(Movie) m.clone();
			
			//loop while the doc cannot be downloaded
			for(;;){
				
				doc=Jsoup.connect(m.getDoubanUrl())
						.timeout(5000)//set timeout to 5sec
						.get();
				if(doc==null){
					Builder dialog=new AlertDialog.Builder(context);
					dialog.setTitle("抱歉");
					dialog.setMessage("数据加载失败，请重试。");
					dialog.setPositiveButton("确定", new OnClickListener(){

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
						
					});
					dialog.setNegativeButton("取消", new OnClickListener(){

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//loop=false;
							dialog.dismiss();
							loop=false;
							return;
						}
						
					});
					dialog.show();
					if(!loop){
					return null;
					}
				}
				else{
					break;
				}
			}
			
			Elements info=doc.getElementsByAttributeValue("id", "info");
			movie.setDirActHtml(info.get(0).html());
			
			Elements detail=doc.getElementsByAttributeValue("property", "v:summary");
			movie.setDetailHtml(detail.get(0).html());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return movie;
	}
	
	
	/**
	 * get the image IDs of a movie
	 * @return
	 */
	public static Set<String> getImageIDs(int ID){
		
		Set<String> idSet=null;
		try {
			Document doc=Jsoup.connect(URL_PREFIX+ID+URL_SUFFIX).timeout(5000).get();
			Elements elems=doc.getElementsByTag("li");
			idSet=new HashSet<String>();
			for(int i=0;i<elems.size();i++){
				//Log.d("MovieNews",elems.get(i).html());
				String id=getMatchedID(elems.get(i).html());
				if(id!=null)
					idSet.add(id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idSet;
	}
	
	/**
	 * get the movie from title from an element
	 * @param elem
	 * @return
	 */
	private static String getTitle(Element elem){
		String html=elem.html();
		String title="";
		int start=html.indexOf("</a>");
		
		while(html.charAt(--start)!='>'){
			title=html.charAt(start)+title;
		}
		//System.out.println("title:"+title);
		return title;
	}
	
	/**
	 * get weekly box office/producer/total box office of an element
	 * all of these elements are in this format:"<tag>what we want</tag>"
	 * @param elem
	 * @return
	 */
	private static String getOtherInfo(Element elem){
		List<Node> nodeList=elem.childNodes();
		return nodeList.get(0).toString();
	}
	
	/**
	 * movie.douban.com cannot be loaded on the virtual machine, so I have to download it and store it
	 * on the sd card. This method is dedicated to loading the html file from sd card and get a Document 
	 * instance for the {@link #getIntroduction(String)} method to use.
	 * @param file
	 * @return
	 */
	public static Document getDocumentFromSDCard(String filepath,String filename){
		String sdStatus=Environment.getExternalStorageState();
		
		if(sdStatus.equals(Environment.MEDIA_MOUNTED))//in this case the sd-card has been mounted in ready state
		{

			File file=new File(filepath+filename);
			if(file.exists()){
				try {
					Document doc=Jsoup.parse(file, "utf-8", "http://movie.douban.com/");
					return doc;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else{
				Log.e("error:", "file not exsits");
				return null;
			}
		}
		
		else 
			Log.e("error:", "SD card not ready!");
			return null;
	}

	
	private static String getMatchedID(String html){
		
		String regex="\\d{5,}.jpg";
		Pattern patern =Pattern.compile(regex);
		Matcher matcher=patern.matcher(html);
		String id=null;
		if(matcher.find()){
			id=matcher.group().replace(".jpg", "");
		}
		//Log.d("Movies News", "id:"+id);
		return id;
	}
}
