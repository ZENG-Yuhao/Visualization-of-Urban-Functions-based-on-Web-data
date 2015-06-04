package weibo_dianping;

import java.util.ArrayList;

import method.DBSCAN;
import method.KMeans1;
import dataset.AdjacencyList;
import dataset.DBSCAN_Clusters;
import dataset.Point;
import dataset.Weibo;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

public class dbscanMap2 extends PApplet {

	// Map
	public static UnfoldingMap map;

	// Cluster
	public static int num_targets = 10;
	public static DBSCAN[] dbscan = new DBSCAN[num_targets];
	public static DBSCAN_Clusters[] dbclusters = new DBSCAN_Clusters[num_targets];
	public static AdjacencyList[] adjList = new AdjacencyList[num_targets];
	public static int[] numclusters = new int[num_targets];
	public static int total = 0; // total numclusters;
	public static Weibo[][] kernels = new Weibo[num_targets][];
	public static double[][] radius = new double[num_targets][];
	public static double[][] radius1 = new double[num_targets][];
	public static ArrayList<Point> points = new ArrayList<Point>();
	public static KMeans1 kmeans;
	public static String[] targets = new String[num_targets];
	// map
	public static int[] colors = new int[20];
	public static double minLat, minLon, maxLat, maxLon, interval;
	public static int assisX, assisY;
	public static double Eps = 0.0025;
	public static int[] MinPts = new int[num_targets];
	public static int k = 100;
	public static String[] name = new String[num_targets];

