package weibo_dianping;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControlWindow;
import controlP5.Textarea;
import controlP5.Textfield;

import codeanticode.glgraphics.GLConstants;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;
import processing.core.PFont;

public class getGeometriesFromMap extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	UnfoldingMap map;
	Location L;//location of screenPosition
	SimplePointMarker pM = new SimplePointMarker();
	SimpleLinesMarker lM = new SimpleLinesMarker();
	MarkerManager markerManager = new MarkerManager();
	ArrayList<Location> past_L = new ArrayList<Location>();
	int count;
    double[] lat,lon;
	Location locationGeos;
	ScreenPosition posGeos;
	SimplePointMarker shopMarker;
	ResultSet rs;
	
	//ControlP5
	ControlP5 cp5;
	ControlWindow controlWindow;
	Textarea textarea;
	String roadName;
	String record="";
	
	String[] lineName = new String[45];
    public void setup(){
		//unfolding map
		  size(1366,730);	
		  //OpenStreetMap.OpenStreetMapProvider();
		  //OpenStreetMap.CloudmadeProvider(API KEY, STYLE ID);
		  //Stamen.TonerProvider();
		  //Google.GoogleMapProvider();
		  //Google.GoogleTerrainProvider();
		  //Microsoft.RoadProvider();
		  //Microsoft.AerialProvider();
		  //Yahoo.RoadProvider();
		  //Yahoo.HybridProvider();
		  map = new de.fhpotsdam.unfolding.Map(this,new OpenStreetMap.OpenStreetMapProvider());
		  map.setTweening(false);
		  map.zoomToLevel(13);
		  map.panTo(new Location(31.244417,121.457326));
		  MapUtils.createDefaultEventDispatcher(this, map);

		  
          map.draw();
          initializeLineName();
	      //loadDatas();	  
	      controlP5_settings();
   
          
    }//setup  
    
    public void draw(){
	    background(0);
	    map.draw();	
    }//draw
    
    public void controlP5_settings(){
    	  cp5 = new ControlP5(this);

    	  controlWindow = cp5.addControlWindow("controlP5window", 100, 100, 400, 400)
    	                     .hideCoordinates()
    	                     .setBackground(color(40));
    	  
    	  PFont font = createFont("arial",20);
    	  
    	  
    	  cp5.addTextfield("Input Name")
    	     .setPosition(10,10)
    	     .setSize(200,40)
    	     .setFont(createFont("arial",20))
    	     .setAutoClear(false)
    	     .setMoveable(false)
    	     .moveTo(controlWindow)
    	     ;
    	  
    	  cp5.addButton("Save")
    	     //.setValue(0)
    	     .setPosition(230,10)
    	     .setSize(60,40)
    	     .moveTo(controlWindow)
    	     .setMoveable(false)
    	     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
    	     ;
    	  
    	  textarea = cp5.addTextarea("txt")
    			  .moveTo(controlWindow)
                  .setPosition(10,90)
                  .setSize(380,200)
                  .setFont(createFont("arial",12))
                  .setLineHeight(12)
                  .setColor(color(128))
                  .setMoveable(false)
                  .setColorBackground(color(255,100))
                  .setColorForeground(color(255,100));
    	          
        
    	  
    	  textFont(font);
    	  
    	  
    }//controlP5_settings()

    public void Save(){
	    //SQL(roadName);
    	past_L.clear();
    	lM = new SimpleLinesMarker();
	    textarea.setText(" Saving done. \n the stack has been clean");
    }
    public void controlEvent(ControlEvent theEvent) {
    	  if(theEvent.isAssignableFrom(Textfield.class)) {
    		roadName = theEvent.getStringValue();  		
    		textarea.setText(" the roadName has been changed to '" + roadName +"'" );
    	  }
    	}
    
    public void loadDatas(){
    	try 
        {
		    
           Class.forName( "org.postgresql.Driver" ).newInstance();
           String url = "jdbc:postgresql://localhost:5432/weibo_dianping" ;
           Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
           Statement st = con.createStatement();
           String sql="";
           
           int j;
            
           for (int i=0;i<=33; i++){
               sql = "select lat, lon from " + lineName[i] + ";";
               if (i==34) sql = "select lat ,lon from tmpRd2";
               rs = st.executeQuery(sql);
               lat = new double[2000000];
               lon = new double[2000000];
               count = 0;
               while (rs.next()){    
              		 lat[count] = rs.getDouble("lat");
                     lon[count] = rs.getDouble("lon");
              	
				     locationGeos = new Location(lat[count], lon[count]);
				     posGeos = map.getScreenPosition(locationGeos);
				     shopMarker = new SimplePointMarker(locationGeos);
				     shopMarker.setColor(color(255,0,0));
				  
				    
				     //shopMarker.setStrokeColor(color(255, 0, 0));
				     shopMarker.setStrokeWeight(0);
				     shopMarker.setRadius(5);
				     if (i>=34 && i<=44) {
				    	 shopMarker.setRadius(7); 
				    	 shopMarker.setColor(color(255,0,0));
				     }
				     if (i<34){
				    	 shopMarker.setColor(color(0,0,0));
					     shopMarker.setRadius(3);
				     }
				    	  
				     map.addMarkers(shopMarker);
				
                   count++;
                }// while 
           }//for 
           
           
           
           
	         System.out.println("Set up done");
	         
	         
	        
  	              
           rs.close();
           st.close();
           con.close();
        }catch (Exception ee){
           System.out.print(ee.getMessage());
        } 
    }//loadDatas

	public void keyPressed(){
	
		//if (!theEvent.isFrom("ControlP5window")){		
	    if (this.focused){
		  if (key == 'a' || key == 'A'){
			    System.out.println("ADD POI");

			    L=new Location(map.getLocationFromScreenPosition((float)(mouseX), (float)(mouseY)));
			    lM.addLocations(L);
			    lM.setColor(color(255,0,0));
		        past_L.add(L);
			  
		    }//end if
		    if ((key == 'd' || key == 'D') && (past_L.size()>0)){
		    	System.out.println("DELETE POI");
                lM.removeLocation(past_L.get(past_L.size()-1));
                past_L.remove(past_L.size()-1);            
		    }//end if
	    	if (key == 'c' || key == 'C'){
		    	past_L.clear();
		    	markerManager.removeMarker(lM);
		    	lM = new SimpleLinesMarker();
		    	
		    	System.out.println("CLEAR POI");
		    }
	    	if (key == 'p' || key == 'P'){
		    	for (int i=0; i<=past_L.size()-1; i++){
		    		float a =past_L.get(i).getLat();
		    		float b =past_L.get(i).getLon();
		    		System.out.println("Location(" + a + "," + b +")");
		    	}
		    }
	    	if (key=='q' || key =='Q'){
	    		L=new Location(map.getLocationFromScreenPosition((float)(mouseX), (float)(mouseY)));
	    		System.out.println("lat: "+ L.getLat()+"    lon: "+ L.getLon()+ "mouseX: " +mouseX + "    mouseY: "+ mouseY);
	    	}
	    	markerManager.addMarker(lM);
		    map.addMarkerManager(markerManager);
		    //map.addMarkers(pM);
		}//if
	}//keyPressed
	
	public void mousePressed(){
		
	}
	public void SQL(String currentTable){

		   try{
		      //initialize
			  Class.forName( "org.postgresql.Driver" ).newInstance();
		      String url = "jdbc:postgresql://localhost:5432/weibo_dianping" ;
	          Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
	         
	          
	          //create table
	         
	          PreparedStatement createTable = con.prepareStatement("create table "+currentTable+" (lat double precision, lon double precision);");
	          //createTable.setString(1,currentTable);
	          createTable.executeUpdate();
	          createTable.close(); 
	          
	          //insert geos
	          for (int i=0; i<=past_L.size()-1; i++){
	             PreparedStatement insertGeos = con.prepareStatement("insert into "+currentTable+" values(?,?);");
	             //insertGeos.setString(1, currentTable);
	             insertGeos.setDouble(1, past_L.get(i).getLat());
	             insertGeos.setDouble(2, past_L.get(i).getLon());
	             insertGeos.executeUpdate();
	             insertGeos.close();
	          }
	          
	  		  System.out.println(currentTable+" completed");
	          con.close();
		   }catch(Exception ee){
		      System.out.print(ee.getMessage());
		   }finally{
			 
	           
		   }
    }// end SQL
	
    public void initializeLineName(){
    	lineName[0]="changjiangxi_road";
    	lineName[1]="changzhong_rd";
    	lineName[2]="huangxing_road";
    	lineName[3]="hujia_expy";
    	lineName[4]="huning_expy";
    	lineName[5]="hutai_raod";
    	lineName[6]="jinhai_road";
    	lineName[7]="jinqiu_road";
    	lineName[8]="jufeng_road";
    	lineName[9]="jungong_road";
    	lineName[10]="longdong_road";
    	lineName[11]="longwu_road";
    	lineName[12]="middle_ring_rd";
    	lineName[13]="outer_ring_rd";
    	lineName[14]="pudongbei_road";
    	lineName[15]="shenjiang_road";
    	lineName[16]="subwayline01";
    	lineName[17]="subwayline02";
    	lineName[18]="subwayline03";
    	lineName[19]="subwayline04";
    	lineName[20]="subwayline05";
    	lineName[21]="subwayline06";
    	lineName[22]="subwayline07";
    	lineName[23]="subwayline08";
    	lineName[24]="subwayline09";
    	lineName[25]="subwayline10_m";
    	lineName[26]="subwayline10_z";
    	lineName[27]="subwayline11_m";
    	lineName[28]="subwayline11_z";
    	lineName[29]="wuzhou_road";
    	lineName[30]="yanggao_road";
    	lineName[31]="yangshupu_road";
    	lineName[32]="yinxing_road";
    	lineName[33]="zhangyangbei_road";
    	lineName[34]="stops01";
    	lineName[35]="stops02";
    	lineName[36]="stops03";
    	lineName[37]="stops04";
    	lineName[38]="stops05";
    	lineName[39]="stops06";
    	lineName[40]="stops07";
    	lineName[41]="stops08";
    	lineName[42]="stops09";
    	lineName[43]="stops10";
    	lineName[44]="stops11";
    }//initializeLineName
}//getGeometriesFromMap