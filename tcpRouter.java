package assignment1;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey; 
import java.security.Key;

public class tcpRouter {
	private static final int PORT = 1123; //Sender to Router port
	private static final int PORT2 = 1125; //Router to Receiver port
	private static InetAddress host;
	private static int random,i=0;;
	private static int dropPacketSize;
	private static boolean needtoencrypt = false;
	private static int counter,temp,n;
	private static String packetrouter[]=new String[10000];
	static Cipher cipher;
	
	
	public static void main(String args[]) throws Exception
	{
		ServerSocket routerSocket = new ServerSocket(PORT); //for receiving packets from sender
		host = InetAddress.getLocalHost();
		Socket routerSocket2 = null; //for sending packets to receiver
		ObjectOutputStream oos = null;
		DataInputStream ois = null;		
	    	    
	    dropPacketSize = (int) (0.1*20);   //To determine probability of losing packets
	   
		System.out.println("Router started...");
			
		while(true) {		
			 Random rand=new Random();
	            random = rand.nextInt(10);
	           
				
	            try {
			
			System.out.println("Waiting for sender");
			Socket connectionSocket2 = routerSocket.accept();
		
			//Get Packet from Sender
			
			ObjectInputStream inputStream = new ObjectInputStream(
	                connectionSocket2.getInputStream());	
			
			 String sendermessagepacket = (String)inputStream.readObject();
						 
			 //store it in router array
			 if(sendermessagepacket.charAt(0)!='e')
			 {
				 String seq[]=sendermessagepacket.split(" ");
				 temp=Integer.parseInt(seq[0]);
			 counter=temp;
			
			 packetrouter[counter]=sendermessagepacket;
			 }
			 System.out.println("Working"+packetrouter[counter]);
			 
             //Print the received Packet
			System.out.println("Packet received to Router: "+sendermessagepacket);
			 if(sendermessagepacket.equalsIgnoreCase("exit")) break;
			inputStream.close();
			
			n=3;
			
			if(n==3)
			{
			System.out.println("Sending packet to Receiver"); 
							try {		
								routerSocket2 = new Socket(host, PORT2);
								//sending first packet	
								if(needtoencrypt==true) {
									//encrypt the data and then send
									System.out.println("Encrypting:"+packetrouter[i]);
									
									encryptmessage();
									
									needtoencrypt=false;
								}
			oos = new ObjectOutputStream(routerSocket2.getOutputStream());					
		    oos.writeObject(""+packetrouter[i]);	   
		    ois=new DataInputStream(routerSocket2.getInputStream());
		    
		    int flag=ois.readInt();
		   
		    if(flag==0)
		    {
		    	i++;
		    }
		    else
		    {
		    	System.out.println("NACK received for packet - "+flag);
		    	i=flag;
		    	needtoencrypt = true;
		    	System.out.println("There is a need for encryption!");
	    	 continue;
		    }
								
             if(random<dropPacketSize) {
            	 i++;
		    	
		    }   
							}catch(Exception e)
							{
							}
					    
			connectionSocket2.close();		    
	    }
	            }catch(Exception e) {
	            	
	            }
	}
		routerSocket2.close();
		routerSocket.close();
	}
	
	public static void encryptmessage() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	        keyGenerator.init(128); // block size is 128bits
	        SecretKey secretKey = keyGenerator.generateKey();
	        cipher = Cipher.getInstance("AES"); 
	        String plainText = packetrouter[i];
	        System.out.println("Plain Text Before Encryption: " + plainText);
	        
	        byte[] plainTextByte = plainText.getBytes();
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] encryptedByte = cipher.doFinal(plainTextByte);
	        Base64.Encoder encoder = Base64.getEncoder();
	        String encryptedText = encoder.encodeToString(encryptedByte);

	        System.out.println("Encrypted Text After Encryption: " + encryptedText);
	        //Add the key with the encrypted text with text divider as $$$
	        
	        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
	        System.out.println("The key before encoding is" + secretKey);
	        System.out.println("Secret key is " + encodedKey);
	        
            packetrouter[i]=encodedKey+","+encryptedText;
		
		}catch(Exception e) {
			
		}
	}

}

