import java.util.ArrayList;

public class PSOLA {
	
	private int BUFFER_SIZE;	// size of a given chunk of audio to analyze
	private int SAMPLE_RATE;	// number of samples per second
	
	private float[] buffer;		// audio buffer currently being analyzed / shifted
	private int pitchPeriod;	// estimated period (in samples) of audio signal
	private ArrayList<Integer> analysisPeaks;	// indices of analysis peaks in buffer
	
	// constructor for new PSOLA object with given buffer size and sample rate
	public PSOLA(int BUFFER_SIZE, int SAMPLE_RATE) {
		this.BUFFER_SIZE = BUFFER_SIZE;
		this.SAMPLE_RATE = SAMPLE_RATE;
		this.analysisPeaks = new ArrayList<Integer>();
	}
	
	// determine pitch period and analysis peaks for a given audio buffer, storing internally
	public void analyze(float[] buffer, float frequencyEstimate) {
		// store given float buffer internally
		this.buffer = buffer;
		
		// calculate pitch period and store locally
		this.pitchPeriod = (int) Math.floor(this.SAMPLE_RATE / frequencyEstimate);
		
		// reset array of analysis peaks to empty
		this.analysisPeaks.clear();
		
		// calculate 0.5 of pitch period and 1.5 of pitch period
		int halfPeriod = (int) Math.floor(pitchPeriod / 2);
		int oneAndHalfPeriod = pitchPeriod + halfPeriod;
		
		// initialize right and left bounds for local max search (start in first period)
		int leftBound = 0, rightBound = pitchPeriod, localMax;
		
		// search for peaks until end of buffer reached
		while (true) {
			localMax = leftBound;	// assume local max is leftmost sample
			
			// for each sample in current interval
			for (int i = leftBound; i < rightBound; i++) {
				// if a new max is found
				if (this.buffer[i] > this.buffer[localMax]) {
					localMax = i;
				}
			}
			
			// add local max for this interval as a new peak
			this.analysisPeaks.add(localMax);
			
			// center new interval one pitch period forward from current peak
			leftBound = localMax + halfPeriod;
			rightBound = localMax + oneAndHalfPeriod;
			
			// if end of buffer reached, break out of loop
			if (rightBound >= this.BUFFER_SIZE) {
				break;
			}
		}
	}
	
	// generate a buffer shifted to a given frequency
	public float[] shift(float targetFrequency) {
		
	}

}
