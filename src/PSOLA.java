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
		// if analysis was successfull, proceed
		if (this.analysisPeaks.size() > 0) {
			// find synthesis peaks for desired frequency
			ArrayList<Integer> synthesisPeaks = this.getSynthesisPeaks(targetFrequency);
			
			// determine mapping of synthesis to analysis
			int[] mapping = this.getMapping(synthesisPeaks);
			
			// calculate output buffer by overlap adding synthesis peaks with given mapping (use window size = 2 * pitch period)
			return this.overlapAndAdd(synthesisPeaks, mapping, this.pitchPeriod);
		} else {
			// fallback: return unchanged buffer
			return this.buffer;
		}
	}

	// calculate positions of synthesis peaks for a given frequency (assumes analysis peaks exist)
	private ArrayList<Integer> getSynthesisPeaks(float targetFrequency) {
		// calculate sample period of target frequency
		int targetPeriod = (int) Math.floor(this.SAMPLE_RATE / targetFrequency);
		
		// align initial peaks of synthesis and analysis
		int initialPeak = this.analysisPeaks.get(0);
		
		// if an earlier initial synthesis peak is possible, use this (prevents peaks from starting late in buffer)
		while (initialPeak - targetPeriod > 0) {
			initialPeak -= targetPeriod;
		}
		
		// create empty array of synthesis peaks
		ArrayList<Integer> synthPeaks = new ArrayList<Integer>();
		
		// determine number of synthesis peaks that can fit into this buffer
		int numPeaks = (this.BUFFER_SIZE - initialPeak) / targetPeriod + 1;
		
		// calculate and add each peak using multiples of the target period
		for (int i = 0; i < numPeaks; i++) {
			synthPeaks.add(initialPeak + (i * targetPeriod));
		}
		
		return synthPeaks;
	}
	
	// generate a mapping of synthesis peaks to analysis peaks (using closest analysis peak)
	private int[] getMapping(ArrayList<Integer> synthesisPeaks) {
		// initialize mapping as empty array, with one spot per synthesis peak
		int[] mapping = new int[synthesisPeaks.size()];
		
		// record the previous closest peak each time to use as a starting point for next peak's search
		int previousClosest = 0, closest;
		
		// for each synthesis peak
		for (int s = 0; s < synthesisPeaks.size(); s++) {
			int synth = synthesisPeaks.get(s);
			
			closest = previousClosest;	// assume closest parent peak is same as closest peak for previous synthesis peak
			
			// for each possible parent peak, starting at last closest
			for (int p = previousClosest; p < this.analysisPeaks.size(); p++) {
				int analysisPeak = this.analysisPeaks.get(p);
				
				// if this parent is closer than current closest, replace
				if (Math.abs(analysisPeak - synth) < Math.abs(this.analysisPeaks.get(closest) - synth)) {
					closest = p;
				}
				
				// if at or past child synthesis peak, look no further for parent (distance only increases)
				if (analysisPeak >= synth) {
					break;
				}
			}
			
			// set this synthesis peak's parent as the closest analysis peak
			mapping[s] = closest;
			
			// update this closest peak so next iteration will start from here
			previousClosest = closest;
		}
		
		return mapping;
	}
	
	// overlap and add together synthesis windows, resulting in new output buffer
	private float[] overlapAndAdd(ArrayList<Integer> synthesisPeaks, int[] mapping, int halfWindow) {
		// initialize output buffer as zeroed array of same size as buffer
		float[] out = new float[this.BUFFER_SIZE];
		
		// precompute array of Hann window coefficients
		float[] Hann = new float[2 * halfWindow];
		for (int i = -halfWindow; i < halfWindow; i++) {
			Hann[i + halfWindow] = H(i, halfWindow);
		}
		
		int cMid, pMid, c, p;
		
		// for each synthesis peak
		for (int s = 0; s < synthesisPeaks.size(); s++) {
			// get midpoint indices of child (synthesis) and parent (analysis) peaks
			cMid = synthesisPeaks.get(s);
			pMid = this.analysisPeaks.get(mapping[s]);
			
			// for each possible window shift
			for (int shift = -halfWindow; shift < halfWindow; shift++) {
				// calculate shifted indices for child and parent
				c = cMid + shift;
				p = pMid + shift;
				
				// if both indices in bounds in buffer
				if (c > 0 && p > 0 && c < this.BUFFER_SIZE && p < this.BUFFER_SIZE) {
					// add weighted sample from parent window to child window
					out[c] += Hann[shift + halfWindow] * this.buffer[p];
				}
			}
		}
		
		return out;
	}
	
	// the Hann window function, where n is current shift in window, w is half of window size
	// (domain for n is -w to w)
	private float H(int n, int w) {
		return (float) (0.5 * (1 - Math.cos((2 * Math.PI * (n + w)) / (2 * w))));
	}

}
