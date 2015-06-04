package method;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import dataset.Block;
import dataset.Cidsort;
import dataset.Point;
import dataset.Weibo;

public class KMeans1 {
	//for dbscanMap.java
	int nb_polygon=0;
	int k; 
    double mu; 
    public Point[] center; 
    public double[] centerRadius; 
    public Point[] bestCenter;
    public ArrayList<Point> kernels;
    public double[] radius;
    public double bestS = Double.MAX_VALUE;
    public int bestM;
    public ArrayList<Point> bestSolution = new ArrayList<Point>();
    public int repeat; 
    public double[] crita;
    public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public Point[] getCenter() {
		return center;
	}

	public void setCenter(Point[] center) {
		this.center = center;
	}

	public void setRadius(double[] radius){
		this.radius = radius;
	}
	
	public double[] getRadius(){
		return radius;
	}
	public ArrayList<Cidsort> bubblesort(ArrayList<Cidsort> arr){
		int len=arr.size();
		Collections.sort(arr);
		return arr;
    }
    


    private double editDistance(Point p, Point c){
    	double dist=0;
    	dist = Math.sqrt(  Math.pow(p.x - c.x, 2.0) + Math.pow(p.y - c.y, 2.0)  );
        
    	return dist;
    }
    
    private double editDistance1(Point p, Point c){
    	double dist=0;
    	dist = Math.sqrt(  Math.pow(p.x - c.x, 2.0) + Math.pow(p.y - c.y, 2.0)  );
        dist = dist-radius[p.pid];
        if (dist>0.0) return dist;
        else return 0.0;
    }

    private double editDistance2(Point p, Point c){
    	double dist=0;
    	dist = Math.sqrt(  Math.pow(p.x - c.x, 2.0) + Math.pow(p.y - c.y, 2.0)  );

    	//System.out.println(p.pid+ " ::: "+c.pid);
    	return dist-radius[p.pid]-centerRadius[c.pid];
    }
    public double max(double a, double b){
    	if (a>b) return a;
    	else return b;
    	
    }
    
    public ArrayList<Point> getSolution(){
    	
    	return bestSolution;
    }
  


    public KMeans1(int k, double mu, int repeat, ArrayList<Point> kernels, double[] radius) {
        this.k = k;
        this.mu = mu;
        this.repeat = repeat;
        this.kernels = kernels;
        this.radius = radius;
        
        center = new Point[k];
        for (int i=0; i<k; i++)  center[i] = new Point();
        
        centerRadius = new double[k];
        crita = new double[repeat];

    }
    
    public void run() {
    	for (int i=1; i<=repeat; i++){
    		System.out.println(i+"--->");
    		center = new Point[k];
    		centerRadius = new double[k];
    		for (int j=0; j<k; j++) { 
    			center[j] = new Point();  
    			center[j].pid = j;
    		
    		}
    		
            long count=0;
            initializeCenter(kernels);
            //printf();
            classify(kernels);
            //printf();
            while (!calNewCenter(kernels)) {
                classify(kernels);
                count++;
                //System.out.println(count);
            }
            double ss = getSatisfaction(kernels);
            System.out.println("加权方差：" + ss);
            if (ss <= bestS){
            	bestM = i;
            	bestS = ss;
            	bestCenter = center;
            	bestSolution.clear();
            	for (int m=0; m<=kernels.size()-1; m++){
            		bestSolution.add(kernels.get(m));
            	}
            }
    	}//for i
    	System.out.println("BestM:" + bestM);
    	System.out.println("BestS:" + bestS);
    	
    }
    
    public void printf(){
    	for (int i=0; i<kernels.size(); i++){
    		System.out.println("kernel"+i+": "+ kernels.get(i).cid);
    	}
    }
    
