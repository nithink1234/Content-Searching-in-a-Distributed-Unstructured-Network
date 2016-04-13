import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public class join {
	
	public static void join_request(Hashtable<String,String> routing_table, String self_port) throws UnknownHostException{
		
		//Prepare the join message to send
		StringBuilder join_packet = new StringBuilder();
		
		int length = 11 + self_port.length() + InetAddress.getLocalHost().getHostAddress().toString().length();
		
		join_packet.append("00" + length + " " + "JOIN" + " " + InetAddress.getLocalHost().getHostAddress()+" " + self_port);
		
		//String conversion
		String message = new String(join_packet);
		
		//Enumeration
		Enumeration<String> keys = routing_table.keys();
		
	    while (keys.hasMoreElements()) {
	      	
	    	String entry = keys.nextElement();	    
	    	
	    	//Converting to respective types to send send_receive class
	      	InetAddress ip = InetAddress.getByName(entry.toString().trim() );
	      	int port = Integer.parseInt(routing_table.get(entry).toString().trim());
	      	
	      	// Sending the Join Message
			send_receive.send(message, ip, port);
			
			System.out.println(message + " " + ip + " " + port);
	
		}
		
	}

}