	/*
	 * 餐饮 0.0025 70 公司 购物 快餐 生活娱乐 休闲 学校 医院 运动户外 住宿
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

		targets[0] = "餐饮";
		targets[1] = "公司";
		targets[2] = "购物";
		targets[3] = "快餐";
		targets[4] = "生活娱乐";
		targets[5] = "休闲";
		targets[6] = "学校";
		targets[7] = "医院";
		targets[8] = "运动户外";
		targets[9] = "住宿";

		MinPts[0] = 16265;
		MinPts[1] = 4804;
		MinPts[2] = 5925;
		MinPts[3] = 2799;
		MinPts[4] = 6512;
		MinPts[5] = 2859;
		MinPts[6] = 3764;
		MinPts[7] = 2348;
		MinPts[8] = 3384;
		MinPts[9] = 3302;

		name[0] = "Restaurant";
		name[1] = "Company";
		name[2] = "Shopping";
		name[3] = "Fast Food";
		name[4] = "Entertainment";
		name[5] = "Leisure";
		name[6] = "School";
		name[7] = "Hospital";
		name[8] = "Sport & Outdoor";
		name[9] = "Lodging";
	}

	public void setup() {
		size(1366, 768);
		map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomAndPanTo(new Location(31.2f, 121.4f), 11);
		map.setPanningRestriction(new Location(31.2f, 121.4f), 100);
		MapUtils.createDefaultEventDispatcher(this, map);
		for (int i = 0; i < num_targets; i++) {
			dbscan[i] = new DBSCAN(Eps, MinPts[i], targets[i]);
			dbscan[i].run();
			dbclusters[i] = dbscan[i].getClusters();
			numclusters[i] = dbclusters[i].getNumCluster();
			adjList[i] = dbscan[i].getAdjList();
			radius1[i] = new double[numclusters[i]];
			calKernels(i);
			calRadius(i);
		}
		kmeans();
	}

	public void draw() {
		map.draw();
		// draw_data();
		// draw_block();
		// draw_data1();
		draw_kmeans();

		for (int i = 0; i < num_targets; i++) {
			fill(colors[i]);
			rect(0, 25 * i, 25, 25);
			textSize(16);
			fill(0, 0, 0, 255);
			text(name[i], 30, 25 * i + 20);
		}
	}

	public void draw_kmeans() {

		for (int index = 0; index < num_targets; index++) {
			for (int j = 0; j < numclusters[index]; j++) {
				double r = 0.0;
				Location k = new Location(kernels[index][j].lat, kernels[index][j].lon);
				ScreenPosition posk = map.getScreenPosition(k);
				for (int i = 0; i < dbclusters[index].getMembers(j).size(); i++) {
					Location l = new Location(dbclusters[index].getMembers(j).get(i).lat, dbclusters[index].getMembers(j).get(i).lon);
					ScreenPosition pos = map.getScreenPosition(l);
					double xx = Math.pow(posk.x - pos.x, 2.0);
					double yy = Math.pow(posk.y - pos.y, 2.0);
					// if (r<Math.sqrt(xx+yy)) r=Math.sqrt(xx+yy);
					r += Math.sqrt(xx + yy);
				}
				r /= dbclusters[index].getMembers(j).size();

				radius1[index][j] = 2 * r * 1.4;
				// System.out.println(radius[j]);
			}
		}
		for (int index = 0; index < num_targets; index++) {
			for (int j = 0; j < numclusters[index]; j++) {
				Location l = new Location(kernels[index][j].lat, kernels[index][j].lon);
				ScreenPosition pos = map.getScreenPosition(l);
				// noStroke();
				fill(colors[index]);
				// fill(0, 102, 153, 120);
				// noFill();
				strokeWeight(1);
				if (radius1[index][j] > 5)
					ellipse(pos.x, pos.y, (float) radius1[index][j], (float) radius1[index][j]);
				else
					ellipse(pos.x, pos.y, 5, 5);
			}
		}

		Point[] kcenter = kmeans.getCenter();
		for (int i = 0; i < k; i++) {
			Location l = new Location(kcenter[i].x, kcenter[i].y);
			ScreenPosition pos = map.getScreenPosition(l);
			strokeWeight(1);
			fill(255, 0, 0, 255);
			ellipse(pos.x, pos.y, 5, 5);
			for (int j = 0; j < total; j++) {
				if (points.get(j).cid == i) {
					Location l1 = new Location(points.get(j).x, points.get(j).y);
					ScreenPosition pos1 = map.getScreenPosition(l1);
					strokeWeight(1);
					line(pos.x, pos.y, pos1.x, pos1.y);
				}
			}
		}

	}

	public void kmeans() {
		System.out.println("Start Kmeans.");
		for (int index = 0; index < num_targets; index++)
			total += numclusters[index];
		double[] radiusTmp = new double[total];

		int count = 0;
		for (int index = 0; index < num_targets; index++) {
			for (int i = 0; i < numclusters[index]; i++) {
				Point p = new Point();
				p.pid = i;
				p.x = kernels[index][i].lat;
				p.y = kernels[index][i].lon;
				p.category = targets[index];
				p.cateid = index;
				points.add(p);
				radiusTmp[count] = radius[index][i];
				count++;
			}
		}
		System.out.println(points.size());
		kmeans = new KMeans1(k, 1e-20, 150, points, radiusTmp);
		kmeans.run();
		points = kmeans.getSolution();

	}

	public void draw_block() {
		for (double i = minLat; i <= maxLat; i += interval) {
			Location l1 = new Location(i, minLon);
			ScreenPosition pos1 = map.getScreenPosition(l1);
			Location l2 = new Location(i, maxLon);
			ScreenPosition pos2 = map.getScreenPosition(l2);
			// stroke(0,0,0,0);
			strokeWeight(1);
			fill(0, 0, 0, 0);
			line(pos1.x, pos1.y, pos2.x, pos2.y);
		}

		for (double j = minLon; j <= maxLon; j += interval) {
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

	public void calKernels(int index) {
		kernels[index] = new Weibo[numclusters[index]];
		for (int i = 0; i < numclusters[index]; i++)
			kernels[index][i] = new Weibo();

		for (int j = 0; j < numclusters[index]; j++) {
			double lat = 0.0;
			double lon = 0.0;
			long count = 0;
			for (int i = 0; i < dbclusters[index].getMembers(j).size(); i++) {
				lat += dbclusters[index].getMembers(j).get(i).lat * dbclusters[index].getMembers(j).get(i).checkin_num;
				lon += dbclusters[index].getMembers(j).get(i).lon * dbclusters[index].getMembers(j).get(i).checkin_num;
				count += dbclusters[index].getMembers(j).get(i).checkin_num;
			}
			lat /= count;
			lon /= count;

			kernels[index][j].lat = lat;
			kernels[index][j].lon = lon;
		}

		// caculate radius

	}

	public void calRadius(int index) {
		// caculate radius
		radius[index] = new double[numclusters[index]];
		for (int j = 0; j < numclusters[index]; j++) {
			double r = 0.0;
			long count = 0;
			for (int i = 0; i < dbclusters[index].getMembers(j).size(); i++) {
				double xx = Math.pow(kernels[index][j].lat - dbclusters[index].getMembers(j).get(i).lat, 2.0);
				double yy = Math.pow(kernels[index][j].lon - dbclusters[index].getMembers(j).get(i).lon, 2.0);
				// if (r<Math.sqrt(xx+yy)) r=Math.sqrt(xx+yy);
				r += Math.sqrt(xx + yy) * dbclusters[index].getMembers(j).get(i).checkin_num;
				count += dbclusters[index].getMembers(j).get(i).checkin_num;
			}
			r /= count;

			radius[index][j] = r * 1.4;
			// System.out.println(radius[j]);
		}
	}

}// dbscanMap