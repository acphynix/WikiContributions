package composition;

import java.util.Random;

import main.Note;

public class SimpleAlgorithm extends Algorithm {

	public SimpleAlgorithm() {
		random= new Random(System.currentTimeMillis());
	}
	Random random;
	int[] pentatonic = new int[]{60, 62, 64, 67, 69, 72, 74, 76, 79, 81, 84};

	protected Note generateNextNote(){
		Note n;
		if (noteList.size()==0 || Math.random()<0.25)n=new Note(0.2f+(float)Math.random()*0.8f, pentatonic[random.nextInt(pentatonic.length-1)], 300+100*random.nextInt(10));
		else if(noteList.size()>10 && Math.random()<0.4)n=noteList.get(Math.max(0,noteList.size()-10));
		//else if(Math.random()<0.3)return noteList.get(Math.max(0,noteList.size()-50));
		else n=null;
		if(n!=null){
			for(int i=60;i<n.getPitch();i++)System.out.print("-");
			System.out.print("o");
			for(int i=84;i>n.getPitch();i--)System.out.print("-");
			System.out.println();
		}
		return n;
	}
}
