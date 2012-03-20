package org.lvxiang.MovieNews.Adapters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import org.lvxiang.MovieNews.Activities.MyActivity;
import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter implements Callback{

	private List<Movie> movieList;
	private LayoutInflater inflater;
	private Hashtable<Integer,ViewHolder> holders;
	private Hashtable<Integer,Bitmap> bitmaps;
	private Handler handler;
	private static final String MOVIE_INDEX="index";
	
	public SearchResultAdapter(Context context,List<Movie> list){
		this.movieList=list;
		this.inflater=LayoutInflater.from(context);
		bitmaps=new Hashtable<Integer,Bitmap>();
		handler=new Handler(this);
		holders=new Hashtable<Integer,ViewHolder>();
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return movieList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return movieList.get(arg0);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	/**
	 * the list view uses limited views to avoid using a separate view for each list
	 * item. When the view starts, it calculates how many sub views are needed for 
	 * each list item. When the user scrolls the listview, some item will appear and 
	 * some disappear. The disappeared ones, called convertView, are converted into 
	 * the newly shown ones. This helps enhance efficiency. 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		
		synchronized(holders){
		if(convertView==null){
			convertView=inflater.inflate(R.layout.search_item, null);
			holder=new ViewHolder();
			holder.pic=(ImageView)convertView.findViewById(R.id.search_imageView);
			holder.pic.setScaleType(ScaleType.CENTER);
			holder.title=(TextView)convertView.findViewById(R.id.search_title);
			holder.title.setTextSize(12);
			holder.title.setTextColor(Color.BLACK);
			holder.info =(TextView)convertView.findViewById(R.id.search_info);
			holder.info.setTextSize(7);
			holder.info.setTextColor(Color.BLACK);
			holder.current_id=position;
			holders.put(position, holder);
			
			Log.d("init:", ""+position);
			
			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder) convertView.getTag();
			holders.remove(holder.current_id);
			holder.current_id=position;
			holders.put(position, holder);
		}
		}
		holder.title.setText(movieList.get(position).getMovieName());
		holder.info.setText(movieList.get(position).getDirActHtml());
		
		
		if(bitmaps.containsKey(position)){
			holder.pic.setImageBitmap(bitmaps.get(position));
		}
		else{
			holders.put(position, holder);
			new PicDownloadThread(position).start();
		}
		
		
		return convertView;
	}
	
	private class PicDownloadThread extends Thread{
		
		private int index;
		
		public PicDownloadThread(int index){
			this.index=index;
		}
		
		public void run(){
			
			try {
				Looper.prepare();
				URL url=new URL(MyActivity.searchResults.get(index).getImgUrl());
				HttpURLConnection connection=(HttpURLConnection) url.openConnection();
				Bitmap bitmap=BitmapFactory.decodeStream(connection.getInputStream());
				MyActivity.searchResults.get(index).setCover(bitmap);
				bitmaps.put(index, bitmap);
				
				Message message=new Message();
				Bundle bundle=new Bundle();
				bundle.putInt(MOVIE_INDEX, index);
				message.setData(bundle);
				handler.sendMessage(message);
				Log.d("index sent:", ""+index);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class ViewHolder{
		public int       current_id;//the id is used to record the position of the item this holder holds
		public ImageView pic;
		public TextView  title;
		public TextView  info;
	}

	
	
	public synchronized boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		this.notifyDataSetChanged();
		return false;
	}

}
