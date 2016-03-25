package finalProject;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Offense {
	private Odometer odo;
	private Navigation nav;
	EV3LargeRegulatedMotor pickUpMotor;
	EV3LargeRegulatedMotor launchMotor;
	private double blueBallValue = 0.1;				// VALUES NEED TESTING
	private double blueColorMargin = 0.05;
	private double redBallValue = 0.2;
	private double redColorMargin = 0.08;
	private int sweepAngle = 225;			// initial sweep of arm to bring ball up to sensor
	private int keepBallAngle = 30;			// if ball is desired color then push further up into trough 
	private int ballCount = 0;
	public Offense(Navigation nav,Odometer odo){
		this.odo = odo;
		this.nav = nav;
		
		this.pickUpMotor = odo.getPickUpMotor();
		this.launchMotor = odo.getLaunchMotor();
	
	}
	
	public  void doOffense(){
		
		int corner= Main.getParameter(0);
		int forwadZoneWidth = Main.getParameter(4);
		
		switch(corner){						// want to travel to closeset point in the offending zone from given corner
		case 1:  
			nav.travelToTile(1,1 );
			break;
		case 2:
			nav.travelToTile(9,1 );
			break;
		case 3:
			nav.travelToTile(9,forwadZoneWidth );
			break;
		case 4:
			nav.travelToTile(1,forwadZoneWidth );
			break;
		}
	
	}
	public void pickUpBall(){

		launchMotor.rotate(sweepAngle);
	
		int desiredBallType = Main.getParameter(9);
		if(desiredBallType==0){
			ballCount++;
			detectRedBall();
		}
		else if(desiredBallType==1){
			ballCount++;
			detectBlueBall();
		}
		else if(desiredBallType==2){
			ballCount++;
			launchMotor.rotate(keepBallAngle);
			launchMotor.rotate(-keepBallAngle-sweepAngle);
			
		}
		
		
	}
	public boolean detectRedBall(){
		float redColorReading = SensorPoller.getRedValueColorLauncher();
		if(redColorReading <(redBallValue+redColorMargin) && redColorReading > (redBallValue -redColorMargin)){
			launchMotor.rotate(keepBallAngle);
			launchMotor.rotate(-keepBallAngle-sweepAngle);
			return true;
		}
		else{
			launchMotor.rotate(-sweepAngle);
			return false;
		}
	}
	public boolean detectBlueBall(){
		float blueColorReading = SensorPoller.getBlueValueColorLauncher();
		if(blueColorReading <(blueBallValue+blueColorMargin) && blueColorReading > (blueBallValue -blueColorMargin)){
			launchMotor.rotate(keepBallAngle);
			launchMotor.rotate(-keepBallAngle-sweepAngle);
			return true;
		}
		else{
			launchMotor.rotate(-sweepAngle);
			return false;
		}
	}
	
	public void launch(){		//for now will just launch to middle of goal
		int acceleration = 2000000;
		int launchSpeed = 50000000;
		int launchAngle = 95;				// angle rotated when launching
		
		int rotateSpeed = 65;				// speed when picking up ball
		int pickUpAngle = 45;				// angle rotated to pick up
		
		launchMotor.setSpeed(rotateSpeed);													// set values of speed and acceleration
		launchMotor.setAcceleration(acceleration);
		launchMotor.setSpeed(rotateSpeed);													// set values of speed and acceleration
		launchMotor.rotate(pickUpAngle,false);
		for(int i =0; i<ballCount; i++){
		
			launchMotor.setSpeed(launchSpeed);													
			launchMotor.rotate(launchAngle,false);
			launchMotor.setSpeed(rotateSpeed);													// set values of speed and acceleration
			launchMotor.rotate(360-launchAngle,false);
		}

		
	}
}
