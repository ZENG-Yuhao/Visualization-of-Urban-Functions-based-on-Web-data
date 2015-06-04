package method;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import dataset.AdjacencyList;
import dataset.DBSCAN_Clusters;
import dataset.Weibo;

public class DBSCAN {
	private double Eps;
	private int MinPts;
	private double minLat, minLon, maxLat, maxLon, interval, n_part;
	private int assisX, assisY;
	private int n_datas;
	private ArrayList<Weibo> datas;
	private ArrayList<Weibo>[][] assisList;
	private AdjacencyList adjList;
	private DBSCAN_Clusters clusters;
	private String target;

	public DBSCAN(double Eps, int MinPts, String target) {
		this.Eps = Eps;
		this.MinPts = MinPts;
		this.target = target;

	}// constructor

	public DBSCAN_Clusters getClusters() {
		return clusters;
	}

	public AdjacencyList getAdjList() {
		return adjList;
	}

	private void allocate(Weibo weibo) {

		double distLat = weibo.lat - minLat;
		double distLon = weibo.lon - minLon;
		int intx = (int) Math.floor(distLat / interval);
		int inty = (int) Math.floor(distLon / interval);
		double x = distLat / interval;
		double y = distLon / interval;
		double rmdx = x - intx;
		double rmdy = y - inty;
		assisList[intx][inty].add(weibo);
		System.out.println("------- alocate (" + weibo.lat + ", " + weibo.lon + ") to [" + intx + ", " + inty + "];");
		// if (rmdx<=1e-5) assisList[intx-1][inty].add(weibo);
		// if (rmdy<=1e-5) assisList[intx][inty-1].add(weibo);

	}// allocate

