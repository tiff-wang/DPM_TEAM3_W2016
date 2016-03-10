package finalProject;

// 
// THIS IS AN OLD LAB FROM NAWRAS, NO MODIFICATION HAS BEEN MADE YET
//

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread  {
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static final int LOCALIZE_SPEED = 100;
	
	public double thetar, xr, yr;
	private boolean navigating;
	
	private Odometer odo;
	private double WHEEL_RADIUS;
	private double WHEEL_WIDTH;
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	public Navigation(Odometer odometer){
		this.odo =  odometer;
		this.WHEEL_RADIUS = odo.get_WHEEL_RADIUS();
		this.WHEEL_WIDTH = odo.get_WHEEL_WIDTH();
		this.leftMotor = odo.getLeftMotor();
		this.rightMotor = odo.getRightMotor();
		navigating = false;
	}
	
/**
 * Has the robot move to a position, relative to starting coordinates
 * 
 * Calculates angle and distance to move to using basic trig and then calls
 * the turnTo and goForward method to move to that point
 * 
 * @param X Coordinate of destination
 * @param Y Coordinate of destination
 */
	public void travel (double x, double y){
		//gets position. Synchronized to avoid collision
			synchronized (odo.lock) {
				thetar = odo.getTheta() * 180 / Math.PI;
				xr = odo.getX();
				yr = odo.getY();
			}
			//calculates degrees to turn from 0 degrees
			double thetad =  Math.atan2(x - xr, y - yr) * 180 / Math.PI;
			//calculates actual angle to turn
			double theta =  thetad - thetar;
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
			//finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
			if(theta < -180){
				turnDegreesClockwise(theta + 360);
			}
			else if(theta > 180){
				turnDegreesClockwise(theta - 360);
			}
			else turnDegreesClockwise(theta);
			//updates values to display
			
			goForward(distance);
	}
	
	public void goForward(double distance){
		
		// drive forward 
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//for isNavigatingMethod
		navigating = true;
		
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), false);
		
		navigating = false;
	}
	
	public void turnDegreesClockwise (double theta){
	
		// turn degrees clockwise
		leftMotor.setSpeed(LOCALIZE_SPEED);
		rightMotor.setSpeed(LOCALIZE_SPEED);
		
		navigating = true;
		//calculates angel to turn to and rotates
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, theta), false);
		
		navigating = false;
	}
	public void rotate (boolean forward){
		leftMotor.setSpeed(LOCALIZE_SPEED);
		rightMotor.setSpeed(LOCALIZE_SPEED);
		if (forward){
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	
	public void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}
/**
 * Returns true if the robot is navigating
 * 
 * @return boolean indicating if the robot is traveling
 */
	public boolean isNavigating(){
		return this.navigating;
	}
/**
 * Returns degrees to turn servos in order to rotate robot by that amount
 * 
 * Uses basic math to convert and absolute angle to degrees to turn.
 * 
 * @param Radius of lego wheel
 * @param Width of wheel base
 * @param Absolute angle to turn to
 * 
 * @return Degrees the servo should turn
 */
	public static int convertAngle(double radius, double width, double angle) {
		//(width * angle / radius ) / (2)
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
/**
 * Moves robot linerly a certain distance
 * 
 * @param Radius of lego wheel
 * @param Distance to travel
 * 
 * @return degrees to turn servos in order to move forward by that amount
 */
	public static int convertDistance(double radius, double distance) {
		// ( D / R) * (360 / 2PI)
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
