package composition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import main.Note;

public abstract class Algorithm {
	
	protected long step=0;
	protected final LinkedList<Note> noteList = new LinkedList<Note>();
	
	protected abstract Note generateNextNote();
	public final Note getNextNote(){
		noteList.add(generateNextNote());
		++step;
		return noteList.getLast();
	}
}
