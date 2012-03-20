package org.lvxiang.MovieNews.Adapters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import org.lvxiang.MovieNews.Activities.GalleryActivity;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.View.R;


import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements Callback{

	private int film_id;
	private Context context;
	private String[] picIDs;
	private Bitmap[] pics;
	private Handler handler;
	private Hashtable<Integer,ViewHolder> viewList;
	
	private static final String URL_PREFIX="http://img3.douban.com/view/photo/albumicon/public/p";
	private static final String URL_SUFFIX=".jpg";
	private static final String IMAGE_NUM="num";
	
	/**
	 * 
	 * @param context the context to show the views rendered by adapter
	 * @param ids id of images to be loaded from internet
	 * @param film_id the id of the current film
	 */
	public ImageAdapter(Context context,String[] ids,int film_id){
		
		this.film_id=film_id;
		this.context=context;
		this.picIDs=ids;
		
		if(ids!=null)
			this.pics=new Bitmap[ids.length];
		viewList=new Hashtable<Integer,ViewHolder>();
		
		handler=new Handler(this);
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		if(picIDs!=null){
			return picIDs.length;
		}
		return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return picIDs[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub123
		
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		
		if(convertView==null){
			holder=new ViewHolder();
			holder.index=position;
			holder.image=new ImageView(context);
			holder.image.setImageResource(R.drawable.icon);
			holder.image.setLayoutParams(new GridView.LayoutParams(100,120));
			holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
			holder.image.setBackgroundColor(color.white);
			convertView=holder.image;
			convertView.setTag(holder);
			synchronized(viewList){
				viewList.put(position, holder);
			}
		}
		else{
			holder=(ViewHolder) convertView.getTag();
			synchronized(viewList){
			viewList.remove(holder.index);
			viewList.put(position, holder);
			}
		}
		if(pics[position]!=null){
			holder.image.setImageBitmap(pics[position]);
		}
		holder.image.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(context, GalleryActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt(Constants.FILM_ID, film_id);
				bundle.putStringArray(Constants.PIC_ARRAY, picIDs);
				bundle.putInt(Constants.START_POINT, position);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
			
		});
		return convertView;
	}

	private class ViewHolder{
		public int index;
		public ImageView image;
	}
	
	private class PicDownloadThread extends Thread{
		
		public void run(){
			
			lockScreen();
			
			if(picIDs!=null){
				if(!ImageBuffer.contains(film_id)){
					ImageBuffer.addImageArray(film_id, pics);
				}
				for(int i=0;i<picIDs.length;i++){
					try {
						URL url=new URL(URL_PREFIX+picIDs[i]+URL_SUFFIX);
						Log.d("Movie News:", URL_PREFIX+picIDs[i]+URL_SUFFIX);
						try {
							HttpURLConnection connection=(HttpURLConnection) url.openConnection();
							pics[i]=BitmapFactory.decodeStream(connection.getInputStream());
							Bundle bundle=new Bundle();
							bundle.putInt(IMAGE_NUM, i);
							Message message=new Message();
							message.setData(bundle);
							handler.sendMessage(message);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}
	
	public void startDownloadThread(){
		PicDownloadThread thread=new PicDownloadThread();
		thread.start();
	}

	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		/*
		Bundle bundle=msg.getData();
		int index=bundle.getInt(IMAGE_NUM);
		synchronized(viewList){
		if(viewList.contains(index))
			viewList.get(index).image.setImageBitmap(pics[index]);
		}*/
		this.notifyDataSetChanged();
		return false;
	}
	
	/**
	 * if there are pictures in the buffer, this method will be called to 
	 * load pictures from buffer and display them
	 * @param images
	 */
	public void setImageArray(Bitmap[] images){
		this.pics=images;
		for(int i=0;i<pics.length;i++){
			if(viewList.containsKey(i)){
				viewList.get(i).image.setImageBitmap(pics[i]);
			}
		}
	}
	
	private void lockScreen(){
		
		if(context.getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else{
			((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
}
