package evolution;

import java.util.ArrayList;

public class Constant extends Node{
	static{
		consts=new ArrayList<Float>();
	}
	public static ArrayList<Float> consts;
	public Constant(int c){
		val=c;
		this.c=new Node[0];
	}
	private int val;
	public float eval(){
		return consts.get(val);
	}
	public String toString(){
		return ""+consts.get(val);
	}
	public static Value random(){
		return Node.valuei((int)(Math.random()*(Constant.consts.size())));
	}
	protected void cleanself() {
		
	}
	public boolean equals(Object o){
		if(!(o instanceof Constant)){
			return false;
		}
		Constant c=(Constant)o;
		return ((int)c.val==0);
	}
}