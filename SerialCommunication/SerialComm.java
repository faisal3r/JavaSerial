/*
 * Initiated SerialComm object reads and prints input from a port defined in the constructor
 * SerialComm.close() must be called on an initiated instance once done
 * 
 * SUMMARY:
 * Constructor: SerialComm(portName, baudRate)
 * initialize(): returns 1 if success, 0 if failure
 * sendSerialData(data): sends data through initialized port
 * close(): must be called before closing the program
 */

package SerialCommunication;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;

public class SerialComm implements SerialPortEventListener{
	SerialPort serialPort;
	private String portName;
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private int dataRate;
	private String inputLine;
	private boolean dataAvailable;
	private int initializationResult;
	
	public SerialComm(String portName, int dataRate){
		this.portName = portName;
		this.dataRate = dataRate;
		try{
			initializationResult = this.initialize();
			Thread.sleep(2000);//wait for initialization
		}catch(UnsupportedCommOperationException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	/*
	 * initialize function returns 1 if port portName opened, 0 if cannot open port portName
	 */
	public int initialize() throws UnsupportedCommOperationException{
		CommPortIdentifier portId = null;
		
		//Search for portName in the system
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portId == null && portEnum.hasMoreElements()){
		    CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
		    if (currPortId.getName().equals(portName)){
	        	portId = (CommPortIdentifier)currPortId;
	        	//Open the port using portId
	    		try{
	    			serialPort = (SerialPort)portId.open(this.getClass().getName(), TIME_OUT);
	    			System.out.println("Port: "+portName+ " opened");
	    	        serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	    		}
	    		catch(PortInUseException e){
	    			System.out.println("ERROR Initializing Port: Port busy");
	    			e.printStackTrace();
	    			System.out.println("Would you like to force close port "+portName+"? (Y/N)");
	    			Scanner sc = new Scanner(System.in);
	    			String response = sc.nextLine();
	    			if(response.toLowerCase().contains("Y")){
	    				serialPort.close();
	    				sc.close();
	    			}
	    			return 0;
	    		}
	    		break;
		    }
		}
		
		if(portId == null){
			System.out.println("ERROR Initializing Port: Could not open port");
			return 0;
		}
		
        try{
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e){
			System.err.println(e.toString());
		}
        
        return 1;
	}

	public synchronized void close(){
		if (serialPort != null){
			serialPort.removeEventListener();
			serialPort.close();
			System.out.println("Port: "+portName+ " closed");
		}
	}
	
	
	public synchronized void serialEvent(SerialPortEvent oEvent){
	    try{
	        switch (oEvent.getEventType()){
	            case SerialPortEvent.DATA_AVAILABLE: 
	                if(input == null){
	                    input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	                }
	                inputLine = input.readLine();
	                dataAvailable = true;
	                break;
	            default:
	                break;
	        }
	    } 
	    catch (Exception e){
	        System.err.println(e.toString());
	    }
	}
	/*
	 * check if initialization of serial port is successful
	 */
	public boolean isInitialized(){
		if(initializationResult == 1){
			return true;
		}
		else{
			return false;
		}
	}
	/*
	 * check if serial data is incoming
	 */
	public boolean available(){
		if(dataAvailable){
			return true;
		}
		else return false;
	}
	public String read(){
		dataAvailable = false;
		return inputLine;
	}
	public void write(String str) throws IOException{
		output = serialPort.getOutputStream();
		output.write(str.getBytes());
	}
}