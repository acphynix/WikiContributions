package snowplows;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Path extends LinkedList<Point>{

	private static final long serialVersionUID = -6141571989581136729L;
	Random random=new Random();
	
	public void mutateOn(Grid grid, double mutationChance){
		if(random.nextDouble()>mutationChance)return;
		int index = random.nextInt(size()-2)+1;
		Point a = get(index);
		Point b = get(index+1);
		if(a.y == b.y){		//horizontal. push vertically.
			int direction = 0;
			if(a.y==0)direction=1;
			else if(a.y==grid.shape.height-1)direction=-1;
			else direction=random.nextDouble()>0.5?1:-1;
			
			if(direction == 1){
				add(index+1, new Point(a.x,a.y+1));
				add(index+2, new Point(b.x,a.y+1));
			}else{
				add(index+1, new Point(a.x,a.y-1));
				add(index+2, new Point(b.x,a.y-1));
			}
			if(random.nextDouble()<0.5){
				add(index+3, new Point(b.x,b.y));
				add(index+4, new Point(a.x,a.y));
			}
		}
		else if(a.x == b.x){		//horizontal. push vertically.
			int direction = 0;
			if(a.x==0)direction=1;
			else if(a.x==grid.shape.width-1)direction=-1;
			else direction=random.nextDouble()>0.5?1:-1;
			
			if(direction == 1){
				add(index+1, new Point(a.x+1,a.y));
				add(index+2, new Point(a.x+1,b.y));
			}else{
				add(index+1, new Point(a.x-1,a.y));
				add(index+2, new Point(a.x-1,b.y));
			}
			if(random.nextDouble()<0.5){
				add(index+3, new Point(b.x,b.y));
				add(index+4, new Point(a.x,a.y));
			}
		}
	}
}