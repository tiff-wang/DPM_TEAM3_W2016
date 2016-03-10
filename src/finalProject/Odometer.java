package finalProject;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {

	// Motor variables
	private double WHEEL_RADIUS = 2.1;
	private double WHEEL_WIDTH = 13.5;
	private final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

	// Odometry variables
	private static final int ODOMETER_PERIOD = 25;
	private static int previousTachoL;
	private static int previousTachoR;
	private static int currentTachoL;
	private static int currentTachoR;
	private double x, y, theta;

	// Lock object for mutual exclusion
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

		LCD.clear();
		LCD.drawString("Odometer Demo", 0, 0, false);
		LCD.drawString("Current X  ", 0, 4, false);
		LCD.drawString("Current Y  ", 0, 5, false);
		LCD.drawString("Current T  ", 0, 6, false);

	}

	// run method (required for Thread)
	public void run() {

		long updateStart, updateEnd;

		while (true) {

			updateStart = System.currentTimeMillis();

			double leftDistance, rightDistance, deltaDistance, deltaTheta, dX, dY;
			currentTachoL = leftMotor.getTachoCount();
			currentTachoR = rightMotor.getTachoCount();

			leftDistance = 3.14159 * WHEEL_RADIUS * (currentTachoL - previousTachoL) / 180;
			rightDistance = 3.14159 * WHEEL_RADIUS * (currentTachoR - previousTachoR) / 180;

			previousTachoL = currentTachoL;
			previousTachoR = currentTachoR;

			deltaDistance = .5 * (leftDistance + rightDistance);
			deltaTheta = (leftDistance - rightDistance) / WHEEL_WIDTH;

			synchronized (lock) {

				theta = (theta + deltaTheta) % (2 * Math.PI);

				dX = deltaDistance * Math.sin(theta);
				dY = deltaDistance * Math.cos(theta);

				x += dX;
				y += dY;
			}

			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
				}
			}
		}
	}

	// NOT SURE WHY THERE ARE 2 getPositions HERE, we will only use one
	public void getPosition(double[] position) {
		synchronized (lock) {
			position[0] = x;
			position[1] = y;
			position[2] = theta / (2 * Math.PI) * 360;
		}
	}

	public void getPosition2(double[] position, boolean[] update) {
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	// Accessor to Motor Variables
	public EV3LargeRegulatedMotor[] getMotors() {
		return new EV3LargeRegulatedMotor[] { this.leftMotor, this.rightMotor };
	}

	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}

	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	public double get_WHEEL_RADIUS() {
		return this.WHEEL_RADIUS;
	}

	public double get_WHEEL_WIDTH() {
		return this.WHEEL_WIDTH;
	}

	// Getter for X,Y,Theta

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

	// Mutators for Position, X, Y, Theta
	public void setPosition(double[] position, boolean[] update) {
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2] % (2 * Math.PI);
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

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta % (2 * Math.PI);
		}
	}
}