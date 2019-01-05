import java.util.ArrayList;

public class NoteHandler {
	
	private int[] keys;								// array representation of which notes are currently held (1 for held, 0 for released)
	private int MIN_PITCH, MAX_PITCH;				// MIDI number of lowest and highest note on this particular MIDI instrument
	private int numActive;							// number of notes currently being held on keyboard
	private ArrayList<Float> possibleFrequencies;	// list of frequencies of MIDI notes in range MIN_PITCH to MAX_PITCH
	private ArrayList<Float> heldFrequencies; 		// list of frequencies of notes being held on MIDI keyboard
	
	// construct new NoteHandler
	public NoteHandler(int MIN_PITCH, int MAX_PITCH) {
		// record min and max pitches
		this.MIN_PITCH = MIN_PITCH;
		this.MAX_PITCH = MAX_PITCH;
		
		// set up new array with enough space to hold every note from MIN_PITCH to MAX_PITCH
		this.keys = new int[MAX_PITCH - MIN_PITCH + 1];
		
		// initialize active notes to none
		this.numActive = 0;
		
		// create new ArrayList object for heldFrequencies
		this.heldFrequencies = new ArrayList<Float>();
		
		// precompute Hz frequencies of all possible MIDI notes on this instrument (eliminates redundant conversions)
		this.initializeFrequencies();
	}
	
	// convert from MIDI pitch number to frequency in Hz (taken from TarsosDSP, https://github.com/JorenSix/TarsosDSP/blob/master/src/core/be/tarsos/dsp/util/PitchConverter.java
	public static float midiCentToHertz(double midiCent) {
		return (float) (440 * Math.pow(2, (midiCent - 69) / 12d));
	}
	
	// calculate all possible frequency values for MIDI notes in the given range (MIN_PITCH to MAX_PITCH)
	private void initializeFrequencies() {
		this.possibleFrequencies = new ArrayList<Float>();
		
		// for each pitch in given range
		for (int i = this.MIN_PITCH; i <= this.MAX_PITCH; i++) {
			this.possibleFrequencies.add(midiCentToHertz(i));
		}
	}
	
	// get the corresponding frequencies of notes currently being held
	public ArrayList<Float> getFrequencies() {
		// reset frequencies array
		this.heldFrequencies.clear();
		
		// if any notes are active
		if (this.numActive > 0) {
			// for each note
			for (int i = 0; i < this.keys.length; i++) {
				// if active note
				if (this.keys[i] == 1) {
					// add Hz frequency of note to held frequencies array
					this.heldFrequencies.add(this.possibleFrequencies.get(i));
				}
			}
		}
		
		return this.heldFrequencies;
	}
	
	/**
	 * Is passed the channel, pitch and velocity associated with every new NoteOn MIDI message received by a MidiBus
	 *
	 * @param channel the channel on which the NoteOn arrived
	 * @param pitch the pitch associated with the NoteOn
	 * @param velocity the velocity associated with the NoteOn
	*/
	public void noteOn(int channel, int pitch, int velocity) {
		this.keys[pitch - this.MIN_PITCH] = 1;	// mark active note with 1
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
		this.keys[pitch - this.MIN_PITCH] = 0;			// mark inactive note with a 0
		if (this.numActive > 0) this.numActive--;	// decrease number of active notes
	}
}