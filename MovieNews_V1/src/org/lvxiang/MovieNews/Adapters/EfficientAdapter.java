package org.lvxiang.MovieNews.Adapters;

import java.util.List;

import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


public class EfficientAdapter extends BaseAdapter{

	private List<Movie> movieList;
	private LayoutInflater mInflater;
	private Context context;
	
	public EfficientAdapter(Context context,List<Movie> list){
		this.movieList=list;
		mInflater=LayoutInflater.from(context);
		this.context=context;
	}
	

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
    	if(movieList!=null)
        return movieList.size();
    	
    	return 0;
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder = null;
        Log.i("position"+position+":", movieList.get(position).getHtml());
        // When convertView is not null, we can reuse it directly, there is no need
        // to re-inflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
        	holder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_icon_text, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder.imgView = (WebView)convertView.findViewById(R.id.textView1);
            holder.textView= (WebView)convertView.findViewById(R.id.textView2);
            holder.ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar1);
            holder.ratingText=(TextView)convertView.findViewById(R.id.ratingText);
            
            holder.imgView.setFocusable(false);
            holder.imgView.setEnabled(false);
            holder.imgView.setFocusable(false);
            
            holder.textView.setFocusable(false);
            holder.textView.setEnabled(false);
            holder.textView.setFocusable(false);
            
            holder.ratingBar.setEnabled(false);
            holder.ratingBar.setFocusable(false);
            holder.ratingText.setFocusable(false);
            holder.ratingText.setEnabled(false);
            
            holder.imgView.setBackgroundColor(Color.TRANSPARENT);
            holder.ratingBar.setBackgroundColor(Color.TRANSPARENT);
            holder.ratingText.setBackgroundColor(Color.TRANSPARENT);
            holder.textView.setBackgroundColor(Color.TRANSPARENT);
            holder.ratingBar.setNumStars(5);
            holder.ratingText.setTextSize(16);
            holder.ratingText.setTextColor(Color.GREEN);
            convertView.setTag(holder);
            
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.imgView.loadDataWithBaseURL(null,
        		movieList.get(position).getImgHtml(),
        		context.getResources().getString(R.string.MIME_TYPE),
        		context.getResources().getString(R.string.ENCODING),
        		null);
        holder.textView.loadDataWithBaseURL(null,
        		movieList.get(position).getInfoHtml(),
        		context.getResources().getString(R.string.MIME_TYPE),
        		context.getResources().getString(R.string.ENCODING),
        		null);
        holder.ratingBar.setRating((float) (Float.parseFloat(movieList.get(position).getDoubanRank())*0.5));
        holder.ratingText.setText(movieList.get(position).getDoubanRank());
        return convertView;
    }


	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(movieList!=null){
			return movieList.get(position);
		}
		return null;
	}
    
	private class ViewHolder{
		public WebView imgView;
		public WebView textView;
		public RatingBar ratingBar;
		public TextView ratingText;
	}
	
}
