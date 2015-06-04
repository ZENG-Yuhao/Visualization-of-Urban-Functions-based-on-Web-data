package weibo_dianping;

import java.util.ArrayList;

import method.DBSCAN;
import method.KMeans1;
import dataset.AdjacencyList;
import dataset.DBSCAN_Clusters;
import dataset.Point;
import dataset.Weibo;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

public class dbscanMap1 extends PApplet{
	
	//Map
	public static UnfoldingMap map;
	
	
	//Cluster
	public static DBSCAN dbscan;
	public static DBSCAN_Clusters dbclusters;
	public static AdjacencyList adjList;
	public static int numclusters;
	public static int j=0;
	public static Weibo[] kernels;
	public static double[] radius;
	public static double[] radius1;
	public static ArrayList<Point> points = new ArrayList<Point>();
	public static KMeans1 kmeans;
	public static String[] targets = new String[11]; 
	//map
	public static int[] colors=new int[20];
	public static double minLat, minLon, maxLat, maxLon, interval;
	public static int assisX, assisY;
	public static double Eps=0.0025;
	public static int MinPts=16265;
	public static int k=30;
	
	/*  餐饮   0.0025  70
	          公共交通
	          公司
	          购物
	          快餐
	          生活娱乐
	          休闲
	          学校
	          医院
	          运动户外
	          住宿
	          住宅
	*/
	{
		
		
    	minLat = 31.12283;    
    	minLon = 121.31982;  
    	//maxLat = 31.38283;  
    	//maxLon = 121.65482;  
    	maxLat = 31.383453;
    	maxLon = 121.65763;
    	//interval = 0.005;		
    	//assisX = 52;
    	//assisY = 67;
    	interval = Eps;
    	assisX= (int)Math.floor((maxLat-minLat)/interval);
    	assisY= (int)Math.floor((maxLon-minLon)/interval);
    	
    	double tmpx = (maxLat-minLat)/interval;
    	double tmpy = (maxLon-minLon)/interval;
    	
    	maxLat-=interval*(tmpx-assisX);
    	maxLon-=interval*(tmpy-assisY);
    	
    	
		colors[0]=color(126,211,207);
		colors[1]=color(215,76,51);
		colors[2]=color(200,80,202);
		colors[3]=color(208,211,61);
		colors[4]=color(63,61,39);
		colors[5]=color(206,68,121);
		colors[6]=color(68,74,113);
		colors[7]=color(193,131,118);
		colors[8]=color(90,127,49);
		colors[9]=color(200,142,57	);
		colors[10]=color(112,213,74);
		colors[11]=color(199,137,189);
		colors[12]=color(111,214,151);
		colors[13]=color(118,107,203);
		colors[14]=color(132,164,205);
		colors[15]=color(126,56,36);
		colors[16]=color(207,199,171);
		colors[17]=color(89,126,109);
		colors[18]=color(193,207,119);
		colors[19]=color(99,43,76);
		
		targets[0]="餐饮";
		targets[1]="公共交通";
		targets[2]="公司";
		targets[3]="购物";
		targets[4]="快餐";
		targets[5]="生活娱乐";
		targets[6]="休闲";
		targets[7]="学校";
		targets[8]="医院";
		targets[9]="运动户外";
		targets[10]="住宿";
	}
	
	public void setup(){
		size(1366, 768);
		map = new UnfoldingMap(this,new OpenStreetMap.OpenStreetMapProvider());
		map.zoomAndPanTo(new Location(31.2f, 121.4f), 11);
		map.setPanningRestriction(new Location(31.2f,121.4f), 100);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		dbscan = new DBSCAN(Eps, MinPts, "餐饮");
		dbscan.run();
		dbclusters = dbscan.getClusters();
		numclusters = dbclusters.getNumCluster();
		adjList = dbscan.getAdjList();
		radius1 = new double[numclusters];
	    calKernels();
	    calRadius();
	    kmeans();
	}
	
	public void draw(){
		map.draw();
		//draw_data();
		//draw_block();
        //draw_data1();
	    draw_kmeans();
	
	}
	
