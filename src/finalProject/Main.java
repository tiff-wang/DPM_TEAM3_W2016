package finalProject;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Main {
	
	private static final Port usPort = LocalEV3.get().getPort("S1");		
	private static final Port colorPort = LocalEV3.get().getPort("S2");


	public static void main(String[] args) {

		final SensorPoller sensorPoller = new SensorPoller();
		sensorPoller.start();
		Odometer odo = new Odometer();
		Navigation nav = new Navigation(odo);
		
		
		SensorModes us = new EV3UltrasonicSensor(usPort);
		SampleProvider usSensor = us.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usSensor.sampleSize()];				// colorData is the buffer in which data are returned
		
		SensorModes color = new EV3ColorSensor(colorPort);
		SampleProvider colorSensor = color.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorSensor.sampleSize()];			// colorData is the buffer in which data are returned
		
		
		
		float distance;
		double angle = 90;
		
		LCD.drawString("< Left	| 	Right >	", 0, 0);
		int buttonChoice = Button.waitForAnyPress();
		
		if(buttonChoice== Button.ID_LEFT){		// left for US sensor
			
			while(true){
				nav.turnTo(angle,true);
				usSensor.fetchSample(usData, 0);
				distance= usData[0]*100;
				System.out.println(distance);
			}
			
		
		} else if(buttonChoice== Button.ID_RIGHT){		// right for color sensor
			
			while(true){
				nav.turnTo(angle,true);
				colorSensor.fetchSample(colorData, 0);
				distance =colorData[0]*100;
				System.out.println(distance);
			}
			
		}

		buttonChoice = Button.waitForAnyPress();
		Button.waitForAnyPress();
		System.exit(0);
	}

	
}
