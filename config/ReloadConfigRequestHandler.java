import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReloadConfigRequestHandler implements Runnable {

    // connection to app side client
    private final Socket socket;
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    ReloadConfigRequestHandler(final Socket socket) {

        this.socket = socket;

        try {

            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());

        }
        catch (IOException e) {

            inputFromClient = null;
            outputToClient = null;
            e.printStackTrace();

        }

    }

    /**
     * Receive a reload message with username, generate config file for
	 * this user, and refresh smart mirror webpage
     */
    @Override
    public void run() {

        String username = null;

        // receive a reload message with username and its length from client
        try {

            final int usernameLength = inputFromClient.readInt();
			
			////////////////////////////
			System.out.println("***usernameLength:" + usernameLength);
			////////////////////////////
			
            final byte[] ch = new byte[1024];
            int lenTotal = 0, len = 0;
            while (lenTotal < usernameLength && len != -1) {

                len = inputFromClient.read(ch, lenTotal, usernameLength);
                if (len != -1)
                    lenTotal += len;

            }
            username = new String(ch, 0, usernameLength);

        } catch (IOException e) {

            e.printStackTrace();

        }
		
		
		////////////////////////////
		System.out.println("***username:" + username);
		////////////////////////////

        // TODO: trigger config.js generation shell script with given username;
        //       only trigger if username is not null
		System.out.println("Pretending running config generation...");

    }

}
