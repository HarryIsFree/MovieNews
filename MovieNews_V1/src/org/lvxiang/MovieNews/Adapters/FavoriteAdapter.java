package org.lvxiang.MovieNews.Adapters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lvxiang.MovieNews.Activities.PicActivity;
import org.lvxiang.MovieNews.DAO.DataBaseUtil;
import org.lvxiang.MovieNews.Utility.HtmlUtil;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.Utility.NetworkUtil;
import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

public class FavoriteAdapter extends BaseAdapter implements Callback{
	
	private List<Movie> favorites;
	private LayoutInflater inflater;
	private DataBaseUtil db;
	private Context context;
	private Handler handler;
	private ProgressDialog pDialog;
	
	private static final String IMAGE_ID="ids";
	private static final String FILM_ID="film_id";
	
	public FavoriteAdapter(Context context,DataBaseUtil db){
		inflater=LayoutInflater.from(context);
		this.context=context;
		this.db=db;
		this.pDialog=new ProgressDialog(context);
		pDialog.setIndeterminate(true);
		pDialog.setMessage("loading...");
		handler=new Handler(this);
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		if(favorites!=null)
			return favorites.size();
		
		return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(favorites!=null){
			return favorites.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.db_movie_layout, null);
			holder.button=(ImageButton) convertView.findViewById(R.id.db_imageButton);
			holder.text  =(Button)convertView.findViewById(R.id.db_button);
			holder.text.setTextSize(20);
			holder.text.setTextColor(Color.BLACK);
			holder.delete=(ImageButton) convertView.findViewById(R.id.db_delete);

			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.button.setImageBitmap(favorites.get(position).getCover());
		holder.text.setText(favorites.get(position).getMovieName());
		holder.movie=favorites.get(position);
		
		holder.delete.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(db.deleteMovie(holder.movie.getID())){
					favorites=db.getMoviesInDatabase();
					if(ImageBuffer.contains(holder.movie.getID())){
						ImageBuffer.remove(holder.movie.getID());
					}
					
					FavoriteAdapter.this.notifyDataSetChanged();
				}
			}
			
		});
		
		holder.text.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog dialog;
				Builder builder=new AlertDialog.Builder(context);
				builder.setTitle(holder.movie.getMovieName());
				
				WebView webView=new WebView(context);
				webView.loadDataWithBaseURL(null, holder.movie.getIntroduction(), 
						context.getResources().getString(R.string.MIME_TYPE),
						context.getResources().getString(R.string.ENCODING),
						null);
				builder.setView(webView);
				builder.setPositiveButton(context.getResources().getString(R.string.SEE_MORE_PICS), new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(NetworkUtil.checkNetState(context)){
						new ImageLoadThread(holder.movie.getID()).start();
						pDialog.show();
						}
					}
					
				});
				builder.setNegativeButton(context.getResources().getString(R.string.MOVIE_EXIT), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialog=builder.create();
				dialog.show();
			}
			
		});
		
		
		
		return convertView;
	}
	
	public void setMovieList(List<Movie> list){
		this.favorites=list;
	}
	
	private static class ViewHolder{
		public ImageButton button;
		public ImageButton delete;
		public Button text;
		public Movie movie;
	}
	/**
	 * remove a node from the adapter
	 * @param position
	 */
	public void remove(int position){
		favorites.remove(position);
		this.notifyDataSetChanged();
	}

	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		pDialog.dismiss();
		Intent intent=new Intent(context,PicActivity.class);
		intent.putExtras(arg0.getData());
		context.startActivity(intent);
		return false;
	}

	/**
	 * the thread to load pictures from douban.com
	 * @author lvxiang
	 *
	 */
	private class ImageLoadThread extends Thread{
		
		private int image_id;
		
		public ImageLoadThread(int imageID){
			this.image_id=imageID;
		}
		
		public void run(){
			
			Bundle bundle=new Bundle();
			
			Set<String> ids=new HashSet<String>();
			String[] array=new String[20];
			if(ImageBuffer.contains(image_id)){
				array=ImageBuffer.getPicIds(image_id);
			}
			else{
				ids=HtmlUtil.getImageIDs(image_id);
				array=ids.toArray(array);
				ImageBuffer.addPicIds(image_id, array);
			}
			
			bundle.putInt(FILM_ID, image_id);
			bundle.putStringArray(IMAGE_ID, array);
			
			Message message=new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}
		
	}
}

