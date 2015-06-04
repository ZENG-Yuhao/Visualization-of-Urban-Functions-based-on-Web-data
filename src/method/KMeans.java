package method;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import dataset.Block;
import dataset.Cidsort;

public class KMeans {
	//for clusteringMap.java
	int nb_polygon=0;
	int k; 
    double mu; 
    public double[][] center; 
    public double[][] bestCenter;
    public double bestS = Double.MAX_VALUE;
    public int bestM;
    ArrayList<Block> bestBlocks = new ArrayList<Block>();
    public int repeat; 
    public double[] crita;
    public double[]  maxFacility = new double[18];
    public double[] maxRow = new double[200];
    public int beginHour = 0, endHour = 23;
    public double maxAll;
    ArrayList<Cidsort> gy = new ArrayList<Cidsort>();
    public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public double[][] getCenter() {
		return center;
	}

	public void setCenter(double[][] center) {
		this.center = center;
	}

	public ArrayList<Cidsort> bubblesort(ArrayList<Cidsort> arr){
		int len=arr.size();
		Collections.sort(arr);
		return arr;
    }
    

    public double editDistance(ArrayList<Double> vector, double[] center, int len, double area){
    	double dist=0;
    	for (int i=0;i<len;i++)
    	{
    		double l=(vector.get(i) - center[i]);
            //l=l*max(vector.get(i),center[i]);
           
    		dist+= l*l;
    	}
        
    	return dist;
    }
    
    public double editDistance(ArrayList<Double> vector, double[] center, int len){
    	double dist=0;
    	for (int i=0;i<len;i++)
    	{
    		double l=(vector.get(i) - center[i]);
            //l=l*max(vector.get(i),center[i]);
           
    		dist+= l*l;
    	}
        
    	return dist;
    }

    public double max(double a, double b){
    	if (a>b) return a;
    	else return b;
    	
    }
    
  
    public double editDistance(double[] vector, double[] center, int len){
    	double dist=0;
    	for (int i=0;i<len;i++)
    	{
    		double l=vector[i]-center[i];
    		dist+= l*l;
    	}
    	
    	return dist;
    }

    public KMeans(int k, double mu, int repeat, int len) {
        this.k = k;
        this.mu = mu;
        this.repeat = repeat;
        center = new double[k][];
        for (int i = 0; i < k; i++)
            center[i] = new double[len];
        crita = new double[repeat];

    }
    
    public void setHourRange(int beginHour, int endHour){
    	this.beginHour = beginHour;
    	this.beginHour = endHour;
    }
    
    public void initializeCenter(int len, ArrayList<Block> objects) {
        Random random = new Random(System.currentTimeMillis());
        int[] count = new int[k];
        Iterator<Block> iter = objects.iterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            int id = random.nextInt(100)%k; 
            block.setCid(id);
            count[id]++;
            for (int i = 0; i < len; i++){
            	center[id][i]+= block.getFacilityScore().get(i);
            }
        }

