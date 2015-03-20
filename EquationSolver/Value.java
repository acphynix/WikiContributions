package evolution;

import java.util.ArrayList;
import java.util.HashMap;

public class Value extends Node{
	static{
		vals=new ArrayList<Float>();
	}
	public static ArrayList<Float> vals;
	public Value(int v){
		val=v;
		c=new Node[0];
	}
	private int val;
	public float eval(){
		return vals.get(val);
	}
	public String toString(){
		return "["+val+"]";
	}
	public static Value random(){
		return Node.valuei((int)(Math.random()*(Value.vals.size())));
	}
	protected void cleanself() {
		
	}
	public boolean equals(Object o){
		return (o instanceof Value)&&((Value)o).val==val;
	}
}