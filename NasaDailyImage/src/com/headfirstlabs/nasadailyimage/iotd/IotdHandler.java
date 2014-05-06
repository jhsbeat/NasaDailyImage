package com.headfirstlabs.nasadailyimage.iotd;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IotdHandler extends DefaultHandler {
	
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
	private boolean inEnclosure = false;
	private boolean inUrl = false;
	private boolean inTitle = false;
	private boolean inDescription = false;
	private boolean inItem = false;
	private boolean inDate = false;
	private String image = null;
	private String title = null;
	private StringBuffer description = new StringBuffer();
	private String date = null;
	
	public void processFeed() {
		try {
		SAXParserFactory factory =
		SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser(); XMLReader reader = parser.getXMLReader(); reader.setContentHandler(this);
		InputStream inputStream = new URL(url).openStream(); 
		reader.parse(new InputSource(inputStream));
		} catch (Exception e) { }
	}
	
	private Bitmap getBitmap(String url) {
		try {
		HttpURLConnection connection =
		(HttpURLConnection)new URL(url).openConnection(); connection.setDoInput(true);
		connection.connect();
		InputStream input = connection.getInputStream(); Bitmap bitmap = BitmapFactory.decodeStream(input); input.close();
		return bitmap;
		} catch (IOException ioe) { return null; } 
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (localName.equals("enclosure")) { 
			image = attributes.getValue("url");
		}  
		if (localName.startsWith("item")) { inItem = true; } else if (inItem) {
		if (localName.equals("title")) { inTitle = true; } else { inTitle = false; }
		if (localName.equals("description")) { 
			inDescription = true;
			description = new StringBuffer(); 
		} else { inDescription = false; }
		if (localName.equals("pubDate")) { inDate = true; }
		else { inDate = false; } }
	}
	
	public void characters(char ch[], int start, int length) {
		String chars = new String(ch).substring(start, start + length); 
		if (inTitle && title == null) { title = chars; }
		if (inDescription) { description.append(chars); }
		if (inDate && date == null) { date = chars; }
	}
	
	public String getImage() { return image; }
	public Bitmap getImageAsBitmap() { return this.getBitmap(image); }
	public String getTitle() { return title; }
	public StringBuffer getDescription() { return description; } 
	public String getDate() { return date; }
	
	
//	private static final String TAG = IotdHandler.class.getSimpleName();
//	
//	private boolean inTitle = false;
//	private boolean inDescription = false;
//	private boolean inItem = false;
//	private boolean inDate = false;
//	
//	private String url = null;
//	private StringBuffer title = new StringBuffer();
//	private StringBuffer description = new StringBuffer();
//	private String date = null;
//	
//	private IotdHandlerListener listener;
//	
//	public void startElement(String uri, String localName, String qName,
//			Attributes attributes) throws SAXException {
//		
//		if (localName.equals("enclosure")) { 
//			url = attributes.getValue("url");
//		}  
//		
//		if (localName.startsWith("item")) { 
//			inItem = true;
//		} else { 
//			if (inItem) { 
//				if (localName.equals("title")) { 
//					inTitle = true;
//				} else { 
//					inTitle = false;
//				}
//				
//				if (localName.equals("description")) { 
//					inDescription = true;
//				} else { 
//					inDescription = false;
//				}
//				
//				if (localName.equals("pubDate")) { 
//					inDate = true;
//				} else { 
//					inDate = false;
//				}
//			}
//		}
//		
//	}
//	
//	 public void characters(char ch[], int start, int length) {
//		 String chars = (new String(ch).substring(start, start + length));
//		 
//		 if (inTitle) { 
//			 title.append(chars);
//		 }
//		 
//		 if (inDescription) { 
//			 description.append(chars);
//		 }
//		 
//		 if (inDate && date == null) {
//			 //Example: Tue, 21 Dec 2010 00:00:00 EST
//			 String rawDate = chars;
//			 try { 
//				 SimpleDateFormat parseFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
//				 Date sourceDate = parseFormat.parse(rawDate);
//				 
//				 SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
//				 date = outputFormat.format(sourceDate);
//			 } catch (Exception e) { 
//				 e.printStackTrace();
//			 }
//		 }
//		 
//	 }
//	
//	public void processFeed(Context context, URL url) {
//        try {
//        	
//            SAXParserFactory spf = SAXParserFactory.newInstance();
//            SAXParser sp = spf.newSAXParser();
//            XMLReader xr = sp.getXMLReader();
//            xr.setContentHandler(this);
//            xr.parse(new InputSource(url.openStream()));
//               
//        } catch (IOException e) {
//            Log.e("", e.toString());
//        } catch (SAXException e) {
//            Log.e("", e.toString());
//        } catch (ParserConfigurationException e) {
//            Log.e("", e.toString());
//        }
//	}
//	
//	public void endElement(String uri, String localName, String qName) {
//		if (url != null && title != null && description != null && date != null) { 
//			 if (listener != null) { 
//				 listener.iotdParsed(url, title.toString(), description.toString(), date);
//				 listener = null;
//			 }
//		 }
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public String getTitle() {
//		return title.toString();
//	}
//
//	public String getDescription() {
//		return description.toString();
//	}
//
//	public String getDate() {
//		return date;
//	}
//	
//	
//	public IotdHandlerListener getListener() { 
//		return listener; 
//	}
//
//	public void setListener(IotdHandlerListener listener) {
//		this.listener = listener;
//	}
	
	
}
