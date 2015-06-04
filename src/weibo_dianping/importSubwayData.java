package weibo_dianping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class importSubwayData{
   static double[] lat = new double[200000];
   static double[] lon = new double[200000];
   static int dimension;	 
	 
   public static void main(String[] args){
      String path;
      String content;
      /*for (int i=1; i<=11; i++){
    	  if ((i!=10) && (i!=11)){
             path ="d:\\data\\subwayline\\subway0"+i+".txt";
	         //System.out.println(path);
             content = "";
	         try {
	             content = getContent(path);
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	         SQL("subwayLine0"+i);
    	 }else{
    		 path ="d:\\data\\subwayline\\subway"+i+"_m.txt";
	         //System.out.println(path);
             content = "";
	         try {
	             content = getContent(path);
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	         SQL("subwayLine"+i+"_m");
	         
	         path ="d:\\data\\subwayline\\subway"+i+"_z.txt";
	         //System.out.println(path);
             content = "";
	         try {
	             content = getContent(path);
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	         SQL("subwayLine"+i+"_z");
    	 }
      }//end for i   */
    
      String currentTable="";
      for (int i=1; i<=11; i++){
          if (i<10) currentTable = "stops0"+i;
          else currentTable = "stops"+i;
          content = "";
          path="d:\\data\\subwayline\\"+currentTable+".txt";
	         try {
	             content = getContent(path);
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	         SQL(currentTable);
      }//end for i
          System.out.println("Works Done");
		    
   }//end main()
   public static String getContent(String path) throws IOException{
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
			            content=content+line;
			            lat[index] = getGeo(line,1); 
			            lon[index] = getGeo(line,2);
			            System.out.println(lat[index]);
			            System.out.println(lon[index]);
			            index++;
			            line=br.readLine();
			        }//end while 
			        dimension=index-1;
				fr.close();
				br.close();
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	        return content;
	    }//end getContent
   
   public static double getGeo(String content, int mark){
	   int len = content.length();
	   int i=0;
	   while ((i<=len) && (!content.substring(i,i+1).equals(","))) i++;
	   if (mark == 1) return Double.parseDouble(content.substring(0,i));
	   else if (mark == 2) return Double.parseDouble(content.substring(i+2,len));
	   else return 0;
	   
   }// end getGeo
   
   
   public static void SQL(String currentTable){

	   try{
	      //initialize
		  Class.forName( "org.postgresql.Driver" ).newInstance();
	      String url = "jdbc:postgresql://localhost:5432/weibo_dianping" ;
          Connection con = DriverManager.getConnection(url, "postgres" , "zyh3667886" );
         
          
          //create table
          System.out.println(currentTable);
          PreparedStatement createTable = con.prepareStatement("create table "+currentTable+" (lat double precision, lon double precision, primary key(lat,lon));");
          //createTable.setString(1,currentTable);
          createTable.executeUpdate();
          createTable.close(); 
          
          //insert geos
          for (int i=0; i<=dimension; i++){
             PreparedStatement insertGeos = con.prepareStatement("insert into "+currentTable+" values(?,?);");
             //insertGeos.setString(1, currentTable);
             insertGeos.setDouble(1, lat[i]);
             insertGeos.setDouble(2, lon[i]);
             insertGeos.executeUpdate();
             insertGeos.close();
          }
          
          
          con.close();
	   }catch(Exception ee){
	      System.out.print(ee.getMessage());
	   }finally{
		  
           
	   }
   }// end SQL
   

   
}//end importSubwayData