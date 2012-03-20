package org.lvxiang.MovieNews.Activities;


import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class MovieNewsActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout=new LinearLayout(this);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        WebView web=new WebView(this);
       
        layout.addView(web);
        setContentView(layout); 
        
        //String data=HtmlUtil.getIntroduction("http://movie.douban.com/");
        //web.loadDataWithBaseURL(null, data, mimeType, encoding,null);
    }

	
}