package snowplows;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

class Node{
	public Node(Point in, Edge[] neighbors){
		loc=in;
		conn=new ArrayList<Edge>(neighbors.length);
		for(int i=0;i<neighbors.length;i++){
			conn.add(neighbors[i]);
		}
	}
	public Node(Point in, ArrayList<Edge> neighbors){
		this(in, (Edge[])neighbors.toArray());
	}
	public Node(Point in){
		conn =new ArrayList<Edge>();
		loc=in;
	}
	ArrayList<Edge> conn;	//connected
	Point loc;
	@Override
	public boolean equals(Object in){
		if(in instanceof Node){
			return ((Node)in).loc.equals(loc);
		}
		return false;
	}
	public int manhattanDistance(Node in){
		return Math.abs(loc.y-in.loc.y) + Math.abs(loc.x-in.loc.x);
	}
	public boolean addEdgeShadow(Node k, int add){
		for(Edge i: conn){
			if(i.n.equals(k)){
				i.shadow+=add;
				for(Edge j: k.conn){
					if(j.n.equals(this)){
						j.shadow+=add;
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean setEdgeShadow(Node k, int value){
		for(Edge i: conn){
			if(i.n.equals(k)){
				i.shadow=value;
				for(Edge j: k.conn){
					if(j.n.equals(this)){
						j.shadow=value;
						return true;
					}
				}
			}
		}
		return false;
	}
	public void addNode(Node n){
		conn.add(new Edge(n,0));
	}
	public void addTemporaryNode(Node n){
		conn.add(new Edge(n,0, true));
	}
	public String toString(){
		return "("+degree()+"/"+loc.x+","+loc.y+")";
	}
	/**
	 * @param depth 0 is equivalent to toString()
	 * @return
	 */
	public String toStringDeep(int depth){
		String ret = "("+loc.x+", "+loc.y+")";
		if(depth>0){
			ret += " -> ";
			for(Edge i: conn)ret+=i.shadow+" "+i.n.toStringDeep(depth-1) + " | ";
		}
		return "{"+ret+"}";
	}
	void disconnect(Node n){
		if(conn.contains(n)){
			n.conn.remove(this);
			conn.remove(n);
		}
	}
	int degree(){
		int deg = 0;
		for(Edge i: conn)if(i.shadow==0)++deg;
		return deg;
	}
	public static String toString(Node[][] in){
		String ret = "";
		for(Node[] is : in){
			for(Node j : is){
				ret+=j.degree();
			}
			ret+="\n";
		}
		return ret;
	}
	class Edge{
		public Edge(Node b, int shadow){
			this.n=b;
			this.shadow=shadow;
			this.temporary=false;
		}
		public Edge(Node b, int shadow, boolean temporary){
			this.n=b;
			this.shadow=shadow;
			this.temporary=temporary;
		}
		Node n;
		int shadow;
		boolean temporary;
		public String toString(){
			return ("[->"+n+" "+shadow+(temporary?"T":"")+"]");
		}
	}
	/**
	 * Removes all shadows and temporary edges.
	 */
	public void clear() {
		for(Edge k:conn)k.shadow=0;
		Edge edge=null;
		Iterator<Edge> itr = conn.iterator();
		while(itr.hasNext()){
			edge=itr.next();
			if(edge!=null && edge.temporary)itr.remove();
		}
	}
}