import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PhotoCollectionAndRefreshRequestServer {

    private static final int portNum = 2540;

    public static void main(final String[] args) {

        try {

            // 1. Set up server
            final PhotoCollectionAndRefreshRequestServer server = new PhotoCollectionAndRefreshRequestServer();
            ServerSocket serverSocket = server.setupServer();
            server.registerShutdownHook(serverSocket);

            // 2. Handle requests
            while (true) {

                // 2.1 Wait for new connection from client
                Socket socket = server.acceptConnection(serverSocket);

                // 2.2 Create a new thread to handle the request
                PhotoCollectionAndRefreshRequestHandler handler = new PhotoCollectionAndRefreshRequestHandler(socket);
                new Thread(handler).start();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /**
     * Set up socket for this server
     *
     * @return             the created socket
     * @throws IOException if server setup failed or host is unknown
     */
    public ServerSocket setupServer() throws IOException {

        ServerSocket serverSocket;

        try {

            serverSocket = new ServerSocket(portNum);
            System.out.println("***Server is on!");

        } catch (IOException e) {

            System.err.println("ERROR: Set up server failed! (Port " + portNum +
                    " might be occupied)");
            throw e;

        }

        return serverSocket;

    }

    /**
     * Register shutdown shutdown hook triggered by Ctrl+C as interrupt signal
     *
     * @param serverSocket to which we would add a shutdown hook
     */
    public void registerShutdownHook(final ServerSocket serverSocket) {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {

                try {

                    serverSocket.close();
                    System.out.println("***Server is closed!");

                } catch (IOException e) {

                    System.err.println("ERROR: Shutdown server error!");

                }

            }
        });
    }

    /**
     * Accept connection from clients
     *
     * @param serverSocket the socket that is listening to clients
     * @return             the socket created to serve t a client
     * @throws IOException if socket connection interrupted
     */
    public Socket acceptConnection(final ServerSocket serverSocket) throws IOException {

        try {

            return serverSocket.accept();

        } catch (IOException e) {

            System.err.println("***Socket connection interrupted!");
            throw e;

        }
    }

}
