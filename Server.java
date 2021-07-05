import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Server {

	// defineing the ports to be used
	public static final int TRANSPOSE_PORT = 6666;
	public static final int DETERMINATE_PORT = 6667;

	public static int clientNumber = 0; // to keep track of the number of clients connecting to the server.

	public static void main(String[] args) throws IOException {
		System.out.println("The server started .. ");

		// Creating a new thread for every port to be able to handle multiple clients
		// thread for TRANSPOSE_PORT
		new Thread() {
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(TRANSPOSE_PORT);
					while (true) {
						new matrixOperations(ss.accept(), clientNumber++).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		// thread for DETERMINATE_PORT
		new Thread() {
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(DETERMINATE_PORT);
					while (true) {
						new matrixOperations(ss.accept(), clientNumber++).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static class matrixOperations extends Thread {
		Socket socket;
		int clientNum;

		// just a constructor
		public matrixOperations(Socket s, int clientNo) {
			this.socket = s;
			this.clientNum = clientNumber;
			System.out.println("Connection with Client #" + this.clientNum + " at socket " + socket);
		}

		public void run() {
			try {
				// buffers to communicate with the server
				BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);

				// Send a welcome message to the client.
				out.println("Hello, you are client #" + this.clientNum + ".");

				String message = null;
				List<String> matrix;
				int determinant;
				while (true) {
					// check the message
					if ((message = br.readLine()) == null || message.equals("."))
						break;
					// format and print the message
					matrix = Arrays.asList(message.replace(" ", ",").split(","));
					System.out.println("The input matrix is:");
					printSqMatrix(matrix);

					// deal with the message according to the specified port
					if (this.socket.getLocalPort() == DETERMINATE_PORT) {
						determinant = getDeterminant(matrix);
						out.println(determinant);
					} else if (this.socket.getLocalPort() == TRANSPOSE_PORT) {
						matrix = getTranspose(matrix);
						System.out.println("Server send transpose " + matrix + " to the client# " + this.clientNum);
						out.println(matrix.toString());
					}

					// send the message to the client
					out.println(message);
				}
				// close the connection
				out.close();
				br.close();
			} catch (IOException e) {
				System.out.println("Error handling client# " + this.clientNum + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with client# " + this.clientNum + " closed");
			}
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

		// calculate the determinant of the matrix
		public int getDeterminant(List<String> matrix) {

			int det = 0;
			if (matrix.size() == 4) {
				det = Integer.parseInt(matrix.get(0)) * Integer.parseInt(matrix.get(3))
						- Integer.parseInt(matrix.get(1)) * Integer.parseInt(matrix.get(2));
			} else if (matrix.size() == 9) {
				det = Integer.parseInt(matrix.get(0))
						* (Integer.parseInt(matrix.get(4)) * Integer.parseInt(matrix.get(8))
								- Integer.parseInt(matrix.get(5)) * Integer.parseInt(matrix.get(7)))
						- Integer.parseInt(matrix.get(1))
								* (Integer.parseInt(matrix.get(3)) * Integer.parseInt(matrix.get(8))
										- Integer.parseInt(matrix.get(5)) * Integer.parseInt(matrix.get(6)))
						+ Integer.parseInt(matrix.get(2))
								* (Integer.parseInt(matrix.get(3)) * Integer.parseInt(matrix.get(7))
										- Integer.parseInt(matrix.get(4)) * Integer.parseInt(matrix.get(6)));

			}
			return det;
		}

		// getting the transpose of the matrix
		public List<String> getTranspose(List<String> matrix) {
			int size = matrix.size(); // it should be square
			String[] transposeMatrix = new String[size]; // I wan't able to use a differnet List<String> as java assign
															// refernces
			size = (int) Math.sqrt(size);
			System.out.println("The transpose of the matrix is: \n");
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					transposeMatrix[i + j * size] = matrix.get(j + i * size);
					System.out.print(transposeMatrix[i + j * size] + " ");
				}
				System.out.print("\n");
			}
			return Arrays.asList(transposeMatrix);
		}
	}

}
