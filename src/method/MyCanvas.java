package method;

import java.math.BigDecimal;

import processing.core.PApplet;
import controlP5.Canvas;

 public class MyCanvas{
	      public float ox, oy, oheight, owidth;
	      public float ix, iy, singleWidth, originX, proportion;  
	      public int npeople, ndata;
	      public int maxValue, minValue;
	      public int[] datas;
	      public String[] tags;
	      public boolean dataDone = false; 

	      public MyCanvas(float ox, float oy, float owidth, float oheight){
	          this.ox = ox;
	          this.oy = oy;
	          this.owidth = owidth;
	          this.oheight = oheight;
	      }//constructor
	      
	      public void setup(PApplet p){
	 

	      }
	  
	      public void draw(PApplet p){
	          maxValue =0;    minValue = 5000000;
	          for (int i=0; i<=ndata-1; i++){
	              if (datas[i]> maxValue)  
	                  maxValue = datas[i];
	              if (datas[i] < minValue)
	                  minValue = datas[i];
	          }
	          proportion = (oheight-100)/(maxValue - minValue);  
	          
	          p.rect(ox,oy,5,5);
	      
	          singleWidth = owidth / ndata -20;
	          /*
	          if ( singleWidth>= 20) {
	              singleWidth = 20;
	              originX = (owidth - ndata * singleWidth) / 2 + ox;   
	          } else { 
	              originX = ox; 
	          }
	          */
	          originX = ox;
	          ix = originX - singleWidth;
	          int save_i=0;
	          float save_iy=0;
	          boolean mouseE=false;
	          for (int i = 0; i <= ndata-1; i++){
	              ix = ix + singleWidth +20;
	              iy = oy+oheight - (datas[i] - minValue) * proportion;
	              p.fill(0, 102, 153, 204);
	              p.rect(ix, oy+oheight, singleWidth, 2);
	              p.rect(ix, iy, singleWidth, (datas[i] - minValue)*proportion);
	              
	            	 
	              if (p.mouseX>=ix && p.mouseX<ix+singleWidth && p.mouseY>=iy && p.mouseY<oy+oheight){
	                     save_i = i;
	                     save_iy = iy;
	                     mouseE = true;
	              } 
	          }//end for
	              if (mouseE){
	                     p.fill(255,0,0);
	                     p.rect(ox, save_iy, owidth, 1);
	                     p.textSize(16);
	                     p.text(tags[save_i],ox, save_iy-5);
	                     int temp=datas[save_i], len = 1;
	                     while (temp / 10 != 0){
	                         temp = temp /10;
	                         len++;
	                     }
	                     p.text(datas[save_i],ox+owidth-len*7, save_iy-5);
	              }
	      }//end draw
	      
	      public MyCanvas setCanvasSize(float h, float w){
	          oheight = h;    owidth = w;
	          return this;
	      }
	      public MyCanvas setCanvasOxOy(float x, float y){
	          ox = x;      oy = y;
	          return this;
	      }
	      public void setDatas(int[] datas, int ndata){
	          this.datas = datas;
	          this.ndata = ndata;
	          dataDone = true;
	          //for (int i=0; i<=ndata-1; i++) tags[i]=String.valueOf(new BigDecimal(datas[i]).setScale(0, BigDecimal.ROUND_HALF_UP));
	         
	      }
	      public void setTags(String[] tags, int ntag){
	          if ( dataDone && (ndata == ntag) )
	             this.tags =tags;
	      }
	  }//end class MyCanvas