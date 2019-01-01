
// DOCS: https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class Main {

	public static void main(String[] args) throws LineUnavailableException {		
		int BUFFER_SIZE = 1024;
		float SAMPLE_RATE = 44100;
		
		// set up audio stream from default mic
		AudioDispatcher d = AudioDispatcherFactory.fromDefaultMicrophone(BUFFER_SIZE, 0);
		
		// remove any frequencies below 110 Hz
		d.addAudioProcessor(new HighPass(110, SAMPLE_RATE));
		
		// setup new PitchDetection handler
		PitchDetectionHandler handler = new PitchDetectionHandler() {
	        @Override
	        public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
	        	// if pitch detected
	        	if (result.getPitch() != -1) {
	        		
	        		System.out.println(result.getPitch());
	        		
	        		float[] buffer = audioEvent.getFloatBuffer();
	        		
	        		for (int i = 0; i < buffer.length; i++) {
	        			System.out.print(buffer[i] + ", ");
	        		}
	        		System.out.println();
	        		
	        	// if no pitch detected
	        	} else {
	        		// do not play audio if no pitch detected
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




