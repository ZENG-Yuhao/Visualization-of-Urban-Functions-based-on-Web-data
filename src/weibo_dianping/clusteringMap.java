package weibo_dianping;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import method.Database;
import method.KMeans;
import method.MyCanvas;

import controlP5.Accordion;
import controlP5.CheckBox;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Group;
import controlP5.RadioButton;
import controlP5.Range;
import controlP5.Slider;

import processing.core.PApplet;
import processing.core.PImage;
import codeanticode.glgraphics.GLConstants;

import dataset.Block;
import dataset.Cidsort;
import dataset.Weibo;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class clusteringMap extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// K-means
	public static int len = 14;
	public static int k = 10;
	public static int repeat = 500;
	public static String[] cates, cates1, cates2;
	public static KMeans km, km_p1, km_p2, km_p3, km_p4;
	public static int[] colors = new int[10];
	public static ArrayList<Block> blocks, blocks_p1, blocks_p2, blocks_p3, blocks_p4;
	public static ArrayList<Cidsort> arr = new ArrayList<Cidsort>();

	// map
	public static UnfoldingMap map;
	public static ArrayList<Weibo> weibos = new ArrayList<Weibo>();
	public static ArrayList<Weibo> tmp_weibos = new ArrayList<Weibo>();
	public static ArrayList<Location>[] polygon = new ArrayList[200];
	public static SimplePolygonMarker spm;
	public static MarkerManager markerManager = new MarkerManager();
	public static SimpleLinesMarker lM = new SimpleLinesMarker();
	public static boolean customPolygon = false, drawable = false;
	public static int color_customPolyg;
	public static ArrayList<Location> customPolyg = new ArrayList<Location>();
	public static String[] lineName = new String[45];
	public static ArrayList<Float>[] lineLat = new ArrayList[13];
	public static ArrayList<Float>[] lineLon = new ArrayList[13];

	// GUI
	public static boolean mapVisible = true;
	public static Accordion accordion;
	public static ControlP5 cp5;
	public static RadioButton radioBox, cpd;
	public static Location centerOfScreen;
	public static Group g1, g2, g3;
	public static CheckBox checkbox;
	public static Range rangeSeekBar;
	public static DropdownList dpd;
	public static int para = -1;
	public static int[] catesOption = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static int skbarHeight, skbarWidth, skbarX, skbarY, skbarHandleSize;
	public static int skbarRange0, skbarRange1;
	public static int dpdX, dpdY;
	public static int dpdItemHeight;
	public static int dpdBarHeight, dpdWidth;
	public static int handleMax, handleMin;

	// Color
	public static int hotelColor;
	public static int sportColor;
	public static int shopColor;
	public static int restaurantColor;
	public static int mariageColor;
	public static int lifeColor;
	public static int carsColor;
	public static int enterTColor;
	public static int childColor;
	public static int womenColor;
	int yo = color(174, 178, 179);

	{
		for (int i = 0; i <= 200 - 1; i++)
			polygon[i] = new ArrayList<Location>();

		cates = new String[len];
		cates[0] = "生活娱乐";
		cates[1] = "餐饮";
		cates[2] = "公司";
		cates[3] = "公共交通";
		cates[4] = "快餐";
		cates[5] = "学校";
		cates[6] = "购物";
		cates[7] = "休闲";
		cates[8] = "住宿";
		cates[9] = "运动户外";
		cates[10] = "医院";
		cates[11] = "机构";
		cates[12] = "旅游";
		cates[13] = "住宅";

		cates1 = new String[len];
		cates1[0] = "Entertainment";
		cates1[1] = "Restuarant";
		cates1[2] = "Company";
		cates1[3] = "Public Transportation";
		cates1[4] = "Fast Food";
		cates1[5] = "School";
		cates1[6] = "Shopping";
		cates1[7] = "Leisure";
		cates1[8] = "Lodging";
		cates1[9] = "Sport & Outdoor";
		cates1[10] = "Hospital";
		cates1[11] = "Organization";
		cates1[12] = "Travelling";
		cates1[13] = "Residence";

		/*
		 * colors[0]=color(132,147,195); colors[1]=color(137,205,81); colors[2]=color(187,82,53); colors[3]=color(80,93,53); colors[4]=color(125,204,174); colors[5]=color(171,93,199);
		 * colors[6]=color(199,82,129); colors[7]=color(204,177,70); colors[8]=color(81,53,75); colors[9]=color(200,173,152);
		 */
		colors[0] = color(31, 119, 180);
		colors[1] = color(255, 127, 14);
		colors[2] = color(44, 160, 44);
		colors[3] = color(214, 39, 40);
		colors[4] = color(148, 103, 189);
		colors[5] = color(140, 86, 75);
		colors[6] = color(227, 119, 194);
		colors[7] = color(127, 127, 127);
		colors[8] = color(188, 189, 34);
		colors[9] = color(23, 190, 207);

		hotelColor = color(31, 119, 180);
		sportColor = color(255, 127, 14);
		shopColor = color(44, 160, 44);
		restaurantColor = color(214, 39, 40);
		mariageColor = color(148, 103, 189);
		lifeColor = color(140, 86, 75);
		carsColor = color(227, 119, 194);
		enterTColor = color(127, 127, 127);
		childColor = color(188, 189, 34);
		womenColor = color(23, 190, 207);

		lineName[0] = "changjiangxi_road";
		lineName[1] = "changzhong_rd";
		lineName[2] = "huangxing_road";
		lineName[3] = "hujia_expy";
		lineName[4] = "huning_expy";
		lineName[5] = "hutai_raod";
		lineName[6] = "jinhai_road";
		lineName[7] = "jinqiu_road";
		lineName[8] = "jufeng_road";
		lineName[9] = "jungong_road";
		lineName[10] = "longdong_road";
		lineName[11] = "longwu_road";
		lineName[12] = "middle_ring_rd";
		lineName[13] = "outer_ring_rd";
		lineName[14] = "pudongbei_road";
		lineName[15] = "shenjiang_road";
		lineName[16] = "subwayline01";
		lineName[17] = "subwayline02";
		lineName[18] = "subwayline03";
		lineName[19] = "subwayline04";
		lineName[20] = "subwayline05";
		lineName[21] = "subwayline06";
		lineName[22] = "subwayline07";
		lineName[23] = "subwayline08";
		lineName[24] = "subwayline09";
		lineName[25] = "subwayline10_m";
		lineName[26] = "subwayline10_z";
		lineName[27] = "subwayline11_m";
		lineName[28] = "subwayline11_z";
		lineName[29] = "wuzhou_road";
		lineName[30] = "yanggao_road";
		lineName[31] = "yangshupu_road";
		lineName[32] = "yinxing_road";
		lineName[33] = "zhangyangbei_road";
		lineName[34] = "stops01";
		lineName[35] = "stops02";
		lineName[36] = "stops03";
		lineName[37] = "stops04";
		lineName[38] = "stops05";
		lineName[39] = "stops06";
		lineName[40] = "stops07";
		lineName[41] = "stops08";
		lineName[42] = "stops09";
		lineName[43] = "stops10";
		lineName[44] = "stops11";

	}

	public void setup()
	{

		size(1366, 768);
		GUI();
		System.out.println("GUI Done.");

		// load_subway(); System.out.println("load_subway Done.");
		weibos = getWeibo();
		System.out.println("getWeibo Done.");
		map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomAndPanTo(new Location(31.2f, 121.4f), 11);
		map.setPanningRestriction(new Location(31.2f, 121.4f), 100);
		MapUtils.createDefaultEventDispatcher(this, map);
		centerOfScreen = map.getCenter();
		map.setTweening(true);
		startClustering();
		System.out.println("startClustering Done.");

	}// setup

	public void draw()
	{
		if (!g3.isOpen() && customPolyg.size() > 0)
		{
			customPolyg.clear();
			markerManager.clearMarkers();
			customPolyg.clear();
			lM = new SimpleLinesMarker();
		}

		map.draw();
		if (!g1.isOpen())
		{
			rangeSeekBar.setVisible(false);
			dpd.setVisible(false);
		}

		if (g1.isOpen())
		{
			// draw_subway();
			rangeSeekBar.setVisible(true);
			dpd.setVisible(true);
			draw_weibo(handleMin, handleMax);

		} else if (g2.isOpen())
		{
			draw_data();
			if (para != -1)
			{
				noStroke();
				fill(color(0, 0, 0), 200);
				this.rect(0, 0, 1366, 768);

				MyCanvas canv = new MyCanvas(350, 50, 700, 600);
				int[] data = new int[len];
				// String[] tags = new String[18];
				for (int i = 0; i <= len - 1; i++)
				{
					data[i] = (int) Math.round(km.bestCenter[para][i]);
					// tags[i]=String.valueOf(i);
				}
				canv.setDatas(data, len);
				canv.setTags(cates1, len);
				canv.draw(this);

				fill(colors[para], 255);
				this.rect(350, 50, 40, 40);
			}
		} else if (g3.isOpen())
		{
			if (customPolygon)
				linkPolygon(customPolyg, color_customPolyg);

			/*
			 * else for (int i=0; i<=customPolyg.size()-2; i++){ ScreenPosition sp1=map.getScreenPosition(customPolyg.get(i)); ScreenPosition sp2=map.getScreenPosition(customPolyg.get(i+1)); fill(255,0,0);
			 * strokeWeight(2); line(sp1.x, sp1.y, sp2.x, sp2.y); }
			 */
		}

	}// draw

	public void startClustering()
	{
		System.out.println("start clustering");
		km = new KMeans(k, 1e-25, repeat, len);
		blocks = km.KMeansData(len, cates, "place");
		for (int i = 0; i < km.getK(); i++)
		{
			System.out.print("(");
			for (int j = 0; j < len; j++)
			{
				// 输出四舍五入取整
				System.out.print(new BigDecimal(km.bestCenter[i][j]).setScale(0, BigDecimal.ROUND_HALF_UP)
						+ ",");
			}
			System.out.println(")");
			Cidsort cid = new Cidsort(km.bestCenter[i][13], i);
			arr.add(cid);
		}
		arr = km.bubblesort(arr);
		for (int i = 0; i < arr.size(); i++)
		{
			System.out.println(arr.get(i).toString());
		}
		System.out.println(blocks.size());
		for (int i = 0; i <= blocks.size() - 1; i++)
		{
			polygon[blocks.get(i).getBlockid()] = getBlockLocations(blocks.get(i).getBlockid());
		}
		// draw_data();
		System.out.println(km.bestM);

	}// startClustering

	public void draw_data()
	{
		Iterator it = blocks.iterator();
		int color = 10;
		int cidnum;

		while (it.hasNext())
		{
			Object obj = it.next();
			Block block = (Block) obj;
			cidnum = block.getCid();
			for (int i = 0; i < k; i++)
			{
				if (cidnum == (arr.get(i).getId()))
				{
					color = i;
					break;
				}
			}

			// ArrayList<Location> locations =
			// getBlockLocations(block.getBlockid());

			int colorr;
			if (color == -1)
			{
				colorr = colors[0];
			} else
			{
				colorr = colors[color];
			}
			// System.out.println(block.getBlockid() + "  "+ (blocks.size()-1));
			linkPolygon(polygon[block.getBlockid()], colorr);

		}
	}// draw_data

	public void load_subway()
	{
		// initialize
		for (int i = 0; i <= 12; i++)
			lineLat[i] = new ArrayList<Float>();
		for (int i = 0; i <= 12; i++)
			lineLon[i] = new ArrayList<Float>();

		String url = "jdbc:postgresql://localhost:5432/weibo_dianping";
		String usr = "postgres";
		String psw = "123";
		Database db = new Database(url, usr, psw);

		if (db.creatConn())
		{
			for (int index = 16; index <= 28; index++)
			{
				String sql = "select lat, lon from " + lineName[index];
				db.query(sql);
				while (db.next())
				{
					float lat = db.getFloat("lat");
					float lon = db.getFloat("lon");
					lineLat[index - 16].add(lat);
					lineLon[index - 16].add(lon);

				}

			}
		}
		db.closeRs();
		db.closeStm();
		db.closeConn();
	}// load_subway

	public void draw_subway()
	{
		for (int index = 1; index <= 12; index++)
		{
			for (int i = 1; i <= lineLat[index].size() - 1; i++)
			{
				Location l1 = new Location(lineLat[index].get(i - 1), lineLon[index].get(i - 1));
				Location l2 = new Location(lineLat[index].get(i), lineLon[index].get(i));
				ScreenPosition sp1 = map.getScreenPosition(l1);
				ScreenPosition sp2 = map.getScreenPosition(l2);

				this.strokeWeight(2);
				this.line(sp1.x, sp1.y, sp2.x, sp2.y);
			}
		}
	}// draw_subway

	public void find_weibo(String cate)
	{
		Iterator it = weibos.iterator();
		while (it.hasNext())
		{
			Object obj = it.next();
			Weibo weibo = (Weibo) obj;
			if (weibo.category != null)
				if (weibo.category.equals(cate))
				{
					tmp_weibos.add(weibo);
				}
		}
	}// find_weibo

	public void draw_weibo(int minHour, int maxHour)
	{
		Iterator it = tmp_weibos.iterator();
		while (it.hasNext())
		{
			Object obj = it.next();
			Weibo weibo = (Weibo) obj;
			if (weibo.hour >= minHour && weibo.hour <= maxHour)
			{
				ScreenPosition screenPos;
				screenPos = map.getScreenPosition(new Location(weibo.lat, weibo.lon));
				noStroke();
				fill(0, 102, 153, 204);
				ellipse(screenPos.x, screenPos.y, 3, 3);
			}
		}
	}// draw_weibo

	public ArrayList<Weibo> getWeibo()
	{
		ArrayList<Weibo> weibos = new ArrayList<Weibo>();
		try
		{
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost:5432/newWeiboDianping";
			Connection con = DriverManager.getConnection(url, "postgres", "123");
			String sql = "select weibo.mid, weibo.poiid, weibo.hour, place.lat, place.lon, place.newcate from weibo, place where weibo.poiid = place.poiid";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next())
			{
				Weibo wb = new Weibo();
				wb.mid = rs.getString(1);
				wb.poiid = rs.getString(2);
				wb.hour = rs.getInt(3);
				wb.lat = rs.getDouble(4) + 0.002036d;
				wb.lon = rs.getDouble(5) - 0.00445d;
				wb.category = rs.getString(6);
				String ps = "POINT(";
				ps += (wb.lat) + " " + (wb.lon);
				ps += ")";
				wb.geom = ps;
				weibos.add(wb);
				System.out.println(wb.toString());
			}

			rs.close();
			st.close();
			con.close();

		} catch (Exception ee)
		{
			System.out.print(ee.getMessage());
		}
		return weibos;
	}// getWeibo

	public ArrayList<Location> getBlockLocations(int blockid)
	{
		ArrayList<Location> locations = new ArrayList<Location>();
		try
		{
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost:5432/newWeiboDianping";
			Connection con = DriverManager.getConnection(url, "postgres", "123");
			String sql = "select lat, lon from block" + blockid;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next())
			{
				Location loc = new Location(rs.getDouble("lat"), rs.getDouble("lon"));
				// System.out.println(rs.getDouble("lat")+"  "+
				// rs.getDouble("lon"));
				locations.add(loc);
			}

			rs.close();
			st.close();
			con.close();

		} catch (Exception ee)
		{
			System.out.print(ee.getMessage());
		}

		return locations;
	}

	public void linkPolygon(ArrayList<Location> locations, int color)
	{

		/*
		 * //markerManager.clearMarkers(); spm = new SimplePolygonMarker(locations); spm.setColor(color); //spm.setHighlightColor(color); markerManager.addMarker(spm); map.addMarkerManager(markerManager);
		 * //map.draw();
		 */
		ScreenPosition screenPos;
		this.strokeWeight(1);
		stroke(color);
		fill(color, 150);
		beginShape();
		for (int i = 0; i <= locations.size() - 1; i++)
		{
			screenPos = map.getScreenPosition(locations.get(i));
			vertex(screenPos.x, screenPos.y);
		}
		endShape(CLOSE);
	}

	public void GUI()
	{

		cp5 = new ControlP5(this);

		// group 1
		g1 = cp5.addGroup("weibo").setBackgroundColor(color(0, 64)).setBackgroundHeight(600).setBarHeight(25);

		/*
		 * cp5.addSlider("K-means") .setPosition(10,75) .setSize(150,15) .setRange(4,10) .setValue(8) .setNumberOfTickMarks(7) .setSliderMode(Slider.FLEXIBLE) .moveTo(g1) ;
		 * 
		 * cp5.addSlider("repeat") .setPosition(10,110) .setSize(150,15) .setRange(1,20) .setValue(1) .setNumberOfTickMarks(20) .setSliderMode(Slider.FLEXIBLE) .moveTo(g1) ;
		 */

		checkbox = cp5.addCheckBox("checkBox").setPosition(20, 20).setItemWidth(30).setItemHeight(30)
				.setItemsPerRow(1).setSpacingRow(10).setSpacingColumn(70).addItem(cates1[0], 0)
				.addItem(cates1[1], 1).addItem(cates1[2], 2).addItem(cates1[3], 3).addItem(cates1[4], 4)
				.addItem(cates1[5], 5).addItem(cates1[6], 6).addItem(cates1[7], 7).addItem(cates1[8], 8)
				.addItem(cates1[9], 9).addItem(cates1[10], 10).addItem(cates1[11], 11)
				.addItem(cates1[12], 12).addItem(cates1[13], 13)
				// .addItem(cates1[14], 14)
				// .addItem(cates1[15], 15)
				// .addItem(cates1[16], 16)
				// .addItem(cates1[17], 17)
				.setColorLabel(color(255))
				// .setColorForeground(color(0,100))
				.moveTo(g1);
		/*
		 * checkbox.getItem(0).setColorActive(hotelColor); checkbox.getItem(1).setColorActive(sportColor); checkbox.getItem(2).setColorActive(shopColor); checkbox.getItem(3).setColorActive(restaurantColor);
		 * checkbox.getItem(4).setColorActive(mariageColor); checkbox.getItem(5).setColorActive(lifeColor); checkbox.getItem(6).setColorActive(carsColor); checkbox.getItem(7).setColorActive(enterTColor);
		 * checkbox.getItem(8).setColorActive(childColor); checkbox.getItem(9).setColorActive(womenColor);
		 */

		// group 2
		g2 = cp5.addGroup("clustering parameters").setBackgroundColor(color(0, 64)).setBackgroundHeight(350)
				.setBarHeight(25);

		radioBox = cp5.addRadioButton("radiobox").setPosition(40, 20).setItemWidth(30).setItemHeight(30)

		.addItem("1", 0).addItem("2", 1).addItem("3", 2).addItem("4", 3).addItem("5", 4).addItem("6", 5)
				.addItem("7", 6).addItem("8", 7).addItem("9", 8).addItem("10", 9).setColorLabel(color(255))
				.setItemsPerRow(3).setSpacingColumn(40).setSpacingRow(60).moveTo(g2);
		radioBox.getItem(0).setColorActive(color(255));
		radioBox.getItem(1).setColorActive(color(255));
		radioBox.getItem(2).setColorActive(color(255));
		radioBox.getItem(3).setColorActive(color(255));
		radioBox.getItem(4).setColorActive(color(255));
		radioBox.getItem(5).setColorActive(color(255));
		radioBox.getItem(6).setColorActive(color(255));
		radioBox.getItem(7).setColorActive(color(255));
		radioBox.getItem(8).setColorActive(color(255));
		radioBox.getItem(9).setColorActive(color(255));

		radioBox.getItem(0).setColorBackground(colors[0]);
		radioBox.getItem(1).setColorBackground(colors[1]);
		radioBox.getItem(2).setColorBackground(colors[2]);
		radioBox.getItem(3).setColorBackground(colors[3]);
		radioBox.getItem(4).setColorBackground(colors[4]);
		radioBox.getItem(5).setColorBackground(colors[5]);
		radioBox.getItem(6).setColorBackground(colors[6]);
		radioBox.getItem(7).setColorBackground(colors[7]);
		radioBox.getItem(8).setColorBackground(colors[8]);
		radioBox.getItem(9).setColorBackground(colors[9]);

		// group 3
		g3 = cp5.addGroup("custom polygon display").setBackgroundColor(color(0, 64)).setBackgroundHeight(200)
				.setBarHeight(25);

		cpd = cp5.addRadioButton("customPolygonDisplay").setPosition(10, 20).setItemWidth(20)
				.setItemHeight(20).addItem("", 0).setColorLabel(color(255)).setItemsPerRow(5)
				.setSpacingColumn(15).setSpacingRow(5).moveTo(g3);

		PImage[] imgs = { loadImage("button_a.png"), loadImage("button_b.png"), loadImage("button_a.png") };
		cp5.addButton("play").setPosition(100, 80).setImages(imgs).updateSize().moveTo(g3);
		;

		cp5.setFont(createFont("Ubuntu-Bold", 10, true));
		// create a new accordion
		// add g1, g2, and g3 to the accordion.
		accordion = cp5.addAccordion("acc").setPosition(40, 20).setWidth(250).addItem(g1).addItem(g2)
				.addItem(g3);

		accordion.setCollapseMode(ControlP5.SINGLE);

		skbarHeight = 30;
		skbarWidth = 400;
		skbarX = 483;
		skbarY = 10;
		skbarHandleSize = 5;
		skbarRange0 = 0;
		skbarRange1 = 23;
		rangeSeekBar = cp5.addRange("Date").setBroadcast(false).setPosition(skbarX, skbarY)
				.setSize(skbarWidth, skbarHeight).setHandleSize(skbarHandleSize)
				.setRange(skbarRange0, skbarRange1).setRangeValues(0, 6)
				// .setColorForeground(color(255,40))
				// .setColorBackground(color(255,40))
				// .setColorValueLabel(color(70))
				// after the initialization we turn broadcast back on again
				.setBroadcast(true);

		dpdX = 990;
		dpdY = 40;
		dpdItemHeight = 25;
		dpdBarHeight = 20;
		dpdWidth = 180;

		dpd = cp5.addDropdownList("Type").setPosition(dpdX, dpdY);
		// dpd.setBackgroundColor(color(190));
		dpd.setItemHeight(dpdItemHeight);
		dpd.setBarHeight(dpdBarHeight);
		dpd.setWidth(dpdWidth);
		dpd.captionLabel().set("dropdown");
		// dpd.captionLabel().style().marginTop = 4;
		// dpd.captionLabel().style().marginLeft = 4;
		// dpd.valueLabel().style().marginTop = 4;
		dpd.addItem("1. 00:00 ~ 06:00 ", 1);
		dpd.addItem("2. 06:00 ~ 12:00 ", 2);
		dpd.addItem("3. 12:00 ~ 18:00 ", 3);
		dpd.addItem("4. 18:00 ~ 24:00 ", 4);
		dpd.addItem("5. all day ", 5);
		dpd.setHeight(200);
		// dpd.setColorBackground(color(60));
		// dpd.setColorActive(color(255, 128));
		// dpd.setIndex(0);

	}// GUI

	public void play()
	{

		if (customPolyg.size() > 2)
		{
			String url = "jdbc:postgresql://localhost:5432/newWeiboDianping";
			String usr = "postgres";
			String psw = "123";

			// create polygon string for postGIS
			customPolyg.add(customPolyg.get(0)); // CLOSE THE SHAPE
			String polygonString = "POLYGON((";
			for (int i = 0; i <= customPolyg.size() - 1; i++)
			{
				polygonString += customPolyg.get(i).getLat();
				polygonString += " ";
				polygonString += customPolyg.get(i).getLon();
				if (i != customPolyg.size() - 1)
					polygonString += ", ";
			}
			polygonString += "))";
			System.out.println(polygonString);

			String sql = "select sum(checkin_num) from place where st_within(place.geom, st_geomfromtext('"
					+ polygonString + "'))";

			Database db = new Database(url, usr, psw);
			ArrayList<Integer> facility = new ArrayList<Integer>();
			int maxNum = Integer.MIN_VALUE;
			double maxDouble = Double.MIN_VALUE;
			if (db.creatConn())
			{
				for (String cate : cates)
				{
					String sql_1 = sql + " and newcate='" + cate + "'";
					db.query(sql_1);
					int index = 0;
					while (db.next())
					{
						int number = db.getInt(1);
						double value = number * km.maxAll / km.maxFacility[index];
						index++;
						facility.add(number);
						if (value > maxDouble)
							maxDouble = value;
					}
				}

			}

			ArrayList<Double> facilityScore = new ArrayList<Double>();
			for (int i = 0; i <= facility.size() - 1; i++)
			{
				double score = facility.get(i) * km.maxAll / km.maxFacility[i] / maxDouble * 1000;
				facilityScore.add(score);
			}
			int index = 0;
			double neardist = Double.MAX_VALUE;
			for (int i = 0; i < k; i++)
			{
				double dist = km.editDistance(facilityScore, km.bestCenter[i], len);
				if (dist < neardist)
				{
					neardist = dist;
					index = i;
				}
			}
			color_customPolyg = colors[index];
			customPolygon = true;
			System.out.println("Custom Polygon is divided to " + index);
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}// if

	}// clustering cp5

	public void checkBox(float[] a)
	{
		tmp_weibos.clear();
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] == 1)
			{
				catesOption[i] = 1;
				find_weibo(cates[i]);

			} else if (a[i] == 0)
			{
				catesOption[i] = 0;
			}
		}
	}

	public void radiobox(int theC)
	{

		switch (theC) {
		case (0):
			para = 0;
			break;
		case (1):
			para = 1;
			break;
		case (2):
			para = 2;
			break;
		case (3):
			para = 3;
			break;
		case (4):
			para = 4;
			break;
		case (5):
			para = 5;
			break;
		case (6):
			para = 6;
			break;
		case (7):
			para = 7;
			break;
		case (8):
			para = 8;
			break;
		case (9):
			para = 9;
			break;
		default:
			para = -1;
			break;
		}

	}// radiobox cp5

	public void customPolygonDisplay(int theC)
	{
		switch (theC) {
		case (0):
			drawable = true;
			System.out.println("drawable");
			break;
		default:
			drawable = false;
			System.out.println("undrawable");
			break;
		}

	}// cpd cp5

	public void mouseDragged()
	{

		float minX = accordion.getPosition().x - 30;
		float minY = accordion.getPosition().y - 30;
		float maxX = minX + accordion.getWidth() + 30 + 30;
		float maxY = minY + 30 + 30;
		if (g1.isOpen())
			maxY += g1.getBackgroundHeight() + g1.getBarHeight();
		else
			maxY += g1.getBarHeight();
		if (g2.isOpen())
			maxY += g2.getBackgroundHeight() + g2.getBarHeight();
		else
			maxY += g2.getBarHeight();
		if (g3.isOpen())
			maxY += g3.getBackgroundHeight() + g3.getBarHeight();
		else
			maxY += g3.getBarHeight();

		float dpdMinX = dpdX;
		float dpdMinY = dpdY - dpdBarHeight;
		float dpdMaxX = dpdMinX + dpdWidth;
		float dpdMaxY = dpdMinY;
		if (dpd.isOpen())
			dpdMaxY += dpdItemHeight * 4 + dpdBarHeight;

		if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY)
		{
			map.panTo(centerOfScreen);
		} else if (mouseX >= skbarX - 30 && mouseY >= skbarY - 30 && mouseX <= skbarX + skbarWidth + 30
				&& mouseY <= skbarY + skbarHeight + 30)
		{
			map.panTo(centerOfScreen);
		} else if (mouseX >= dpdMinX - 20 && mouseY >= dpdMinY - 20 && mouseX <= dpdMaxX + 20
				&& mouseY <= dpdMaxY + 20)
		{
			map.panTo(centerOfScreen);
		} else
		{
			centerOfScreen = map.getCenter();
		}

	}

	public void keyPressed()
	{

		// if (!theEvent.isFrom("ControlP5window")){
		if (this.focused && g3.isOpen() && drawable)
		{
			if ((key == 'a' || key == 'A') && !customPolygon)
			{
				System.out.println("ADD POI");
				Location L = new Location(map.getLocationFromScreenPosition((float) (mouseX),
						(float) (mouseY)));
				customPolyg.add(L);
				lM.addLocations(L);
				lM.setColor(color(255, 0, 0));
			}// end if
			if ((key == 'd' || key == 'D') && (customPolyg.size() > 0) && !customPolygon)
			{
				System.out.println("DELETE POI");
				lM.removeLocation(customPolyg.get(customPolyg.size() - 1));
				customPolyg.remove(customPolyg.size() - 1);

			}// end if
			if (key == 'c' || key == 'C')
			{
				customPolyg.clear();
				customPolygon = false;
				System.out.println("CLEAR POI");
				markerManager.removeMarker(lM);
				lM = new SimpleLinesMarker();
			}
			if (key == 'p' || key == 'P')
			{
				for (int i = 0; i <= customPolyg.size() - 1; i++)
				{
					float a = customPolyg.get(i).getLat();
					float b = customPolyg.get(i).getLon();
					if (i < customPolyg.size() - 1)
					{
						ScreenPosition sp1 = map.getScreenPosition(customPolyg.get(i));
						ScreenPosition sp2 = map.getScreenPosition(customPolyg.get(i + 1));
						System.out.println("Location(" + a + "," + b + ");  " + sp1.x + " " + sp1.y + " "
								+ sp2.x + " " + sp2.y);
					}
				}
			}
			markerManager.addMarker(lM);
			map.addMarkerManager(markerManager);
		}// if

	}// keyPressed

	public void controlEvent(ControlEvent theControlEvent)
	{
		if (theControlEvent.isFrom("Date"))
		{
			// min and max values are stored in an array.
			// access this array with controller().arrayValue().
			// min is at index 0, max is at index 1.

			handleMin = (int) (theControlEvent.getController().getArrayValue(0));
			handleMax = handleMin
					+ (int) (theControlEvent.getController().getArrayValue(1) - theControlEvent
							.getController().getArrayValue(0));
			if (handleMax < handleMin)
			{
				handleMax = handleMin;
			}

		}
		if (theControlEvent.isGroup() && theControlEvent.name().equals("Type"))
		{
			int item = (int) theControlEvent.group().value();

			switch (item) {
			case 1:
				rangeSeekBar.setRangeValues(0, 6);
				break;
			case 2:
				rangeSeekBar.setRangeValues(6, 12);
				break;
			case 3:
				rangeSeekBar.setRangeValues(12, 18);
				break;
			case 4:
				rangeSeekBar.setRangeValues(18, 24);
				break;
			case 5:
				rangeSeekBar.setRangeValues(0, 24);
				break;

			}
			handleMin = (int) (theControlEvent.getController().getArrayValue(0));
			handleMax = handleMin
					+ (int) (theControlEvent.getController().getArrayValue(1) - theControlEvent
							.getController().getArrayValue(0));
			if (handleMax < handleMin)
			{
				handleMax = handleMin;
			}
		}
	}// controlEvent
}// end class
