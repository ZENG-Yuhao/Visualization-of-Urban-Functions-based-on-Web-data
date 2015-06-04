package weibo_dianping;

import method.DBSCAN;
import dataset.AdjacencyList;
import dataset.DBSCAN_Clusters;
import dataset.Weibo;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

public class dbscanMap extends PApplet {

	// Map
	public static UnfoldingMap map;

	// Cluster
	public static DBSCAN dbscan;
	public static DBSCAN_Clusters dbclusters;
	public static AdjacencyList adjList;
	public static int numclusters;
	public static int j = 0;
	public static Weibo[] kernels;
	public static double[] radius;

	// map
	public static int[] colors = new int[20];
	public static double minLat, minLon, maxLat, maxLon, interval;
	public static int assisX, assisY;
	public static double Eps = 0.0025;
	public static int MinPts = 16265;
	public static String target = "餐饮";
	/*
	 * 餐饮 0.0025 16265 公司 0.0025 4804 购物 0.0025 5925 快餐 0.0025 2799 生活娱乐 0.0025
	 * 6512 休闲 0.0025 2859 学校 0.0025 3764 医院 0.0025 2348 运动户外 0.0025 3384 住宿
	 * 0.0025 3302 住宅
	 */
	{

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

		colors[0] = color(126, 211, 207);
		colors[1] = color(215, 76, 51);
		colors[2] = color(200, 80, 202);
		colors[3] = color(208, 211, 61);
		colors[4] = color(63, 61, 39);
		colors[5] = color(206, 68, 121);
		colors[6] = color(68, 74, 113);
		colors[7] = color(193, 131, 118);
		colors[8] = color(90, 127, 49);
		colors[9] = color(200, 142, 57);
		colors[10] = color(112, 213, 74);
		colors[11] = color(199, 137, 189);
		colors[12] = color(111, 214, 151);
		colors[13] = color(118, 107, 203);
		colors[14] = color(132, 164, 205);
		colors[15] = color(126, 56, 36);
		colors[16] = color(207, 199, 171);
		colors[17] = color(89, 126, 109);
		colors[18] = color(193, 207, 119);
		colors[19] = color(99, 43, 76);
	}

	public void setup() {
		size(1366, 768);
		map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomAndPanTo(new Location(31.2f, 121.4f), 11);
		map.setPanningRestriction(new Location(31.2f, 121.4f), 100);
		MapUtils.createDefaultEventDispatcher(this, map);

		dbscan = new DBSCAN(Eps, MinPts, target);
		dbscan.run();
		dbclusters = dbscan.getClusters();
		numclusters = dbclusters.getNumCluster();
		adjList = dbscan.getAdjList();
		calKernels();
	}

	public void draw() {
		map.draw();
		draw_data();
		draw_block();
		// draw_data1();

	}

	public void draw_data() {
		// members
		int total = 0;
		for (int j = 0; j < numclusters; j++) {
			for (int i = 0; i < dbclusters.getMembers(j).size() - 1; i++) {
				Location l = new Location(dbclusters.getMembers(j).get(i).lat, dbclusters.getMembers(j).get(i).lon);
				ScreenPosition pos = map.getScreenPosition(l);
				// noStroke();
				int id = dbclusters.getMembers(j).get(i).id;
				if (!adjList.isKernel(id, MinPts))
					fill(0, 102, 153, 255);
				// fill(0,0,0,255);
				else
					fill(0, 255, 0, 250);

				// fill(colors[j],255);
				strokeWeight(1);
				ellipse(pos.x, pos.y, 4, 4);
				total++;
			}
		}
		// caculate radius

		for (int j = 0; j < numclusters; j++) {
			double r = 0.0;
			long count = 0;
			Location k = new Location(kernels[j].lat, kernels[j].lon);
			ScreenPosition posk = map.getScreenPosition(k);
			for (int i = 0; i < dbclusters.getMembers(j).size(); i++) {
				Location l = new Location(dbclusters.getMembers(j).get(i).lat, dbclusters.getMembers(j).get(i).lon);
				ScreenPosition pos = map.getScreenPosition(l);
				double xx = Math.pow(posk.x - pos.x, 2.0);
				double yy = Math.pow(posk.y - pos.y, 2.0);
				// if (r<Math.sqrt(xx+yy) &&
				// adjList.isKernel(dbclusters.getMembers(j).get(i).id, MinPts))
				// r=Math.sqrt(xx+yy);
				// if (r<Math.sqrt(xx+yy)) r=Math.sqrt(xx+yy);
				r += Math.sqrt(xx + yy) * dbclusters.getMembers(j).get(i).checkin_num;
				count += dbclusters.getMembers(j).get(i).checkin_num;
			}
			// r/=dbclusters.getMembers(j).size();
			r /= count;
			radius[j] = 2 * r * 1.4;
			// System.out.println(radius[j]);
		}

		// kernels
		for (int j = 0; j < numclusters; j++) {
			Location l = new Location(kernels[j].lat, kernels[j].lon);
			ScreenPosition pos = map.getScreenPosition(l);
			// noStroke();
			fill(255, 0, 0, 204);
			strokeWeight(1);
			ellipse(pos.x, pos.y, 6, 6);
			noFill();
			strokeWeight(2);
			ellipse(pos.x, pos.y, (float) radius[j], (float) radius[j]);
		}

	}

