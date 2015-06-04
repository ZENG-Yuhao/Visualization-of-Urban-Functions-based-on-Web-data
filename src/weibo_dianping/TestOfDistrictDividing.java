package weibo_dianping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestOfDistrictDividing{
	
	static int nb_polygon = 0;
	static int intsecSize = 10;
	static ArrayList<Integer>[] polygon = new ArrayList[2000000];
	static boolean[][] adjMatrix = new boolean[2000][2000];
	static boolean[] arrived = new boolean[intsecSize];
	static ArrayList<Integer> route = new ArrayList<Integer>();
	static int nb_rings = 0;
	static Set<Integer>[] ring = new HashSet[2000000];
	static Set<Integer> tmpSet = new HashSet();
	static{
		for (int i=0; i<=intsecSize-1; i++) arrived[i]=false;
		for (int i=0; i<=200000-1; i++) ring[i]=new HashSet();
	}
	
	public static void main(String[] args){
		buildAdjacencyMatrix();
		findRings();
		
	}
	public static void buildAdjacencyMatrix(){
	    for (int i=0; i<=9; i++)
	    	for (int j=0; j<=9; j++)
	    		adjMatrix[i][j] = false;
	    
		adjMatrix[0][1] = true;  adjMatrix[0][2] = true;  adjMatrix[0][3] = true;
		adjMatrix[1][0] = true;  adjMatrix[1][4] = true;  adjMatrix[1][5] = true;
		adjMatrix[2][0] = true;  adjMatrix[2][6] = true;  adjMatrix[2][7] = true; adjMatrix[2][3] = true;
		adjMatrix[3][0] = true;  adjMatrix[3][2] = true;
		adjMatrix[4][1] = true;  adjMatrix[4][8] = true;
		adjMatrix[5][1] = true;  adjMatrix[5][8] = true;
		adjMatrix[6][2] = true;  adjMatrix[6][8] = true;  adjMatrix[6][9] = true;
		adjMatrix[7][2] = true;  adjMatrix[7][9] = true;
		adjMatrix[8][4] = true;  adjMatrix[8][5] = true;  adjMatrix[8][6] = true;
		adjMatrix[9][6] = true;  adjMatrix[9][7] = true;
	}//buildAdjacencyMatrix
	
	public static void findRings(){
		
		for (int start=0; start<=intsecSize-1; start++){
			//initialize();
			route.add(start); 
			tmpSet.add(start);
	        for (int i=0; i<=intsecSize-1; i++){
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
	}//findRings
	
	public static void dfs(int present, int last, int target){
		if (present == target && !exist()){
			
			System.out.print("Ring " + nb_rings +":");
			for (int j=0; j<=route.size()-1; j++) {
				ring[nb_rings].add(route.get(j));
				System.out.print(route.get(j)+" ");
			}
			System.out.println("");
			nb_rings++;
		}else{
			for (int i=0; i<=intsecSize-1; i++){
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
	
	public static boolean exist(){
		for (int i=0; i<=nb_rings; i++)
			if (ring[i].equals(tmpSet))
				return true;
		
		return false;
	}
	
}//TestOfDistrictDividing