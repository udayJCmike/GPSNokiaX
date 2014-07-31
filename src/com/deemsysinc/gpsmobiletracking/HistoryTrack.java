package com.deemsysinc.gpsmobiletracking;




import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



@SuppressLint("SimpleDateFormat")
public class HistoryTrack  extends Activity {
	    private int year;
	    private int month;
	    private int day;
	    String checkdate;
	    String vehicle_reg_numb;
	    static final int DATE_PICKER_ID = 1111; 
	    StringBuilder date;
	    Boolean isInternetPresent = false;
		ConnectionDetector cd;
		public ProgressDialog cDialog;
		JsonParser jsonParser = new JsonParser();
		JSONObject jArray;
		JSONArray user = null;
		private GoogleMap googleMap;
		TextView welcomeusername;
		Button signout,hmey;
		ToggleButton tgbutton;
		final Context context=this;
		public static ArrayList<HashMap<String, String>> vehiclehistory1= new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, Double> map1 = new HashMap<String, Double>();
		private static final String TAG_SRES= "serviceresponse";
		private static final String TAG_VEHICLE_ARRAY = "VehicleHistory List";
		 static final String TAG_Vechicle_REG= "vechicle_reg_no";
		private static final String TAG_Latitude= "latitude";
		private static final String TAG_Longitude= "longitude";
		private static final String TAG_Speed= "speed";
		private static final String TAG_Exceed_Speed= "exceed_speed_limit";
		private static final String TAG_bus_tracking_timestamp= "bus_tracking_timestamp";
		private static final String TAG_address= "address";
		
