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

	public static void main(String[] args) {

		final SensorPoller sensorPoller = new SensorPoller();
		final Odometer odo = new Odometer();
		final DisplayLCD display = new DisplayLCD(odo);
		final Navigation nav = new Navigation(odo);
		final LocalizationUS loc = new LocalizationUS(odo, nav);
		
		sensorPoller.start();
		odo.start();
		nav.start();
		
		LCD.drawString("< Left	| 	Right >	", 0, 0);
		int buttonChoice = Button.waitForAnyPress();
		display.start();
		
		if(buttonChoice== Button.ID_LEFT){		// left for US sensor
			
			nav.travelTo(50, 50);
			
			nav.goForward(20);
			nav.turnDegreesClockwise(90);
			
		
		} else if(buttonChoice== Button.ID_RIGHT){		// right for color sensor

			loc.doLocalization();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

	
}
