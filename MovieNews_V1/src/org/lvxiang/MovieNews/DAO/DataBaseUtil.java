package org.lvxiang.MovieNews.DAO;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.lvxiang.MovieNews.bean.Movie;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * this is the database utility class for MovieNews. At first this class is designed 
 * with every function bracked by a openDatabase() and closeDatabase() pair. But it
 * turned out that the database doesn't have to be closed each time it is used, what 
 * matters is the management of Cursor. So I decided that a new Object instead of a 
 * overall instance of Cursor will be used each time reading the database.
 * 
 * @author lvxiang
 *
 */
public class DataBaseUtil {

	private Context context;
	private SQLiteDatabase mDatabase;
	private static final String DB_NAME="mn_database.db";
	private static final String TABLE_NAME="mn_main_table";
	private static final String TABLE_ID="_id";
	private static final String MOVIE_NAME="movie_name";
	private static final String MOVIE_DIRECTOR="movie_director";
	private static final String MOVIE_ACTOR="movie_actor";
	private static final String MOVIE_INTRODUCTION="introduction";
	private static final String MOVIE_COVER="movie_cover";
	private static final String COVER_URL="cover_url";
	private static final String CREATE_TABLE="create table " +
											  TABLE_NAME +
											  " ("+TABLE_ID+" INTEGER PRIMARY KEY, " +
											  	   MOVIE_NAME+" TEXT, "+
											       MOVIE_DIRECTOR+" TEXT, "+
											  	   MOVIE_ACTOR+" TEXT, "+
											  	   MOVIE_INTRODUCTION+" TEXT, "+
											  	   MOVIE_COVER+" BLOB, "+
											  	   COVER_URL+" TEXT"+
											  	   ")";

	public DataBaseUtil(Context context){
		this.context=context;
		mDatabase=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		mDatabase.close();
	}
	
	/**
	 * get saved movies in database 
	 * @return a list of Movie instances
	 */
	public List<Movie> getMoviesInDatabase(){
		
		List<Movie> movieList=new ArrayList<Movie>();
		openDatabase();
		Cursor cursor=mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME, null);
		if(cursor.moveToFirst()){
			do{
				
				Movie m=new Movie();
				//get movie id
				m.setID(cursor.getInt(cursor.getColumnIndex(TABLE_ID)));
				//get movie name
				m.setMovieName(cursor.getString(cursor.getColumnIndex(MOVIE_NAME)));
				//get movie director
				m.setDiretor(cursor.getString(cursor.getColumnIndex(MOVIE_DIRECTOR)));
				//get movie actors
				m.setActors(cursor.getString(cursor.getColumnIndex(MOVIE_ACTOR)));
				//get movie introduction
				m.setIntroduction(cursor.getString(cursor.getColumnIndex(MOVIE_INTRODUCTION)));
				//get movie cover
				m.setCover(getBitmapFromDatabase(cursor));

				movieList.add(m);
			}while(cursor.moveToNext());
		}
		cursor.close();
		closeDatabase();
		
		return movieList;
	}
	
	/**
	 * load picture from database with current Cursor
	 * @param cursor
	 * @return
	 */
	private Bitmap getBitmapFromDatabase(Cursor cursor){
		
		openDatabase();
		
		byte[] blob=cursor.getBlob(cursor.getColumnIndex(MOVIE_COVER));
		if(blob!=null){
			return BitmapFactory.decodeByteArray(blob, 0, blob.length);
		}
		
		closeDatabase();
		
		return null;
	}
	
	/**
	 * add a movie to database, the movie is passed in as <br>
	 * a parameter, and information is gathered through its <br>
	 * get() methods. 
	 * @param m The passed Movie instance
	 * @return true if stored successfully
	 */
	public boolean addMovie(Movie m){
		openDatabase();
		ContentValues values=new ContentValues();
		values.put(MOVIE_NAME, m.getMovieName());
		values.put(TABLE_ID, m.getID());
		values.put(MOVIE_INTRODUCTION, m.getDetailHtml());
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		m.getCover().compress(CompressFormat.JPEG, 100, os);
		values.put(MOVIE_COVER, os.toByteArray());
		mDatabase.insert(TABLE_NAME, null, values);
		closeDatabase();
		return true;
	}
	
	/**
	 * delete the specified movie from database
	 * @param id  the id of the movie to be deleted
	 */
	public boolean deleteMovie(int id){
		openDatabase();
		mDatabase.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+TABLE_ID+"="+id);
		closeDatabase();
		return true;
	}
	
	/**
	 * check if there is a movie in database with the same id;
	 * @param id
	 * @return true if exists
	 */
	public boolean contains(int id){
		openDatabase();
		Cursor cursor=mDatabase.rawQuery("select * from "+TABLE_NAME+" where "+TABLE_ID+"=?;", new String[]{""+id});
		if(cursor.moveToFirst()){
			closeDatabase();
			return true;
		}
		cursor.close();
		closeDatabase();
		return false;
	}
	
	/**
	 * application is first started, need to create database and table and make directory
	 * which serves the whole application
	 */
	public void firstStart(){
		mDatabase=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		mDatabase.execSQL(CREATE_TABLE);
		mDatabase.close();
	}

	public void openDatabase(){
		mDatabase=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
	}
	
	public void closeDatabase(){
		
		mDatabase.close();
		Log.d("Movie News", "database closed");
	}

}
