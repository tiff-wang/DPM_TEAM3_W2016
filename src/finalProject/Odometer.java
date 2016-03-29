package finalProject;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	
	// Corners coordinates
	public static final int[] X1 = { 0, 0 };
	public static final int[] X2 = { 0, 210 };
	public static final int[] X3 = { 210, 210 };
	public static final int[] X4 = { 210, 0 };
	public static final int cornerBuffer = 30;
	public static int nearCorner = 0;
	
	// robot position
	private final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private final EV3LargeRegulatedMotor launchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private final EV3LargeRegulatedMotor pickUpMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	/*constants*/
	private double WHEEL_RADIUS = 2.07;
	private double WHEEL_WIDTH = 17.6;
	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;
	/*variables*/ 
	private static int previousTachoL;          /* Tacho L at last sample */
	private static int previousTachoR;          /* Tacho R at last sample */
	private static int currentTachoL;           /* Current tacho L */
	private static int currentTachoR;           /* Current tacho R */
	private double x, y, theta, Theta=0;
	// lock object for mutual exclusion
	public Object lock;
	// default constructor
	public Odometer() {

		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();

		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();

		previousTachoL = 0;
		previousTachoR = 0;
		currentTachoL = 0;
		currentTachoR = 0;

	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			
			//check if we are near a corner
			if (x < X1[0]+cornerBuffer && y < X1[1]+cornerBuffer){
				nearCorner = 1;
			}
			else if (x < X2[0]+cornerBuffer && y > X2[1]-cornerBuffer){
				nearCorner = 2;
			}
			else if (x > X3[0]-cornerBuffer && y > X3[1]-cornerBuffer){
				nearCorner = 3;
			}
			else if (x > X4[0]-cornerBuffer && y < X4[1]+cornerBuffer){
				nearCorner = 4;
			}
			else {
				nearCorner = 0;
			}
					
					
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			double leftDistance, rightDistance, deltaDistance, deltaTheta, dX, dY;
			currentTachoL = leftMotor.getTachoCount();
			currentTachoR = rightMotor.getTachoCount();

			leftDistance = 3.14159 * WHEEL_RADIUS * (currentTachoL - previousTachoL) / 180;
			rightDistance = 3.14159 * WHEEL_RADIUS * (currentTachoR - previousTachoR) / 180;

			previousTachoL = currentTachoL;
			previousTachoR = currentTachoR;

			deltaDistance = .5 * (leftDistance + rightDistance);
			deltaTheta = (leftDistance - rightDistance) / WHEEL_WIDTH;
			
			Theta += deltaTheta;
			if(Theta > Math.PI*2)
				Theta= 0;
			if(Theta <0)
				Theta = Math.PI*2 - Theta;
			dX = deltaDistance * Math.sin(Theta);
			dY = deltaDistance * Math.cos(Theta);
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				

				x += dX;
				y += dY;
				theta =Theta;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public EV3LargeRegulatedMotor[] getMotors() {
		return new EV3LargeRegulatedMotor[] { this.leftMotor, this.rightMotor };
	}

	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}

	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}
	public EV3LargeRegulatedMotor getLaunchMotor() {
		return this.launchMotor;
	}
	public EV3LargeRegulatedMotor getPickUpMotor() {
		return this.pickUpMotor;
	}

	// accessor to wheel radius and width
	public double get_WHEEL_RADIUS() {
		return this.WHEEL_RADIUS;
	}

	public double get_WHEEL_WIDTH() {
		return this.WHEEL_WIDTH;
	}
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta / (2 * 3.141592) * 360;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}
	public double angleDegreeCorrection(double number) {
		if(number > 360)
			number -= 360;
		else if(number <0)
			number += 360;
		return number;
	
	}
	public double degreesToRadian(double number) {
		number = number/360*2*Math.PI;
		return number;
	
	}
	public double radianToDegree(double number) {
		number = number/(2*Math.PI)*360;
		return number;
	
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	
	}
}