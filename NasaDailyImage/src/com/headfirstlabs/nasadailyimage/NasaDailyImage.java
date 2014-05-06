package com.headfirstlabs.nasadailyimage;

import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.headfirstlabs.nasadailyimage.iotd.IotdHandler;

public class NasaDailyImage extends ActionBarActivity {
	
	Handler handler;
	IotdHandler iotdHandler;
	ProgressDialog dialog;
	Bitmap image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nasa_daily_image);
		handler = new Handler();
		refreshFromFeed();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onRefresh(View view){
		refreshFromFeed();
	}
	
	public void onSetWallpaper(View view){
		Thread th = new Thread(){

			@Override
			public void run() {
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(NasaDailyImage.this);
				try {
					wallpaperManager.setBitmap(image);
					handler.post(new Runnable(){
						@Override
						public void run() {
							Toast.makeText(NasaDailyImage.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(new Runnable(){
						@Override
						public void run() {
							Toast.makeText(NasaDailyImage.this, "Error setting wallpaper", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
			
		};
		th.start();
		
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_nasa_daily_image, container, false);
			return rootView;
		}
	}
	
	private void refreshFromFeed() {
		
		dialog = ProgressDialog.show(this, "Loading", "Loading the image of the Day");
		
		
		Thread th = new Thread() { public void run() {
			if (iotdHandler == null) {
				iotdHandler = new IotdHandler(); 
			}
			iotdHandler.processFeed();
			InputStream is;
			try {
				is = getAssets().open(iotdHandler.getImage());
				image = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			handler.post(new Runnable(){

				@Override
				public void run() {
					resetDisplay(iotdHandler.getTitle(), iotdHandler.getDate(), iotdHandler.getImage(), iotdHandler.getDescription().toString());
					dialog.dismiss();
				}
				
			});
			
		}};
		th.start();
		
	}
	
	private void resetDisplay(String title, String date, String imageUrl, String description) {

		TextView titleView = (TextView)findViewById(R.id.imageTitle);
		titleView.setText(title);

		TextView dateView = (TextView)findViewById(R.id.imageDate);
		dateView.setText(date);

		
		ImageView imageView = (ImageView)findViewById(R.id.imageDisplay);
		InputStream is;
		try {
			is = getAssets().open(imageUrl);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			imageView.setImageBitmap(bitmap);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView descriptionView = (TextView)findViewById(R.id.imageDescription);
		descriptionView.setText(description);
	}

}
