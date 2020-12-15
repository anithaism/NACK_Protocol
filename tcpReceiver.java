package assignment1;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class tcpReceiver {
	private static final int PORT = 1125;	
	private static int expectedpacket = 0;
	private static boolean needtodecrypt = false;
	private static int nextpacket,flag=0,temp;//flag=1 for nack
	private static String nack,encryptedText,sample;
	static Cipher cipher; 
	
	public static void main(String args[]) throws Exception {
		ServerSocket receiverSocket = new ServerSocket(PORT);		
		System.out.println("Server(Receiver) started...");
		try {
		while(true) {
			System.out.println("Waiting for Router");
			Socket connectionSocket = receiverSocket.accept();	
			
			//Get the first packet
				
			ObjectInputStream inputStream = new ObjectInputStream(
	                connectionSocket.getInputStream());	
			
			 String messagepacket = (String)inputStream.readObject();
			 System.out.println("Packet received: "+messagepacket);	
			 
			 if(messagepacket.charAt(1)!=' ' && !messagepacket.equals("null")) {
				
				 System.out.println("This is a encrypted message!! DECRYPTING NOW");
				 
				 String[] msg = messagepacket.split(",",2);
				 System.out.println("The shared key is "+msg[0]);
				 System.out.println("The encrypted message is "+msg[1]);
				 
				 Base64.Decoder decoder = Base64.getDecoder();
				 encryptedText = msg[1];
				 byte[] encryptedTextByte = decoder.decode(encryptedText);
				 sample = msg[0];
				 
				 byte[] decodedKey = Base64.getDecoder().decode(sample);
				 SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
				 
				 System.out.println("The key after decoding is "+originalKey);
				 cipher = Cipher.getInstance("AES"); 
				
			     cipher.init(Cipher.DECRYPT_MODE, originalKey);     
			     byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			     String decryptedText = new String(decryptedByte);
			        
			     System.out.println("This is the decrypted packet : " + decryptedText);
				 
				 expectedpacket++;
				 flag=0;
			 				
			 }
			 
			 if(messagepacket.equals("null"))
			 {
				 //System.out.println("NACK");
				 System.out.println("NACK - Expected :"+expectedpacket);
				 needtodecrypt=true;
				 //send this nack to Router			 
				 DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
				 flag=expectedpacket;
				 outputStream.writeInt(flag);
				 continue;
			 }			 
			 
			 if(messagepacket.charAt(0)!='e')
			 {
				 String seq[]=messagepacket.split(" ");
				 temp=Integer.parseInt(seq[0]);
			 
			 if(expectedpacket == temp)
			 {	
				 System.out.println("Expected packet received!");	 
				 expectedpacket++;
				 flag=0;
				 
				 //send this ack to Router
				 DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
					outputStream.writeInt(flag);			 
			 }
			 else
			 {
				 System.out.println("NACK - Need expected packet:" + expectedpacket);
				 String nackmessage = "NACK";
				 needtodecrypt=true;
				 System.out.println("WAIT! AM DECRYPRING");
				 flag=1;
				 DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
				 flag=expectedpacket;
				 outputStream.writeInt(flag);
		       continue;
			 }
			 
			 }
			 
				if(flag==1)
				{
					System.out.println("I am going to call the required packet");
					ObjectOutputStream outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
					outputStream.writeObject(flag);
				}
			 
			 inputStream.close();
			 if(messagepacket.equalsIgnoreCase("exit")) break;
			 
			connectionSocket.close();
		}
	}catch (Exception e)
	{
	}
			receiverSocket.close();
		
	}	
	
}
