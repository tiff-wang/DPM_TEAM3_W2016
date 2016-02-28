package finalProject;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor; // heiii
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class main {

	// Robot Parameters
	private static double WHEEL_RADIUS = 2.1;
	private static double WHEEL_WIDTH = 13.5;

	// Instantiate Motors
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

	// Instantiate Ports
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final Port colorPort = LocalEV3.get().getPort("S2");

	public static void main(String[] args) {
		
		
		// US SENSOR
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		
		// COLOR SENSOR FRONT
		EV3ColorSensor colorSensorFront = new EV3ColorSensor(colorPort);
		SampleProvider frontColorValue = colorSensorFront.getRGBMode();			// colorValue provides samples from this instance
		float[] colorData = new float[frontColorValue.sampleSize()];			// colorData is the buffer in which data are returned
				

		// COLOR SENSOR LAUNCHER
		EV3ColorSensor colorSensorLauncher = new EV3ColorSensor(colorPort);
		SampleProvider ballColorValue = colorSensorLauncher.getRGBMode();			// colorValue provides samples from this instance
		float[] ballColorData = new float[ballColorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		// COLOR SENSOR BACK
		EV3ColorSensor backSensorLauncher = new EV3ColorSensor(colorPort);
		SampleProvider backColorValue = colorSensorLauncher.getRGBMode();			// colorValue provides samples from this instance
		float[] backColorData = new float[backColorValue.sampleSize()];			// colorData is the buffer in which data are returned
						
		
		
		int buttonChoice;
		
		buttonChoice = Button.waitForAnyPress();
		if(buttonChoice== Button.ID_LEFT|| buttonChoice == Button.ID_RIGHT){
			
		}
		
		buttonChoice = Button.waitForAnyPress();
		Button.waitForAnyPress();
		System.exit(0);
	}

}
