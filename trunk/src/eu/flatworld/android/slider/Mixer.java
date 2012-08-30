package eu.flatworld.android.slider;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.CircularBuffer;

public class Mixer implements Runnable {
	List<Keyboard> keyboards;
	AudioDevice audioDevice;
	
	boolean stop = false;
	boolean stopped = true;
	Thread thread;

	int bufferSize;
	short[] buffer;

	int snapshotBufferSize;
	short[] snapshotBuffer;
	CircularBuffer snapshotCircularBuffer;
	

	public Mixer() {
		keyboards = new ArrayList<Keyboard>();
		bufferSize = 1024;
		snapshotBufferSize = 1920;
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

	public int getSnapshotBufferSize() {
		return snapshotBufferSize;
	}

	public void setSnapshotBufferSize(int snapshotBufferSize) {
		this.snapshotBufferSize = snapshotBufferSize;
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
		snapshotCircularBuffer.write(buffer, 0, buffer.length);
	}

	public void run() {
		while (!stop) {
			fillBuffer(buffer);
			audioDevice.writeSamples(buffer, 0, bufferSize);
		}
		stopped = true;
	}

	void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception ex) {
		}
	}

	public void start() {
		if (!stopped) {
			return;
		}
		snapshotCircularBuffer = new CircularBuffer(snapshotBufferSize);
		snapshotBuffer = new short[snapshotBufferSize];
		buffer = new short[bufferSize];		
		stop = false;
		stopped = false;
		thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void stop() {
		stop = true;
		while (!stopped) {
			sleep(500);
		}
		keyboards.clear();
		keyboards = null;
		snapshotCircularBuffer.clear();
		snapshotCircularBuffer = null;
		snapshotBuffer = null;
		buffer = null;
	}
	
	public void takeSnapshot() {
		snapshotCircularBuffer.read(snapshotBuffer, 0, snapshotBufferSize);
	}
}