	private ArrayList<Weibo> getDatas() {
		ArrayList<Weibo> weibos = new ArrayList<Weibo>();
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost:5432/newWeiboDianping";
			Connection con = DriverManager.getConnection(url, "postgres", "zyh3667886");
			// String
			// sql="select weibo.mid, place.lat, place.lon from weibo, place where weibo.poiid = place.poiid";
			String sql = "select poiid, lat, lon, checkin_num from place where";
			sql = sql + " newcate ='" + target + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			int id = 0;
			while (rs.next()) {
				Weibo wb = new Weibo();
				wb.mid = rs.getString(1);
				wb.lat = rs.getDouble(2) + 0.002036d;
				wb.lon = rs.getDouble(3) - 0.00445d;
				wb.checkin_num = rs.getInt(4);

				if (wb.lat >= minLat && wb.lat <= maxLat && wb.lon >= minLon && wb.lon <= maxLon) {
					wb.id = id;
					id++;
					weibos.add(wb);
					System.out.println("--- " + wb.toString());
					allocate(wb);
				}
			}

			rs.close();
			st.close();
			con.close();

		} catch (Exception ee) {
			System.out.print(ee.getMessage());
		}
		return weibos;
	}// getDatas

	private ArrayList<Weibo>[][] createAssisList() {
		// lat: 31.383453 lon: 121.49423 上
		// lat: 31.12283 lon: 121.49285 下
		// Dlat= 0.260623 ---0.260

		// lat: 31.22505 lon: 121.31982 左
		// lat: 31.23091 lon: 121.65763 右
		// Dlon=0.33781 ----0.335

		/*
		 * 最小间隔 lat: 31.176882 lon: 121.489426 lat: 31.177471 lon: 121.49423
		 * Dlon=0.004804 ---0.005
		 */

		minLat = 31.12283;
		minLon = 121.31982;
		// maxLat = 31.38283;
		// maxLon = 121.65482;
		maxLat = 31.383453;
		maxLon = 121.65763;
		// interval = 0.005;
		// assisX = 52;
		// assisY = 67;
		interval = Eps;
		assisX = (int) Math.floor((maxLat - minLat) / interval);
		assisY = (int) Math.floor((maxLon - minLon) / interval);

		double tmpx = (maxLat - minLat) / interval;
		double tmpy = (maxLon - minLon) / interval;

		maxLat -= interval * (tmpx - assisX);
		maxLon -= interval * (tmpy - assisY);

		System.out.println("assisX= " + assisX + ";   assisY= " + assisY + ";");
		System.out.println(tmpx + "  " + tmpy);

		ArrayList<Weibo>[][] assisL = new ArrayList[assisX + 1][assisY + 1];
		for (int i = 0; i <= assisX; i++)
			for (int j = 0; j <= assisY; j++)
				assisL[i][j] = new ArrayList<Weibo>();

		return assisL;
	}// createTmpList

	private void scan(Weibo weibo, AdjacencyList adjL, int x, int y) {
		Iterator<Weibo> iter = assisList[x][y].iterator();
		while (iter.hasNext()) {
			Weibo e = iter.next();
			double xx = Math.pow(weibo.lat - e.lat, 2.0);
			double yy = Math.pow(weibo.lon - e.lon, 2.0);
			double dist = Math.sqrt(xx + yy);
			if (dist <= Eps && weibo.id != e.id) {
				adjL.setListElement(weibo.id, e);
				adjL.addNumKernel(weibo.id, weibo.checkin_num);
			}
		}

	}

	private AdjacencyList createAdjList(int len) {
		AdjacencyList adjL = new AdjacencyList(len);
		Iterator<Weibo> iter = datas.iterator();
		while (iter.hasNext()) {
			Weibo weibo = iter.next();
			// System.out.println("--- Scan " + weibo.id);

			double distLat = weibo.lat - minLat;
			double distLon = weibo.lon - minLon;
			int intx = (int) Math.floor(distLat / interval);
			int inty = (int) Math.floor(distLon / interval);
			double x = distLat / interval;
			double y = distLon / interval;
			double rmdx = x - intx;
			double rmdy = y - inty;

			int intx1 = intx, inty1;
			int intx2, inty2 = inty;
			int intx3, inty3;

			if (rmdx >= interval / 2) {
				intx2 = intx + 1;
				intx3 = intx + 1;
			} else {
				intx2 = intx - 1;
				intx3 = intx - 1;
			}

			if (rmdy >= interval / 2) {
				inty1 = inty + 1;
				inty3 = inty + 1;
			} else {
				inty1 = inty - 1;
				inty3 = inty - 1;
			}
			/*
			 * scan(weibo, adjL, intx, inty); if (intx1>=0 && intx1<=assisX &&
			 * inty1>=0 && inty1<=assisY) scan(weibo, adjL, intx1, inty1); if
			 * (intx2>=0 && intx2<=assisX && inty2>=0 && inty2<=assisY)
			 * scan(weibo, adjL, intx2, inty2); if (intx3>=0 && intx3<=assisX &&
			 * inty3>=0 && inty3<=assisY) scan(weibo, adjL, intx3, inty3);
			 */
			scan(weibo, adjL, intx, inty);
			if (inty + 1 <= assisY)
				scan(weibo, adjL, intx, inty + 1);
			if (inty - 1 >= 0)
				scan(weibo, adjL, intx, inty - 1);
			if (intx + 1 <= assisX)
				scan(weibo, adjL, intx + 1, inty);
			if (intx - 1 >= 0)
				scan(weibo, adjL, intx - 1, inty);
			if (intx + 1 <= assisX && inty + 1 <= assisY)
				scan(weibo, adjL, intx + 1, inty + 1);
			if (intx + 1 <= assisX && inty - 1 >= 0)
				scan(weibo, adjL, intx + 1, inty - 1);
			if (intx - 1 >= 0 && inty + 1 <= assisY)
				scan(weibo, adjL, intx - 1, inty + 1);
			if (intx - 1 >= 0 && inty - 1 >= 0)
				scan(weibo, adjL, intx - 1, inty - 1);
		}// end while

		return adjL;
	}// createAdjList

	private void scanNeighbor(ArrayList<Integer> queue, int wid) {
		ArrayList<Weibo> neighbor = adjList.getListElement(wid);
		Iterator<Weibo> iter = neighbor.iterator();
		while (iter.hasNext()) {
			Weibo e = iter.next();
			if (wid != e.id && adjList.isClusterable(e.id)) {
				int pos = clusters.getNumCluster() - 1;
				clusters.addMember(pos, e);
				adjList.setStateOfBeenClustered(e.id, true);

				if (adjList.isKernel(e.id, MinPts)) {
					queue.add(e.id);
				}
			}
		}
	}// scanNeighbor

	private void cluster() {
		Iterator<Weibo> iter = datas.iterator();
		clusters = new DBSCAN_Clusters();

		while (iter.hasNext()) {
			Weibo wb = iter.next();
			if (adjList.isClusterable(wb.id) && adjList.isKernel(wb.id, MinPts)) {
				clusters.newCluster(wb);

				ArrayList<Integer> queue = new ArrayList<Integer>();
				queue.add(wb.id);
				int p = 0;
				while (p < queue.size()) {
					scanNeighbor(queue, queue.get(p));
					p++;
				}
			}
		}

	}// cluster

	public void run() {
		System.out.println("Create Assistant List.");
		assisList = createAssisList();

		System.out.println("Get Datas.");
		datas = getDatas();
		n_datas = datas.size();

		System.out.println("Create Adjacency List.");
		adjList = createAdjList(n_datas);

		System.out.println("Clustering.");
		cluster();

		System.out.println("Clustering done.");
		System.out.println("Number of Clusters: " + clusters.getNumCluster());
	}// run
}// DBSCAN