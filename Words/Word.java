package wordmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.imageio.stream.FileImageInputStream;

public class Word implements Comparable<Word>, Serializable{
	public static final ArrayList<Word> words = new ArrayList<Word>();
	private Word(String text){
		this.text=text;
		next = new ArrayList<>();
		for(int i=0;i<PREDICT_SIZE;i++)next.add(new HashSet<WordEntry>());
	}
	static{
		wordBuffer = new ArrayList<>();
	}
	public final String text;
	public static final int PREDICT_SIZE=5;
	ArrayList<HashSet<WordEntry>> next;
	/**
	 * list of N last-entered words. The most recent word is wordBuffer[wordBuffer.size()-1],
	 * while the fifth-last word is wordBuffer[wordBuffer.size()-5].
	 */
	public static ArrayList<Word> wordBuffer;
	public boolean equals(Word w){
		return text.equals(w.text);
	}
	/**
	 * Deletes all words in both the next and prev arrays
	 * that have a num less than the specified threshold.
	 * @param threshold
	 */
	void clean(int threshold){
		if(threshold-100000<0)return;
		for(int i=0;i<next.size();i++){
			HashSet<WordEntry> list = next.get(i);
			Iterator<WordEntry> it = list.iterator();
			while(it.hasNext()){
				WordEntry t = it.next();
				if(t.num<threshold){
					it.remove();
				}
			}
		}
	}
	int numChildren(){
		return next.size();
	}
	public static Word get(String w){
		Word n=new Word(w);
		for(Word e:words){
			if(e.text.equals(w)){
				return e;
			}
		}
		//System.out.println("Creating '"+w+"'");
		words.add(n);
		return n;
	}
	/**
	 * Adds the specified word <b>w</b> to this word's <i>next</i> list,
	 * and adds <b>this</b> to <b>w</b>'s <i>prev</i> list
	 * @param w
	 */
	public static void addNext(Word w){
		boolean alreadyinlist=false;
		int totalNum = Math.min(PREDICT_SIZE, wordBuffer.size());
		for(int i=0;i<totalNum;i++){							//iterate through all words in memory.
			Word modifying = wordBuffer.get(i);					//high 'i' is more recent position.
			WordEntry entry = modifying.new WordEntry(w,1);
			HashSet<WordEntry> list = modifying.next.get(i+PREDICT_SIZE-totalNum);		//modify 
			if(list.contains(entry)){					//if it's already contained...find it, and increment one.
				Iterator<WordEntry> itr = list.iterator();
				while(itr.hasNext()){
					WordEntry temp = itr.next();
					if(temp.equals(entry)){
						temp.num++;
						break;
					}
				}
			}else list.add(entry);						//otherwise, add a new word entry.
		}
		wordBuffer.add(w);
		while(wordBuffer.size()>PREDICT_SIZE)wordBuffer.remove(0);
	}
	public static void newPhrase(){
		wordBuffer.clear();
	}
	public static ArrayList<Word> predictionSet = new ArrayList<>();
	private static HashSet getIntersection(HashSet...sets){
		HashSet s = new HashSet(sets[0]);
		ArrayList toRemove = new ArrayList<>();
		for(int i=1;i<sets.length;i++){
			if(sets[i]==null)continue;
			Iterator it = s.iterator();
			while(it.hasNext()){
				Object t = it.next();
				if(!sets[i].contains(t)){
					toRemove.add(t);
				}
			}
		}
		for(Object o : toRemove){
			if(s.contains(o))s.remove(o);
		}
		return s;
	}
	public static HashSet<WordEntry> predict(Word newadd){
		predictionSet.add(newadd);
		int totalWordsInSet = Math.min(PREDICT_SIZE, predictionSet.size());
		while(predictionSet.size()>PREDICT_SIZE)predictionSet.remove(0);
		HashSet[] sets = new HashSet[PREDICT_SIZE];
		for(int i=0;i<Math.min(PREDICT_SIZE, predictionSet.size());i++){
			sets[i] = predictionSet.get(totalWordsInSet-i-1).next.get(PREDICT_SIZE-i-1);
		}
		return getIntersection(sets);
	}
	public static void saveWords(File out){
		/*
		 * Format:
		 * 
		 * [words.size()]
		 * 	[text]
		 * 	[size of hashset arraylist]
		 * 		[size of hashset]
		 * 			[text]
		 * 			[num]
		 * 		   /
		 * 		/
		 * /
		 */
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
			oos.writeObject(words.size());
			for(Word i:words){
				oos.writeObject(i.text);
				oos.writeInt(i.next.size());
				for(HashSet<WordEntry> j:i.next){
					oos.writeInt(j.size());
					Iterator<WordEntry> it = j.iterator();
					while(it.hasNext()){
						WordEntry temp =  it.next();
						oos.writeObject(temp.word.text);
						oos.writeInt(temp.num);
					}
				}
			}
			oos.close();
		}catch(Exception e){e.printStackTrace();}
	}
	public static void loadWords(File in){
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(in));
			ArrayList<Word> w = (ArrayList<Word>)ois.readObject();
			words.addAll(w);
			ois.close();
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	public int hashCode(){
		return text.hashCode();
	}
	public String toString(){
		String ret = text+": ";
		for(int i=next.size()-1;i>=0;i--){
			ret+="<"+next.get(i)+">\t";
		}
		return ret;
	}
	@Override
	public int compareTo(Word o) {
		return -(o.text.compareTo(text));
	}
	protected class WordEntry implements Comparable<WordEntry>, Serializable{
		public WordEntry(Word word){
			this.word=word;
			num=0;
		}
		public WordEntry(Word word, int num){
			this.word=word;
			this.num=num;
		}
		final Word word;
		int num;
		@Override
		public boolean equals(Object o){
			if(o instanceof WordEntry){
				return word.equals(((WordEntry)o).word);
			}else return false;
		}
		@Override
		public int hashCode(){
			return word.hashCode();
		}
		@Override
		public int compareTo(WordEntry o) {
			return -(o.word.compareTo(word));
		}
		public String toString(){
			if(num>0)
			return "["+word.text+", "+num+"]";
			else return "";
		}
	}
}
