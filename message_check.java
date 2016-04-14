import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

public class message_check {

	public static void mess_check(String output,
			Hashtable<String, String> routing_table) throws IOException {

		// Variables of type byte created
		byte[] sendbuffer = new byte[10024];

		// To remove last character
		output = output.replace("\n", "").trim();
		String[] response = output.split(" ");

		switch (response[1]) {

		case "JOIN": {
			// Logic to split based on bootstrap output

			node.message_received++;
			String join_ip = response[2].trim();
			String join_port = response[3].trim();

			InetAddress source_ip = InetAddress.getByName(join_ip);

			// Updating routing table
			routing_table.put(join_port, join_ip);

			StringBuilder packet = new StringBuilder();

			// Making the packet ready for transmission to server
			packet.append("0014").append(" ");
			packet.append("JOINOK").append(" ");
			packet.append("0");

			String message = packet.toString();

			// Converting message to be sent to server
			sendbuffer = message.getBytes();

			// Creating send packet
			DatagramPacket packet_to_be_sent = new DatagramPacket(sendbuffer,
					sendbuffer.length, source_ip, Integer.parseInt(join_port));

			node.clientside_socket.send(packet_to_be_sent);
			node.message_forwarded++;
			break;

		}

		case "JOINOK": {
			// Printing the JoinOK message
			node.message_received++;

			System.out.println("Join has been Successful \n" + output);

			break;

		}

		case "LEAVE": {
			// Extracting IP address and Port
			String leave_name = response[2].trim();
			String leave_port = response[3].trim();

			node.message_received++;
			InetAddress leave_ip = InetAddress.getByName(leave_name);
			// Enumeration
			Enumeration<String> keys_leave = routing_table.keys();

			while (keys_leave.hasMoreElements()) {

				// Extracting the key of next element
				String compare_leave = (String) keys_leave.nextElement();

				System.out.println(routing_table);

				// Check and Remove the Leave IP
				if (compare_leave.trim().compareToIgnoreCase(leave_port.trim()) == 0) {
					routing_table.remove(compare_leave);

					StringBuilder packet_leave = new StringBuilder();

					// Making the packet ready for transmission to server
					packet_leave.append("0015").append(" ");
					packet_leave.append("LEAVEOK").append(" ");
					packet_leave.append("0");

					String message_leave = packet_leave.toString();

					// Converting message to be sent to server
					sendbuffer = message_leave.getBytes();

					// Creating send packet
					DatagramPacket packet_to_be_sent_leave = new DatagramPacket(
							sendbuffer, sendbuffer.length, leave_ip,
							Integer.parseInt(leave_port));

					node.clientside_socket.send(packet_to_be_sent_leave);
					node.message_forwarded++;
					System.out.println(message_leave);

				}

			}

			break;
		}

		case "LEAVEOK": {

			// Printing the LEAVE message

			node.message_received++;
			System.out.println("Node has Successfully left the Network \n"
					+ output);

			break;

		}

		case "SER": {

			// Declaring local variables
			String source_name = response[2].trim();
			int source_port = Integer.parseInt(response[3].trim());
			String moviename = response[4].trim();
			String hops = response[5].trim();

			// Decrementing the hop count
			int hop_count = Integer.parseInt(hops.trim()) - 1;

			// Searching the node
			String result = process.search(moviename, node.self_port);

			// Incrementing the receive message
			node.packets_received++;
			node.message_received++;

			// Splitting chirags message
			String[] search_result = result.split(" ");

			if (search_result[0].trim().compareToIgnoreCase("0") != 0) {

				StringBuilder packet = new StringBuilder();

				node.packets_answered++;

				InetAddress source_ip = InetAddress.getByName(source_name);

				int length = 16
						+ node.self_port.length()
						+ InetAddress.getLocalHost().getHostAddress()
								.toString().length() + result.length();

				packet.append("00" + length + " " + "SEROK" + " "
						+ search_result[0] + " "
						+ InetAddress.getLocalHost().getHostAddress() + " "
						+ node.self_port + " " + hop_count + " ");

				for (int j = 0; j < Integer.parseInt(search_result[0]); j++) {

					packet.append(search_result[j + 1] + " ");
				}

				String message = packet.toString().trim();
				System.out.println(message);

				// Converting message to be sent to server
				sendbuffer = message.trim().getBytes();

				// Creating send packet
				DatagramPacket packet_to_be_sent = new DatagramPacket(
						sendbuffer, sendbuffer.length, source_ip, source_port);

				node.clientside_socket.send(packet_to_be_sent);
				node.message_forwarded++;
			}

			else {

				if (hop_count == 0) {

					StringBuilder packet = new StringBuilder();

					InetAddress source_ip = InetAddress.getByName(source_name);

					int length = 17
							+ node.self_port.length()
							+ InetAddress.getLocalHost().getHostAddress()
									.toString().length();

					packet.append("00" + length + " " + "SEROK" + " " + "0"
							+ " " + InetAddress.getLocalHost().getHostAddress()
							+ " " + node.self_port + " " + hop_count + " ");

					String message = packet.toString().trim();
					System.out.println(message);

					// Converting message to be sent to server
					sendbuffer = message.trim().getBytes();

					// Creating send packet
					DatagramPacket packet_to_be_sent = new DatagramPacket(
							sendbuffer, sendbuffer.length, source_ip,
							source_port);

					node.clientside_socket.send(packet_to_be_sent);
					node.message_forwarded++;
				}

				else {

					// Enumeration
					Enumeration<String> keys = routing_table.keys();

					while (keys.hasMoreElements()) {

						String entry = keys.nextElement();

						// messages forwarded
						node.packets_forwarded++;
						node.message_forwarded++;

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

						if (forward_port != source_port) {

							byte[] sendbuffer_join = new byte[1024];

							// Appending the decremented hop count to the SER
							// and forwarding

							// Preparing the packet
							StringBuilder packet = new StringBuilder();

							// Calculating length
							int length = 14 + source_name.trim().length()
									+ response[3].trim().length()
									+ moviename.trim().length();

							// Creating SER message
							packet.append("00" + length + " " + "SER" + " "
									+ source_name.trim() + " "
									+ response[3].trim() + " "
									+ moviename.trim() + " " + hop_count);

							// Converting message to be sent to server
							String message = packet.toString();

							sendbuffer_join = message.getBytes();

							// Sending the Join Message
							DatagramPacket join_packet = new DatagramPacket(
									sendbuffer_join, sendbuffer_join.length,
									forward_ip, forward_port);
							node.clientside_socket.send(join_packet);
							node.message_forwarded++;

						}
					}

				}
			}

			break;
		}

		case "UNREGOK": {

			// Printing the UnReg message

			node.message_received++;
			System.out
					.println("Node has Successfully Unregisterd from Bootstrap Server \n"
							+ output);

			break;
		}

		default:
			break;

		}

	}
}
