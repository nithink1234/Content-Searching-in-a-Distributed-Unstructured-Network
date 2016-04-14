import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class node {

	// Declaring all the static variables here in this section
	public static DatagramSocket clientside_socket = null;
	public static DatagramSocket receive_socket = null;

	// Creating a Hash table
	public static Hashtable<String, String> routing_table = new Hashtable<String, String>();

	// Variables for statistics recording
	public static int packets_received = 0;
	public static int packets_forwarded = 0;
	public static int packets_answered = 0;

	// Total Messages received and forwarded
	public static int message_forwarded = 0;
	public static int message_received = 0;

	// Variables to accept arguements from user
	public static String self_port = null;
	public static String bootstrap_name = null;
	public static int bootstrap_port = 0;

	// Variables of type byte created
	public static byte[] sendbuffer = new byte[1024];
	public static byte[] recvbuffer = new byte[1024];

	public static void main(String[] args) throws IOException,
			InterruptedException {

		// Accepting all the arguments here
		self_port = args[0];
		bootstrap_name = args[1];
		int bootstrap_port = Integer.parseInt(args[2]);

		try {
			// Declare the client side socket
			clientside_socket = new DatagramSocket();
			receive_socket = new DatagramSocket(
					Integer.parseInt(args[0].trim()));

		} catch (Exception e) {

			System.out.println("Problem opening a socket");
		}

		// Fetching IP address of local node
		InetAddress address = InetAddress.getLocalHost();
		String ipAddress = address.getHostAddress();

		// Fetching IP address of the bootstrap
		InetAddress bootstrap_IP = InetAddress.getByName(bootstrap_name);

		StringBuilder packet = new StringBuilder();

		// Length calculation
		int length = 15 + self_port.length()
				+ bootstrap_IP.getHostAddress().toString().length();

		// Making the packet ready for transmission to server
		packet.append("00" + length).append(" ").append("REG").append(" ")
				.append(ipAddress).append(" ").append(self_port).append(" ")
				.append("n_c");

		String message = packet.toString();

		System.out
				.println("Sending the Register message to Bootstrap Server: \n"
						+ message);

		// Converting message to be sent to server
		sendbuffer = message.getBytes();

		// DatagramPacket variable created
		DatagramPacket packet_to_be_sent = new DatagramPacket(sendbuffer,
				sendbuffer.length, bootstrap_IP, bootstrap_port);

		// Sending the register message
		clientside_socket.send(packet_to_be_sent);
		node.message_forwarded++;

		// Receiving the response from bootstrap
		// DatagramPacket variable created
		DatagramPacket packet_to_be_received = new DatagramPacket(recvbuffer,
				recvbuffer.length);

		clientside_socket.receive(packet_to_be_received);
		node.message_received++;

		node.message_received++;
		// Convert it back to string
		String output = new String(packet_to_be_received.getData(),
				packet_to_be_received.getOffset(),
				packet_to_be_received.getLength());

		// Display output from bootstrap server
		System.out.println("Response from Bootstrap Server : " + output);

		// To remove last character
		output = output.replace("\n", "");

		// Logic to split based on bootstrap output
		String[] response = output.split(" ");

		int no_nodes = Integer.parseInt(response[2].trim());

		// Initializing node with files

		process.file_allocation(self_port);

		// Logic to receive and update the routing table
		if (no_nodes == 0) {
			System.out.println("First Node Registered");

		} else if (no_nodes == 1) {

			byte[] recvbuffer_join = new byte[1024];

			routing_table.put((String) response[4], (String) response[3]);

			join.join_request(routing_table);

			System.out.println("Waiting for JOINOK:");

			// Wait to receive join message from other nodes
			DatagramPacket recv_packet = new DatagramPacket(recvbuffer_join,
					recvbuffer_join.length);
			receive_socket.receive(recv_packet);

			// Convert it back to string
			String join_response = new String(recv_packet.getData(),
					recv_packet.getOffset(), recv_packet.getLength());

			// Call nithins functions
			message_check.mess_check(join_response, routing_table);

		} else if (no_nodes == 2) {

			byte[] recvbuffer_join = new byte[1024];

			routing_table.put((String) response[4], (String) response[3]);
			routing_table.put((String) response[6], (String) response[5]);
			join.join_request(routing_table);

			System.out.println("Waiting for JOINOK:");

			DatagramPacket recv_packet;

			for (int i = 0; i < 2; i++) {
				// Wait to receive join message from other nodes
				recv_packet = new DatagramPacket(recvbuffer_join,
						recvbuffer_join.length);
				receive_socket.receive(recv_packet);

				// Convert it back to string
				String join_response = new String(recv_packet.getData(),
						recv_packet.getOffset(), recv_packet.getLength());

				// Call nithins functions
				message_check.mess_check(join_response, routing_table);

			}

		} else if (no_nodes == 9999) {
			System.out.println("Failed, there is some error in the command");

		} else if (no_nodes == 9998) {
			System.out
					.println("Failed, already registered to you, unregister first");

		} else if (no_nodes == 9997) {
			System.out
					.println("Failed, registered to another user, try a different IP and port");

		} else if (no_nodes == 9996) {
			System.out.println("Failed, can’t register. BS full");

		} else {
			System.out.println("Bootstrap error");

		}

		// Start a Thread here and handle user inputs
		// new userthread().start();

		while (true) {

			// Get what the user types.
			System.out.println("------------------------------");
			System.out.println("Please Enter the option:");
			System.out
					.println("1. Receive (r) 2. Search (search) 3. Leave (leave) 4. details 5. neighbours 6. files");
			System.out.println("------------------------------");

			Scanner user_input = new Scanner(System.in);
			String option = user_input.next();

			try {
				switch (option) {

				case "search":

					// send search message
					join.query(node.routing_table);
					break;

				case "r":
					int a = 0;
					// Receive continously here
					while (true) {
						// while (a != 5) {

						byte[] recvbuffer_join = new byte[1024];

						DatagramPacket recv_packet = new DatagramPacket(
								recvbuffer_join, recvbuffer_join.length);

						try {

							node.receive_socket.setSoTimeout(1000 * 75);

							node.receive_socket.receive(recv_packet);

							String join_output = new String(
									recv_packet.getData());
							// Do what ever
							System.out.println(join_output);

							message_check.mess_check(join_output,
									node.routing_table);
						}

						catch (SocketTimeoutException s) {

							System.out
									.println("\n"
											+ "The no of Packets Recieved for this Search : "
											+ packets_received);
							System.out
									.println("The no of Packets Forwarded for this Search : "
											+ packets_forwarded);
							System.out
									.println("The no of Packets Answered for this Search : "
											+ packets_answered);
							System.out
									.println("The size of the Routing table is:  "
											+ routing_table.size() + "\n");

							File file = new File("results_20" + "_" + self_port
									+ ".txt");
							FileWriter fileWritter = new FileWriter(
									file.getName(), true);
							BufferedWriter bufferWritter = new BufferedWriter(
									fileWritter);

							bufferWritter
									.write("The no of Packets Recieved for this Search : "
											+ packets_received + "\n");
							bufferWritter
									.write("The no of Packets Forwarded for this Search : "
											+ packets_forwarded + "\n");
							bufferWritter
									.write("The no of Packets Answered for this Search : "
											+ packets_answered + "\n");
							bufferWritter
									.write("The size of the Routing table is:  "
											+ routing_table.size() + "\n");
							bufferWritter
									.write("---------------------------------------------------------------- \n");

							bufferWritter
									.write("The total no of Packets Recieved in this node: "
											+ message_received + "\n");
							bufferWritter
									.write("The total no of Packets Forwarded by this node : "
											+ message_forwarded + "\n");
							bufferWritter.write("The Node degree is:  "
									+ routing_table.size() + "\n");

							bufferWritter
									.write("---------------------------------------------------------------- \n");

							bufferWritter.close();

							System.out
									.println("The total no of Packets Recieved in this node: "
											+ message_received + "\n");
							System.out
									.println("The total no of Packets Forwarded by this node : "
											+ message_forwarded + "\n");
							System.out.println("The Node degree is:  "
									+ routing_table.size() + "\n");

							bufferWritter.close();

							// a++;
							// if (a == 3)
							break;
						}

					}
					break;

				case "leave":

					byte[] recvbuffer_join = new byte[1024];

					DatagramPacket leave_packet = new DatagramPacket(
							recvbuffer_join, recvbuffer_join.length);

					join.leave(node.routing_table);

					// Node leaves the network
					int b = 0;
					for (b = 0; b < node.routing_table.size(); b++) {

						node.receive_socket.receive(leave_packet);

						String leave_output = new String(leave_packet.getData());

						message_check.mess_check(leave_output,
								node.routing_table);

					}

					// UnReg code

					StringBuilder packet_unreg = new StringBuilder();

					// Length calculation
					int length_unreg = 17 + self_port.length()
							+ bootstrap_IP.getHostAddress().toString().length();

					// Making the packet ready for transmission to server
					packet_unreg.append("00" + length_unreg).append(" ")
							.append("UNREG").append(" ").append(ipAddress)
							.append(" ").append(self_port).append(" ")
							.append("n_c");

					String message_unreg = packet_unreg.toString();

					System.out
							.println("Sending the UnRegister message to Bootstrap Server: \n"
									+ message_unreg);

					// Converting message to be sent to server
					node.sendbuffer = message_unreg.getBytes();

					// DatagramPacket variable created
					DatagramPacket packet_to_be_sent_unreg = new DatagramPacket(
							sendbuffer, sendbuffer.length, bootstrap_IP,
							bootstrap_port);

					// Sending the unregister message
					node.clientside_socket.send(packet_to_be_sent_unreg);
					node.message_forwarded++;

					// Receiving the response from bootstrap
					// DatagramPacket variable created
					DatagramPacket packet_to_be_received_unreg = new DatagramPacket(
							recvbuffer, recvbuffer.length);

					node.clientside_socket.receive(packet_to_be_received_unreg);
					node.message_received++;

					// Convert it back to string
					String output_unreg = new String(
							packet_to_be_received_unreg.getData(),
							packet_to_be_received_unreg.getOffset(),
							packet_to_be_received_unreg.getLength());

					// Display output from bootstrap server
					System.out.println("Response from Bootstrap Server : "
							+ output_unreg);

					// Writing the Statistic to file

					File file = new File("results_20" + "_" + self_port
							+ ".txt");
					FileWriter fileWritter = new FileWriter(file.getName(),
							true);
					BufferedWriter bufferWritter = new BufferedWriter(
							fileWritter);
					bufferWritter
							.write("The total no of Packets Recieved in this node: "
									+ message_received + "\n");
					bufferWritter
							.write("The total no of Packets Forwarded by this node : "
									+ message_forwarded + "\n");
					bufferWritter.write("The Node degree is:  "
							+ routing_table.size() + "\n");
					bufferWritter.close();

					System.out
							.println("The total no of Packets Recieved in this node: "
									+ message_received + "\n");
					System.out
							.println("The total no of Packets Forwarded by this node : "
									+ message_forwarded + "\n");
					System.out.println("The Node degree is:  "
							+ routing_table.size() + "\n");

					node.clientside_socket.close();
					System.exit(0);

				case "details": {

					System.out.println("\n" + "The Self IP is: " + ipAddress);
					System.out.println("The Self Port is: " + self_port);
					break;

				}

				case "neighbours": {

					// Enumeration
					Enumeration<String> keys = routing_table.keys();

					// Sending SER message to all IP's in Routing table
					while (keys.hasMoreElements()) {

						String entry = keys.nextElement();

						/*
						 * // Converting to respective types to send
						 * send_receive class InetAddress forward_ip =
						 * InetAddress.getByName(entry .toString().trim());
						 * 
						 * int forward_port = Integer.parseInt(routing_table
						 * .get(entry).toString().trim());
						 */

						InetAddress forward_ip = InetAddress
								.getByName(routing_table.get(entry).toString()
										.trim());

						int forward_port = Integer.parseInt(entry.toString()
								.trim());

						System.out.println("\n" + "The Neighbour IP is: "
								+ forward_ip);
						System.out.println("The Self Port is: " + forward_port
								+ "\n");

					}
					break;
				}

				case "files": {

					BufferedReader reader = new BufferedReader(new FileReader(
							"node_contents_20_" + "_" + self_port + ".txt"));

					String line = reader.readLine();

					System.out.println("\n");
					while (line != null) {

						System.out.println(line);
						line = reader.readLine();

					}
					reader.close();
					break;
				}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
