package dataset;

import java.util.ArrayList;

public class DBSCAN_Clusters{
	private class Members{
		public ArrayList<Weibo> member = new ArrayList<Weibo>();
	}//class Members
	
	private int numCluster =0;
	private ArrayList<Members> clusters = new ArrayList<Members>();
	
	
	public void newCluster(Weibo wb){
	    numCluster++;
	    Members m = new Members();
	    m.member.add(wb);
	    clusters.add(m);
	}
	public void addMember(int id, Weibo wb){
	    clusters.get(id).member.add(wb);	
	}
	
	public ArrayList<Weibo> getMembers(int id){
		return clusters.get(id).member;
	}
	
	public int getNumCluster(){
		return numCluster;
	}
}//DBSCAN_Clusters