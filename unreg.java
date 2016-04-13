
import java.net.*;
import java.io.*;
import java.util.*;

public class unreg {

	public static void main(String[] args) throws IOException{
		
		// Accepting all the arguments here								
		String self_port = args[0];
		String bootstrap_name = args[1];
		int bootstrap_port = Integer.parseInt(args[2]);
		
		//Fetching IP address of local node
		InetAddress address = InetAddress.getLocalHost();
		String ipAddress = address.getHostAddress();
		
		//Fetching IP address of the bootstrap
		InetAddress bootstrap_IP = InetAddress.getByName(bootstrap_name);
		
		StringBuilder packet = new StringBuilder();
		
		//Length calculation
		//System.out.println(bootstrap_IP.getHostAddress());
		int length = 17 + self_port.length() + bootstrap_IP.getHostAddress().toString().length();
		
		//Making the packet ready for transmission to server
		packet.append("00"+length).append(" ");
		packet.append("UNREG").append(" ");
		packet.append(ipAddress).append(" ");
		packet.append(self_port).append(" ");
		packet.append("n_c");
		
		String message = packet.toString();
		
		System.out.println("Register message: "+ message);
				
		//Variable creations
		DatagramSocket clientside_socket = null;
		
		//DatagramPacket variable created
		DatagramPacket packet_to_be_sent = null;
		DatagramPacket packet_to_be_recieved = null;
			
		//Variables of type byte created
		byte[] sendbuffer = new byte[1024];
		byte[] recvbuffer = new byte[1024];
		
		//DatagramScket variable creation here
		clientside_socket = new DatagramSocket();
		
		//Converting message to be sent to server
		sendbuffer = message.getBytes();
		
		packet_to_be_sent = new DatagramPacket(sendbuffer, sendbuffer.length, bootstrap_IP, bootstrap_port);
	    
		//Sending the packet over to the server side
		clientside_socket.send(packet_to_be_sent);
		
		//Collect the packet from server side
		packet_to_be_recieved = new DatagramPacket(recvbuffer, recvbuffer.length);
		
		//Receive packet from server
		clientside_socket.receive(packet_to_be_recieved);
		
		//Convert it back to string
		String output = null;
		
		//output = new String(recvbuffer);
		output = new String(packet_to_be_recieved.getData());
		
		//Display the result
		System.out.println("Reply from Bootstrap: \n" + output);
		
		clientside_socket.close();
		System.exit(0);
		
		
	}

	
}