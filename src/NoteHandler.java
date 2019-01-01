
public class NoteHandler {
	
	private int[] keys;
	private int MIN_PITCH, MAX_PITCH;
	private int numActive;
	
	// construct new NoteHandler
	public NoteHandler(int MIN_PITCH, int MAX_PITCH) {
		this.MIN_PITCH = MIN_PITCH;
		this.MAX_PITCH = MAX_PITCH;
		this.keys = new int[MAX_PITCH - MIN_PITCH + 1];
		this.numActive = 0;
	}
	
	// convert from MIDI pitch number to frequency in Hz (taken from TarsosDSP, https://github.com/JorenSix/TarsosDSP/blob/master/src/core/be/tarsos/dsp/util/PitchConverter.java
	public static double midiCentToHertz(final double midiCent) {
		return 440 * Math.pow(2, (midiCent - 69) / 12d);
	}
	
	// get the corresponding frequencies of notes currently being held
	public float[] getFrequencies() {
		float[] frequencies = new float[this.numActive];
		int midiNum;
		
		// if any notes are active
		if (frequencies.length > 0) {
			int j = 0;
			// for each note
			for (int i = 0; i < this.keys.length; i++) {
				// if active note
				if (this.keys[i] == 1) {
					midiNum = i + this.MIN_PITCH;
					
					// if valid MIDI number
					if (midiNum > -1 && midiNum < 128) {
						frequencies[j++] = (float) midiCentToHertz(midiNum);
					} else {
						frequencies[j++] = 0.0f;
					}
				}
			}
		}
		
		return frequencies;
	}
	
	/**
	 * Is passed the channel, pitch and velocity associated with every new NoteOn MIDI message received by a MidiBus
	 *
	 * @param channel the channel on which the NoteOn arrived
	 * @param pitch the pitch associated with the NoteOn
	 * @param velocity the velocity associated with the NoteOn
	*/
	public void noteOn(int channel, int pitch, int velocity) {
		this.keys[pitch - MIN_PITCH] = 1;	// mark active note with 1
		this.numActive++;					// increase number of active notes
	}
	
	/**
	 * Is passed the channel, pitch and velocity associated with every new NoteOff MIDI message received by a MidiBus
	 *
	 * @param channel the channel on which the NoteOff arrived
	 * @param pitch the pitch associated with the NoteOff
	 * @param velocity the velocity associated with the NoteOff
	*/
	public void noteOff(int channel, int pitch, int velocity) {
		this.keys[pitch - MIN_PITCH] = 0;			// mark inactive note with a 0
		if (this.numActive > 0) this.numActive--;	// decrease number of active notes
	}
}