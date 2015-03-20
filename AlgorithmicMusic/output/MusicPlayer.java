package output;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import main.Note;

public class MusicPlayer implements Runnable{

	public MusicPlayer() throws MidiUnavailableException{
		synth = MidiSystem.getSynthesizer();
		synth.open();
		channels = synth.getChannels();
		
		Thread midiHandler = new Thread(this);
		midiHandler.start();
	}
	Synthesizer synth;
	MidiChannel[]	channels;
	PriorityQueue<Task> tasks = new PriorityQueue<Task>();
	Task nextTask;
	public void play(Note in){
		playNote(in.getChannel(), in.getPitch(), in.getVelocityByte(), in.getDuration());
	}
	public void close(){
		synth.close();
	}
	public boolean isPlaying(){
		return nextTask!=null;
	}
	private void playNote(int channel, int pitch, int velocity, long duration){
//		System.out.println("Playing");
		channels[channel].noteOn(pitch, velocity);
		tasks.add(new Task(channel, pitch, Task.Type.NoteOff,System.currentTimeMillis()+duration));
//		channels[channel].noteOff(pitch);
		nextTask = tasks.poll();
		synchronized(this){
			this.notify();
		}
	}
	public void run() {
		while(true){
			if(nextTask!=null){
				if(System.currentTimeMillis() > nextTask.timeAt){
					if(nextTask.type == Task.Type.NoteOff){
						channels[nextTask.channel].noteOff(nextTask.pitch);
//						System.out.println("Executed task "+ nextTask);
					}
					nextTask = tasks.poll();
				}
			}
			else{
				try {
					synchronized(this){
						this.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) throws MidiUnavailableException, InterruptedException{
		MusicPlayer k = new MusicPlayer();
		Thread.sleep(250);
		for(int i=0;i<12;i++){
			k.play(new Note((float)Math.random(), i+60, 150));
			Thread.sleep(250);
		}
		while(k.isPlaying()){
			Thread.sleep(100);
		}
		Thread.sleep(1000);
		k.close();
		System.exit(0);
	}
	/**
	 * Something that must be done at a specific time in the future (eg. turning a note off)
	 */
	private static class Task{
		Task(int channel, int pitch, Type type, long time){
			this.channel=channel;
			this.pitch=pitch;
			this.type=type;
			this.timeAt=time;
		}
		enum Type{NoteOff};
		Type type;
		long timeAt;
		int pitch;
		int channel;
		public String toString(){
			return "["+type+": "+pitch+" c_"+channel+" @ "+timeAt+"]";
		}
	}
}
