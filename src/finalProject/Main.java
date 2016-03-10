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

	public static void main(String[] args) {

		final SensorPoller sensorPoller = new SensorPoller();
		sensorPoller.start();
		
		LCD.drawString("< Left	| 	Right >	", 0, 0);
		int buttonChoice = Button.waitForAnyPress();
		
		if (buttonChoice == Button.ID_LEFT || buttonChoice == Button.ID_RIGHT) {

			(new Thread() {
				public void run() {

					while (true) {

						System.out.println(SensorPoller.getValueUS());

					}
				}
			}).start();

		}

		buttonChoice = Button.waitForAnyPress();
		Button.waitForAnyPress();
		System.exit(0);
	}

}
