package main;

import javax.sound.midi.MidiUnavailableException;

import composition.Algorithm;
import composition.SimpleAlgorithm;
import output.MusicPlayer;

/**
 * Tasks
 * 	1. Convert Generated sequence to 
 * @author Ashwin
 *
 */
public class MusicGenerator {
	public MusicGenerator() {
		try {
			player = new MusicPlayer();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			System.err.println("Could not create MIDI output device. Exiting.");
			System.exit(0);
		}
	}
	MusicPlayer player;
	boolean running = true;
	Algorithm generator;
	private void sleep(long millis){
		try{
			Thread.sleep(millis);
		}catch(InterruptedException e){e.printStackTrace();}
	}
	public void generateMusic(){
		sleep(250);
		generator = new SimpleAlgorithm();
		long granularity = 75;				//granularity is 125ms.
		int step = 0;
		long numSteps = 120*1000/granularity;	// first 120 seconds of music
		while(step < numSteps){
			while(System.currentTimeMillis() % granularity != 0);
			Note in = generator.getNextNote();
			if(in != null) player.play(in);
			++step;
			sleep(1);
		}
		while(player.isPlaying()){
			sleep(100);
		}
		sleep(1000);
		player.close();
		System.exit(0);		
	}
	public static void main(String[] args) {
		MusicGenerator k = new MusicGenerator();
		k.generateMusic();
	}
}
