package snowplows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Display extends JFrame{
	public Display(){
		super("Snowplow Problem, Ashwin Chetty");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,800);
		view = new AffineTransform();
		view.translate(100, 140);
		buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		buffer.createGraphics();
	}
	Grid grid;
	Path[] paths;
	AffineTransform view;
	String[] text;
	int renderModulus = 0;
	BufferedImage buffer;
	public void fitSize(){
		setSize(Math.max(200+100*grid.shape.width, 700), Math.max(140+(grid.shape.height-1)*100+200, 200+100*grid.shape.height));
	}
	public void set(Grid in, String[] text, Path... paths){
		grid=in;
		this.paths=paths;
		this.text=text;
	}
	Color[] colors = {new Color(255,100,100,200), Color.red, new Color(100,255,100,200), Color.blue};
	public void render(){
		Graphics d = getGraphics();
		Graphics k = buffer.getGraphics();
		if(isVisible())drawBuffer(k);
		if(d!=null)d.drawImage(buffer, 0, 0, this);
	}
	public void paint(Graphics g){
		buffer=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		render();
	}
	public void drawBuffer(Graphics d){
		Graphics2D g = (Graphics2D) d;
		g.setColor(Color.lightGray);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setTransform(view);
		int numpath = 0;
		for(Path path : paths){
			if(path!=null && !path.isEmpty()){
				g.setStroke(new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
				Object[] arrayPath = ((LinkedList<Point>) path.clone()).toArray();
//				Iterator<Point> itrI = path.iterator();
				Point prev = null;
				Point curr = null;
				int index = 0;
				for(int i=1;i<arrayPath.length;i++){
					++index;
					curr = (Point)arrayPath[i];
					prev = (Point)arrayPath[i-1];
					g.setColor(colors[numpath*2]);
					g.drawLine((numpath*6)+prev.x*100, (numpath*6)+prev.y*100, (numpath*6)+curr.x*100, (numpath*6)+curr.y*100);
					double intrx = (0.8)*curr.x + (0.2)*prev.x;
					double intry = (0.8)*curr.y + (0.2)*prev.y;
					g.setColor(colors[numpath*2+1]);
					g.drawOval((numpath*6)+(int)(intrx*100.0)-2, (numpath*6)+(int)(intry*100.0)-2, 4, 4);
					prev = curr;
				}
			}
			++numpath;
		}
		++renderModulus;
		g.setColor(Color.black);
		if(grid!=null){
			for(int x=0;x<grid.shape.width;x++)for(int y=0;y<grid.shape.height;y++){
				g.fillOval(x*100-6, y*100-6, 12, 12);
			}
		}
		g.setFont(new Font("Consolas", Font.BOLD, 24));
		g.setColor(Color.black);
		if(text!=null){
			int yval = (grid.shape.height-1)*100+40;
			for(String k: text){
				g.drawString(k, 30, yval+=30);
			}
		}
	}
}
