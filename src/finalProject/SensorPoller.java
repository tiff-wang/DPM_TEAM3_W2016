package finalProject;

import java.util.Arrays;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class SensorPoller extends Thread{
	
	// Sensor values that are constantly being updated
	private static int valueUs;
	private static float valueColorFront;
	private static float valueColorBack;
	private static float valueColorLauncher;

	// Instantiate Sensor Ports
	private static final Port portUs = LocalEV3.get().getPort("S1");
	private static final Port portColorFront = LocalEV3.get().getPort("S2");
	private static final Port portColorBack = LocalEV3.get().getPort("S3");
	private static final Port portColorLauncher = LocalEV3.get().getPort("S4");

	// US SENSOR
	SensorModes usSensor = new EV3UltrasonicSensor(portUs);
	SampleProvider usValue = usSensor.getMode("Distance");
	float[] usData = new float[usValue.sampleSize()];

	/*
	// COLOR SENSOR FRONT
	EV3ColorSensor colorSensorFront = new EV3ColorSensor(portColorFront);
	SampleProvider colorValueFront = colorSensorFront.getRGBMode();
	float[] colorDataFront = new float[colorValueFront.sampleSize()];

	// COLOR SENSOR BACK
	EV3ColorSensor colorSensorBack = new EV3ColorSensor(portColorBack);
	SampleProvider colorValueBack = colorSensorBack.getRGBMode();
	float[] colorDataBack = new float[colorValueBack.sampleSize()];

	// COLOR SENSOR LAUNCHER
	EV3ColorSensor colorSensorLauncher = new EV3ColorSensor(portColorLauncher);
	SampleProvider colorValueLauncher = colorSensorLauncher.getRGBMode();
	float[] colorDataLauncher = new float[colorValueLauncher.sampleSize()];
	*/
	
	public void run() {
		
		while (true) {
			
			usSensor.fetchSample(usData,0);							
			valueUs=(int)(usData[0]*100.0);			
			if (valueUs > 255)
				valueUs = 255;	
			
			try { Thread.sleep(50); } catch(Exception e){}			
		}
	}
	
	// Getters for the sensor values
	
	public static int getValueUS(){
		return valueUs;
	}
	
}
	

	/* +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+  GARRET'S MEDIAN FILTER +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	// take initial 5 readings of each sensor
	public void initializeSensors() {

		for (int i = 0; i < 5; i++) {

			usSensor.fetchSample(usData, 0);

			usDataReadings[i] = usData[0] * 100;
			if (usDataReadings[i] > 60)
				usDataReadings[i] = 60; // max distance capped at 60

			frontColorSensor.fetchSample(colorDataFront, 0);
			frontColorReadings[i] = colorDataFront[0];

			backColorSensor.fetchSample(colorDataFront, 0);
			backColorReadings[i] = colorDataFront[0];

			ballColorSensor.fetchSample(colorDataFront, 0);
			ballColorReadings[i] = colorDataFront[0];
		}

	}

	// this method takes sensor array passed to it takes another reading
	// other values shifted and returns the median value
	// mode 1 for US, 2 for front colorSensor, 3 for back colorSensor, 4 for
	// ball colorSensor

	public float[] getFilteredData() {
		float[] medians = new float[4];

		for (int i = 0; i < dataLength; i++) { // take new reading and shift
												// others to the left
			if (i == 4) {
				usSensor.fetchSample(usData, 0);
				usDataReadings[i] = usData[0] * 100;
				if (usDataReadings[i] > 60)
					usDataReadings[i] = 60; // caps max distance of US sensor at
											// 60

				frontColorSensor.fetchSample(colorDataFront, 0); // read and
																	// save each
																	// of the
																	// color
																	// sensors
				frontColorReadings[i] = colorDataFront[0];

				backColorSensor.fetchSample(colorDataFront, 0);
				backColorReadings[i] = colorDataFront[0];

				ballColorSensor.fetchSample(colorDataFront, 0);
				ballColorReadings[i] = colorDataFront[0];
			} else { // shift each of the readings
				usDataReadings[i] = usDataReadings[i + 1];
				frontColorReadings[i] = frontColorReadings[i + 1];
				backColorReadings[i] = backColorReadings[i + 1];
				ballColorReadings[i] = ballColorReadings[i + 1];
			}

		}
		// GET MEDIAN OF EACH SENSOR
		medians[0] = getMedian(usDataReadings); // US in position 0
		medians[1] = getMedian(frontColorReadings); // frontColor position 1
		medians[2] = getMedian(backColorReadings); // backColor position 2
		medians[3] = getMedian(ballColorReadings); // ballCOlor position 3

		return (medians);
	}

	public float getMedian(float[] sensorData) {

		float[] numList = new float[5]; // making a copy of sensorData to find
										// median without changed order

		for (int i = 0; i < sensorData.length; i++) {
			numList[i] = sensorData[i];

		}

		Arrays.sort(numList); // sort array in ascending order
		float median;
		if (numList.length % 2 == 0)
			median = ((float) numList[numList.length / 2] + (float) numList[numList.length / 2 - 1]) / 2;
		else
			median = (float) numList[numList.length / 2];
		return median;
	}

}

 +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+  GARRET'S MEDIAN FILTER +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
*/