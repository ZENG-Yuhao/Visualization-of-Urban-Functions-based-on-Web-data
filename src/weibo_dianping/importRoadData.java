package weibo_dianping;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

public class importRoadData extends PApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static ArrayList<String> source= new ArrayList<String>();
	static ArrayList<String> target= new ArrayList<String>();
	static ArrayList<String> length= new ArrayList<String>();
	static ArrayList<String> car= new ArrayList<String>();
	static ArrayList<String> car_reverse= new ArrayList<String>();
	static ArrayList<String> bike= new ArrayList<String>();
	static ArrayList<String> bike_reverse= new ArrayList<String>();
	static ArrayList<String> foot= new ArrayList<String>();
	static ArrayList<String> WKT = new ArrayList<String>();
	static ArrayList<String> lineString = new ArrayList<String>();
	
	UnfoldingMap map;
	SimpleLinesMarker line;
	Location location, last_location;
	
    public void setup(){
    	String path = "d:\\data\\Shanghai network road\\edges.csv";
        //System.out.println(Integer.MAX_VALUE);
    	try {
			getContent(path);
			System.out.println("Input Done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	   /* for (int i=0; i<=lineString.size()-1;i++){
    		   if (!lineString.get(i).equals(WKT.get(i).substring(11,WKT.get(i).length()-1))){
    		 	   System.out.println("Wrong "+i);
    		 	  System.out.println(lineString.get(i));
    		 	 System.out.println(WKT.get(i));
    		   }
            }*/
    	
    	
    	/*for (int i=98; i<=104; i++){
    		
    		System.out.println(i+" "+ source.get(i)+" "+target.get(i)+" "+length.get(i)+" "+car.get(i)+" "+car_reverse.get(i)+" "+bike.get(i)+" "+bike_reverse.get(i)+" "+foot.get(i)+" "+lineString.get(i));
    	}*/
    	/*System.out.println(source.size());
    	System.out.println(target.size());
    	System.out.println(length.size());
    	System.out.println(car.size());
    	System.out.println(car_reverse.size());
    	System.out.println(bike.size());
    	System.out.println(bike_reverse.size());
    	System.out.println(foot.size());
    	System.out.println(lineString.size());
    	*/
    	
    	
    	//Import roads to database
    	// SQL();
    	
    	  //unfolding map;
		  size(1500,1000);	
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
  	      
          //roadDraw();
          roadDrawSQL();
  	    
        System.out.println("SetUp Done.");

    }//setup
	
    public void draw(){
	    background(0);
	    map.draw();

    }//draw
    
    public void getContent(String path) throws IOException{
    	String content="";
    	String line;
    	File f=new File(path);
    	FileReader fr;	
		BufferedReader br;
        if (!f.exists()) System.out.println("File not exist.");
        else
        try {
		    fr=new FileReader(f);
		    br=new BufferedReader(fr);
		    line=br.readLine();
		    Long count=1l;
		    content="";   
		        while (line!=null){
		        	if (count==1) {line=br.readLine(); System.out.println(line);}
		        	else{
		            getParams(line);
		            getLineString(line);
		            line=br.readLine();
		            
		        	}
		        	count++;
		        }//end while 
			fr.close();
			br.close();
        }catch (Exception e){
        	e.printStackTrace();
        }
        //return content;
    }//end getContent
    
    public void getParams(String content){
    	int len = content.length();
    	int pos,index;
    	String keyWord,info;
        
    	pos=0;   index=0;
    	for (int i=0; i<=len-1; i++){
    		keyWord = content.substring(i,i+1);
    		if (keyWord.equals(",") || i==len-1){
    			info = content.substring(pos,i);
    			switch(index){
    			case 0: break;
    			case 1:  source.add(info);  break;    				   
    			case 2:  target.add(info);  break;
    			case 3:  length.add(info);  break;
    			case 4:  car.add(info);  break;
    			case 5:  car_reverse.add(info); break;
    			case 6:  bike.add(info);  break;
    			case 7:  bike_reverse.add(info);  break;
    			case 8:  foot.add(info);  break;
    			case 9:  WKT.add(content.substring(pos,len)); 
    			//System.out.println(content.substring(pos+11,len-1)); System.out.println(content.substring(pos,len));
    			break;
    			}//case
    		 
    			pos = i+1;  index++;
    		}//if
    		if (index==10) break;
    	}//for i
    	if (WKT.size()!=source.size()) System.out.println("WRIONG: "+content);
    }//getParams
    
    public void getLineString(String content){
    	int len = content.length();
    	int i,j;
    	String keyWord;
    	i=0;
    	while (i<=len-10){
    		keyWord= content.substring(i,i+10);
    		if (keyWord.equals("LINESTRING")){
    			j=i+11;
    			while (!content.substring(j,j+1).equals(")")) j++; 
    			lineString.add(content.substring(i+11,j));
    			i=j;
    		}
    		i++;
    	}// for i
    }//getLineString
    
    public void SQL(){

 	   try{
 	      //initialize
 		  Class.forName( "org.postgresql.Driver" ).newInstance();
 	      String url = "jdbc:postgresql://localhost:5432/test" ;
           Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
          
           
           //create table
           //PreparedStatement createTable = con.prepareStatement("create table roads_info1 (roadId int, source varchar, target varchar, length double precision, car int, car_reverse int, bike int, bike_reverse int, foot int, WKT geometry, primary key (roadId));");
           //createTable.setString(1,currentTable);
           //createTable.executeUpdate();
           //createTable.close(); 
           
           //insert geos
           
           for (int i=1; i<=WKT.size()-1; i++){
        	   System.out.println("insert into roads_info1 values("+ i +"," + source.get(i) + "," + target.get(i) + "," +length.get(i) + "," +car.get(i) + "," +car_reverse.get(i) + "," +bike.get(i) + "," +bike_reverse.get(i) + "," +foot.get(i) + "," +WKT.get(i) +");");
        	   PreparedStatement insertGeos = con.prepareStatement("insert into roads_info1 values("+ i +"," + source.get(i) + "," + target.get(i) + "," +length.get(i) + "," +car.get(i) + "," +car_reverse.get(i) + "," +bike.get(i) + "," +bike_reverse.get(i) + "," +foot.get(i) + ",'" +WKT.get(i) +"');");
              //PreparedStatement insertGeos = con.prepareStatement();
              //insertGeos.setString(1, currentTable);
              //insertGeos.setInt(1, i);
              insertGeos.executeUpdate();
              insertGeos.close();
              System.out.println("No."+i+"sql Done.");
           }
           
           System.out.println("SQL Done.");
           con.close();
 	   }catch(Exception ee){
 	      System.out.print(ee.getMessage());
 	   }finally{
 		  
            
 	   }
    }// end SQL
    
    public void roadDraw(){
    	String str; 
        double lat, lon;
        int last_sym;
        
        //31.281166  121.450745; 818 312
        //31.279255  121.45527; 1237 522
        //-0.001911d   +0.004535d
        for (int i=0; i<=lineString.size()-1; i++){
	     	str = lineString.get(i);
	     	//System.out.println(str);
	     	last_location=null; lat=0; lon=0;
	     	last_sym=0;
	     	for (int j=0; j<=str.length()-1;j++){
	     		if (str.substring(j, j+1).equals(" ")){
	     			if (!str.substring(last_sym,j).equals("0"))
	     			    lon= Double.parseDouble(str.substring(last_sym,j));
	     			last_sym=j+1;
	     			
	     		}else if(str.substring(j, j+1).equals(",")){
                    if(!str.substring(last_sym,j).equals("0")){
	     			    lat= Double.parseDouble(str.substring(last_sym,j));
	     			    location = new Location(lat, lon);
	     			    if (last_location!=null){
	     				    line=new SimpleLinesMarker(location,last_location);
	     				    map.addMarkers(line);
	     			    }
	     			    last_location=location;
                    }
	     			last_sym=j+1;
	     		}else if(j==str.length()-1){
	     			if(!str.substring(last_sym,j+1).equals("0")){
	     			lat= Double.parseDouble(str.substring(last_sym,j+1));
	     			location = new Location(lat, lon);
	     			if (last_location!=null){
	     				line=new SimpleLinesMarker(location,last_location);
	     				map.addMarkers(line);
	     			}
	     			}
	     		}
	     	}//for j
	    }//for i
    }//roadDraw
    
    public void roadDrawSQL(){
    	String str; 
        double lat, lon;
        int last_sym;
    	try{
	        //initialize
		    Class.forName( "org.postgresql.Driver" ).newInstance();
	        String url = "jdbc:postgresql://localhost:5432/test" ;
            Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
            ResultSet rs;
          /*# note
            #The integers mean:
            #* cars 
            #    * 0 forbiden
            #    * 1 residential street
            #    * 2 tertiary road
            #    * 3 secondary road
            #    * 4 primary road
            #    * 5 trunk
            #    * 6 motorway
            #* bike
            #    * 0 forbiden
            #    * 1 cycling lane in the opposite direction of the car flow
            #    * 2 allowed without specific equipment
            #    * 3 cycling lane
            #    * 4 bus lane allowed for cycles
            #    * 5 cycling track
            #* foot (no distinction in made on the direction)
            #    * 0 forbiden
            #    * 1 allowed */

            //create table
            PreparedStatement createTable = con.prepareStatement("select roadId from roads_info1 where(car = 4 or car =5 or car =6 or car_reverse=4 or car_reverse=5 or car_reverse=6);");
            //PreparedStatement createTable = con.prepareStatement("select roadId from roads_info1;");
            //createTable.setString(1,currentTable);
            rs=createTable.executeQuery();
            int roadId;
            while (rs.next()){
            	roadId =  rs.getInt("roadId");
            	str = lineString.get(roadId-1);
            	
    	     	//System.out.println(str);
    	     	last_location=null; lat=0; lon=0;
    	     	last_sym=0;
    	     	
    	     	for (int j=0; j<=str.length()-1;j++){
    	     		if (str.substring(j, j+1).equals(" ")){
    	     			if (!str.substring(last_sym,j).equals("0"))
    	     			    lon= Double.parseDouble(str.substring(last_sym,j));
    	     			last_sym=j+1;
    	     			
    	     		}else if(str.substring(j, j+1).equals(",")){
                        if(!str.substring(last_sym,j).equals("0")){
    	     			    lat= Double.parseDouble(str.substring(last_sym,j));
    	     			    location = new Location(lat, lon);
    	     			    if (last_location!=null){
    	     				    line=new SimpleLinesMarker(location,last_location);
    	     				    line.setColor(color(0,0,0));
    	     				    map.addMarkers(line);
    	     			    }
    	     			    last_location=location;
                        }
    	     			last_sym=j+1;
    	     		}else if(j==str.length()-1){
    	     			if(!str.substring(last_sym,j+1).equals("0")){
    	     			lat= Double.parseDouble(str.substring(last_sym,str.length()));
    	     			location = new Location(lat, lon);
    	     			if (last_location!=null){
    	     				line=new SimpleLinesMarker(location,last_location);
    	     				line.setColor(color(0,0,0));
    	     				map.addMarkers(line);
    	     			}
    	     			}
    	     		}
    	     	}//for j
            }//while
            rs.close();
            createTable.close(); 
            con.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }//roadDrawSQL
       
    public void mousePressed(MouseEvent e){
    	Location L;
    	
    	L=map.getLocationFromScreenPosition((float)(mouseX),(float)(mouseY));
    	//L= map.getLocationFromScreenPosition(e.getX,e.getY());
    	System.out.print(L.getLat()+"  "+ L.getLon()+"; ");
        System.out.println(mouseX+" "+ mouseY);
    }//mousePressed
}//importRoadData