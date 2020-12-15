package assignment1;
import java.io.*;
import java.net.*;
import java.util.*;
public class tcpSender {
	
	private static String packet[];
	private static InetAddress host;
	private static final int PORT = 1123,Y=10;  //Y is the Packetcount	
	
	public static void main(String args[]) throws Exception
	{
		packet=new String[2000];
		try {
		   host = InetAddress.getLocalHost();
		   }catch(UnknownHostException uhEx){
               System.out.println("Host ID not found!");
               System.exit(1);
             }
		 Socket senderSocket = null;				 
		 ObjectOutputStream oos = null;
	     
		 try {
		 //Generating random messages   
		 for(int i=0;i<Y;i++) {
			packet[i]=i+" packet";
		 }

		//This is general case
		  for(int j=0;j<=(Y+5);j++) {   //Change packet count values here
			  senderSocket = new Socket(host, PORT);
			  oos = new ObjectOutputStream(senderSocket.getOutputStream());
			  System.out.println("Sending request to Socket Receiver");
			  if(j==(Y+5))oos.writeObject("exit");  //Change packet count values here
	            else oos.writeObject(""+packet[j]);

			  oos.close();
	            Thread.sleep(100);
	 
		 }
		 
		 }catch(Exception e) {
			 
		 }		 
		 
		 senderSocket.close();
	}
}
