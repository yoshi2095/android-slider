package eu.flatworld.android.slider;

import android.util.Log;

import com.badlogic.gdx.math.MathUtils;


public final class Oscillator {
	float frequency;
	float sampleRate;

	WaveForm waveForm = WaveForm.SINE;
	long currentSample = 0;
	long periodSamples;

	float value = 0;

	public Oscillator(float frequency, float sampleRate) {
		this.frequency = frequency;
		this.sampleRate = sampleRate;
		periodSamples = (long) (sampleRate / frequency);
	}

	public void setWaveForm(WaveForm waveForm) {
		this.waveForm = waveForm;
	}
	
	public WaveForm getWaveForm() {
		return waveForm;
	}

	public float getFrequency() {
		return frequency;
	}
	
	public void setFrequency(float frequency) {
		if(this.frequency == frequency) {
			return;
		}
		//Log.d(Slider.LOGTAG, String.format("%.2f", frequency));
		this.frequency = frequency;
		if(frequency == 0) {
			periodSamples = Long.MAX_VALUE;
		} else {
			periodSamples = (long) MathUtils.round(sampleRate / frequency);
			if(periodSamples == 0) {
				periodSamples = 1;
			}
		}
	}
	
	public long getCurrentSample() {
		return currentSample;
	}

	public float getValue() {
		if (frequency == 0) {
			return value;
		}
		float x = (float) currentSample / periodSamples;
		switch (waveForm) {
		case SINE:
			value = (float)MathUtils.sin(2.0f * MathUtils.PI * x);
			break;
		case SQUARE:
			if (currentSample < (periodSamples / 2)) {
				value = 1.0f;
			} else {
				value = -1.0f;
			}
			break;
		case TRIANGLE:
			value = (float)(2.0f * Math.abs(2.0f * x - 2.0f * MathUtils.floor(x) - 1.0f) - 1.0f);
			break;
		case SAWTOOTH:
			value = (float)(2.0f * (x - MathUtils.floor(x) - 0.5f));
			break;
		case REVERSE_SAWTOOTH:
			value = (float)(2.0f * (MathUtils.floor(x) - x + 0.5f));
			break;
		default:
			throw new RuntimeException("Illegal wave type: " + waveForm);
		}
		currentSample = (currentSample + 1) % (periodSamples);
		return value;
	}

};
