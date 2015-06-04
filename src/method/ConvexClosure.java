package method;

import java.util.ArrayList;
import java.util.Iterator;

import dataset.Point;

public class ConvexClosure{
	
	private ArrayList<Point> datas; 
	private ArrayList<Point> ConvexClosure = new ArrayList<Point>();
	
	public ConvexClosure(ArrayList<Point> datas){
		this.datas = datas;
		
	}//constructor
	
	public ArrayList<Point> caculate(){
		Iterator<Point> iter = datas.iterator();
		while (iter.hasNext()){
			Point point = iter.next();
			if (isConvexClosure(point)){
				
			}
		}
	    return ConvexClosure;	
	}
	
	private boolean isConvexClosure(Point p){
		Iterator<Point> iter = datas.iterator();
		Point lastp = ConvexClosure.get(ConvexClosure.size()-1);
		while (iter.hasNext()){
			
		}
		
		return false; 
	}
}//end class