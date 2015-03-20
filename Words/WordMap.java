package wordmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import wordmap.Word.WordEntry;

public class WordMap {

	void begin(){
		
	}
	String parse(File in){
		System.out.println("Loading "+in.getName());
	    StringBuilder sb=new StringBuilder();
		try {
		    final ZipFile zipFile = new ZipFile(in);
		    ZipEntry docx = zipFile.getEntry("word/document.xml");
		    InputStream input = zipFile.getInputStream(docx);
		    BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		    int c=0;
		    boolean ignore=false;
		    while((c=br.read())!=-1){
		    	char n=(char)c;
		    	if(c=='\n'||c=='\r')n='.';
		    	if(c=='—')n=';';
		    	if(c==':')n=';';
		    	if(c=='"'||c=='…')n=' ';
		    	if(c=='\'')n=' ';
		    	if(c=='<')ignore=true;
		    	if(!ignore){
		    		sb.append(n);
			    	if(n=='.'||n==';')sb.append(' ');
		    	}
		    	if(c=='>')ignore=false;
		    }
		    br.close();
		}
		catch (final Exception ioe) {
			System.out.println("\terror");
		    //System.err.println("Unhandled exception:");
		    //ioe.printStackTrace();
		    return null;
		}
		return sb.toString();
	}
	
