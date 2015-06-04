package dataset;

public class Cidsort implements Comparable{
		public Double num;
		public Integer id;
		public Cidsort(double num,int id){
			this.num=num;
			this.id=id;
		}
		
		public String toString(){
			return num+","+id;
		}
		public int hashCode(){
			return id*num.hashCode();
		}
		public Double getNum() {
			return num;
		}

		public void setNum(double num) {
			this.num = num;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			Cidsort cid=(Cidsort)o;
			if (cid.getNum().compareTo(this.getNum())>0)
				return -1;
			else if(cid.getNum().compareTo(this.getNum())==0)
				return 0;
			else
				return 0;
		}
}

