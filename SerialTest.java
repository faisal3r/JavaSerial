/*
 * Test code for SerialComm class
 */

import SerialCommunication.*;
import java.util.Scanner;

public class SerialTest{
	public static void main(String[] args) throws Exception {
		SerialComm serial = new SerialComm("COM39", 9600);
		Scanner in = new Scanner(System.in); 
		
		//Define how serial should stop
		Thread t = new Thread(){
			boolean finished = false;
			public void run(){
				System.out.println("Press enter to stop");
				while(!finished){
					if(in.nextLine()!=null){
						System.out.println("Closing opened port...");
						serial.close();
						in.close();
						finished = true;
					}
				}
			}
		};
		
		if(serial.isInitialized()){
			t.start();
		}
		
		//Serial reading part
		while(t.isAlive()){
			//serial.write(data); //send serial data through initialized port
			while(serial.available()){
				System.out.println("[SERIAL]"+serial.read());
			}
		}
	}		
}