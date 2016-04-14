import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class process {

	// Function that allocates 3 to 5 files randomly to a file at each node
	public static void file_allocation(String self_port) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(
				"inputfile.txt"));

		ArrayList<String> lines = new ArrayList<String>();

		for (int i = 0; i < 20; i++) {
			String line = reader.readLine();
			lines.add(line);

		}
		// Close the buffered reader object
		reader.close();

		Collections.shuffle(lines);

		Random random = new Random();

		// initialize 3 or 4 movie names to each node
		int num = random.nextInt(3) + 3;

		System.out.println("-----------------------------------------------");
		System.out.println("Allocating " + num + " Files to the node");
		System.out.println("The Files allocated to the node are as follows: ");
		System.out.println("-----------------------------------------------");

		for (int i = 0; i < num; i++) {

			// Choose a random line from the list
			String randomString = lines.get(i);
			System.out.println(randomString);

			// Create a new file to store the contents at each node
			File file = new File("node_contents_20_" + self_port + ".txt");

			// if file doesnt exists, then create it
			// if (!file.exists()) {
			file.createNewFile();
			// }

			// true = append file, create buffered write and file write objects
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(randomString + "\n");
			bufferWritter.close();
		}

		System.out
				.println("-------------------------------------------------\n");
	}

	public static String search(String moviename, String self_port) {

		// condition check for found or not found(If found = true)
		int i = 0;
		String result;
		StringBuilder packet = new StringBuilder();

		try {
			// Create object of buffered reader and file reader type
			BufferedReader buffer = new BufferedReader(new FileReader(
					"node_contents_20" + "_" + self_port + ".txt"));
			String line = null;

			// Read the contents till the last line
			while ((line = buffer.readLine()) != null) {
				// Check for the existence of the search pattern
				String query_lower = line.toLowerCase();

				if (line.compareToIgnoreCase(moviename) == 0
						|| query_lower.contains(moviename.toLowerCase())) {

					// replacing space with underscore
					line.trim();
					String underscore_string = line.replaceAll(" ", "_");

					packet.append(underscore_string).append(" ");

					// System.out.println("found it i say");
					// System.out.println(line);
					i++;
					// Send the details to the querying node
					// or else forward to some other node

				}

			}

			result = packet.insert(0, String.valueOf(i) + " ").toString();

		} catch (Exception e) {
			e.printStackTrace();

		}

		// Convert the packet to string
		result = packet.toString();
		// System.out.println(result);
		return result;
	}

}// last line
