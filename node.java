import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class node {

	public static void main(String[] args) throws IOException{
		
		//Declare the client side socket
		DatagramSocket clientside_socket = new DatagramSocket();
		
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
		int length = 15 + self_port.length() + bootstrap_IP.getHostAddress().toString().length();
		
		//Making the packet ready for transmission to server
		packet.append("00"+length).append(" ");
		packet.append("REG").append(" ");
		packet.append(ipAddress).append(" ");
		packet.append(self_port).append(" ");
		packet.append("n_c");
		
		String message = packet.toString();
		
		System.out.println("Register message: "+ message);
		
		//Sending the register message
		//	send_receive send_rec_obj = new send_receive();
		send_receive.send(message, bootstrap_IP, bootstrap_port);
		
		//Calling the receive function
		String output = send_receive.receive();
		
		//To remove last character
		output = output.replace("\n","");
		
		//Logic to split based on bootstrap output
		String[] response = output.split(" ");
		String string_nodes = response[2].trim();
		
		int no_nodes = Integer.parseInt(string_nodes);

		// Creating a Hash table
		Hashtable<String,String> routing_table = new Hashtable<String,String>();

		//Logic to receive and update the routing table
		if (no_nodes == 0){
			System.out.println("First Node Registered \n");
			while(true){
				String inf_output = send_receive.receive();
				//Do what ever with the output
				
			}
			
			
		}
		else if (no_nodes == 1){
			String ip_1 = response[3];
			String port_1 = response[4];
			routing_table.put (ip_1,port_1);
			
		}
		else if (no_nodes == 2){
			String ip_1 = response[3];
			String port_1 = response[4];
			String ip_2 = response[5];
			String port_2 = response[6];
			routing_table.put (ip_1,port_1);
			routing_table.put (ip_2,port_2);
			
		}
		else if (no_nodes == 9999){
			System.out.println("Failed, there is some error in the command \n");
			
		}
		else if (no_nodes == 9998){
			System.out.println("Failed, already registered to you, unregister first \n");
			
		}	
		else if (no_nodes == 9997){
			System.out.println("Failed, registered to another user, try a different IP and port \n");
			
		}
		else if (no_nodes == 9996){
			System.out.println("Failed, can’t register. BS full \n");
			
		}	
		else{
			System.out.println("Bootstrap error");
			
		}
		
		//Create object of join class and pass the hashtable to it
	
		for(int i = no_nodes ; i>0; i--){			
		join.join_request(routing_table,self_port);
		}
		
		//for (long stop=System.nanoTime()+TimeUnit.SECONDS.toNanos(2);stop>System.nanoTime();){		
		
		//Calling the receive function
		String join_output = send_receive.receive();
		
		//To remove last character
		join_output = join_output.replace("\n","");
				
		System.out.println(join_output);
		
		//Logic to split based on bootstrap output
		String[] join_response = join_output.split(" ");
		String join_ip = join_response[2].trim();
		String join_port = join_response[3].trim();
		
		//Updating routing table
		routing_table.put(join_ip, join_port);
		
		
		//}
		
		//Display the result
		System.out.println("Routing table: \n" + routing_table);
		
		clientside_socket.close();
		System.exit(0);
		
	}
	
}