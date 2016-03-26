package finalProject;
import org.freedesktop.dbus.bin.DBusDaemon.Reader;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

public class BallReader extends Thread{
	private Odometer odometer; 
	private double minDistance, dNow, ymin, xmin, tnow;
	private int counter = 0;
	private boolean detected;
	
	public BallReader(Odometer odometer){
		this.odometer = odometer; 
		detected = false;
	}
	
	public void run(){
		minDistance = 150;
		while(true){
			if(!detected){
				try{
					Reader.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(!detected){
					dNow = SensorPoller.getValueUS();
					if(dNow < minDistance && dNow < 30){
						ymin = odometer.getY();
						xmin = odometer.getX();
						tnow = odometer.getTheta();
						counter = 0;
						this.minDistance = dNow; 
						System.out.println(minDistance);
					}else if (counter < 80 && minDistance < 30) {
						counter++;
						try {
							Reader.sleep(5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if (this.minDistance < 30){
						System.out.println("     ***  " + minDistance);
						detected = true;
					}
				}
			}
		}
	}
	
	public boolean getDetected(){
		return this.detected;
	}
	
	public double getXmin(){
		return this.xmin;
	}
	
	public double getYmin(){
		return this.ymin;
	}
	
	public double getDmin(){
		return this.minDistance;
	}
	
	public double getTheta(){
		return tnow;
	}
	
	public void setDetected(boolean detected){
		this.detected = detected;
		this.minDistance = 150;
	}
}
