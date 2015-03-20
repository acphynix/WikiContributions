package snowplows;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Array;

/**
 * This makes no attempt at being thread safe.
 *
 */
public class Grid {
	public Grid(Dimension in){
		this.shape=in;
		degreeList= new int[in.height][in.width];
		nodes = new Node[in.height][in.width];
		for(int i=0;i<degreeList.length;i++)for(int j=0;j<degreeList[i].length;j++){
			if(i==0 || i == degreeList.length-1 || j==0 || j == degreeList[i].length-1)degreeList[i][j]=3;
			else degreeList[i][j]=4;
			nodes[i][j]=new Node(new Point(j, i));
		}
		for(int y=0;y<degreeList.length;y++)for(int x=0;x<degreeList[y].length;x++){
			if(y>0)nodes[y][x].addNode(nodes[y-1][x]);
			if(x>0)nodes[y][x].addNode(nodes[y][x-1]);
			if(y<degreeList.length-1)	nodes[y][x].addNode(nodes[y+1][x]);
			if(x<degreeList[y].length-1)nodes[y][x].addNode(nodes[y][x+1]);
		}
		for(Node[] is: nodes){
			for(Node j : is)System.out.println(j.toStringDeep(1));
		}
		degreeList[0][0]=2;
		degreeList[in.height-1][0]=2;
		degreeList[0][in.width-1]=2;
		degreeList[in.height-1][in.width-1]=2;
		System.out.println(Node.toString(nodes)+"__\n");
	}
	public final Dimension shape;
	private final int[][] degreeList;
	private final Node[][] nodes;
	public int[][] getDegreeListClone(){
		int[][] cp = degreeList.clone();
		for(int i=0;i<cp.length;i++){
			cp[i]=degreeList[i].clone();
		}
		return cp;
	}
	public Node[][] getNodeList(){
		return nodes;
	}
	public Path getPerimeterPath(){
		Path path =new Path();
		Point initial = new Point(0,0);
		path.add(initial);
		for(int i=1;i<shape.height;i++)path.add(new Point(0,i));
		for(int i=1;i<shape.width;i++)path.add(new Point(i,shape.height-1));
		for(int i=2;i<shape.height;i++)path.add(new Point(shape.width-1,shape.height-i));
		for(int i=1;i<=shape.width;i++)path.add(new Point(shape.width-i, 0));
		return path;
	}
	
	public boolean[][] getBooleanGrid(){
		boolean[][] big = new boolean[shape.width][shape.height];
		for(int i=0;i<big.length;i++)for(int j=0;j<big[i].length;j++)big[i][j]=true;
		return big;
	}
}
