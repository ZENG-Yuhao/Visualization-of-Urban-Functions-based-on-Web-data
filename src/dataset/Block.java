package dataset;

import java.util.ArrayList;

import de.fhpotsdam.unfolding.geo.Location;

public class Block {
	private int blockid;
	private int cid=-1;
	private ArrayList<Integer> facility=new ArrayList<Integer>();
	public ArrayList<Double> facilityScore = new ArrayList<Double>();
	public ArrayList<Double> facilityValue = new ArrayList<Double>();
	public ArrayList<Location> polygon = new ArrayList<Location>();
	public String polygonString;
	public int idInDatabase;
	public double area;

	public Block(int blockid,ArrayList<Integer> facility){
		this.blockid=blockid;
		this.facility=facility;
	}
	public Block(int blockid,int cid,int x){
		this.cid=cid;
		this.blockid=blockid;
		this.facility.add(x);
	}
	
	public void addfacility(int a){
		this.facility.add(a);
	}
	public int getBlockid() {
		return blockid;
	}
	public void setBlockid(int blockid) {
		this.blockid = blockid;
	}
	public ArrayList<Integer> getFacility() {
		return facility;
	}
	public ArrayList<Double> getFacilityScore() {
		return facilityScore;
	}
	public ArrayList<Double> getFacilityValue() {
		return facilityValue;
	}
	public void setFacility(ArrayList<Integer> facility) {
		this.facility = facility;
	}
	public void setFacilityScore(ArrayList<Double> facilityScore) {
		this.facilityScore = facilityScore;
	}
	public void setFacilityValue(ArrayList<Double> facilityValue) {
		this.facilityValue = facilityValue;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	
}
