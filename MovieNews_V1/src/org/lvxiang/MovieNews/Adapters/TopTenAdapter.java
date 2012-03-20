package org.lvxiang.MovieNews.Adapters;

import java.util.List;

import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;


import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopTenAdapter extends BaseAdapter{

	private List<Movie> movieList;
	private Context context;
	private LayoutInflater inflater;
	
	private static final int[] res={R.drawable.rank_one,R.drawable.rank_two,R.drawable.rank_three,
									R.drawable.rank_four,R.drawable.rank_five,R.drawable.rank_six,
									R.drawable.rank_seven,R.drawable.rank_eight,R.drawable.rank_nine,
									R.drawable.rank_ten};
	
	public TopTenAdapter(Context context,List<Movie> list){
		this.context=context;
		this.movieList=list;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		inflater=LayoutInflater.from(context);
		ViewHolder holder=null;
		
		if(convertView==null){
			
			holder=new ViewHolder();
			
			convertView=inflater.inflate(R.layout.top_ten_item_layout, null);
			holder.img=(ImageView)convertView.findViewById(R.id.top_ten_imageView);
			holder.text=(TextView)convertView.findViewById(R.id.top_ten_textView);
			holder.text.setBackgroundResource(R.drawable.item_bg);
			holder.text.setTextColor(Color.BLACK);
			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.img.setBackgroundResource(res[position]);
		holder.text.setText(Html.fromHtml(getHtml(movieList.get(position))));
		
		return convertView;
	}

	private class ViewHolder{
		public ImageView img;
		public TextView  text;
	}
	
	private String getHtml(Movie m){
		
		String html="<b><font size='25px'>";
		html+=m.getMovieName();
		html+="</font></b><br>";
		
		html+="<i><font size='12px'>";
		html+=context.getResources().getString(R.string.WEEKLY)+m.getWeeklyBox();
		html+="</font></i><br>";
		
		html+="<i><font size='12px'>";
		html+=context.getResources().getString(R.string.TOTAL)+m.getTotalBox();
		html+="</font></i>";
		
		return html;
	}
	
}
