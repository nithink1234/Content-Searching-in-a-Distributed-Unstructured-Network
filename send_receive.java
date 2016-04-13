import java.net.*;
import java.io.*;
import java.util.*;

public class send_receive {
	
		//Variable creations
		static DatagramSocket clientside_socket = null;
	
		public static void send(String message, InetAddress ip , int port){
			
			//DatagramPacket variable created
			DatagramPacket packet_to_be_sent = null;
				
			//Variables of type byte created
			byte[] sendbuffer = new byte[10024];
			byte[] recvbuffer = new byte[10024];
			
			try{
			
			//DatagramScket variable creation here
			clientside_socket = new DatagramSocket();
			
			//Converting message to be sent to server
			sendbuffer = message.getBytes();
			
			packet_to_be_sent = new DatagramPacket(sendbuffer, sendbuffer.length, ip, port);
		    
			//Sending the packet over to the server side
			clientside_socket.send(packet_to_be_sent);
			
			}catch (IOException e){
				System.out.println("Sending Error");
				
			}

		}
		
		public static String receive(){
			
			//variable type byte declared
			byte[] recvbuffer = new byte[10024];
			
			//DatagramPacket variable created and received
			DatagramPacket packet_to_be_recieved = new DatagramPacket(recvbuffer, recvbuffer.length);
			
			//Receive packet from server
			try {
				clientside_socket.receive(packet_to_be_recieved);
				
				
			} catch (IOException e) {
				System.out.println("Receive error");
				
			}
			
			//Convert it back to string
			String output = new String(packet_to_be_recieved.getData());
			
			System.out.println(output);
			return output;
		}
		
}
