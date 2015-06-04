package weibo_dianping;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import method.Database;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

import processing.core.PApplet;
import processing.core.PFont;

public class districtDividing extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class Lines{
		ArrayList<Double> lat; //该线段的纬度ArrayList数组
		ArrayList<Double> lon; //该线段的经度ArrayList数组
		ArrayList<Integer> intsec, pos; //intesec交点ArrayList数组
		String lineString; //PostGIS中的数据格式LINESTRING(...)
		double minLat, maxLat, minLon, maxLon;
		
		Lines(){
		    lat = new ArrayList<Double>();	
		    lon = new ArrayList<Double>();
		    intsec = new ArrayList<Integer>();
	        pos = new ArrayList<Integer>();  //record the position of intsec_i on Lines;
		    minLat = Double.MAX_VALUE;
		    maxLat = Double.MIN_VALUE;
		    minLon = Double.MAX_VALUE;
		    maxLon = Double.MIN_VALUE;
		}
		
	}//class Lines
	
	public class Intersections{
        double lat,lon;
        ArrayList<Integer> father =  new ArrayList<Integer>();
        ArrayList<Integer> pos = new ArrayList<Integer>();
	}//intersections
	
	public class commonLine{
		int index;
		int minPos, maxPos, pos1, pos2, d;
		double lat1, lon1, lat2, lon2;
	}//commonLine
	SimplePolygonMarker spm = new SimplePolygonMarker();
	SimpleLinesMarker slm = new SimpleLinesMarker();
	Location L;//location of screenPosition
	SimplePointMarker pM = new SimplePointMarker();
	SimpleLinesMarker lM = new SimpleLinesMarker();
	ArrayList<Location> past_L = new ArrayList<Location>();
	int count;
    double[] lat,lon;
	Location locationGeos;
	ScreenPosition posGeos, posGeos1;
	SimplePointMarker shopMarker;
	ResultSet rs;
	String tmps;
	
	int tmp=78;
	int turn_key=0;
	
	UnfoldingMap map;
    SimpleLinesMarker slM;
    SimplePointMarker spM;
    MarkerManager markerManager = new MarkerManager();
    
    int nb_lineString = 34;
    int nb_polygon =0;
	int max_polygon = 1000000;
	int max_intersection = 20000;
	
	String[] lineName = new String[45];
    Lines[] line = new Lines[nb_lineString];
    ArrayList<Intersections> intsec = new ArrayList<Intersections>();
	boolean adjMatrix[][]= new boolean[max_intersection][max_intersection];
	ArrayList<Location>[] rebuildedPolygon = new ArrayList[max_polygon];
	ArrayList<Integer>[] polygon = new ArrayList[max_polygon];
	int[] du = new int[nb_lineString];
    
	boolean[] arrived = new boolean[max_intersection];
	ArrayList<Integer> route = new ArrayList<Integer>();
	int nb_rings = 0;
	Set<Integer>[] ring = new HashSet[2000000];
	Set<Integer> tmpSet = new HashSet();
    //initialisation block
    {
    	for (int i=0; i<=max_polygon-1; i++){
    		polygon[i] = new ArrayList<Integer>();
    	    rebuildedPolygon[i] = new ArrayList<Location>();
    	}
    	for (int i=0; i<=nb_lineString-1; i++)    line[i] = new Lines();
    	for (int i=0; i<=max_intersection-1; i++) arrived[i]=false;
		for (int i=0; i<=200000-1; i++) ring[i]=new HashSet();
    }
    
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
    
    public void setup(){
        
		initializeLineName();
        trans();
		findIntersections();
		buildAdjacencyMatrix();
		try {
			String str = loadPolygon("d:\\data\\districts1.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rebuildPolygon();
		
		
		fixLastPolygon();
		//exportPolygonToDatabase();
		//addGeometryAttribute();
		
		for (int i=0; i<=nb_polygon-1; i++){
			Location l1 = rebuildedPolygon[i].get(0);
			Location l2 = rebuildedPolygon[i].get( rebuildedPolygon[i].size()-1 );
			if (!l1.equals(l2)){
				System.out.println(i+":" + l1.getLat()+" " +l1.getLon()+" ; " + l2.getLat() +"  " + l2.getLon()  );
			    for (int j=0; j<=polygon[i].size()-1; j++) System.out.println(polygon[i].get(j)+ " " );
		
			}
		}
		//map.addMarkers(slm);
		
		/*
		System.out.println("Intersections:");
		 
		for (int i=0; i<=intsec.size()-1; i++){
			System.out.println("intsec "+i);
			for (int j=0; j<=intsec.get(i).father.size()-1; j++){
				
				System.out.println("Father : " + intsec.get(i).father.get(j) + "; Position : " +intsec.get(i).pos.get(j));
			}
		}
	    */
		
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
		  map = new de.fhpotsdam.unfolding.Map(this, new OpenStreetMap.OpenStreetMapProvider());
		  map.setTweening(false);
		  map.zoomToLevel(13);
		  map.panTo(new Location(31.244417,121.457326));
		  MapUtils.createDefaultEventDispatcher(this, map);
          map.draw();
        
          
          
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
					     shopMarker.setColor(color(0,0,0));
					  
					    
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
		    
	        
			for (int i=0; i<=intsec.size()-1; i++){
				spM = new SimplePointMarker();
				spM.setRadius(8);
				spM.setColor(color(0,255,0,255));
				spM.setLocation( (float)(intsec.get(i).lat), (float)(intsec.get(i).lon));
				posGeos = map.getScreenPosition(spM.getLocation());
				this.text(i, posGeos.x-3, posGeos.y-3);
			    map.addMarkers(spM);
			    //System.out.println(intsec.get(i).lat +" "+ intsec.get(i).lon);
			}
			
			/*for (int i=0; i<=intsec.size()-2; i++){
				for (int j=0; j<=intsec.size()-1; j++){
					if (adjMatrix[i][j]){
					    slM = new SimpleLinesMarker();
					    slM.setStrokeWeight(2);
					    slM.addLocation((float)(intsec.get(i).lat), (float)(intsec.get(i).lon));
					    slM.addLocation((float)(intsec.get(j).lat), (float)(intsec.get(j).lon));
					    map.addMarkers(slM);
					}//if
				}//for j
			}//for i */	
			
			//polygonTest();
			System.out.println(getPolygonString(11));
	}//setup 
	
	public void draw(){
        map.draw();
       
        
        Location tmp;
		for (int i=0; i<=intsec.size()-1; i++){
			tmp = new Location( (float)(intsec.get(i).lat), (float)(intsec.get(i).lon));
			posGeos = map.getScreenPosition(tmp);
			this.fill(255,0,0,255);
			this.textSize(20);
			this.text(i, posGeos.x-10, posGeos.y-10);
            
		}
		
		//showTest();
		/*
		for (int i=0; i<=nb_polygon-1; i++){
			for (int j=1; j<=rebuildedPolygon[i].size()-1; j++ ){
				posGeos = map.getScreenPosition(rebuildedPolygon[i].get(j-1));
				posGeos1 = map.getScreenPosition(rebuildedPolygon[i].get(j));
				
				this.line(posGeos.x, posGeos.y, posGeos1.x, posGeos1.y);
				
			}
		}
		*/
		/*
		for (int i=0; i<=nb_lineString-1; i++){
			for (int j=1; j<=line[i].lat.size()-1; j++){
				Location a = new Location(line[i].lat.get(j-1), line[i].lon.get(j-1));
				Location b = new Location(line[i].lat.get(j), line[i].lon.get(j));
				posGeos = map.getScreenPosition(a);
				posGeos1 = map.getScreenPosition(b);
				
				this.line(posGeos.x, posGeos.y, posGeos1.x, posGeos1.y);
			}
		}
		*/
		linkPolygon1(rebuildedPolygon[turn_key],turn_key);
		//for (int i=0; i<=nb_polygon-1; i++) 
	}//draw
	
	private PFont Font(String string, int italic, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public void trans(){
		try{
			Class.forName( "org.postgresql.Driver" ).newInstance();
	 	    String url = "jdbc:postgresql://localhost:5432/weibo_dianping" ;
	        Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
		    
	        //load datas
	        
	        String sqlInstruction ="";
	      	Statement load = con.createStatement();

	        Double latitude, longitude;

	        for (int i=0; i<=nb_lineString-1; i++){
	        	sqlInstruction = "select lat, lon from " + lineName[i] +";";
	        	
	        	rs = load.executeQuery(sqlInstruction);
	            
	            String str="LINESTRING(";
                
	        	while (rs.next()){
	        		str = str + rs.getString("lat") + " " + rs.getString("lon");
	        		//if (rs.next()) str = str + ",";
	        	    latitude = rs.getDouble("lat");   longitude = rs.getDouble("lon");
	        		line[i].lat.add(latitude); 	 line[i].lon.add(longitude);
	        		
	        		if (latitude > line[i].maxLat) line[i].maxLat = latitude;
	        		if (latitude < line[i].minLat) line[i].minLat = latitude;
	        		if (longitude > line[i].maxLon) line[i].maxLon = longitude;
	        		if (longitude < line[i].minLon) line[i].minLon = longitude;
	        		
	        	}//while 
	        	str = str + ")";
	        	line[i].lineString = str;
	        
	        }//for i
	        
	        /*for (int i=0; i<=nb_lineString-1; i++){
	            System.out.println("Range: "+line[i].minLat+" "+line[i].maxLat+" "+line[i].minLon+" "+line[i].maxLat);
	        	for (int j=0; j<=line[i].lat.size()-1; j++){
	                System.out.print("lat: "+line[i].lat.get(j)+"; lon: "+line[i].lon.get(j));
	            }//for j
	        	System.out.println(" ");
	        }//for i */
	    	rs.close();
            load.close();
            con.close();
		}catch(Exception ee){
			System.out.print(ee.getMessage());
		}//try
		
	}//trans
	
	public void findIntersections(){
		double ax1, ay1, ax2, ay2, bx1, by1, bx2, by2;
		double af1, af2, bf1, bf2, x, y;
		Intersections tmp = new Intersections();
		
		for (int i=0; i<=nb_lineString-2; i++){
		    for (int j=i+1; j<=nb_lineString-1; j++){
		        if (possible(i,j)){
		    	     for (int s=1; s<=line[i].lat.size()-1; s++){
	    	    	     ax1 = line[i].lat.get(s-1);
	    	    	     ay1 = line[i].lon.get(s-1);
	    	    	     ax2 = line[i].lat.get(s);
	    	    	     ay2 = line[i].lon.get(s);
	    	    	     
		    	    	 for (int t=1; t<=line[j].lat.size()-1; t++){  	    	      
		    	    	      bx1 = line[j].lat.get(t-1);
		    	    	      by1 = line[j].lon.get(t-1);
		    	    	      bx2 = line[j].lat.get(t);
		    	    	      by2 = line[j].lon.get(t);
		    	    	      
		    	    	      x = bx1;  y = by1;
		    	    	      af1 = (x-ax1)*(ay1-ay2) - (y-ay1)*(ax1-ax2); //B1点代入A1 A2所在直线
		    	    	      x = bx2;  y = by2;
		    	    	      af2 = (x-ax1)*(ay1-ay2) - (y-ay1)*(ax1-ax2);//B2点代入A1 A2所在直线
		    	    	      x = ax1;  y = ay1;
		    	    	      bf1 = (x-bx1)*(by1-by2) - (y-by1)*(bx1-bx2);//A1点代入B1 B2所在直线
		    	    	      x = ax2;  y = ay2;
		    	    	      bf2 = (x-bx1)*(by1-by2) - (y-by1)*(bx1-bx2);//A2点代入B1 B2所在直线
		    	    	      
		    	    	      if ((af1*af2<=0 && bf1*bf2<=0)){  //满足相交条件
		    	    	    	  //System.out.println(af1 + " " + af2 + " " + bf1 + " " + bf2);
		    	    	    	  tmp = new Intersections();
		    	    	          tmp.lat=(ax1+ax2+bx1+bx2)*0.25;       //交点为四个点的线性中点
		    	    	          tmp.lon=(ay1+ay2+by1+by2)*0.25;
		    	    	          tmp.father.add(i); tmp.pos.add(s);
		    	    	          tmp.father.add(j); tmp.pos.add(t);
		    	    	          intsec.add(tmp);
		    	    	          //insert index of intsec to a right position on line[i]
		    	    	          int k;
		    	    	          for (k=0; k<=line[i].intsec.size()-1; k++)
		    	    	        	  if (line[i].pos.get(k)>s)
		    	    	        		  break;
		    	    	          
		    	    	          //if didnt found the pos, then k will be the last pos+1;
		    	    	          line[i].intsec.add(k,intsec.size()-1);   
		    	    	          line[i].pos.add(k,s);
		    	    	          
		    	    	          int g;
		    	    	          for (g=0; g<=line[j].intsec.size()-1; g++)
		    	    	        	  if (line[j].pos.get(g)>t)
		    	    	        		  break;
		    	    	          
		    	    	          //if didnt found the pos, then g will be the last pos+1;
		    	    	          line[j].intsec.add(g,intsec.size()-1);
		    	    	          line[j].pos.add(g,t);
		    	    	      }
		    	    	 }//for t
		    	    	 
		    	     }//for s 
		    		 
		    	 }//if 
		    	 
		    }//for j
		}//for
		System.out.println("findIntersections Done.");
	}//findIntersections
	
	public void buildAdjacencyMatrix(){
		//initialize matrix
		for (int i=0; i<=line[i].intsec.size()-1; i++){
			for (int j=0; j<=line[i].intsec.size()-1; j++){
				adjMatrix[i][j] = false;
			}
		}
        
		//build
		int a,b;
		for (int i=0; i<=nb_lineString-1; i++){
			for (int j=1; j<=line[i].intsec.size()-1; j++){
				a=line[i].intsec.get(j-1); b=line[i].intsec.get(j);
				adjMatrix[a][b]=true;    
				adjMatrix[b][a]=true;
			
			}//for j 
		}//for i
		/*
		int c;
		for (int i=0; i<=nb_lineString-1; i++){
			c=0;
			for (int j=1; j<=line[i].intsec.size()-1; j++){
				if (adjMatrix[i][j]) c++;
			}//for j
			du[i]=c;
		}//for i
		*/
		for (int i=0; i<=intsec.size()-1; i++){
			for (int j=0; j<=intsec.size()-1; j++){
				if (adjMatrix[i][j]) System.out.print("1 ");
				else System.out.print("0 ");
			}
			System.out.println(" ");
		}
		
		for (int i=0; i<=nb_lineString-1; i++){
			System.out.print("Index "+i+": ");
			for (int j=0; j<=line[i].intsec.size()-1; j++){
				System.out.print(line[i].intsec.get(j)+" ");
			}
			System.out.println(" ");
		}
		
		System.out.println("buildAdjacencyMatrix Done.");
	}//buildAdjacencyMatrix
	
	public void findRings(){
		
		for (int start=0; start<=intsec.size()-1; start++){
			//initialize();
			route.add(start); 
			tmpSet.add(start);
	        for (int i=0; i<=intsec.size()-1; i++){
	        	if (adjMatrix[start][i] && start!=i && !arrived[i]){
	        		route.add(i);  
	        		arrived[i] = true; 
	        		tmpSet.add(i);
	        		dfs(i,start,start);
	        		route.remove(route.size()-1);  
	        		arrived[i] = false;
	        		tmpSet.remove(i);
	        	}
	        }
	        route.remove(route.size()-1);
	        tmpSet.remove(start);
		}
		System.out.println("findRings done.");
	}//findRings
	

	
	public void rebuildPolygon(){
		commonLine cmnL;
		Location location;
		double lat, lon;
		int i,j,k;
        for (i=0; i<=nb_polygon-1; i++){
        	for (j=1; j<=polygon[i].size()-1; j++){
                cmnL = common(polygon[i].get(j-1), polygon[i].get(j));
                //System.out.println("intsec" +polygon[i].get(j-1) +" and intsec" + polygon[i].get(j)+" :   common="+cmnL.index +"  pos1=" + cmnL.pos1 + "  pos2=" +cmnL.pos2+ "  d="+cmnL.d);
                
                int index = cmnL.index;
                location = new Location(intsec.get(polygon[i].get(j-1)).lat, intsec.get(polygon[i].get(j-1)).lon);
                rebuildedPolygon[i].add(location);
                
                int begin, end;  //end can't be arrived
                if (cmnL.d == 1 || cmnL.d == 0){
                    begin = cmnL.pos1;	 end = cmnL.pos2;  //end' = end-1/end-0 = cmnL.pos2 -1/ cmnL.pos2;
                }else{
                	begin = cmnL.pos1-1;  end = cmnL.pos2 -1;  // end' = end+1 = cmnL.pos2; 
                }
                k=begin;
                do{ 
                	//System.out.println("k=" + k +"; begin=" + begin+ "; end="+ end);
        			location = new Location(line[index].lat.get(k), line[index].lon.get(k));
        			rebuildedPolygon[i].add(location);
        			k =k+cmnL.d;
        		}while(k != end);
        		

        	}//for j
        	j--;  //now j is index of the last one
        	location = new Location(intsec.get(polygon[i].get(j)).lat, intsec.get(polygon[i].get(j)).lon);
            rebuildedPolygon[i].add(location);
        }//for i
        System.out.println("rebuildPolygon Done.");
	}//rebuildPolygon
	
	public commonLine common(int a, int b){
		int i,j;
		double latA, lonA, latB, lonB;
	    latA = intsec.get(a).lat;  lonA = intsec.get(a).lon;
	    latB = intsec.get(b).lat;  lonB = intsec.get(b).lon;
	    
		commonLine cl = new commonLine();
		for (i=0; i<=intsec.get(a).father.size()-1; i++)
		    for (j=0; j<=intsec.get(b).father.size()-1; j++)
		    	if (intsec.get(a).father.get(i) == intsec.get(b).father.get(j)){
		    		cl.index = intsec.get(a).father.get(i);
		    		cl.pos1 = intsec.get(a).pos.get(i);
		    		cl.pos2 = intsec.get(b).pos.get(j);
		    		if (cl.pos1 < cl.pos2) cl.d = 1;
		    		else if (cl.pos1 > cl.pos2) cl.d = -1;
		    		else cl.d = 0;
		    	    break;
		    	}//if 
		            
		return cl;
	}//common
	
	public void dfs(int present, int last, int target){
		if (present == target && !exist()){
			
			//System.out.print("Ring " + nb_rings +":");
			for (int j=0; j<=route.size()-1; j++) {
				ring[nb_rings].add(route.get(j));
				//System.out.print(route.get(j)+" ");
			}
			//System.out.println("");
			nb_rings++;
		}else{
			for (int i=0; i<=intsec.size()-1; i++){
	            if (adjMatrix[present][i] && i!=present && i!=last && !arrived[i]){
					route.add(i);  
					arrived[i] = true;
					tmpSet.add(i);
	        		dfs(i,present,target);
	        		route.remove(route.size()-1);  
	        		arrived[i] = false;
	        		tmpSet.remove(i);
				}
			}
		}//if
	}//dfs
	
	public boolean exist(){
		for (int i=0; i<=nb_rings; i++)
			if (ring[i].equals(tmpSet))
				return true;
		
		return false;
	}//exist
	
	public boolean possible(int i, int j){
	    if (line[i].maxLat < line[j].minLat) return false;
	    else if (line[i].minLat > line[j].maxLat) return false;
	    else if (line[i].maxLon < line[j].minLon) return false;
	    else if (line[i].minLon > line[j].maxLon) return false;
	    else 
		    return true;
	}//possible
	public void fixLastPolygon(){
		//outer_ring_rd最后22个点
		int pSize = rebuildedPolygon[nb_polygon-1].size()-1;
		int lSize = line[13].lat.size()-1;
		//去掉最后一个节点
		rebuildedPolygon[nb_polygon-1].remove(pSize);
		
		//补充中间节点
		for (int i=lSize-22+1; i<=lSize; i++){	
			Location tmpL = new Location(line[13].lat.get(i), line[13].lon.get(i));
			rebuildedPolygon[nb_polygon-1].add(tmpL);
		}
		
		//在末尾加上第一个节点
		Location headL = rebuildedPolygon[nb_polygon-1].get(0);
		rebuildedPolygon[nb_polygon-1].add(headL);
		
		String url="jdbc:postgresql://localhost:5432/weibo_dianping";
		String usr="postgres";
		String psw="zyh3667886";
		Database getPlgn = new Database(url,usr,psw);
		
		if(getPlgn.creatConn()){
			for (int i=1; i<=3; i++){
			    String sql="select lat,lon from tmpPlgn"+i;
		        getPlgn.query(sql);
		        
		   
		        while (getPlgn.next()){
		            Location l= new Location(getPlgn.getDouble("lat"), getPlgn.getDouble("lon"));
		            rebuildedPolygon[nb_polygon].add(l);
		        }  
		        rebuildedPolygon[nb_polygon].add( rebuildedPolygon[nb_polygon].get(0)); //首尾相连
		        
		        nb_polygon++;
			}
		}	
	}//fixLastPolygon
	
	public void addPolygon(String line, int index){
        int len = line.length();
        int start=0, end=0;
        int[] rec = new int[20];
        rec[0]=-1;
        
        int nb_rec = 1;
        
        for (int i=0; i<=len-1; i++){
        	if (line.substring(i,i+1).equals(" ")){
        		rec[nb_rec]=i;
        		nb_rec++;
        	}
        }
        rec[nb_rec]=len;
        
        for (int i=0; i<=nb_rec-1; i++){
        	int intsec_index= Integer.parseInt(line.substring(rec[i]+1, rec[i+1]));
        	polygon[index].add(intsec_index);
        }
	}
	
	   public String loadPolygon(String path) throws IOException{
	    	String content="";
	    	String line;
	    	File f=new File(path);
	    	FileReader fr;	
			BufferedReader br;
	        if (!f.exists()) return "File not exist";
	        else
	        try {
			    fr=new FileReader(f);
			    br=new BufferedReader(fr);
			    line=br.readLine();
			    content="";   
			    int index=0;
			        while (line!=null){
			        	addPolygon(line, index);
			            content=content+line;
			            index++;
			            line=br.readLine();
			        }//end while 
                nb_polygon=index;
				fr.close();
				br.close();
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	        return content;
	    }//end getContent
	   
    public void linkPolygon(int i){
            
    	    //markerManager.clearMarkers();
     		spm = new SimplePolygonMarker(rebuildedPolygon[i]);
     		spm.setColor(color(255,0,0));
     		markerManager.addMarker(spm);
     	    map.addMarkerManager(markerManager);
     	    //map.draw();
    	 
    }
    
    public String getPolygonString(int index){
    	int len = rebuildedPolygon[index].size()-1;
    	String polygonString = "POLYGON((";
    	for (int i=0; i<=len; i++){
    		polygonString += rebuildedPolygon[index].get(i).getLat();
    		polygonString += " ";
    		polygonString += rebuildedPolygon[index].get(i).getLon();
    		if (i!= len) polygonString += ", ";
    	}
    	polygonString += "))";
    	
    	return polygonString;
    }
    
    public void polygonTest(){
    	String polygonString = "POLYGON((";
    	for (int i=0; i<=rebuildedPolygon[0].size()-1; i++){
    		polygonString += rebuildedPolygon[0].get(i).getLat();
    		polygonString += " ";
    		polygonString += rebuildedPolygon[0].get(i).getLon();
    		if (i!= rebuildedPolygon[0].size()-1) polygonString += ", ";
    	}
    	polygonString += "))";
    	
    	System.out.println(polygonString);
    	
    	try{
	        Class.forName( "org.postgresql.Driver" ).newInstance();
            String url = "jdbc:postgresql://localhost:5432/test" ;
            Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
            String sql="insert into polygonTest values('polygon', '" + polygonString + "' );";
            PreparedStatement st =  con.prepareStatement(sql);
            st.executeUpdate();
            
            st.close();
            con.close();
    	}catch(Exception ee){
    		System.out.print(ee.getMessage());
    	}
       
    	
    
    	/*try{
	        Class.forName( "org.postgresql.Driver" ).newInstance();
            String url = "jdbc:postgresql://localhost:5432/test" ;
            Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
            String sql="select lat, lon from shops;";
            String sql1 = "";
            Statement st =  con.createStatement();
            PreparedStatement st1;
            ResultSet rs = st.executeQuery(sql);
            ResultSet rs1;
            while (rs.next()){
                String ps ="POINT(";
                ps+=rs.getString("lat") +" "+ rs.getString("lon");
                ps+=")";
                sql1 ="insert into shops_geom values ('" + rs.getString("lat") + "' , '"+ rs.getString("lon")+"' , '" + ps+ "');";
                st1 = con.prepareStatement(sql1);
                st1.executeUpdate();
            }
            
        
            rs.close();
            st.close();
            con.close();
    	}catch(Exception ee){
    		System.out.print(ee.getMessage());
    	}*/
    System.out.println("polygonTest Done.");
    }
	/*
    public void keyPressed(){
		
		//if (!theEvent.isFrom("ControlP5window")){		
	    if (this.focused){
		  if (key == 'a' || key == 'A'){
		     linkPolygon(tmp);
		     System.out.println(tmp);
		     tmp = (tmp+1) % nb_polygon;
		     
		  }
		}//if
	}//keyPressed    
	*/
    
    public void exportPolygonToDatabase(){
    	try{
	        Class.forName( "org.postgresql.Driver" ).newInstance();
            String url = "jdbc:postgresql://localhost:5432/test" ;
            Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
            PreparedStatement st =null;
            String sql;
            
            //构建nb_polygon个表， 每个表里记录多边形每个点的坐标信息
            for (int i=0; i<=nb_polygon-1; i++){
                sql = "create table Block" + i + " (id int, lat double precision, lon double precision);" ;
                st = con.prepareStatement(sql);
                st.executeUpdate();
               
                for (int j=0; j<=rebuildedPolygon[i].size()-1; j++){
                    sql = "insert into Block" + i + " values(?,?,?)";
                    st =  con.prepareStatement(sql);
                    st.setInt(1, j);
                    st.setDouble(2, rebuildedPolygon[i].get(j).getLat());
                    st.setDouble(3, rebuildedPolygon[i].get(j).getLon());
                    st.executeUpdate();
                    
                }
            }
            
            //构建一个表，表里存每个多边形的polygonString
            sql = "create table Blocks (id int, geom geometry);";
            st = con.prepareStatement(sql);
            st.executeUpdate();
            
            
            for (int i=0; i<=nb_polygon-1; i++){
            	String polygonString = getPolygonString(i);
                sql = "insert into blocks values('" + i+ "', '" +polygonString +"');";
                st = con.prepareStatement(sql);
                st.executeUpdate();
                
            }
            
            st.close();
            con.close();
            System.out.println(nb_polygon);
    	}catch(Exception ee){
    		System.out.print(ee.getMessage());
    	}
    }//exportPolygonToDatabase

    public void addGeometryAttribute(){
        ArrayList<String> pointString = new ArrayList<String>();
    	ArrayList<String> id = new ArrayList<String>();
    	
        try{
    	    Class.forName( "org.postgresql.Driver" ).newInstance();
            String url = "jdbc:postgresql://localhost:5432/newWeiboDianping" ;
            Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
            String sql="select lat, lon, poiid  from place;";
            Statement st =  con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()){
               String ps ="POINT(";
               ps+=(rs.getDouble("lat")+0.002036d) +" "+ (rs.getDouble("lon")-0.00445d);
               ps+=")";
               pointString.add(ps);
               id.add(rs.getString("poiid"));
            }
            System.out.println(pointString.size());
            for (int i=0; i<=pointString.size()-1; i++) System.out.println(pointString.get(i) +"   " + id.get(i));
            
            PreparedStatement st1 = null;
            String sql1;
            
            //sql1 = "alter table place add geom geometry; "; 
            //st1 = con.prepareStatement(sql1);
            //st1.executeUpdate();
            
            
            for (int i=0; i<=pointString.size()-1; i++){
            	sql1 = "update place set geom = '" + pointString.get(i)+ "' where poiid = '" + id.get(i) +"'";
            	st1 = con.prepareStatement(sql1);
            	st1.executeUpdate();
            	
            }
           
            st1.close();
            rs.close();
            st.close();
            con.close();
            
	    }catch(Exception ee){
		   System.out.print(ee.getMessage());
	    }
    }//addGeometryAttribute
    
    public void linkPolygon1(ArrayList<Location> locations, int index){
        
        /*
	    //markerManager.clearMarkers();
 		spm = new SimplePolygonMarker(locations);
 		spm.setColor(color);
 		//spm.setHighlightColor(color);
		markerManager.addMarker(spm);
 	    map.addMarkerManager(markerManager);
 	    //map.draw();
	    */
    	ScreenPosition screenPos;
    	
    	stroke(color(199,82,129));
	    fill(color(199,82,129), 100);
    	beginShape();
    	for (int i=0; i<=locations.size()-1; i++){
    		screenPos = map.getScreenPosition(locations.get(i));
    		vertex(screenPos.x, screenPos.y);
    		
    	}
    	endShape(CLOSE);
    }
    public void keyPressed(){
    	System.out.println(turn_key);
    	turn_key = (turn_key+1) % nb_polygon;
    }
}//districtDividing