    private boolean exist(int pid){
    	boolean state = false;
    	for (int i=0; i<k; i++){
    		if (editDistance1(kernels.get(pid),center[i])<=0.005){
    			state = true;
    		}
    	}
    	return state;
    }
    private void initializeCenter(ArrayList<Point> points) {
    	//System.out.println("initializeCenter");
        Random random = new Random(System.currentTimeMillis());
        double[] count = new double[k];
        
        /*
        Iterator<Point> iter = points.iterator();
        while (iter.hasNext()) {
            Point point = iter.next();
            int id = random.nextInt(100)%k; 
            point.cid = id;
            count[id]++;
            
            
            center[id].x+= point.x;
            center[id].y+= point.y;
        }
        
        for (int id = 0; id < k; id++) {
                    center[id].x /= count[id];
                    center[id].y /= count[id];
        }
        */
        
        
        for (int i=0; i<k; i++){
        	
        	int rpid = random.nextInt(1000)% points.size();
        	//int rpid = points.size();
        	while (exist(rpid)){
        		random = new Random(System.currentTimeMillis());
        		rpid = random.nextInt(1000)% points.size();
        	}
        	center[i].x = points.get(rpid).x;
        	center[i].y = points.get(rpid).y;
        }
        
    }
    


    private void classify(ArrayList<Point> points) {
    	//System.out.println("Classify");
    	//calCenterRadius(points);
    	
        Iterator<Point> iter = points.iterator();
        while (iter.hasNext()) {
            Point point = iter.next();
            int index = 0;//最小距离的中心的编号
            double neardist = Double.MAX_VALUE; //最小的距离
            for (int i = 0; i < k; i++) {
                double dist = editDistance1(point, center[i]); //计算编辑距离
                if (dist < neardist) {
                    neardist = dist;
                    index = i;
                }
            }
            point.cid = index;//将该区块的归属中心编号Cid设置为最近中心的中心编号index
            
        }
        //for (int i=0; i<k; i++) System.out.println(center[i].x +"  " + center[i].y);
    }

    private boolean calNewCenter(ArrayList<Point> points) {
    	//System.out.println("caculate NewCenter.");
        boolean end = true;
        double [] count = new double[k]; // 记录每个聚类中心的簇中区块个数
        Point[] sum = new Point[k];
        for (int i=0; i<k; i++) {sum[i]=new Point(); sum[i].pid = i;}
        Iterator<Point> iter = points.iterator();
        while (iter.hasNext()) {
        	Point point = iter.next();
            int id = point.cid;
            
            count[id]+= Math.pow(radius[point.pid], 2.0) * Math.PI;
            sum[id].x += point.x * Math.pow(radius[point.pid], 2.0) * Math.PI;
            sum[id].y += point.y * Math.pow(radius[point.pid], 2.0) * Math.PI;
            
            /*
            count[id]++;
            sum[id].x += point.x;
            sum[id].y += point.y;
            */
        }

        for (int id = 0; id < k; id++) {
            if (count[id] != 0.0) {
                    sum[id].x /= count[id];
                    sum[id].y /= count[id];
                
            }
            // 调整质心
            else {          
               /*
            	int a=(id+1)%k;
                int b=(id+3)%k;
                int c=(id+5)%k;
                
                sum[id].x = (center[a].x+center[b].x+center[c].x)/3;
                sum[id].y = (center[a].y+center[b].y+center[c].y)/3;
                */
            	
            	Random random = new Random(System.currentTimeMillis());
            	int rpid = random.nextInt(1000)% points.size();
            	while (exist(rpid)){
            		random = new Random(System.currentTimeMillis());
            		rpid = random.nextInt(1000)% points.size();
            	}
            	sum[id].x = points.get(rpid).x;
            	sum[id].y = points.get(rpid).y;
                
            }

        }

        for (int i = 0; i < k; i++) {
        	double dist=editDistance(sum[i], center[i]);
        	//System.out.println("distance: " + dist);
            if (dist>= mu) {
                end = false;
                break;
            }
            
        }

        if (!end) {
            for (int i = 0; i < k; i++) {
                center[i] = sum[i];
            }
        }
        
        return end;
        
    }

    private double getSatisfaction(ArrayList<Point> points) {

        double satisfy = 0.0;
        int[] count = new int[k];
        double[] ss = new double[k];
        Iterator<Point> iter = points.iterator();
        while (iter.hasNext()) {
        	Point point = iter.next();
            int id = point.cid;
            count[id]++;
                ss[id] += Math.pow(editDistance(point, center[id]), 2.0) ;
                //ss[id] += editDistance1(point, center[id]) * Math.pow(radius[point.pid], 2.0) * Math.PI;;
        }
        
        for (int i=0; i<k; i++)
        	if (count[i]!=0)
                satisfy += ss[i]/count[i];
        	else {
        		satisfy = Double.MAX_VALUE;
        	    break;
        	}
        
        return satisfy;
    }

 


    
   
    


}