	String parse(URL url){
		try{
			URLConnection con = url.openConnection();
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(con.getContentType());
			/* If Content-Type doesn't match this pre-conception, choose default and 
			 * hope for the best. */
			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = new InputStreamReader(con.getInputStream(), charset);
			StringBuilder buf = new StringBuilder();
			boolean ignore=false;
			boolean readingTag=false;
			String inTag="";
			String tags="";
			while (true) {
			  int ch = r.read();
			  if (ch < 0)break;
			  char c=(char)ch;
			  if(c=='<'){
				  ignore=true;
				  readingTag=true;
			  }
			  else if(readingTag){
				  inTag+=c;
			  }
			  if(!ignore) buf.append(c);
			  if(ch=='>'){
				  if(inTag.contains(" ")){
					  inTag=inTag.substring(0, inTag.indexOf(" ")).trim();
				  }else inTag=inTag.substring(0,inTag.length()-1);
				  if(inTag.startsWith("/"))tags=tags.substring(0,tags.length()-inTag.length()+1);
				  else tags+=inTag;
				  inTag="";
				  readingTag=false;
				  ignore=false;
				  buf.append(' ');
				  try{Thread.sleep(0);}catch(Exception e){}
				 // System.out.println(tags);
			  }
			  ignore = (readingTag || tags.contains("style") || tags.contains("script"));
			}
			String str = buf.toString();
			//System.out.println(str);
			return str;
		}catch(Exception e){
			return null;
		}
	}
	void process(String contents){
		if(contents==null)return;
		if(contents.contains("  See also   [ edit ]  ")){
			contents=contents.substring(0,contents.indexOf("  See also   [ edit ]  "));
		}
		contents=contents.toLowerCase();
		StringTokenizer st= new StringTokenizer(contents);
		WordManager wm=new WordManager();
		int ind=0;
		boolean ignore=false;
		while(st.hasMoreTokens()){
			String next = st.nextToken();
			next=next.trim();
			if(ignore){
				if(next.equals("]"))ignore=false;
				continue;
			}
			if(next.equals("["))ignore=true;
			if(next.equals("[")||next.equals("]")||next.contains("_"))continue;
			
			Word.addNext(Word.get(next));
			if(next.endsWith("!")||next.endsWith("?")||next.endsWith(".")||next.endsWith(";")){
				Word.newPhrase();
			}
		}
		
	}
	public void crawlThrough(File dir){
		File[] list=dir.listFiles();
		for(File n:list){
			try{
				if(n.getName().endsWith("docx"))
				process(parse(n));
			}
			catch(Exception e){
				System.out.println("err");
				e.printStackTrace();
			}
		}
	}
	public void print(File out){
		try{
			//BufferedWriter bw=new BufferedWriter(new FileWriter(out));
			//FileOutputStream os=new FileOutputStream(out);
			PrintStream ps = new PrintStream(out);
			for(Word nxt:Word.words){
				nxt.clean(2);
				String print=(nxt.toString());
				if(print!="")ps.println(print);
			}
			ps.close();
		}
		catch(Exception e){}
	}
	public static void main(String[] args) throws MalformedURLException {
		WordMap m = new WordMap();
//		Word.wordBuffer.add(Word.get("hi"));
//		Word.wordBuffer.add(Word.get("there"));
//		System.out.println(Word.wordBuffer);
//		System.exit(0);
		System.out.println("Loading file...");
		//Word.loadWords(new File("rsc/words_save.bin"));
		/*
		String[] urls={
				"http://oll.libertyfund.org/index.php?option=com_staticxt&staticfile=show.php%3Ftitle=201&layout=html",
		};
		URL u = new URL("http://en.wikipedia.org/wiki/Special:Random");
		int max=50;
		for(int i=1;i<=max;i++){
			System.out.println(i+"/"+max+"\t"+Word.words.size());
			m.process(m.parse(u));
		}
		/*
		long begtime=System.currentTimeMillis();
		long timeout=14400000;
		for(int i=0;i<0;i++){
			String s ="http://oll.libertyfund.org/simple.php?id="+i;
			System.out.print(i+" -\t"+Word.words.size()+"\t");
			m.process(m.parse(new URL(s)));
			long time=System.currentTimeMillis()-begtime;
			System.out.print(time+"/"+timeout+":\t"+(int)((100.f*(float)time/(float)timeout))+"%");
			System.out.println();
			if(time>timeout){
				System.out.println("Timeout exceeded: " + (System.currentTimeMillis()-begtime) +"ms.");
				System.out.println("Timeout exceeded: " + (System.currentTimeMillis()-begtime)/1000 +" seconds.");
				System.out.println("Timeout exceeded: " + (System.currentTimeMillis()-begtime)/60000 +" minutes.");
				System.out.println("Timeout exceeded: " + (System.currentTimeMillis()-begtime)/360000 +" hours.");
				break;
			}
		}
		*/
		//Collections.sort(Word.words);
		//m.print(new File("rsc/WordMap.txt"));
		//System.out.println(s);
		//if("hello"!=null)return;
		
		m.crawlThrough(new File("C:/Users/shanmugam/Documents"));
		m.crawlThrough(new File("C:/Users/shanmugam/Documents/Homework"));
		m.crawlThrough(new File("C:/Users/shanmugam/Downloads"));
		m.process("and The quick brown fox jumps over the lazy dog");
		System.out.println("sorting.");
		System.out.println("outputing...");
		m.print(new File("rsc/WordMap.txt"));
		Collections.sort(Word.words);
		m.print(new File("rsc/SortedWordMap.txt"));
		System.out.println("Saving...");
		//Word.saveWords(new File("rsc/words_save.bin"));
		System.out.println("Done.");
		Scanner s = new Scanner(System.in);
		String newLine;
		HashSet<WordEntry> lastWordSet=null;
		while((newLine = s.next()).contains("!")!=true){		//! is exit character
			if(newLine.contains("$")){
				Word.predictionSet.clear();
			}else if(newLine.contains("^")){
				for(int i=0;i<100;i++){
					ArrayList<WordEntry> j = new ArrayList<WordEntry>(lastWordSet);
					if(j.size()==0)break;
					int randindex = (int)(Math.random()*j.size());
					//System.out.println(j.get(randindex).toString());
					String text = j.get(randindex).word.text;
					lastWordSet = Word.predict(Word.get(text));
					System.out.print(text+" ");
				}
				System.out.print("\r\n");
			}else{
				System.out.println(lastWordSet = Word.predict(Word.get(newLine)));
			}
		}
	}

}
