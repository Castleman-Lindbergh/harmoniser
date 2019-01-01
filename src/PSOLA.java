
public class PSOLA {
	
	private int BUFFER_SIZE;	// size of a given chunk of audio to analyze
	private int SAMPLE_RATE;	// number of samples per second
	
	private float[] buffer;		// audio buffer currently being analyzed / shifted
	private int pitchPeriod;	// estimated period (in samples) of audio signal
	private int[] analysisPeaks;	// indices of analysis peaks in buffer
	
	// constructor for new PSOLA object with given buffer size and sample rate
	public PSOLA(int BUFFER_SIZE, int SAMPLE_RATE) {
		this.BUFFER_SIZE = BUFFER_SIZE;
		this.SAMPLE_RATE = SAMPLE_RATE;
	}
	
	// determine pitch period and analysis peaks for a given audio buffer, storing internally
	public void analyze(float[] buffer, float frequencyEstimate) {
		
	}
	
	// generate a buffer shifted to a given frequency
	public float[] shift(float targetFrequency) {
		
	}

}
