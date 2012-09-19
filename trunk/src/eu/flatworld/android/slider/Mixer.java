package eu.flatworld.android.slider;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.AudioDevice;

public class Mixer implements Runnable {
	List<Keyboard> keyboards;
	AudioDevice audioDevice;
	
	boolean stop = false;
	Thread thread;

	int bufferSize;
	short[] buffer;	

	public Mixer() {
		keyboards = new ArrayList<Keyboard>();
		bufferSize = 1024;
	}

	public void addKeyboard(Keyboard keyboard) {
		keyboards.add(keyboard);
	}
	
	public void removeKeyboard(Keyboard keyboard) {
		keyboards.remove(keyboard);
	}
	
	public List<Keyboard> getKeyboards() {
		return keyboards;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setAudioDevice(AudioDevice audioDevice) {
		this.audioDevice = audioDevice;
	}
	
	void fillBuffer(short[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			float val = 0;
			for (int j=0; j<keyboards.size(); j++) {
				Keyboard k = keyboards.get(j);
				List<SoundGenerator> sgg = k.getSoundGenerators();
				for (int m = 0; m < sgg.size(); m++) {
					SoundGenerator sg = sgg.get(m);
					if (sg.getEnvelope().isReleased()) {
						continue;
					}
					val += sg.getValue();
				}
			}
			if (val > 1) {
				val = 1;
			}
			if (val < -1) {
				val = -1;
			}
			buffer[i] = (short)(val * Short.MAX_VALUE);
		}
	}

	public void run() {
		while (!stop) {
			fillBuffer(buffer);
			audioDevice.writeSamples(buffer, 0, bufferSize);
		}
	}

	void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception ex) {
		}
	}

	public void start() {
		buffer = new short[bufferSize];		
		stop = false;
		thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void stop() {
		stop = true;
		try {
			thread.join();
		} catch(Exception ex) {			
		}
		keyboards.clear();
		keyboards = null;
		buffer = null;
	}
}
