package main;

public class Note {

	public Note(float velocity, int pitch, long durationMillis) {
		this.velocity=velocity;
		this.velocityByte = (byte)(velocity*128);
		this.pitch=pitch;
		this.duration=durationMillis;
		this.channel=0;
	}
	float velocity;
	byte velocityByte;
	int pitch;
	long duration;
	int channel;
	public byte getVelocityByte(){
		return (byte)(velocity*128);
	}
	/**
	 * @return velocity as a decimal between 0 and 1.
	 */
	public float getVelocitySmooth(){
		return velocity;
	}
	public int getPitch(){
		return pitch;
	}
	public long getDuration(){
		return duration;
	}
	public int getChannel(){
		return channel;
	}
}
