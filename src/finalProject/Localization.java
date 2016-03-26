package finalProject;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;



public class Localization {

	private Odometer odo;
	private Navigation nav;
	
	//static Port lightPort  = LocalEV3.get().getPort("S2");
	//static EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort);
	
	//private static final int ROTATE_SPEED = 150;
	//private ColorSensor cs;
	
	 // class parameters
	int dTx, dTy;
	double radius = 23;
	int angleX1, angleX2, angleY1, angleY2 = 0;
	double X , Y;
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
		
	public Localization(Odometer odo, Navigation nav){
		this.odo = odo;
		this.nav = nav;

		this.leftMotor = odo.getLeftMotor();
		this.rightMotor = odo.getRightMotor();
	
		
		LCD.clear();
		
	}
	
	public void doLocalization(){
		doUSLocalization();
		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		Sound.buzz();
		//doLightLocalization();
		/*
		int corner = Main.getParameter(0);
		
		switch(corner){						//update odometer position to the coordinate axis relative to board
		case 1:  
			break;
		case 2:
			odo.setX(-1*odo.getX()+30*10);
			break;
		case 3:
			odo.setX(-1*odo.getX()+30*10);
			odo.setY(-1*odo.getY()+30*10);
			break;
		case 4:
			odo.setY(-1*odo.getY()+30*10);
			break;
		}
		*/
	}
	public void doUSLocalization(){
		
		double angleA, angleB;
		double distance;
		double dTheta, angle;


			// rotate the robot until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// switch direction and wait until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
	
			
			distance = maxDistance(SensorPoller.getValueUS()) ;
			if(distance<60)
				odo.setPosition(new double [] {0.0, 0.0, Math.PI}, new boolean [] {false, false, true});
			while(distance < 60){ 								//turn clockwise if facing wall
				nav.rotate(true);
				distance =  maxDistance(SensorPoller.getValueUS()) ;
			}

			while(distance > 30){
				
				nav.rotate(true);
				distance =  maxDistance(SensorPoller.getValueUS()) ;
			}

			angleA = Math.toDegrees(odo.getTheta());
			Sound.buzz();
			
			
			while(distance<60){
				nav.rotate(false);
				distance = maxDistance(SensorPoller.getValueUS()) ;
			}
			
			while(distance > 30){
				nav.rotate(false);
				distance = maxDistance(SensorPoller.getValueUS()) ;
			}
	
			angleB = Math.toDegrees(odo.getTheta());
			Sound.buzz();
		
		
			if( angleA >angleB)
				dTheta = 225 -(angleB+angleA)/2;
			else
				dTheta = 45 -(angleB+angleA)/2;
			
			dTheta = odo.angleDegreeCorrection(dTheta);
			
			// angle is the new angle that we want to turn to
			angle = odo.angleDegreeCorrection(dTheta+angleB);
		
			
			odo.setPosition(new double [] {0.0, 0.0, odo.degreesToRadian(angle)}, new boolean [] {true, true, true});

	
			while(odo.getTheta()<2*Math.PI-0.1){
				nav.rotate(true);
			}
		
			Sound.buzz();
			
			nav.turnDegreesClockwise(37);
	
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			Sound.buzz();
	}
	public void doLightLocalization(){
		float sample_int;
		
		while(true){
			

			sample_int = SensorPoller.getValueColorBack();
			
			//System.out.println("          " + sample_int);
			
			
			
			//starts rotating clockwise
			nav.rotate(true);
			
			//get the 4 lines
			
			if( sample_int < 25){
				if( ((90 * (Math.PI/180)) + 0.7853) > odo.getTheta() && odo.getTheta() > (0*(Math.PI/180) + 0.7853)){
					angleX1 = (int)(odo.getTheta() * 180 / Math.PI);
					Sound.beep();
					
				}
				if( 180*(Math.PI/180) + 0.7853 > odo.getTheta() && odo.getTheta() > 90*(Math.PI/180) + 0.7853 ){
					angleY1 = (int)(odo.getTheta() * 180 / Math.PI);
					Sound.beep();
					
				}
				if( 270*(Math.PI/180) + 0.7853 > odo.getTheta() && odo.getTheta() > 180*(Math.PI/180) + 0.7853 ){
					angleX2 = (int)(odo.getTheta() * 180 / Math.PI);
					Sound.beep();
					
				}
				if( 45*(Math.PI/180)  > odo.getTheta() || odo.getTheta() > 270*(Math.PI/180) + 0.7853 ){
					angleY2 = (int)(odo.getTheta() * 180 / Math.PI);
					Sound.beep();
					
				}
		
			}
			if(angleX1 != 0 && angleX2 != 0 && angleY1 != 0 && angleY2 != 0){
				
				nav.stopMotors();
				break;
			}
			
			
		}
		
		// print to screen
		dTx = (int) ((angleX2 - angleX1));
		dTy = (int)((angleY2 - angleY1));
		//System.out.println("        ");
		//System.out.println("        ");
		//System.out.println("        ");
		//System.out.println("        " + dTx +" "+ dTy);
		//System.out.println(angleX1 +" "+ angleX2 +" "+ angleY1 +" "+angleY2);
		calX(dTy);
		calY(dTx);
		//odo.setTheta( (int)( odo.getTheta() * 180 / Math.PI ) + 45  );
		
	}
	private void calX(double dTy){
		this.dTy = (int) dTy;
		X =  -1 * radius * ( Math.cos( (dTy/2) *Math.PI/180  ));
		odo.setX(X);
	}
	private void calY(double dTx){
		this.dTx = (int) dTx;
		Y =  -1 * radius * ( Math.cos( (dTx/2) *Math.PI/180  ));
		odo.setY(Y);
	}
	
	private double maxDistance (double distance){
		if (distance > 60){
			distance = 60;
		}
		return distance;
	}
	
	
	
	
	
}
