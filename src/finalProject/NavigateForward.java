package finalProject;


import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

public class NavigateForward extends Thread{
	public double detectedMin;
	public final int FORWARD_SPEED =  200;
	public BallReader reader;
	private Navigation nav;


	
	public NavigateForward(Navigation nav, BallReader reader){
		this.reader = reader;
		this.nav = nav;
	}
	
	public void run(){
		while(true){
			while(!reader.getDetected()){
				nav.goForward(900,false);
			}
			nav.stopMotors();
			System.out.println("       " + reader.getDmin());
			nav.travelTo(reader.getXmin() + reader.getDmin()*Math.sin(reader.getTheta()-Math.toRadians(45)), 
					reader.getYmin() + reader.getDmin()*Math.cos(reader.getTheta()-Math.toRadians(45)));
			nav.stopMotors();
			//nav.turnTo(Math.toDegrees(reader.getTheta())); IMPORTANT LINE BUT NO TURN TO METHOD
			
			Button.waitForAnyPress();
			reader.setDetected(false);
		}
	}
}
