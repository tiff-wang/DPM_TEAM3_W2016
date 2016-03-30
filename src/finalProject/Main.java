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
/**
 * Main class, from where the user can decide to run tests, execute offense strategy or execute defense strategy
 * @author rabbani, holt
 * @version 1.3
 */
public class Main {
	public static int [] parameters={1,0,3,3,3,10,3,11,6,1};
	public final static int TILE = 30;

	public static void main(String[] args) throws InterruptedException {

		final SensorPoller sensorPoller = new SensorPoller();
		final Odometer odo = new Odometer();
		final DisplayLCD display = new DisplayLCD(odo);
		final Navigation nav = new Navigation(odo);
		final Localization loc = new Localization(odo, nav);
		
		
		sensorPoller.start();
		odo.start();
		nav.start();
		
		LCD.drawString("< Left	| 	Right >	", 0, 0);
		int buttonChoice = Button.waitForAnyPress();
		display.start();
		final Offense off = new Offense(nav,odo);
		if(buttonChoice== Button.ID_LEFT){		// left for US sensor
			
			nav.travelToCorner(3);
			
			/*
			if(parameters[1]==0){
			
				off.testBallPickUp1();
				
			}
			
			else if(parameters[1] ==1){
				final Defense def = new Defense(nav,odo);
				def.doDefense();
				
			}
		*/
			
			buttonChoice = Button.waitForAnyPress();
			if(buttonChoice == Button.ID_LEFT)
				off.testBallPickUp2();

		
		} else if(buttonChoice== Button.ID_RIGHT){		// right for color sensor

			
			//loc.doLocalization();
			//loc.doLightLocalization();
			
			nav.travelTo(6*TILE - 60, 5*TILE - 60);
			nav.leftMotor.waitComplete();
			nav.rightMotor.waitComplete();
			nav.turnTo(90);

		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

	public static int getParameter(int index){
		return parameters[index];
	}
}
