package method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
public class Database {

	private String drv="org.postgresql.Driver";
	private String url="jdbc:postgresql://localhost:5432/WeiboData";
	private String usr="postgres";
	private String pwd="";
	private Connection conn=null;
	private Statement stm=null;
	private ResultSet rs=null;
	public Database(String url,String usr,String pwd){
		this.url=url;
		this.usr=usr;
		this.pwd=pwd;
		this.drv="org.postgresql.Driver";
	}
	public String getDrv() {
		return drv;
	}
	public void setDrv(String drv) {
		this.drv = drv;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsr() {
		return usr;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public Statement getStm() {
		return stm;
	}
	public void setStm(Statement stm) {
		this.stm = stm;
	}
	public ResultSet getRs() {
		return rs;
	}
	public void setRs(ResultSet rs) {
		this.rs = rs;
	}
	
	public boolean creatConn(){
		boolean b= false;
		try{
			Class.forName(drv).newInstance();
			conn=DriverManager.getConnection(url, usr, pwd);
			b=true;
			//System.out.println("create conn successfully");
		}catch(SQLException e){System.out.println(e);}
		catch(ClassNotFoundException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
		catch(InstantiationException e){e.printStackTrace();}
		return b;
	}
	public boolean update(String sql){
		boolean b=false;
		try{
			stm=conn.createStatement();
			stm.execute(sql);
			b=true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return b;
	}
	public void query(String sql){
		try{
			stm=conn.createStatement();
			rs=stm.executeQuery(sql);
			//System.out.println("excute query successfully");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
	}
	public boolean next(){
		boolean b=false;
		try{
			if(rs.next())
				b=true;
		}catch(Exception e){e.printStackTrace();}
		return b;
	}
	public String getValue(String filed){
		String value=null;
		try{
			if(rs!=null)
				value=rs.getString(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public int getInt(int index){
		int value=0;
		try{
			if(rs!=null)
				value=rs.getInt(index);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public String getString(int index){
		String value="";
		try{
			if(rs!=null)
				value=rs.getString(index);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public String getString(String filed){
		String value="";
		try{
			if(rs!=null)
				value=rs.getString(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
	    return value;
	}
	
	public long getLong(String filed){
		long value=0;
		try{
			if(rs!=null)
				value=rs.getLong(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	public int getInt(String filed){
		int value=0;
		try{
			if(rs!=null)
				value=rs.getInt(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	public double getDouble(String filed){
		double value=0.0;
		try{
			if(rs!=null)
				value=rs.getDouble(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	public double getDouble(int index){
		double value=0.0;
		try{
			if(rs!=null)
				value=rs.getDouble(index);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public float getFloat(String filed){
		float value=0;
		try{
			if(rs!=null)
				value=rs.getFloat(filed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	public float getFloat(int index){
		float value=0;
		try{
			if(rs!=null)
				value=rs.getFloat(index);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	public void closeRs(){
		try{
			if(rs!=null)
				rs.close();
		}catch(Exception e){
			
		}
	}
	public void closeStm(){
		try{
			if (stm!=null)
				stm.close();
		}catch(Exception e){
			
		}
	}
	
	public void closeConn(){
		try{
			if (conn!=null)
				conn.close();
		}catch(SQLException e){
			
		}
	}
	/**
	 * @param args
	 */

}