		static final LatLng TutorialsPoint = new LatLng(22.3512639,78.9542827);
		String orgid,vehicle_reg_no,speed,exceed_speed_limit,bus_tracking_timestamp,address;
		String latitude;
		String longitude;
		double latitude1;
		double longitude1;
		//private static String vehiclehistorysurll = "http://192.168.1.158:8888/gpsandroid/service/HistoryTrack.php?service=vehiclehistory"; 
		//private static String vehiclehistorysurll = "http://192.168.1.71:8080/gpsandroid/service/HistoryTrack.php?service=vehiclehistory"; 
	private static String vehiclehistorysurll = "http://208.109.248.89:80/gpsandroid/service/HistoryTrack.php?service=vehiclehistory"; 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.historytrack);
	      signout=(Button)findViewById(R.id.signoutty);
	     
	        ActionBar actions = getActionBar();
	        actions.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0a7dbc")));
	        actions.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        actions.setDisplayShowTitleEnabled(false);
	        SpinnerAdapter adapter = ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.nav_drawer_items1,
	                android.R.layout.simple_spinner_dropdown_item);

	        // Callback
	        OnNavigationListener callback = new OnNavigationListener() {

	            String[] items = getResources().getStringArray(R.array.nav_drawer_items1); // List items from res

	            @Override
			    public boolean onNavigationItemSelected(int itemPosition, long id) {

			        // Do stuff when navigation item is selected
System.out.println("item position value"+itemPosition);
			        //Log.d("NavigationItemSelected", items[position]); // Debug
			        Intent myIntent;
			        if(itemPosition!=0){
			            if(itemPosition == 0){ //Activity#1 Selected
			            	LiveTrack.timer.cancel();
					    	LiveTrack.doAsynchronousTask.cancel();
			                myIntent = new Intent(HistoryTrack.this, HistoryTrack.class);
			                HistoryTrack.this.startActivity(myIntent);
			            } else if (itemPosition == 1){ //Activity#2 Selected
			            	LiveTrack.timer.cancel();
					    	LiveTrack.doAsynchronousTask.cancel();
					    	  myIntent = new Intent(HistoryTrack.this, LiveTrack.class);
					    	myIntent.putExtra("vehicleregnum", LiveTrack.vehicle_reg_no);
					    	myIntent.putExtra("routenum", LiveTrack.routeno);
			                HistoryTrack.this.startActivity(myIntent);
			            } else if (itemPosition == 2){ //Activity#3 Selected
			            	LiveTrack.timer.cancel();
					    	LiveTrack.doAsynchronousTask.cancel();
			                myIntent = new Intent(HistoryTrack.this, AlertMsg.class);
			                HistoryTrack.this.startActivity(myIntent);
			            }
			            else if (itemPosition == 3){ //Activity#3 Selected
			            	LiveTrack.timer.cancel();
					    	LiveTrack.doAsynchronousTask.cancel();
			                myIntent = new Intent(HistoryTrack.this, DashboardActivity.class);
			                HistoryTrack.this.startActivity(myIntent);
			            }
			          
			        }
			        else
			        {
			        	
			        }
			        return true;

			    }


	        };
	        actions.setListNavigationCallbacks(adapter, callback);
	    
			welcomeusername=(TextView)findViewById(R.id.welcomename);
			welcomeusername.setText(LoginActivity.usernamepassed+"!");
	      try { 
	            if (googleMap == null) {
	            
	               googleMap = ((MapFragment) getFragmentManager().
	               findFragmentById(R.id.map)).getMap();
	            }
	         googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	         Marker marker = googleMap.addMarker(new MarkerOptions().
	    	         position(TutorialsPoint).title(""));
	         CameraPosition cameraPosition = new CameraPosition.Builder().target(
		    		  TutorialsPoint).zoom(4).build();
	         googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	         marker.remove();
	         marker.setVisible(false);
	       

	         googleMap.getUiSettings().setZoomGesturesEnabled(true);
	         googleMap.getUiSettings().setRotateGesturesEnabled(true);
	         googleMap.getUiSettings().setCompassEnabled(true);

	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      tgbutton = (ToggleButton) findViewById(R.id.showmapdif);
	      tgbutton.setSelected(true);
	        tgbutton.setOnClickListener(new OnClickListener() {
	 
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                 if (!tgbutton.isChecked()) {
	                	 try { 
	         	            if (googleMap == null) {
	         	               googleMap = ((MapFragment) getFragmentManager().
	         	               findFragmentById(R.id.map)).getMap();
	         	            }
	         	         googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	         	        tgbutton.setBackgroundResource(R.drawable.earth);
	         	    

	         	      } catch (Exception e) {
	         	         e.printStackTrace();
	         	      }
	                       
	                    } else {
	                    	 try { 
	             	            if (googleMap == null) {
	             	               googleMap = ((MapFragment) getFragmentManager().
	             	               findFragmentById(R.id.map)).getMap();
	             	            }
	             	         googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	             	        tgbutton.setBackgroundResource(R.drawable.aerial);
	             	   
	             	      } catch (Exception e) {
	             	         e.printStackTrace();
	             	      }
	                       
	                    }
	            }
	        });
	 
	      vehicle_reg_numb=LiveTrack.vehicle_reg_no;
	      System.out.println("history track veh numb"+vehicle_reg_numb);
	      cd = new ConnectionDetector(getApplicationContext());
			 isInternetPresent = cd.isConnectingToInternet();
	      
	        final Calendar c = Calendar.getInstance();
	        year  = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH);
	        day   = c.get(Calendar.DAY_OF_MONTH);
	       
	       System.out.println("in history track:::::::");
	   
	       signout.setOnClickListener(new View.OnClickListener() {
				
	            
	        	public void onClick(View v) { 
	        		LoginActivity.usernamepassed="";
	       VehichleArrayAdapter.data.clear();
	       DashboardActivity.vehicleall.clear();
	       vehiclehistory1.clear();
	       LiveTrack.doAsynchronousTask.cancel();
	       HistoryTrack.vehiclehistory1.clear();
	       LoginActivity.usernamepassed="";
	        		Intent intentSignUP=new Intent(getApplicationContext(),LoginActivity.class);
  			startActivity(intentSignUP);
	        	}
		 });
	      
	    }
	  
	  @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case DATE_PICKER_ID:
	             
	            
	            return new DatePickerDialog(this, pickerListener, year, month,day);
	        }
	        return null;
	    }
	 
	    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
	 
	     
	        @Override
	        public void onDateSet(DatePicker view, int selectedYear,
	                int selectedMonth, int selectedDay) {
	        	//int noOfTimesCalled = 0;
	        	 if(view.isShown()) {
	            year  = selectedYear;
	            month = selectedMonth;
	            day   = selectedDay;
	          
	            String date1=year+"-"+checkDigit(month+1)+"-"+checkDigit(day);
	          checkdate=date1.toString();
	          if (isInternetPresent)
              {
			        new VehiclePath().execute(); 
		      }
	          
	        	 }
	        } 
	    
	        };
	        public String checkDigit(int number)
	        {
	            return number<=9?"0"+number:String.valueOf(number);
	        }        
  class VehiclePath extends AsyncTask<String,String,String>{
	    		@Override
	    	    protected void onPreExecute() {
	    			  cDialog = new ProgressDialog(HistoryTrack.this);
	    	          cDialog.setMessage("Please wait...");
	    	          cDialog.setIndeterminate(false);
	    	          cDialog.setCancelable(false);
	    	          cDialog.show();
	    		}
	    			
	    			

	    			@Override
	    			protected String doInBackground(String... args) {
	    				// TODO Auto-generated method stub
	    				
	    				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
	    				 ArrayList<HashMap<String,String>> vehiclehistory= new ArrayList<HashMap<String,String>>();
	    	             params1.add(new BasicNameValuePair("org_id", LoginActivity.orgid));
	    	             params1.add(new BasicNameValuePair("vechicle_reg_no", vehicle_reg_numb));
	    	             params1.add(new BasicNameValuePair("date",checkdate ));
	    	             System.out.println("vehicle ddfgate no.fdfsd ."+checkdate);
	    	            // params1.add(new BasicNameValuePair("org_id", LoginActivity.orgid));
	    	           
	    	             jArray = jsonParser.makeHttpRequest(vehiclehistorysurll, "POST", params1);
	    			
	    			    //Log.i("tagconvertstr", "["+jArray+"]");
	    			    
	    			    try
	    			    {
	    			    	if(jArray != null){
	    			    	
	    			    	JSONObject c = jArray.getJSONObject(TAG_SRES);
	    			    	//Log.i("tagconvertstr", "["+c+"]");
	    			    	user = c.getJSONArray(TAG_VEHICLE_ARRAY);
	    			    	Log.i("tagconvertstr1", "["+user+"]");
	    			    	
	    			    	for(int i=0;i<user.length();i++)
	    			    	{
	    			    		System.out.println("forloop i valuie"+i);
	    			    		JSONObject c1 = user.getJSONObject(i);
	    			    		JSONObject c2 = c1.getJSONObject(TAG_SRES);
	    			    		
	    			    	  
	    			    		
	    			           
	    			            vehicle_reg_numb = c2.getString(TAG_Vechicle_REG);
	    			            latitude= c2.getString(TAG_Latitude);
	    			        	 longitude = c2.getString(TAG_Longitude);
	    			        	speed = c2.getString(TAG_Speed);
	    			        	exceed_speed_limit=c2.getString(TAG_Exceed_Speed);
	    			        	bus_tracking_timestamp = c2.getString(TAG_bus_tracking_timestamp);
	    			        	address=c2.getString(TAG_address);
	    			        	 map.put(TAG_Latitude+i,latitude);
	    			        	 map.put(TAG_Longitude+i,longitude);
	    			        	 map.put(TAG_Speed+i, speed);
	    			        	 map.put(TAG_address+i, address);
	    			        	 map.put(TAG_bus_tracking_timestamp+i, bus_tracking_timestamp);
	    			        	
	    			        	
	    			        	vehiclehistory.add(i,map);
	    			        	System.out.println("map values"+map);
	    			    		System.out.println("Values for vehiclehistory list"+vehiclehistory.get(i));
	    			    		 System.out.println("size of arraylist::"+vehiclehistory.size());
	    			    		
	    			    	}
	    			    	
	    			    	}
	    			    	
	    			    	}catch (JSONException e) {
	    			        e.printStackTrace();
	    			    }
	    			    vehiclehistory1=vehiclehistory;
	    			    cDialog.dismiss();
	    				return null;
	    			}
	    			@SuppressWarnings("deprecation")
					@Override
	    			protected void onPostExecute(String file_url) {
	    		   
	    				 super.onPostExecute(file_url);
	    				 cDialog.dismiss();
	    				 
	    				 ArrayList<LatLng> points = null;
	    			      PolylineOptions polyLineOptions = null;
	    			      points = new ArrayList<LatLng>();
	    			        polyLineOptions = new PolylineOptions();
	    			        if(vehiclehistory1.size()==0)
	        				{
	    			        	AlertDialog alertDialog = new AlertDialog.Builder(
	    								HistoryTrack.this).create();

	    						// Setting Dialog Title
	    						alertDialog.setTitle("INFO!");

	    						// Setting Dialog Message
	    						alertDialog.setMessage("No location's found.");

	    						// Setting Icon to Dialog
	    						alertDialog.setIcon(R.drawable.delete);
	    						

	    						// Setting OK Button
	    						alertDialog.setButton("OK",	new DialogInterface.OnClickListener() {

	    									public void onClick(final DialogInterface dialog,
	    											final int which) {
	    										// Write your code here to execute after dialog
	    										// closed
	    										
	    									}
	    								});

	    						// Showing Alert Message
	    						alertDialog.show();
	    			        	/*  AlertDialog.Builder builder= new AlertDialog.Builder(HistoryTrack.this,R.style.MyTheme );
	    		    		        
	    		    	            builder.setMessage("No location found." )
	    		    	                .setTitle( "INFO!" )
	    		    	                .setIcon( R.drawable.pink_pin )
	    		    	                .setCancelable( false )
	    		    	             
	    		    	                .setPositiveButton( "OK", new DialogInterface.OnClickListener()
	    		    	                    {
	    		    	                        public void onClick( DialogInterface dialog, int which )
	    		    	                           {
	    		    	                        	
	    		    	                                dialog.dismiss();
	    		    	                           }
	    		    	                        } 
	    		    	                    );
	    		    	            Dialog dialog = null;
	    		    	            builder.setInverseBackgroundForced(true);
	    		    	            
	    		    	            dialog = builder.create();
	    		    	            dialog.getWindow().setLayout(600, 400); 
	    		    	            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    		    				dialog.show();
	    			  			 */
	       	       				
	        					
	        				}
	    			        else
	    			        {
	    				 for (int k = 0; k < vehiclehistory1.size(); k++) {
	    					 System.out.println("k value"+k);
	    					 LatLng pinLocation = new LatLng(Double.parseDouble(vehiclehistory1.get(k).get(TAG_Latitude+k)), Double.parseDouble(vehiclehistory1.get(k).get(TAG_Longitude+k)));
	    					 System.out.println("pin location"+pinLocation);
	    					 points.add(pinLocation);
	    					 String titlevalue="Speed:"+vehiclehistory1.get(k).get(TAG_Speed+k)+"km/hr "+"Date:"+vehiclehistory1.get(k).get(TAG_bus_tracking_timestamp+k);
	    					 String snippetval="Address:"+vehiclehistory1.get(k).get(TAG_address+k);
	    					 String date="Date:"+vehiclehistory1.get(k).get(TAG_bus_tracking_timestamp+k);
	    					// String titlevalue=vehiclehistory1.get(k).get(TAG_address+k)+vehiclehistory1.get(k).get(TAG_Speed+k);
	    					 MarkerOptions marker = new MarkerOptions().position(pinLocation).title(titlevalue).snippet(snippetval);
       					  marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pin));
       				      googleMap.addMarker(marker);
	    					
	    					 System.out.println("titlevalue of respective pinlocation"+titlevalue);
	    					 int sizeminusone=vehiclehistory1.size()-1;
	    					 if(sizeminusone==k)
	    					 {
	    						 
	    					  CameraPosition cameraPosition = new CameraPosition.Builder().target(pinLocation).zoom(12).build();
	    					  googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    					 }
	    					
	    				 }
	    				 polyLineOptions.addAll(points);
 				        polyLineOptions.width(2);
 				        polyLineOptions.color(Color.BLACK);
	    				 googleMap.addPolyline(polyLineOptions);
	    			        }
	    			
	    		} 	
  		
  }
  @Override
  protected void onDestroy()
  {

  super.onDestroy();  
  moveTaskToBack(true);
  }   		
  @Override

    public void onLowMemory() {

	  moveTaskToBack(true);
//       googleMap.onLowMemory();

    }
  private void initilizeMap() {
      if (googleMap == null) {
          googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                  R.id.map)).getMap();
          Marker marker = googleMap.addMarker(new MarkerOptions().
	    	         position(TutorialsPoint).title(""));
	         CameraPosition cameraPosition = new CameraPosition.Builder().target(
		    		  TutorialsPoint).zoom(4).build();
	         googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	         marker.remove();
	         marker.setVisible(false);
          // check if map is created successfully or not
          if (googleMap == null) {
              Toast.makeText(getApplicationContext(),
                      "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                      .show();
          }
      }
  }
  @SuppressWarnings("deprecation")
@Override
  protected void onResume() {
      super.onResume();
    
      System.out.println("in on resume ");
      initilizeMap();
      showDialog(DATE_PICKER_ID);
  }
  @Override
  public void onBackPressed() {
  }


}
