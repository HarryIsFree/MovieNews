package org.lvxiang.MovieNews.Adapters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.NetworkUtil;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class MySpinnerAdapter extends BaseAdapter implements Callback{

	private String[] imageIDs;
	private Context context;
	private ImageView[] views;
	private Bitmap []low_quality_pics;//used to store low quality pics
	private Bitmap []high_quality_pics;//used to store high quality pics
	private Handler handler;
	private static final String PREFIX="http://img3.douban.com/view/photo/photo/public/p";
	private ImageLoadThread[] threads;
	
	public MySpinnerAdapter(Context context,String[] ids,int start_point,Bitmap [] pics){
		
		this.imageIDs=ids;
		this.context=context;
		this.low_quality_pics=pics;
		this.high_quality_pics=new Bitmap[ids.length];
		this.handler=new Handler(this);
		this.threads=new ImageLoadThread[4];
		
		views=new ImageView[ids.length];
		for(int i=0;i<ids.length;i++){
			views[i]=new ImageView(context);
		}
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		if(imageIDs!=null)
			return imageIDs.length;
		
		return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imageIDs[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
			views[position%imageIDs.length].setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			views[position%imageIDs.length].setScaleType(ImageView.ScaleType.CENTER_CROP);
			if(high_quality_pics[position%imageIDs.length]==null){
				if(threads[position%4]!=null&&threads[position%4].isAlive()){
					threads[position%4].stop();
				}
				threads[position%4]=new ImageLoadThread(position);
				threads[position%4].start();
				if(low_quality_pics[position%imageIDs.length]!=null)
					views[position%imageIDs.length].setImageBitmap(this.low_quality_pics[position%imageIDs.length]);
				
			}
			else{
				views[position].setImageBitmap(this.high_quality_pics[position%imageIDs.length]);
			}
			convertView=views[position];
		}
		return convertView;
	}

	private class ImageLoadThread extends Thread{
		
		private int position;
		
		public ImageLoadThread(int p){
			this.position=p;
		}
		
		public void run(){
			if(NetworkUtil.checkNetState(context)){
				high_quality_pics[position%imageIDs.length]=getBitmapFromURL(PREFIX+imageIDs[position%imageIDs.length]+".jpg");
				sendMessage(position);
			}
		}
		
	}
	
	private Bitmap getBitmapFromURL(String url){
		URL u;
		try {
			u = new URL(url);
			HttpURLConnection connection=(HttpURLConnection) u.openConnection();
			Bitmap result=BitmapFactory.decodeStream(connection.getInputStream());
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void sendMessage(int position){
		Bundle bundle=new Bundle();
		bundle.putInt(Constants.IMAGE_ID, position);
		Message message=new Message();
		message.setData(bundle);
		handler.sendMessage(message);
	}
	
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		int position=msg.getData().getInt(Constants.IMAGE_ID);
		views[position].setImageBitmap(high_quality_pics[position]);
		Log.d("Movie News", "Image updated");
		return false;
	}
}
