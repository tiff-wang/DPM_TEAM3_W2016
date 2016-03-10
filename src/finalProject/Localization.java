package finalProject;

//
//THIS IS AN OLD LAB FROM NAWRAS, NO MODIFICATION HAS BEEN MADE YET
// THIS IS US LOCALIZATION
//

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;

public class Localization {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};
	

	// localization parameters
	public static final double WALL_DISTANCE = 30;
	public static final double NOISE = 5;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;

	public static double distance, angleA, angleB, errorAngle;
	public static String doing = "";
	private Odometer odo;
	private Navigation nav;
	private SampleProvider usValue;
	private float[] usData;
	private LocalizationType locType;
	private double WHEEL_RADIUS;
	private double WHEEL_WIDTH;

	public Localization(Odometer odo, Navigation nav, SampleProvider usValue, float[] usData,
			LocalizationType fallingEdge) {
		this.odo = odo;
		this.nav = nav;
		this.usValue = usValue;
		this.usData = usData;
		this.locType = fallingEdge;
		this.WHEEL_RADIUS = odo.get_WHEEL_RADIUS();
		this.WHEEL_WIDTH = odo.get_WHEEL_RADIUS();
		
	}

	public void doLocalization() {
		double[] pos = new double[3];
		double angleA = 0, angleB = 0;
		
if (locType == LocalizationType.FALLING_EDGE) {
			
			//if the robot is not facing the wall, turn around
			distance = getFilteredData();
			if((distance) > WALL_DISTANCE){
				odo.getPosition2(pos, new boolean [] {false, false, true});
				nav.leftMotor.setSpeed(100);
				nav.rightMotor.setSpeed(100);
				nav.turnDegreesClockwise(pos[2]+ Math.PI);
				try{
					Thread.sleep(2000);
				}
				catch(Exception e){
					
				}
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
				try{
					Thread.sleep(2000);
				}
				catch(Exception e){
					
				}
			}
			
			// rotate the robot until it sees no wall
			
			nav.leftMotor.setSpeed(ROTATE_SPEED);
			nav.rightMotor.setSpeed(ROTATE_SPEED);
			
			//the robot will rotate clockwise
			nav.leftMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true); 
			nav.rightMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true);
			
			boolean angleAChecked = false;
			boolean angleBChecked = false;
			
			while(!angleAChecked){
				float distance = getFilteredData();
					//the distance chosen is 42 so that the robot will not be confused by the corner
					//it corresponds to the diagonal of the 30x30 square rounded up
					if (distance > WALL_DISTANCE){      
						odo.getPosition2(pos, new boolean [] {false, false, true});
						angleA = pos[2];
						angleAChecked = true;
				}
				
			}
			nav.leftMotor.stop();
			nav.rightMotor.stop();
			
			nav.leftMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 20), true); //to be sure the distance detected is not the same as the one we just did
			nav.rightMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 20), true);
			// switch direction and wait until it sees no wall
			//the robot will rotate Counterclockwise
			
			nav.leftMotor.setSpeed(ROTATE_SPEED);
			nav.rightMotor.setSpeed(ROTATE_SPEED);
			nav.leftMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true); 
			nav.rightMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true);
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			while(!angleBChecked){
				int distance = getFilteredData();
					//the distance chosen is 42 so that the robot will not be confused by the corner
					//it corresponds to the diagonal of the 30x30 square rounded up
					if (distance > WALL_DISTANCE){      
						odo.getPosition2(pos, new boolean [] {false, false, true});
						try{
						angleB = pos[2];
						}
						catch (Exception e){
							
						}
						angleBChecked = true;
				}
			}
			nav.leftMotor.stop();
			nav.rightMotor.stop();
			
			nav.turnDegreesClockwise((((angleB+angleA)/2)-45)*0.01745);
			
			// update the odometer position
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall. This
			 * is very similar to the FALLING_EDGE routine, but the robot will
			 * face toward the wall for most of it.
			 */
			// finds wall
			rotateToWall(true);
			// goes to end of wall
			rotateFromWall(true);
			angleA = odo.getTheta();

			Sound.beep();
			nav.turnDegreesClockwise(15);
			Sound.beep();
			// goes in the opposite direction towards a wall
			rotateToWall(false);

			// rotateToWall(false);

			angleB = odo.getTheta();

			errorAngle = getAngle(angleA, angleB);
			nav.turnDegreesClockwise(errorAngle + 45);
			odo.setPosition(new double[] { 0.0, 0.0, 45 }, new boolean[] { true, true, true });

			nav.goForward(5);
		}
	}

	private void rotateFromWall(boolean clockwise) {
		int filter = 0;
		nav.rotate(clockwise);
		distance = getFilteredData();
		while (distance < (WALL_DISTANCE + NOISE) && filter < 10) {
			distance = getFilteredData();
			if (distance >= (WALL_DISTANCE + NOISE)) {
				filter++;
			}
		}
		nav.stopMotors();
	}

	/**
	 * 
	 * @param clockwise
	 *            true is clockwise, false is counterclockwise rotation
	 */
	private void rotateToWall(boolean clockwise) {
		int filter = 0;
		nav.rotate(clockwise);
		distance = getFilteredData();
		while (distance > (WALL_DISTANCE - NOISE) && filter < 10) {
			distance = getFilteredData();
			if (distance <= (WALL_DISTANCE - NOISE)) {
				filter++;
			}
		}
		nav.stopMotors();
	}

	private double getAngle(double alpha, double beta) {

		double deltaTheta;

		if (alpha > beta) {
			deltaTheta = 45 - (alpha + beta) / 2;

		} else {
			deltaTheta = 225 - (alpha + beta) / 2;
		}

		return deltaTheta;
	}

	private int getFilteredData() {

		int distance = SensorPoller.getValueUS();
		
		return distance;
	}

}