	public void draw_kmeans(){
		//kernels
		/*
		for (int j=0; j<numclusters; j++){
			Location l= new Location (points.get(j).x, points.get(j).y);
	    	ScreenPosition pos = map.getScreenPosition(l);
	    	//noStroke();
		    fill(colors[points.get(j).cid]);
		    strokeWeight(1);
	    	ellipse(pos.x, pos.y, 8,8);
	    	
		}
		*/
		for (int j=0; j<numclusters; j++){
			double r=0.0;
			Location k = new Location(kernels[j].lat, kernels[j].lon);
			ScreenPosition posk = map.getScreenPosition(k);
			for (int i=0; i<dbclusters.getMembers(j).size(); i++){
		    	Location l= new Location (dbclusters.getMembers(j).get(i).lat, dbclusters.getMembers(j).get(i).lon);
		    	ScreenPosition pos = map.getScreenPosition(l);
				double xx=Math.pow(posk.x - pos.x , 2.0);
				double yy=Math.pow(posk.y - pos.y , 2.0);
				//if (r<Math.sqrt(xx+yy))  r=Math.sqrt(xx+yy);
			    r+=Math.sqrt(xx+yy);
			}
			r/=dbclusters.getMembers(j).size();
			
			radius1[j]=2*r;
			//System.out.println(radius[j]);
		}
		
		for (int j=0; j<numclusters; j++){
			Location l= new Location (kernels[j].lat, kernels[j].lon);
	    	ScreenPosition pos = map.getScreenPosition(l);
	    	//noStroke();
	    	//fill(colors[points.get(j).cid]);
	    	fill(0, 102, 153, 120);
	    	//noFill();
	    	strokeWeight(1);
	    	if(radius1[j]>5)
	    	    ellipse(pos.x, pos.y, (float)radius1[j], (float)radius1[j]);
	    	else 
	    		ellipse(pos.x, pos.y, 5, 5);
		}
		
		Point[] kcenter = kmeans.getCenter();
		for (int i=0; i<k; i++){
			Location l= new Location (kcenter[i].x, kcenter[i].y);
	    	ScreenPosition pos = map.getScreenPosition(l);
	    	strokeWeight(1);
	    	fill(255,0,0,255);
	    	ellipse(pos.x, pos.y, 5,5);
	    	for (int j=0; j<numclusters; j++){
	    		if (points.get(j).cid == i){
	    		Location l1= new Location (points.get(j).x, points.get(j).y);
	    	    ScreenPosition pos1 = map.getScreenPosition(l1);
		        strokeWeight(1);
		        line(pos.x, pos.y, pos1.x, pos1.y);
	    		}
	    	}
		}
	}
	public void kmeans(){
		System.out.println("Start Kmeans.");
		for (int i=0; i<numclusters; i++){
			Point p = new Point();
			p.pid = i;
			p.x = kernels[i].lat;
			p.y = kernels[i].lon;
			points.add(p);
		}
		System.out.println(points.size());
		kmeans = new KMeans1(k, 1e-20, 100, points, radius);
		kmeans.run();
		points = kmeans.getSolution();
		
	}
	
	
	public void draw_block(){
        for (double i=minLat; i<=maxLat; i+=interval){
        	Location l1= new Location (i, minLon);
	    	ScreenPosition pos1 = map.getScreenPosition(l1);
	    	Location l2= new Location (i, maxLon);
	    	ScreenPosition pos2 = map.getScreenPosition(l2);
	    	//stroke(0,0,0,0);
	    	strokeWeight(1);
        	fill(0,0,0,0);
        	line(pos1.x, pos1.y, pos2.x, pos2.y);
        }
        
        for (double j=minLon; j<=maxLon; j+=interval){
        	Location l1= new Location (minLat, j);
	    	ScreenPosition pos1 = map.getScreenPosition(l1);
	    	Location l2= new Location (maxLat, j);
	    	ScreenPosition pos2 = map.getScreenPosition(l2);
	    	//stroke(0,0,0,0);
	    	strokeWeight(1);
	    	//fill(0,0,0,0);
        	line(pos1.x, pos1.y, pos2.x, pos2.y);
        }

	}//drwa_block

	public void calKernels(){
		kernels = new Weibo[numclusters];
		radius = new double [numclusters];
		
		for (int j=0; j<numclusters; j++){
			kernels[j]= new Weibo();
		    double lat=0.0; 
		    double lon=0.0;
			for (int i=0; i<dbclusters.getMembers(j).size(); i++){
		    	lat += dbclusters.getMembers(j).get(i).lat;
		    	lon += dbclusters.getMembers(j).get(i).lon;
		    }
			lat /= dbclusters.getMembers(j).size();
			lon /= dbclusters.getMembers(j).size();
			
			kernels[j].lat = lat;
			kernels[j].lon = lon;
		}
		
		//caculate radius
		
		
	}
	
	public void calRadius(){
		//caculate radius
		radius = new double[numclusters];
		for (int j=0; j<numclusters; j++){
			double r=0.0;
			for (int i=0; i<dbclusters.getMembers(j).size(); i++){
				double xx=Math.pow(kernels[j].lat - dbclusters.getMembers(j).get(i).lat , 2.0);
				double yy=Math.pow(kernels[j].lon - dbclusters.getMembers(j).get(i).lon , 2.0);
				//if (r<Math.sqrt(xx+yy))  r=Math.sqrt(xx+yy);
			    r+=Math.sqrt(xx+yy);
			}
			r/=dbclusters.getMembers(j).size();
			
			radius[j]=r;
			//System.out.println(radius[j]);
		}
	}
    public void mousePressed(){
    	j=(j+1) % dbclusters.getNumCluster();
    }
}//dbscanMap