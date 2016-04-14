import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class join {

	public static void join_request(Hashtable<String, String> routing_table)
			throws IOException {

		byte[] sendbuffer_join = new byte[1024];

		// Prepare the join message to send
		StringBuilder join_message = new StringBuilder();

		int length = 12
				+ node.self_port.length()
				+ InetAddress.getLocalHost().getHostAddress().toString()
						.length();

		join_message.append("00" + length + " " + "JOIN" + " "
				+ InetAddress.getLocalHost().getHostAddress() + " "
				+ node.self_port);

		// String conversion
		String message = join_message.toString();

		// Enumeration
		Enumeration<String> keys = routing_table.keys();

		while (keys.hasMoreElements()) {

			String entry = keys.nextElement();

			/*
			 * //Converting to respective types to send send_receive class
			 * InetAddress ip = InetAddress.getByName(entry.toString().trim());
			 * 
			 * int port =
			 * Integer.parseInt(routing_table.get(entry).toString().trim());
			 */

			InetAddress ip = InetAddress.getByName(routing_table.get(entry)
					.toString().trim());

			int port = Integer.parseInt(entry.toString().trim());

			System.out.println("Sending the Join Message to: " + ip);
			System.out.println(message + "\n");

			sendbuffer_join = message.getBytes();
			// Sending the Join Message
			DatagramPacket join_packet = new DatagramPacket(sendbuffer_join,
					sendbuffer_join.length, ip, port);
			node.clientside_socket.send(join_packet);
			node.message_forwarded++;

		}

	}

	public static void leave(Hashtable<String, String> routing_table)
			throws IOException {

		byte[] sendbuffer_leave = new byte[1024];

		// Prepare the join message to send
		StringBuilder leave_message = new StringBuilder();

		int length = 13
				+ node.self_port.length()
				+ InetAddress.getLocalHost().getHostAddress().toString()
						.length();

		leave_message.append("00" + length + " " + "LEAVE" + " "
				+ InetAddress.getLocalHost().getHostAddress() + " "
				+ node.self_port);

		// String conversion
		String message = leave_message.toString();

		System.out.println(message);

		// Enumeration
		Enumeration<String> keys = routing_table.keys();

		while (keys.hasMoreElements()) {

			String entry = keys.nextElement();
			// System.out.println("Entry is : " + entry);

			/*
			 * //Converting to respective types to send send_receive class
			 * InetAddress ip = InetAddress.getByName(entry.toString().trim());
			 * 
			 * int port =
			 * Integer.parseInt(routing_table.get(entry).toString().trim());
			 * 
			 * //System.out.println(port + " " + ip);
			 */

			InetAddress ip = InetAddress.getByName(routing_table.get(entry)
					.toString().trim());

			int port = Integer.parseInt(entry.toString().trim());

			sendbuffer_leave = message.getBytes();

			// Sending the Join Message
			DatagramPacket leave_packet = new DatagramPacket(sendbuffer_leave,
					sendbuffer_leave.length, ip, port);

			node.clientside_socket.send(leave_packet);
			node.message_forwarded++;

			System.out.println("The Leave Request has been sent to " + ip
					+ "of port " + port);

		}
	}

	public static void query(Hashtable<String, String> routing_table)
			throws IOException, InterruptedException {

		BufferedReader reader = new BufferedReader(
				new FileReader("queries.txt"));

		// List<String> lines = new ArrayList<String>();

		PrintWriter fileWrite = new PrintWriter("results_20_" + node.self_port
				+ ".txt", "UTF-8");

		String line = null;

		// read until end of line
		int nodes = 20;

		for (nodes = 20; nodes > 0; nodes--) {

			if ((line = reader.readLine()) != null) {

				long starttime_ownnode = System.currentTimeMillis();

				System.out.println("--------------------------------------");
				System.out.println("Searching for " + line);
				line.trim();
				line = line.replaceAll(" ", "_");

				String result = process.search(line, node.self_port);

				if (result.trim().compareToIgnoreCase("0") != 0) {

					System.out
							.println("The Filename has been found in the Querying Node "
									+ result);
					fileWrite
							.println("The Filename has been found in the Querying Node "
									+ result);

					long stoptime_ownnode = System.currentTimeMillis();

					long total_time_ownnode = stoptime_ownnode
							- starttime_ownnode;

					System.out.println("Application level Hops: 0");
					System.out.println("The Query delay is "
							+ total_time_ownnode + "ms" + "\n");
					System.out
							.println("--------------------------------------");

					fileWrite.println("Application level Hops: 0");
					fileWrite.println("The Query delay is "
							+ total_time_ownnode + "ms");
					fileWrite
							.println("---------------------------------------------------------------- \n");

				}

				else {

					// Prepare the SER message

					// Preparing the packet
					StringBuilder packet = new StringBuilder();

					// Calculating length
					int length = 14
							+ node.self_port.length()
							+ InetAddress.getLocalHost().getHostAddress()
									.toString().length() + line.trim().length();

					// Creating SER message
					packet.append("00" + length + " " + "SER" + " "
							+ InetAddress.getLocalHost().getHostAddress() + " "
							+ node.self_port + " " + line.trim() + " " + "5");

					// Converting message to be sent to server
					String message = packet.toString();
					byte[] sendbuffer_search = new byte[1024];
					sendbuffer_search = message.getBytes();

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

						// Creating send packet
						DatagramPacket packet_to_be_sent = new DatagramPacket(
								sendbuffer_search, sendbuffer_search.length,
								forward_ip, forward_port);

						node.clientside_socket.send(packet_to_be_sent);
						node.message_forwarded++;

					}

					// Wait for SEROK

					byte[] recvbuffer = new byte[1024];

					// DatagramPacket variable created
					DatagramPacket serok_packet = new DatagramPacket(
							recvbuffer, recvbuffer.length);

					int serok_received = 7;
					int files_received = 0;
					int time_delay = 5;

					while (serok_received != 0) {

						node.receive_socket.receive(serok_packet);

						node.message_received++;
						// Convert it back to string
						String output = new String(serok_packet.getData(),
								serok_packet.getOffset(),
								serok_packet.getLength());

						output = output.replace("\n", "");
						String[] response = output.split(" ");

						message_check.mess_check(output, node.routing_table);

						if (Integer.parseInt(response[2].trim()) >= 1) {

							files_received++;
							System.out.println("The File " + line
									+ " has been found in " + response[3]);
							fileWrite.println("The File " + line
									+ " has been found in " + response[3]);

							long stoptime_frwdnode = System.currentTimeMillis();

							long total_time_frwdnode = stoptime_frwdnode
									- starttime_ownnode;

							System.out.println("Application level Hops: "
									+ response[5]);
							System.out.println("The Query delay is "
									+ total_time_frwdnode + "ms" + "\n");
							System.out
									.println("--------------------------------------");

							fileWrite.println("Application level Hops: "
									+ response[5]);
							fileWrite.println("The Query delay is "
									+ total_time_frwdnode + "ms");
							fileWrite
									.println("---------------------------------------------------------------- \n");

							if (files_received == 2)
								break;

						}

						else {

							System.out.println(output);

						}
						serok_received--;

					}

					if (files_received == 0) {

						System.out.println(line + " never found ");
						fileWrite.println(line + " never found ");
						long stoptime_nvrfound = System.currentTimeMillis();

						long total_time_nvrfound = stoptime_nvrfound
								- starttime_ownnode;
						System.out.println("Application level Hops: 3");
						System.out.println("The Query delay is "
								+ total_time_nvrfound + "ms \n");
						System.out
								.println("--------------------------------------");

						fileWrite.println("Application level Hops: 3");
						fileWrite.println("The Query delay is "
								+ total_time_nvrfound + "ms");
						fileWrite
								.println("---------------------------------------------------------------- \n");

					}

					node.receive_socket.close();
					TimeUnit.SECONDS.sleep(time_delay);
					node.receive_socket = new DatagramSocket(
							Integer.parseInt(node.self_port.trim()));
					;
				}

				// line = reader.readLine();

				if (nodes == 1)
					break;
			}

		}
		// Close the buffered reader object
		reader.close();
		fileWrite.close();

	}

}