package dataset;

import java.util.ArrayList;

public class AdjacencyList {
	private int length_list;
	private ArrayList<Weibo>[] list;
	private boolean[] beenClustered;
    private int[] numKernel;
    
	public AdjacencyList(int len){
		length_list = len;
		list = new ArrayList[len];
		for (int i=0; i<len; i++)
			list[i] = new ArrayList<Weibo>();
		
		beenClustered = new boolean[len];
	    numKernel = new int[len];
	    
	}//constructor
	
	public void setListElement(int id, Weibo object){
		list[id].add(object);
	}
	
	public ArrayList<Weibo> getListElement(int id){
		return list[id];
	}
	public void setStateOfBeenClustered(int id, boolean state){
		beenClustered[id] = state;
	}
	
	public void addNumKernel(int id, int num){
		numKernel[id] += num;
	}
	public void addNumKernel(int id){
		numKernel[id]++;
	}
	
	public boolean isClusterable(int id){
		if (beenClustered[id])
		    return false;
		else 
			return true;
	}
	
	public boolean isKernel(int id, int num){
		if (numKernel[id]>=num)
			return true;
		else 
			return false;
	}
	
}//AdjacencyList