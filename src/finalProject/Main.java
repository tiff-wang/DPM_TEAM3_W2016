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
	//private static final Port colorPort = LocalEV3.get().getPort("S2");


	public static void main(String[] args) {

		final SensorPoller sensorPoller = new SensorPoller();
		sensorPoller.start();
		Odometer odo = new Odometer();
		Navigation nav = new Navigation(odo);
		
		//SensorModes color = new EV3ColorSensor(colorPort);
		//SampleProvider colorSensor = color.getMode("Red");			// colorValue provides samples from this instance
		//float[] colorData = new float[colorSensor.sampleSize()];			// colorData is the buffer in which data are returned
		
		
		
		float distance;
		double angle = 200;
		
		LCD.drawString("< Left	| 	Right >	", 0, 0);
		int buttonChoice = Button.waitForAnyPress();
		
		if(buttonChoice== Button.ID_LEFT){		// left for US sensor
			nav.rotate();
			while(true){
				
				System.out.println(SensorPoller.getValueUS());
				System.out.println(" ");
				
			}
			
		
		} else if(buttonChoice== Button.ID_RIGHT){		// right for color sensor
			nav.rotate();
			
			while(true){
	
			System.out.println(SensorPoller.getValueColorFront());
			System.out.println(" ");
					
			}
			
		}

		buttonChoice = Button.waitForAnyPress();
		Button.waitForAnyPress();
		System.exit(0);
	}

	
}
