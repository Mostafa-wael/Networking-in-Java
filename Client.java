import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Client {

	// defineing the ports to be used
	public static final int TRANSPOSE_PORT = 6666;
	public static final int DETERMINATE_PORT = 6667;

	public static void main(String[] args) throws UnknownHostException, IOException {
		// selcting the required port
		System.out.println("What is the type of service you want?: (1) TRANSPOSE (other) DETERMINATE");
		Scanner scanner = new Scanner(System.in);
		int port = (scanner.nextInt()) == 1 ? TRANSPOSE_PORT : DETERMINATE_PORT;
		Socket s = new Socket("localhost", port);

		// buffers to communicate with the server
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader socketReader = new BufferedReader(new InputStreamReader(s.getInputStream()));

		// communication with the service
		System.out.println("I am client #" + socketReader.readLine());
		String message = null;
		List<String> matrix;
		//
		while (true) {
			System.out.println("Enter a square matrix sperated by spaces or . to quit");
			// check the message
			if ((message = consoleReader.readLine()) == null || message.equals("."))
				break;

			// send the message to the server
			out.println(message.toString());

			// format and print the message
			matrix = Arrays.asList(message.replace(" ", ",").split(","));
			System.out.println("The input matrix is:");
			printSqMatrix(matrix);
			
			// sleep for a random amount of time...zzz
			try {
				Thread.sleep((new Random()).nextInt(10000));
			} catch (InterruptedException e) {
			}

			// recieve the message from the server
			String receivedMessage = socketReader.readLine();

			// deal with the message according to the specified port
			if (port == TRANSPOSE_PORT) {
				System.out.println("The transpose of the matrix is: \n");
				matrix = Arrays.asList(receivedMessage.split(","));
				printSqMatrix(matrix);
			} else if (port == DETERMINATE_PORT) {
				System.out.println("The determinant  = " + receivedMessage + "\n");
			}
		}
		// close the connection
		System.out.println("I am exiting now, bye.");
		scanner.close();
		out.close();
		consoleReader.close();
		s.close();

	}

	// print the matrix in a matrix fromat
	public static void printSqMatrix(List<String> matrix) {
		int size = matrix.size(); // it should be square
		size = (int) Math.sqrt(size);
		System.out.println("The matrix size is " + size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(matrix.get(j + i * size) + " ");
			}
			System.out.print("\n");
		}
	}

}
