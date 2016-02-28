package finalProject;

import java.util.Arrays;

import lejos.robotics.SampleProvider;

public class sensors {
	private SampleProvider usSensor;
	private float [] usData;
	
	private SampleProvider frontColorSensor;
	
	
	private SampleProvider backColorSensor;
	
	
	private SampleProvider ballColorSensor;
	private float [] colorData;
	
	private int dataLength = 5;
	private float [] usDataReadings= new float[dataLength];
	private float [] frontColorReadings= new float[dataLength];
	private float [] backColorReadings= new float[dataLength];
	private float [] ballColorReadings= new float[dataLength];
	
	public sensors(SampleProvider usSensor, float[] usData, SampleProvider frontColorSensor, SampleProvider backColorSensor, SampleProvider ballColorSensor, float[] colorData){
		
		this.usSensor = usSensor;
		this.usData = usData;
		
		this.frontColorSensor = frontColorSensor;
		this.backColorSensor = backColorSensor;
		this.ballColorSensor = ballColorSensor;
		
		this.colorData = colorData;					// float array where color data stored
	}

	//take initial 5 readings of each sensor
	public void initializeSensors(){

		
		
		for(int i = 0; i< dataLength; i++){				// initialize each  sensor with 5 readings
			usSensor.fetchSample(usData, 0);
			
			usDataReadings[i]= usData[0]*100;
			if(usDataReadings[i]>60)
				usDataReadings[i] = 60;					// max distance capped at 60
			
			frontColorSensor.fetchSample(colorData, 0);
			frontColorReadings[i] = colorData[0];
			
			backColorSensor.fetchSample(colorData, 0);
			backColorReadings[i] = colorData[0];
			
			ballColorSensor.fetchSample(colorData, 0);
			ballColorReadings[i] = colorData[0];
		}
		
		
		
		
		
		
	}
	
	
	
	//this method takes sensor array passed to it takes another reading
	// other values shifted and returns the median value
	// mode 1 for US, 2 for front colorSensor, 3 for back colorSensor, 4 for ball colorSensor
	
	
public float[] getFilteredData() {
		float [] medians = new float[4];
		
		for(int i = 0; i<dataLength; i++){						// take new reading and shift others to the left
			if(i == 4){
					usSensor.fetchSample(usData, 0);					
					usDataReadings[i] = usData[0]*100;
				if(usDataReadings[i]>60)
					usDataReadings[i] = 60;					//caps max distance of US sensor at 60
				
				frontColorSensor.fetchSample(colorData, 0);			// read and save each of the color sensors
				frontColorReadings[i] = colorData[0];
				
				backColorSensor.fetchSample(colorData,0);
				backColorReadings[i] = colorData[0];
				
				ballColorSensor.fetchSample(colorData, 0);
				ballColorReadings[i] = colorData[0];
			}
			else{													// shift each of the readings
				usDataReadings[i]=usDataReadings[i+1];
				frontColorReadings[i] = frontColorReadings[i+1];
				backColorReadings[i] = backColorReadings[i+1];
				ballColorReadings[i] = ballColorReadings[i+1];
			}
				
		}
															//GET MEDIAN OF EACH SENSOR
		medians[0] =getMedian(usDataReadings);					//US in position 0
		medians[1]= getMedian(frontColorReadings);				//frontColor position 1
		medians[2]= getMedian(backColorReadings);				//backColor position 2
		medians[3]= getMedian(ballColorReadings);				//ballCOlor position 3
		
		return(medians);
	}
	public float getMedian(float [] sensorData){
		
		float [] numList = new float[5];			// making a copy of sensorData to find median without changed order
		
		for(int i =0; i<sensorData.length; i++){
			numList[i] = sensorData[i];
			
		}
		
		Arrays.sort(numList);						// sort array in ascending order
		float median;
		if (numList.length % 2 == 0)
		    median = ((float)numList[numList.length/2] + (float)numList[numList.length/2 - 1])/2;
		else
		    median = (float) numList[numList.length/2];
	return median;
	}
	
	
}
