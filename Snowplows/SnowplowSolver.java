package snowplows;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JFrame;

public class SnowplowSolver implements Runnable{
	JFrame frame;
	Map map;
	Point[][][] paths;
	LinkedList<int[]> pathLengths;
	int numSnowplows = 3;
	int maxLength    = 6;
	enum Season	{Summer, Winter, Fall};
	void solve(){
		frame=new JFrame("Snowplow NxN");
		frame.setSize(450, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		Thread renderThread=new Thread(this);
		renderThread.start();
		map=new Map();
		int n =numSnowplows;
		int L =maxLength;
		pathLengths =new LinkedList<int[]>();
		int currentIndex = 0;
	}
	
	public static void main(String[] args) {
		int[] vars = new int[]{1, 2, 3, 4, 5, 6};
		for(int ind=-1, val=vars[0]; ind<=vars.length;val=vars[++ind], System.out.println("i "+ind)){
			System.out.println(val);
		}
		//new SnowplowSolver().solve();
	}
	@Override
	public void run() {
		Graphics2D g = (Graphics2D) frame.getGraphics();
		while(true){
			int w = frame.getWidth();
			int h = frame.getHeight();
			try{
				Thread.sleep(300);
			}catch(InterruptedException e){e.printStackTrace();}
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);
			map.render(g);
		}
	}

}
