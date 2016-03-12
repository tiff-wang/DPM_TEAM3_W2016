//Lab 3
//Tiffany Wang 260684152
//Matthew Rodin 260623844
//Group 22

package finalProject;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class WallFollower {
	
	private final int bandCenter;
	private final int motorStraight = 100;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	static int counter = 0;
	private boolean isSensed = false;
	public double x0; 
	public double y0; 
	public double t0;
	public double xnow = 0;
	public double ynow = 0;
	public double tnow = 0;
	private boolean stop = false;
	private Odometer odometer;
	
	public WallFollower(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, Odometer odometer) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
	}
	

	public void processUSData(int distance) {
		if(!stop){
			//the robot keeps the record of the first point where it starts following the wall 
			if(distance < bandCenter && !isSensed){
				isSensed = true;
				this.t0 = odometer.getTheta(); 
				this.y0 = odometer.getY();
				this.x0 = odometer.getX();
			}
			// once the wall is sensed, you pause the navigation (thus the forward) because you want to concentrate on the wallFollower
			if(isSensed){
				//the robot keeps reading its current position
				ynow = odometer.getY();
				xnow = odometer.getX();
				//if the robot is too far, we bring it closer 
				//if the robot is too close, we make it move away from the wall. 
				float error = Math.abs(distance - bandCenter);
				float adjustedSpeed = error * 10; // the adjustedSpeed is proportional to the error 
				
				//we are using angles to check the original path of the robot 
				//a counter was implemented so that the angle would be matter once the robot was around the block
				if(distance > bandCenter){
						this.rightMotor.setSpeed(250); 
						this.leftMotor.setSpeed(145); 
						counter++;
							
				}else if (distance < bandCenter){
					this.leftMotor.setSpeed(motorStraight + adjustedSpeed);
					
					if((motorStraight - adjustedSpeed) <= 50){ 
						this.rightMotor.setSpeed(50); 
					} else { 
						this.rightMotor.setSpeed(motorStraight - adjustedSpeed); 
					}
				}
				
				
				this.leftMotor.forward(); // after setting up the speed of the wheels, we put the motors to work. 
				this.rightMotor.forward(); 
	//		
				double t2;  
				//if the angle of the original path and the one of the line from (x0, y0) to (xnow, ynow) are equal 
				//then the robot is at a point situated on the original path. 
				//then it turns towards the destination (this part is written in navigation) and stops following the wall 
				if (ynow - y0 == 0 && xnow-x0 > 0) {
					t2 = Math.PI/2;
				} else if (ynow - y0 == 0 && xnow-x0 < 0) {
					t2 = -Math.PI/2;
				} else if (xnow-x0 == 0 && ynow - y0 > 0) {
					t2 = 0;
				} else if (xnow-x0 == 0 && ynow - y0 < 0) {
					t2 = Math.PI;
				} else if((ynow - y0) < 0 && xnow-x0 < 0){
					t2 = Math.atan((xnow- x0)/(ynow - y0)) - Math.PI;
				} else if((ynow - y0) < 0 && xnow-x0 > 0){
					t2 = Math.atan((xnow-x0)/(ynow - y0)) + Math.PI;
				} else{
					t2 = Math.atan((xnow-x0)/(ynow - y0));
				}
				
				if(counter > 25  && Math.abs(t0 -t2) < 0.3*Math.PI/180){
					//once the robot is at the same angle as it was originally, the wallFollower stop and we proceed to travelTo.
					isSensed = false;
					stop = true;
					leftMotor.stop(true);
					rightMotor.stop(false);
				}
			}
		}
	}

	
	
	public int readUSDistance() {
		return this.distance;
	}

	public int getBandCenter(){
		return this.bandCenter;
	}
	

	
}