        for (int id = 0; id < k; id++) {
            for (int j = 0; j < len; j++) {
                center[id][j] /= count[id];
            }
        }
    }

    public void classify(ArrayList<Block> objects) {
        Iterator<Block> iter = objects.iterator();
        while (iter.hasNext()) {
            Block object = iter.next();
            ArrayList<Double> vector = object.getFacilityScore();
            int len = vector.size();
            int index = 0;//最小距离的中心的编号
            double neardist = Double.MAX_VALUE; //最小的距离
            for (int i = 0; i < k; i++) {
                double dist = editDistance(vector, center[i], len, object.area); //计算编辑距离
                if (dist < neardist) {
                    neardist = dist;
                    index = i;
                }
            }
            object.setCid(index);//将该区块的归属中心编号Cid设置为最近中心的中心编号index
            
        }
    }

    public boolean calNewCenter(ArrayList<Block> blocks, int len) {
        boolean end = true;
        double [] count = new double[k]; // 记录每个聚类中心的簇中区块个数
        double[][] sum = new double[k][];
        for (int i = 0; i < k; i++)
            sum[i] = new double[len];
        Iterator<Block> iter = blocks.iterator();
        while (iter.hasNext()) {
        	Block block = iter.next();
            int id = block.getCid();
            count[id]++;
            for (int i = 0; i < len; i++)
                sum[id][i] += block.getFacilityScore().get(i);
        }

        for (int id = 0; id < k; id++) {
            if (count[id] != 0) {
                for (int j = 0; j < len; j++) {
                    sum[id][j] /= count[id];
                }
            }
            // 调整质心
            else {          
                int a=(id+1)%k;
                int b=(id+3)%k;
                int c=(id+5)%k;
                for (int j = 0; j < len; j++) {
                    center[id][j] = (center[a][j]+center[b][j]+center[c][j])/3;
                }

            }

        }

        for (int i = 0; i < k; i++) {
        	double dist=editDistance(sum[i], center[i], len);
        	//System.out.println("distance: " + dist);
            if (dist>= mu) {
                end = false;
                break;
            }
            
        }

        if (!end) {
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < len; j++)
                    center[i][j] = sum[i][j];
            }
        }
        
        return end;
        
    }

    public double getSatisfaction(ArrayList<Block> objects, int len) {

        double satisfy = 0.0;
        double[] count = new double[k];
        double[] ss = new double[k];
        Iterator<Block> iter = objects.iterator();
        while (iter.hasNext()) {
        	Block object = iter.next();
            int id = object.getCid();
            count[id]++;
            for (int i = 0; i < len; i++)
                ss[id] += Math.pow(object.getFacilityScore().get(i)- center[id][i], 2.0);
        }
        
        for (int i = 0; i < k; i++) {
            satisfy += ss[i]/count[i];
        	//satisfy += ss[i];
             
        }
        return satisfy;
    }

 

    public double run(ArrayList<Block> blocks, int len) {
    	for (int i=1; i<=repeat; i++){
    		center = new double[k][];
            for (int s = 0; s < k; s++)
                center[s] = new double[len];
            
            long count=0;
            initializeCenter(len,blocks);
            classify(blocks);     
            while (!calNewCenter(blocks, len) && count<=1e6) {
                classify(blocks);
                count++;
            }
            double ss = getSatisfaction(blocks, len);
            System.out.println(i+"次 加权方差：" + ss);
            if (ss < bestS){
            	bestM = i;
            	bestS = ss;
            	bestCenter = center;
            	bestBlocks.clear();
            	for (int m=0; m<=blocks.size()-1; m++){
            		bestBlocks.add(blocks.get(m));
            	}
            }
    	}//for i
    	System.out.println("BestM:" + bestM);
    	System.out.println("BestS:" + bestS);
    	return bestS;
    }
    
    public Block InitBlock(int id,String cates[],String choise, String polygonString, double area){
    	String url="jdbc:postgresql://localhost:5432/newWeiboDianping";
		String usr="postgres";
		String psw="123";
		String sql = null;
		if (choise.equals("place"))
		    //sql = "select count(*) from place where st_within(place.geom, st_geomfromtext('" + polygonString+"'))";
			sql = "select sum(checkin_num) from place where st_within(place.geom, st_geomfromtext('" + polygonString+"'))";
	    else
			 sql="select count(*) from weibo,place where weibo.poiid = place.poiid and weibo.hour>="+ beginHour + " and weibo.hour<=" + endHour + " and st_within(place.geom, st_geomfromtext('" + polygonString+"'))";
		Database weibo=new Database(url,usr,psw);
		ArrayList<Integer> facility=new ArrayList<Integer>();
		if(weibo.creatConn()){
			for(String cate:cates){
				String sql_1 = sql + " and newcate='" +cate+"'";
				weibo.query(sql_1);
				while (weibo.next()){
					int number=weibo.getInt(1);
					facility.add(number);
					}
				}
			
		}		
		weibo.closeRs();
		weibo.closeStm();
		weibo.closeConn();
		Block block=new Block(id,facility);
		block.polygonString = polygonString;
		block.area = area;
		return block;
		
    }
    
    public ArrayList<Block> KMeansData(int len,String cates[],String choise){
    	ArrayList<Block> blocks=new ArrayList<Block>();
        int index = 0;
        double minsa = Double.MAX_VALUE;
    	String url="jdbc:postgresql://localhost:5432/newWeiboDianping";
		String usr="postgres";
		String psw="123";
        Database getPlgStr = new Database(url,usr,psw);
        if(getPlgStr.creatConn()){
			String sql="select id, st_astext(geom), st_area(geom) from blocks";
		    getPlgStr.query(sql);
		    int count=0;
		   
		    while (getPlgStr.next()){
		       int blockid= getPlgStr.getInt("id");
			   String polygonString = getPlgStr.getString(2);
			   double area = getPlgStr.getDouble(3)*1e5;
			   System.out.println(area);
			   blocks.add(InitBlock(blockid, cates, choise, polygonString, area));			 
			   count++;
		    }
		
		    nb_polygon = count;
		    
		    //归一化参数计算
		    int[] maxNum = new int[18];
		    
		    maxAll = Double.MIN_VALUE;
		    for (int i=0; i<blocks.size()-1; i++) maxRow[i] = Double.MIN_VALUE;
		    for (int j=0; j<=k-1; j++) maxFacility[j] = Double.MIN_VALUE;
		    
		    for (int j=0; j<=k-1; j++) maxFacility[j] = Double.MIN_VALUE;
		    for (int i=0; i<=blocks.size()-1; i++){
		    	for (int j=0; j<=len-1; j++){
		    		
		    		
		    		double num = blocks.get(i).getFacility().get(j);
		    		double density = num/ blocks.get(i).area;
		     		
		     		if (density> maxFacility[j])
		     			maxFacility[j] = density;
		     		
		            if (maxFacility[j] > maxAll)
		            	maxAll = maxFacility[j];
		    		
		    	}
		    }	
		    
		    for (int i=0; i<=blocks.size()-1; i++){
		    	for (int j=0; j<=len-1; j++){
		    		  int facility = blocks.get(i).getFacility().get(j);
		    	      double density = facility/ blocks.get(i).area;
		    	      double scaleProportion = maxAll/maxFacility[j]; 
		    		  //double density = facility;
		    		  //double scaleProportion = 1.0;
		    	      double value = density * scaleProportion;
                      if (value > maxRow[i])
                    	  maxRow[i] = value;
		    	      
                      
                          blocks.get(i).getFacilityValue().add(value);
                      
		    	}
		    }	
		    
		    for (int i=0; i<=blocks.size()-1; i++){
		    	for (int j=0; j<=len-1; j++){
		    		  //double score = blocks.get(i).getFacilityValue().get(j)/maxFacility[j]*1000;
		    		   double score = blocks.get(i).getFacilityValue().get(j)/maxRow[i]*1000;
		    		 //if (j!=17 && j!=15 && j!=13 && j!=12)
		    		      blocks.get(i).getFacilityScore().add(score);
		    		 //else 
		    		  //	  blocks.get(i).getFacilityScore().add(0.0);
		    	}
		    }	
		    System.out.print("MaxFacility: ");
		    for (int i=0; i<=len-1; i++) System.out.print(maxFacility[i]+" ");
		    System.out.println("");
		    /*
		    for (int i=0; i<=blocks.size()-1; i++){
		    	for (int j=0; j<=blocks.get(i).getFacilityScore().size()-1; j++){
		    		System.out.print(new BigDecimal(blocks.get(i).getFacilityScore().get(j)).setScale(0, BigDecimal.ROUND_HALF_UP)+", ");
		    	}
		    	System.out.println("");
		    	
		    }
		    
		    for (int i=0; i<=blocks.size()-1; i++){
		    	for (int j=0; j<=blocks.get(i).getFacilityScore().size()-1; j++){
		    		System.out.print(new BigDecimal(blocks.get(i).getFacility().get(j)).setScale(0, BigDecimal.ROUND_HALF_UP)+", ");
		    	}
		    	System.out.println("");
		    	
		    }
            */
		}			
		getPlgStr.closeRs();
		getPlgStr.closeStm();
		getPlgStr.closeConn();
        
          		
        double ss = run(blocks, len);
        return bestBlocks;
    }

}
