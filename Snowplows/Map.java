package snowplows;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Map {
	Map(){
		w=3;
		h=3;
		nodeDist=60;
		ox=60;
		oy=90;
	}
	int nodeDist;
	int w;
	int h;
	int ox, oy;
	Point getIntersectionLocation(int x, int y){
		return new Point(ox+(nodeDist*(x+x)), oy+(nodeDist*(y+y+1)));
	}
	Point gridLocToIntersectionLoc(Point p){
		return getIntersectionLocation(p.x, p.y);
	}
	int wrap = 0;
	void render(Graphics2D g){
		g.setColor(Color.black);
		int i=0, j=0;
		boolean isIntersection=true;
		boolean drawIntersection = true;
		while(i<w+w-1){
			j=0;
			while(j<h+h-1){
				++j;
				if(isIntersection && drawIntersection)g.drawOval(ox+i*nodeDist-6, oy+j*nodeDist-6, 12, 12);
				else if (!isIntersection)g.fillOval(ox+i*nodeDist-3, oy+j*nodeDist-3, 6, 6);
				isIntersection=!isIntersection;
			}
			drawIntersection=!drawIntersection;
			++i;
		}
		if(wrap>=w*h)wrap=0;
		g.setColor(Color.orange);
		Point draw = getIntersectionLocation(wrap%w, wrap/h);
		System.out.println(wrap+": "+draw);
		g.fillOval(draw.x-4,draw.y-4, 8, 8);
		++wrap;
	}
}