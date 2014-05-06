package com.headfirstlabs.nasadailyimage;

import java.util.List;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
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
	int randomIndex = 0;

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
					if(image == null){
						handler.post(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(NasaDailyImage.this, "Image is NULL!", Toast.LENGTH_SHORT).show();
							}
						});
						return;
					}else{
						wallpaperManager.setBitmap(image);
					}
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
			randomIndex = (int)(Math.random() * iotdHandler.getItemSize());
			image = iotdHandler.getImageAsBitmap(randomIndex);
			handler.post(new Runnable(){

				@Override
				public void run() {
					resetDisplayRandom(iotdHandler.getTitleList(), iotdHandler.getDateList(), iotdHandler.getDescriptionList());
					dialog.dismiss();
				}
				
			});
			
		}};
		th.start();
		
	}
	
	private void resetDisplayRandom(List<StringBuffer> titleList, List<StringBuffer> dateList, List<StringBuffer> descriptionList) {
		resetDisplay(titleList.get(randomIndex).toString(), dateList.get(randomIndex).toString(), image, descriptionList.get(randomIndex).toString());
	}
	
	private void resetDisplay(String title, String date, Bitmap image, String description) {

		TextView titleView = (TextView)findViewById(R.id.imageTitle);
		titleView.setText(title);

		TextView dateView = (TextView)findViewById(R.id.imageDate);
		dateView.setText(date);

		
		ImageView imageView = (ImageView)findViewById(R.id.imageDisplay);
//		InputStream is;
//		try {
//			is = getAssets().open(imageUrl);
//			Bitmap bitmap = BitmapFactory.decodeStream(is);
			imageView.setImageBitmap(image);
//			is.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		TextView descriptionView = (TextView)findViewById(R.id.imageDescription);
		descriptionView.setText(description);
	}

}
