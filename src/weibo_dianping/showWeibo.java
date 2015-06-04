package weibo_dianping;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import method.MyCanvas;



import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;
import controlP5.Accordion;
import controlP5.CColor;
import controlP5.ControlKey;
import controlP5.ControlP5;
import controlP5.ControlTimer;
import controlP5.DropdownList;
import controlP5.Group;
import controlP5.RadioButton;
import controlP5.Slider;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.providers.Yahoo;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class showWeibo extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	UnfoldingMap map,map2;
	public static Accordion accordion;
	public static ControlP5 cp5;
	int c = color(0, 160, 100); 
	//file input

	DropdownList dpd;
	int count;
    double[] lat,lon;
	Location locationGeos;
	ScreenPosition posGeos;
	SimplePointMarker shopMarker;
	ResultSet rs;
	RadioButton radioBox;
	Location centerOfScreen; 
	int zoomLevelOfScreen;
	Group g1,g2,g3;
	boolean timerable=false;
	Long timer;
	int[] colors = new int[10];
	String[] lineName = new String[50];
	MyCanvas canv;
	{
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
	}
	public void setup() {
		//unfolding map
		  size(1366,768);	
		  //OpenStreetMap.OpenStreetMapProvider();
		  //OpenStreetMap.CloudmadeProvider(API KEY, STYLE ID);
		  //Stamen.TonerProvider();
		  //Google.GoogleMapProvider();
		  //Google.GoogleTerrainProvider();
		  //Microsoft.RoadProvider();
		  //Microsoft.AerialProvider();
		  //Yahoo.RoadProvider();
		  //Yahoo.HybridProvider();
			colors[0]=color(132,147,195);
			colors[1]=color(137,205,81);
			colors[2]=color(187,82,53);
			colors[3]=color(80,93,53);
			colors[4]=color(125,204,174);
			colors[5]=color(171,93,199);
			colors[6]=color(199,82,129);
			colors[7]=color(204,177,70);
			colors[8]=color(81,53,75);
			colors[9]=color(200,173,152);
			
		  map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
		  
          //map = new de.fhpotsdam.unfolding.Map(this,new Google.GoogleMapProvider());
		  map.setTweening(false);
		  map.zoomToLevel(13);
		  map.panTo(new Location(31.244417,121.457326));
		  MapUtils.createDefaultEventDispatcher(this, map);
          
          map.draw();
         
          centerOfScreen = map.getCenter();
          zoomLevelOfScreen = map.getZoomLevel();

          
	      try 
	        {
			    
	           Class.forName( "org.postgresql.Driver" ).newInstance();
	           String url = "jdbc:postgresql://localhost:5432/newWeiboDianping" ;
	           Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
	           Statement st = con.createStatement();
	           String sql="";
	           
	           int j;
	            
	           for (int i=0;i<=0; i++){
	               //sql = "select lat, lon from " + lineName[i] + ";";  
	               sql = "select lat, lon from weibo,place where weibo.poiid = place.poiid and place.newcate='ÂÃÓÎ'";
	               //sql = "select lat, lon from shops where cate = 'residence'";
	        	   rs = st.executeQuery(sql);
	               lat = new double[2000000];
	               lon = new double[2000000];
	               count = 0;
	               while (rs.next()){    
	              		 lat[count] = rs.getDouble("lat")+0.002036d;
	                     lon[count] = rs.getDouble("lon")-0.00445d;
	              	
					     locationGeos = new Location(lat[count], lon[count]);
					     posGeos = map.getScreenPosition(locationGeos);
					     shopMarker = new SimplePointMarker(locationGeos);
					     shopMarker.setColor(color(0,0,0));
					  
					    
					     shopMarker.setStrokeColor(color(0, 102, 153, 204));
					     shopMarker.setColor(color(0, 102, 153, 204));
					 
					     shopMarker.setStrokeWeight(0);
					     shopMarker.setRadius(3);
					     if (i>=34 && i<=44) {
					    	 shopMarker.setRadius(7); 
					    	 shopMarker.setColor(color(255,0,0));
					     }
					     /*
					     if (i<34){
					    	 shopMarker.setColor(color(0,0,0));
						     shopMarker.setRadius(3);
					     }
					    */  
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
		  //GUI();
		  
	}//setup

	public void draw() {
	    background(0);
	    map.draw();
	    if (timerable){
	    	//Wait(0l);
	    	noStroke();
	      	fill(color(0,0,0), 150);
	      	this.rect(0,0,1366,768);
	      	canv = new MyCanvas(50,50,500,500);
	      	int [] dk = new int[18];
	      	dk[0]=10;
	      	dk[1]=7;
	      	dk[2]=21;
	      	dk[3]=18;
	      	dk[4]=7;
	        dk[5]=25;
	        dk[6]=5;
	        dk[7]=5;
	        dk[8]=15;
	        dk[9]=22;
	        dk[10]=13;
	        dk[11]=7;
	        dk[12]=13;
	        dk[13]=43;
	        dk[14]=15;
	        dk[15]=30;
	        dk[16]=114;
	        dk[17]=18;
	      	canv.setDatas(dk,18);
	      	canv.draw(this);
	    }
	    /*
	      fill(255,0,0);
	      strokeWeight(2);
	      line(0, 0, 500, 500);
	   */
	}//draw


    

    
    public void keyPressed(){
    	if (key == 's' || key == 'S'){
    		timerable = true;
    		timer =System.currentTimeMillis();
    	}else{
    		timerable = false;
    		
    	}
    	
    }
    
    public void Wait(Long t){
        
        if (System.currentTimeMillis()< timer+t && timer>0){
        	noStroke();
	      	fill(color(0,0,0), (timer-System.currentTimeMillis())*240/t);
	      	this.rect(0,0,1366,768);
	      	redraw();
        	Wait(t);
        }
        timer=-1l;
      
    }
    public void GUI(){
  	  
  	  cp5 = new ControlP5(this);
  	  
  	  // group number 1, contains 2 bangs
  	  g1 = cp5.addGroup("cluster")
  	                .setBackgroundColor(color(0, 64))
  	                .setBackgroundHeight(150)
  	                .setBarHeight(25)
  	                
  	                ;
  	  
  	PImage[] imgs = {loadImage("button_a.png"),loadImage("button_b.png"),loadImage("button_a.png")};
    cp5.addButton("play")
       .setValue(128)
       .setPosition(10,10)
       .setImages(imgs)
       .updateSize()
       .moveTo(g1);
       ;  
       
    cp5.addSlider("K-means")
	   .setPosition(10,75)
	   .setSize(150,15)
       .setRange(4,10)
       .setValue(8)
       .setNumberOfTickMarks(7)
       .setSliderMode(Slider.FLEXIBLE)
       .moveTo(g1)
       ;
    
    cp5.addSlider("repeat")
	   .setPosition(10,110)
	   .setSize(150,15)
       .setRange(1,20)
       .setValue(1)
       .setNumberOfTickMarks(20)
       .setSliderMode(Slider.FLEXIBLE)
       .moveTo(g1)
    ;
    
  	  // group number 2, contains a radiobutton
  	  g2 = cp5.addGroup("parameters")
  	                .setBackgroundColor(color(0, 64))
  	                .setBackgroundHeight(190)
  	                .setBarHeight(25)
  	                ;
  	  
  	  radioBox = cp5.addRadioButton("radiobox")
  	     .setPosition(20,20)
  	     .setItemWidth(20)
  	     .setItemHeight(20)
  	     .addItem("1", 1)
  	     .addItem("2", 2)
  	     .addItem("3", 3)
  	     .addItem("4", 5)
  	     .addItem("5", 5)
  	     .addItem("6", 6)
  	     .addItem("7", 7)
  	     .addItem("8", 8)
  	     .addItem("9", 9)
  	     .addItem("10", 10)
  	     
  	     .setColorLabel(color(255))
  	     .setItemsPerRow(3)
  	     .setSpacingColumn(30)
  	     .setSpacingRow(25)
  	     .moveTo(g2)
  	     ;
		radioBox.getItem(1).setColorActive(colors[0]);
		radioBox.getItem(2).setColorActive(colors[1]);
		radioBox.getItem(3).setColorActive(colors[2]);
		radioBox.getItem(4).setColorActive(colors[3]);
		radioBox.getItem(5).setColorActive(colors[4]);
		radioBox.getItem(6).setColorActive(colors[5]);
		radioBox.getItem(7).setColorActive(colors[6]);
		radioBox.getItem(8).setColorActive(colors[7]);
		radioBox.getItem(9).setColorActive(colors[8]);
		radioBox.getItem(0).setColorActive(colors[9]);
		
		radioBox.getItem(1).setColorForeground(colors[0]);
		radioBox.getItem(2).setColorForeground(colors[1]);
		radioBox.getItem(3).setColorForeground(colors[2]);
		radioBox.getItem(4).setColorForeground(colors[3]);
		radioBox.getItem(5).setColorForeground(colors[4]);
		radioBox.getItem(6).setColorForeground(colors[5]);
		radioBox.getItem(7).setColorForeground(colors[6]);
		radioBox.getItem(8).setColorForeground(colors[7]);
		radioBox.getItem(9).setColorForeground(colors[8]);
		radioBox.getItem(0).setColorForeground(colors[9]);
  	  // group number 3, contains a bang and a slider
  	  g3 = cp5.addGroup("custom polygon display")
  	                .setBackgroundColor(color(0, 64))
  	                .setBackgroundHeight(170)
  	                .setBarHeight(25);
  	                ;
  	              
  	          cp5.addRadioButton("customPolygonDisplay")
  	     	     .setPosition(10,20)
  	     	     .setItemWidth(20)
  	     	     .setItemHeight(20)
  	     	     .addItem("", 0)
  	     	     
  	     	     .setColorLabel(color(255))
  	     	     .setItemsPerRow(5)
  	     	     .setSpacingColumn(15)
  	     	     .setSpacingRow(5)
  	     	     .moveTo(g3)
  	     	     ;
  	 

  	  // create a new accordion
  	  // add g1, g2, and g3 to the accordion.
  	  accordion = cp5.addAccordion("acc")
  	                 .setPosition(40,40)
  	                 .setWidth(250)
  	                 .addItem(g1)
  	                 .addItem(g2)
  	                 .addItem(g3)
  	                 ;
  	
  	  accordion.open(0,1,2);
  	  // use Accordion.MULTI to allow multiple group 
  	  // to be open at a time.
  	  accordion.setCollapseMode(Accordion.MULTI);
  }
    /*
	public void mouseDragged(){
		
		float minX = accordion.getPosition().x;
		float minY = accordion.getPosition().y;
		float maxX= minX + accordion.getWidth();
		float maxY = minY;
		if(g1.isOpen()) maxY+= g1.getBackgroundHeight() + g1.getBarHeight(); else maxY+= g1.getBarHeight();
		if(g2.isOpen()) maxY+= g2.getBackgroundHeight() + g2.getBarHeight(); else maxY+= g2.getBarHeight();
		if(g3.isOpen()) maxY+= g3.getBackgroundHeight() + g3.getBarHeight(); else maxY+= g3.getBarHeight();
		
		if (mouseX>=minX && mouseX<=maxX && mouseY>=minY && mouseY<=maxY){
			map.panTo(centerOfScreen);
		}else{
			centerOfScreen = map.getCenter();
		}
	}
*/
}// end class
