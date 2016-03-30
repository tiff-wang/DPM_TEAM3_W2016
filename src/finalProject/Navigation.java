package finalProject;

import java.util.concurrent.TimeUnit;

import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread {

	private static final int SPEED_FORWARD = 250;
	private static final int SPEED_ROTATE = 150;
	private static final int SPEED_LOCALIZE = 100;
	private static final int WALL_DISTANCE = 20;

	private static double targetX;
	private static double targetY;

	public double thetar, xr, yr;
	private boolean isNavigating;

	private Odometer odo;
	private double WHEEL_RADIUS;
	private double WHEEL_WIDTH;
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;

	public Navigation(Odometer odometer) {
		this.odo = odometer;
		this.WHEEL_RADIUS = odo.get_WHEEL_RADIUS();
		this.WHEEL_WIDTH = odo.get_WHEEL_WIDTH();
		this.leftMotor = odo.getLeftMotor();
		this.rightMotor = odo.getRightMotor();
		isNavigating = false;
	}

	public void run() {

		int distance;
		int usFilter = 0;

		while (true) {

			distance = SensorPoller.getValueUS();
			if (distance < WALL_DISTANCE) {
				usFilter++;
			}

			if (distance < WALL_DISTANCE && Odometer.nearCorner == 0 && usFilter > 15) {
				Sound.beepSequenceUp();
				turnDegreesClockwise(90);
				goForward(15, false);
				turnDegreesClockwise(-90);
				goForward(45, false);
				turnDegreesClockwise(-90);
				goForward(15, false);
				turnDegreesClockwise(90);
				travelTo(targetX, targetY);
				Sound.beepSequenceUp();
				Sound.beepSequence();
				usFilter = 0;
			}

			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}

		}
	}


	/**
	 * Order the robot to move to a position
	 * 
	 * Calculates angle and distance to move to using basic trig and then calls
	 * the turnTo and goForward method to move to that point
	 * 
	 * @param X
	 *            Coordinate of destination
	 * @param Y
	 *            Coordinate of destination
	 */
	public void travelTo(double x, double y) {
		targetX = x;
		targetY = y;
		// gets position. Synchronized to avoid collision
		synchronized (odo.lock) {
			thetar = odo.getTheta() * 180 / Math.PI;
			xr = odo.getX();
			yr = odo.getY();
		}
		// calculates degrees to turn from 0 degrees
		double thetad = Math.atan2(x - xr, y - yr) * 180 / Math.PI;
		// calculates actual angle to turn
		double theta = thetad - thetar;
		// calculates magnitude to travel
		double distance = Math.sqrt(Math.pow((y - yr), 2) + Math.pow((x - xr), 2));
		// finds minimum angle to turn (ie: it's easier to turn +90 deg instead
		// of -270)
		if (theta < -180) {
			turnDegreesClockwise(theta + 360);
		} else if (theta > 180) {
			turnDegreesClockwise(theta - 360);
		} else
			turnDegreesClockwise(theta);
		// updates values to display

		//RETURN IMMEDIATELY
		goForward(distance, true);
	}

	public void travelToTile(int x, int y) {
		travelTo(30 * x, 30 * y);
	}

	public void goForward(double distance, boolean returnImmediately) {
		// drive forward
		leftMotor.setSpeed(SPEED_FORWARD);
		rightMotor.setSpeed(SPEED_FORWARD);

		// for isNavigatingMethod
		isNavigating = true;

		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), returnImmediately);

		isNavigating = false;
	}

	public void turnDegreesClockwise(double theta) {

		// turn degrees clockwise
		leftMotor.setSpeed(SPEED_LOCALIZE);
		rightMotor.setSpeed(SPEED_LOCALIZE);

		isNavigating = true;
		// calculates angel to turn to and rotates
		leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, theta), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, theta), false);

		isNavigating = false;
	}
	
	public void turnTo(double thetaTarget) {

		double theta = thetaTarget - odo.getTheta();
		turnDegreesClockwise(theta);

	}
	

	public void rotate(boolean clockwise) {
		leftMotor.setSpeed(SPEED_LOCALIZE);
		rightMotor.setSpeed(SPEED_LOCALIZE);
		if (clockwise) {
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.backward();
			rightMotor.forward();
		}
	}

	public void stopMotors() {
		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	 * Returns true if the robot is navigating
	 * 
	 * @return boolean indicating if the robot is traveling
	 */
	public boolean isNavigating() {
		return this.isNavigating;
	}

	/**
	 * Calculate the angle the robot should turn
	 * 
	 * @param Radius
	 *            of lego wheel
	 * @param Width
	 *            of wheel base
	 * @param Absolute
	 *            angle to turn to
	 * 
	 * @return Degrees the robot should turn
	 */
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * Moves robot linerly a certain distance
	 * 
	 * @param Radius
	 *            of lego wheel
	 * @param Distance
	 *            to travel
	 * 
	 * @return degrees to turn servos in order to move forward by that amount
	 */
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// Getters

	public int getSPEED_ROTATE() {
		return SPEED_ROTATE;
	}

	public int getSPEED_LOCALIZE() {
		return SPEED_LOCALIZE;
	}

	
	public void travelToCorner(int corner) throws InterruptedException{

		switch (corner) {
		case 1:
			if (Odometer.nearCorner == 4){
				travelTo(Odometer.X3[0],Odometer.X3[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 3){
				travelTo(Odometer.X2[0],Odometer.X2[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 2){
				travelTo(Odometer.X1[0],Odometer.X1[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 1){
				break;
			}
		case 2:
			if (Odometer.nearCorner == 1){
				travelTo(Odometer.X4[0],Odometer.X4[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 4){
				travelTo(Odometer.X3[0],Odometer.X3[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 3){
				travelTo(Odometer.X2[0],Odometer.X2[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 2){
				break;
			}
		case 3:
			if (Odometer.nearCorner == 2){
				travelTo(Odometer.X1[0],Odometer.X1[1]);
				leftMotor.waitComplete();
				leftMotor.waitComplete();
				Sound.beepSequenceUp();
			}
			if (Odometer.nearCorner == 1){		
				travelTo(Odometer.X4[0],Odometer.X4[1]);
				leftMotor.waitComplete();
				leftMotor.waitComplete();
				Sound.beepSequenceUp();
			}
			if (Odometer.nearCorner == 4){
				travelTo(Odometer.X3[0],Odometer.X3[1]);
				leftMotor.waitComplete();
				leftMotor.waitComplete();
				Sound.beepSequenceUp();
			}
			if (Odometer.nearCorner == 3){
				break;
			}
		case 4:
			if (Odometer.nearCorner == 1){
				travelTo(Odometer.X2[0],Odometer.X2[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 2){
				travelTo(Odometer.X3[0],Odometer.X3[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 3){
				travelTo(Odometer.X4[0],Odometer.X4[1]);
				while (isNavigating){
					wait(1000);
				}
			}
			if (Odometer.nearCorner == 4){
				break;
			}
		}	
	}

}