	public void draw_data1() {
		for (int i = 0; i < dbclusters.getMembers(j).size(); i++) {
			Location l = new Location(dbclusters.getMembers(j).get(i).lat, dbclusters.getMembers(j).get(i).lon);
			ScreenPosition pos = map.getScreenPosition(l);
			// noStroke();
			int id = dbclusters.getMembers(j).get(i).id;
			if (!adjList.isKernel(id, MinPts)) {
				// stroke(color(0, 102, 153, 204));
				fill(0, 102, 153, 204);

			} else {
				// stroke(color(0,255,0,255));
				fill(0, 255, 0, 255);
			}
			// fill(colors[j],255);
			ellipse(pos.x, pos.y, 4, 4);
		}
		double r = 0.0;
		Location k = new Location(kernels[j].lat, kernels[j].lon);
		ScreenPosition posk = map.getScreenPosition(k);
		for (int i = 0; i < dbclusters.getMembers(j).size(); i++) {
			Location l = new Location(dbclusters.getMembers(j).get(i).lat, dbclusters.getMembers(j).get(i).lon);
			ScreenPosition pos = map.getScreenPosition(l);
			double xx = (posk.x - pos.x) * (posk.x - pos.x);
			double yy = (posk.x - pos.x) * (posk.x - pos.x);
			if (r < Math.sqrt(xx + yy))
				r = Math.sqrt(xx + yy);

		}
		// r/=dbclusters.getMembers(j).size();
		radius[j] = r;
		Location l = new Location(kernels[j].lat, kernels[j].lon);
		ScreenPosition pos = map.getScreenPosition(l);
		// noStroke();
		fill(255, 0, 0, 204);
		ellipse(pos.x, pos.y, 6, 6);
		noFill();
		// stroke(color(0,0,0,255));
		ellipse(pos.x, pos.y, (float) radius[j], (float) radius[j]);
	}

	public void draw_block() {
		for (double i = minLat; i <= maxLat; i += 2 * interval) {
			Location l1 = new Location(i, minLon);
			ScreenPosition pos1 = map.getScreenPosition(l1);
			Location l2 = new Location(i, maxLon);
			ScreenPosition pos2 = map.getScreenPosition(l2);
			// stroke(0,0,0,0);
			strokeWeight(1);
			fill(0, 0, 0, 0);
			line(pos1.x, pos1.y, pos2.x, pos2.y);
		}

		for (double j = minLon; j <= maxLon; j += 2 * interval) {
			Location l1 = new Location(minLat, j);
			ScreenPosition pos1 = map.getScreenPosition(l1);
			Location l2 = new Location(maxLat, j);
			ScreenPosition pos2 = map.getScreenPosition(l2);
			// stroke(0,0,0,0);
			strokeWeight(1);
			// fill(0,0,0,0);
			line(pos1.x, pos1.y, pos2.x, pos2.y);
		}

	}// drwa_block

	public void calKernels() {
		kernels = new Weibo[numclusters];
		radius = new double[numclusters];

		for (int j = 0; j < numclusters; j++) {
			kernels[j] = new Weibo();
			double lat = 0.0;
			double lon = 0.0;
			long count = 0;
			for (int i = 0; i < dbclusters.getMembers(j).size(); i++) {
				lat += dbclusters.getMembers(j).get(i).lat * dbclusters.getMembers(j).get(i).checkin_num;
				lon += dbclusters.getMembers(j).get(i).lon * dbclusters.getMembers(j).get(i).checkin_num;
				count += dbclusters.getMembers(j).get(i).checkin_num;
			}
			// lat /= dbclusters.getMembers(j).size();
			// lon /= dbclusters.getMembers(j).size();
			lat /= count;
			lon /= count;
			kernels[j].lat = lat;
			kernels[j].lon = lon;
		}

		// caculate radius

	}

	public void mousePressed() {
		j = (j + 1) % dbclusters.getNumCluster();
	}
}// dbscanMap