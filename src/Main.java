
// DOCS: https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class Main {
	
	// MIDI keyboard parameters
	public static final int MIN_PITCH = 48, MAX_PITCH = 84;
	
	// frequencies coming from MIDI instrument
	public static float[] targetFrequencies;

	public static void main(String[] args) throws LineUnavailableException {		
		int BUFFER_SIZE = 8192; // 1024;
		int SAMPLE_RATE = 44100;
		
		// set up MIDI handlers
		NoteHandler noteHandler = new NoteHandler(MIN_PITCH, MAX_PITCH);
		MidiBus.list();
		MidiBus bus = new MidiBus(noteHandler, 0, 1);
		
		// set up audio stream from default mic
		AudioDispatcher d = AudioDispatcherFactory.fromDefaultMicrophone(BUFFER_SIZE, 0);
		
		// remove any frequencies below 110 Hz
		d.addAudioProcessor(new HighPass(110, SAMPLE_RATE));
		
		// create new PSOLA object for pitch shifting
		PSOLA psola = new PSOLA(BUFFER_SIZE, SAMPLE_RATE);
		
		// float[] targetFrequencies; //  = {233.08f, 293.66f, 349.23f, 440.0f};

		// setup new PitchDetection handler
		PitchDetectionHandler handler = new PitchDetectionHandler() {
	        @Override
	        public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
	        	// if pitch detected
	        	if (result.getPitch() != -1) {	        		
	        		// analyze mic audio using pitch estimate
	        		psola.analyze(audioEvent.getFloatBuffer(), result.getPitch());
	        		
	        		float[] avgOut = new float[BUFFER_SIZE], shifted;
	        		
	        		// get frequencies being played on keyboard
	        		targetFrequencies = noteHandler.getFrequencies();
	        		
	        		// calculate each pitch shifted buffer, adding to average
	        		for (int i = 0; i < targetFrequencies.length; i++) {
	        			if (targetFrequencies[i] > 0) {
		        			shifted = psola.shift(targetFrequencies[i]);
		        			
		        			// add to average buffer
		        			for (int j = 0; j < shifted.length; j++) {
		        				avgOut[j] += shifted[j];
		        			}
	        			}
	        		}
	        		
	        		// finalize average by dividing by number of separate frequencies
	        		for (int i = 0; i < avgOut.length; i++) {
	        			avgOut[i] /= targetFrequencies.length;
	        		}
	        		
	        		// replace audio with synthesized signal
	        		audioEvent.setFloatBuffer(avgOut);
	        		
	        	// if no pitch detected
	        	} else {
	        		// do not playback any audio
	        		audioEvent.clearFloatBuffer();
	        	}
	        }
	    };
	    
	    // add pitch detection with YIN algorithm, using above handler
	    d.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, SAMPLE_RATE, BUFFER_SIZE, handler));
	    
	    // add an audio player that plays back audio in realtime
	    d.addAudioProcessor(new AudioPlayer(new AudioFormat(SAMPLE_RATE, 16, 1, true, true)));
	    
	    // run audio dispatcher
	    d.run();
	}

}