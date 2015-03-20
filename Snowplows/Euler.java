package snowplows;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class Euler implements Runnable{
	private Euler(){}
	Grid grid;
	Node[][] nodes;
	Path generatedPath;
	int pathLength  =-1;
	int expectedPathLength = -1;
	
	Display display;
	
	public void cleanGrid(){
		for(Node[] is:nodes)for(Node j:is){
			j.clear();
		}
	}
	public void markEdgesFirstPlow(Path path){
		// Mark all of the edges which are traversed by the first plow.
		nodes = grid.getNodeList();
		Iterator<Point> itrI = path.iterator();
		Point prev = itrI.next();
		Point curr = null;
		while(itrI.hasNext()){
			curr = itrI.next();
			Node start = nodes[prev.y][prev.x];
			for(int j=0;j<start.conn.size();j++){
				Node end = start.conn.get(j).n;
				if(end.loc.equals(curr))
					if(start.addEdgeShadow(end, 1))break;
			}
			prev = curr;
		}
//		for(Node[] is: nodes){
//			for(Node j : is)System.out.println(j.toStringDeep(1));
//		}
	}
	public void calculateEulerPath(){
		// Calculate the redundant edges that must be followed to make an Euler path.
		int minimumStartingDistance = Integer.MAX_VALUE;	// distance to the first node arrived at in this path.
		int minimumRedundancy = Integer.MAX_VALUE;
		int numberUntraversedEdges = 0;
		Node[][] redundantPairings = null;
		Node circuitLocus = null;	// the first node we should arrive at.
		
		LinkedList<Node> oddDegreeNodes=new LinkedList<Node>();
		// Calculate circuit locus
		for(Node[] is: nodes)for(Node j: is){
			int degree = j.degree();
			if(degree != 0){
				int distance = j.manhattanDistance(nodes[0][0]);
				if(distance<minimumStartingDistance){
					minimumStartingDistance = distance;
					circuitLocus = j;
				}
			}
		}
		if(circuitLocus == null){
			circuitLocus = nodes[0][0];
		}
		if(!circuitLocus.loc.equals(nodes[0][0].loc)){	//if the circuit locus is not the origin
//			System.out.println(circuitLocus);
			if(!nodes[0][0].setEdgeShadow(circuitLocus, 0)){
				nodes[0][0].addTemporaryNode(circuitLocus);
				circuitLocus.addTemporaryNode(nodes[0][0]);
			}
		}
		
		// Determine odd degreed nodes
		for(Node[] is: nodes)for(Node j: is){
			int degree = j.degree();
			numberUntraversedEdges += degree;
			if(degree%2 == 1)	oddDegreeNodes.add(j);
		}
		
		
		numberUntraversedEdges/=2;	// Number of untraversed edges = sum(degrees)/2
//		System.out.println("__\n"+Node.toString(nodes));
//		System.out.println("Odd Degreed Nodes: "+oddDegreeNodes);
//		System.out.println("Locus: "+circuitLocus);

		Random random=new Random();
		for(int i=0;i<1;i++){
			Node[][] pairings = new Node[oddDegreeNodes.size()/2][];
			LinkedList<Node> copied = (LinkedList<Node>) oddDegreeNodes.clone();
			int index = 0;
			int sumMetric = 0;
			while(!copied.isEmpty()){
				pairings[index]=new Node[2];
				int v1 =random.nextInt(copied.size());
				pairings[index][0]=copied.get(v1);
				copied.remove(v1);
				int v2 =random.nextInt(copied.size());
				pairings[index][1]=copied.get(v2);
				copied.remove(v2);
				sumMetric+=pairings[index][0].manhattanDistance(pairings[index][1]);
//				System.out.println(pairings[index][0]+" <> "+pairings[index][1]);
				++index;
			}
			// check if the resultant graph is connected.
			HashSet<Point> allNodes=new HashSet<Point>();
//			Node begin = pairings[]
			/*
			index = 0;
			Collections.shuffle(copied);
			int sumMetric =0 ;
			while(!copied.isEmpty()){
				Node master = null;
				Node best = null;
				int metric = Integer.MAX_VALUE;
				for(Node k: copied){
					if(master == null)master=k;
					else if(best == null){
						best=k;
						metric = master.manhattanDistance(k);
					}
					else{
						int distance = master.manhattanDistance(k);
						if(distance<metric){
							metric = distance;
							best = k;
						}
					}
				}
				pairings[index] = new Node[]{master, best};
				copied.remove(master);
				copied.remove(best);
				sumMetric+=metric;
				++index;
			}
			*/
			/*
			System.out.println("Cost: "+sumMetric);
			for(Node[] pair : pairings){
				System.out.println(pair[0]+" - "+pair[1]);
			}
			System.out.println("Odd Degreed Nodes: "+copied);
			*/
			if(sumMetric < minimumRedundancy){
				redundantPairings = pairings;
				minimumRedundancy = sumMetric;
			}
		}
		
//		System.out.println("Redundancy: "+minimumRedundancy);
		for(Node[] k: redundantPairings){
//			System.out.print(k[0]+" - "+k[1]+" : "+k[0].manhattanDistance(k[1])+"\n");
			if(!k[0].setEdgeShadow(k[1], 0)){
				k[0].addTemporaryNode(k[1]);
				k[1].addTemporaryNode(k[0]);
			}
		}

		expectedPathLength = (minimumRedundancy+numberUntraversedEdges);
//		System.out.println("Total path length: "+pathLength);
		
		// Generate a working path using Hierholzer's Algorithm
		Path plow=new Path();
		Node curr = nodes[0][0];
//		for(int x =0; x<circuitLocus.loc.x;x++)plow.add(new Point(x, 0));
//		for(int y =0; y<=circuitLocus.loc.y;y++)plow.add(new Point(circuitLocus.loc.x, y));
		plow.add(new Point(0,0));
		int minIndex = plow.size();
		int addIndex = minIndex-1;
		outer:
		while(true){
//			System.out.println(addIndex);
//			System.out.println(curr);
			for(Node.Edge e : curr.conn){
				if(e.shadow==0){	// this is an untraversed path.
					++addIndex;
//					++e.shadow;
//					System.out.println(curr.conn);
					curr.addEdgeShadow(e.n, 1);
//					System.out.println("Add "+e.n.loc+" at "+addIndex+"in"+plow);
					plow.add(addIndex, e.n.loc);
//					System.out.print("  ");
//					System.out.println(curr + " -> "+ e.n);
					curr = e.n;
//					System.out.println(curr.conn);
//					System.out.println(e);
					continue outer;
				}
			}
//			if(addIndex>=plow.size()-1)--addIndex;
			--addIndex;
			if(addIndex < minIndex)break;
			Point prev = plow.get(addIndex);
			curr = nodes[prev.y][prev.x];
//			System.out.print("- ");
//			System.out.println(curr);
		}
		generatedPath = plow;
		/*
		HashSet<Integer> edgeSet = new HashSet<Integer>();	// contains all traversed edges.
		Point prevPt=null;
		for(Point currPt : plow){
			if(prevPt!=null){
				//add both forward and backward directions..
				edgeSet.add((prevPt.x*grid.shape.width + prevPt.y)
						+ (((currPt.x*grid.shape.width + currPt.y))<<32));
				
				edgeSet.add((currPt.x*grid.shape.width + currPt.y)
						+ (((prevPt.x*grid.shape.width + prevPt.y))<<32));
			}
			prevPt=currPt;
		}
		System.out.println(edgeSet.size());
		*/
		pathLength = 0;
		Point prevPt = null;
		for(Point currPt : plow){
			if(prevPt!=null){
				pathLength+=Math.abs(prevPt.x-currPt.x)+Math.abs(prevPt.y-currPt.y);
			}
			prevPt=currPt;
		}
//		System.out.println(plow);
	}
	public void run(){
		while(true){
			display.render();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void createDisplay(){
		display = new Display();
		Thread render = new Thread(this);
		render.setDaemon(true);
		render.start();
	}
	public static void main(String[] args){
		Euler euler = new Euler();
		
		int popSize = 80000;
		int numGens = 3;
		euler.grid = new Grid(new Dimension(4,5));
		
		
		euler.createDisplay();
		euler.display.set(euler.grid, null, null);
		euler.display.fitSize();
		Path path = euler.grid.getPerimeterPath();
		
		
		Strain[] population = new Strain[popSize];
		for(int i=0;i<population.length;i++){
			population[i] = euler.new Strain(euler.grid.getPerimeterPath());
			population[i].score = Integer.MAX_VALUE;
		}
		int itr = 0;
		String[] text = new String[]{"","","",""};
		text[0]= "Population: "+popSize+" over "+numGens+" generations.";
		for(int generation=0;generation<numGens;generation++){
			itr=0;
			for(int i=0;i<population.length;i++){
				long begin = System.currentTimeMillis();
				
				path = population[i].path;
				//System.out.println(path);
				if(i>200){
					path.mutateOn(euler.grid, 0.7);
					euler.markEdgesFirstPlow(path);
					euler.calculateEulerPath();
					euler.cleanGrid();
					if(euler.pathLength >= euler.expectedPathLength){
						population[i].score=Math.max(euler.pathLength, path.size()-1);
						population[i].gen = euler.generatedPath;
					}
					text[3]= "Score: "+population[i].score;
					text[1]= "Path Length: "+(path.size()-1)+", "+euler.pathLength;
					text[2]= "Itr: "+generation+"."+itr;
					euler.display.set(euler.grid, text, path, euler.generatedPath);
					euler.display.setVisible(true);
				}
				++itr;
			}
			Arrays.sort(population);
			System.arraycopy(population, 0, population, population.length/2, population.length/2);
		}

		text[1]= "Path Length: "+(population[0].path.size()-1)+", "+(population[0].gen.size()-1);
		text[2]= "Optimal";
		text[3]= "Score: "+population[0].score;
		euler.display.set(euler.grid, text, population[0].path, population[0].gen);
		
		try {
			String filename = "rsc/out"+euler.grid.shape.width+"x"+euler.grid.shape.height+"-"+popSize+","+numGens;
			int num =1;
			File file;
			do{
				file = new File(filename+" ("+Integer.toString(num)+")");
			}while(file.exists());
			
			ImageIO.write(euler.display.buffer, "PNG", file);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class Strain implements Comparable<Strain>{
		Strain(Path path){
			this.path=path;
		}
		Path path;
		Path gen;
		int score;
		@Override
		public int compareTo(Strain o) {
			return score - o.score;
		}
	}
}