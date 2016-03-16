package finalProject;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;

public class LocalizationUS{

	// localization parameters
	public static final double WALL_DISTANCE = 30;
	public static final double NOISE = 5;

	public static double distance, angleA, angleB, errorAngle;
	public static String doing = "";
	private Odometer odo;
	private Navigation nav;
	private double WHEEL_RADIUS;
	private double WHEEL_WIDTH;
	private int SPEED_ROTATE;

	public LocalizationUS(Odometer odo, Navigation nav) {
		this.odo = odo;
		this.nav = nav;
		this.WHEEL_RADIUS = odo.get_WHEEL_RADIUS();
		this.WHEEL_WIDTH = odo.get_WHEEL_RADIUS();
		this.SPEED_ROTATE = nav.getSPEED_ROTATE();
		
	}

	public void doLocalization() {
		double[] position = new double[3];
		double angleA = 0, angleB = 0;
			
			//if the robot is not facing the wall, turn around
			distance = SensorPoller.getValueUS();
			if((distance) > WALL_DISTANCE){
				odo.getPosition(position);
				nav.leftMotor.setSpeed(100);
				nav.rightMotor.setSpeed(100);
				nav.turnDegreesClockwise(position[2]+ Math.PI);
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
			
			nav.leftMotor.setSpeed(SPEED_ROTATE);
			nav.rightMotor.setSpeed(SPEED_ROTATE);
			
			//the robot will rotate clockwise
			nav.leftMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true); 
			nav.rightMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true);
			
			boolean angleAChecked = false;
			boolean angleBChecked = false;
			
			while(!angleAChecked){
				float distance = SensorPoller.getValueUS();
					//the distance chosen is 42 so that the robot will not be confused by the corner
					//it corresponds to the diagonal of the 30x30 square rounded up
					if (distance > WALL_DISTANCE){      
						odo.getPosition(position);
						angleA = position[2];
						angleAChecked = true;
				}
				
			}
			nav.leftMotor.stop();
			nav.rightMotor.stop();
			
			nav.leftMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 20), true); //to be sure the distance detected is not the same as the one we just did
			nav.rightMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 20), true);
			// switch direction and wait until it sees no wall
			//the robot will rotate Counterclockwise
			
			nav.leftMotor.setSpeed(SPEED_ROTATE);
			nav.rightMotor.setSpeed(SPEED_ROTATE);
			nav.leftMotor.rotate(-Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true); 
			nav.rightMotor.rotate(Navigation.convertAngle(WHEEL_RADIUS, WHEEL_WIDTH, 360), true);
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			while(!angleBChecked){
				int distance = SensorPoller.getValueUS();
					//the distance chosen is 42 so that the robot will not be confused by the corner
					//it corresponds to the diagonal of the 30x30 square rounded up
					if (distance > WALL_DISTANCE){      
						odo.getPosition(position);
						try{
						angleB = position[2];
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
			
		} 

	private void rotateFromWall(boolean clockwise) {
		int filter = 0;
		nav.rotate(clockwise);
		distance = SensorPoller.getValueUS();
		while (distance < (WALL_DISTANCE + NOISE) && filter < 10) {
			distance = SensorPoller.getValueUS();
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
		distance = SensorPoller.getValueUS();
		while (distance > (WALL_DISTANCE - NOISE) && filter < 10) {
			distance = SensorPoller.getValueUS();
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

}
