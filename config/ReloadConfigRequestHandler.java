import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReloadConfigRequestHandler implements Runnable {

    // connection to app side client
    private final Socket socket;
	private BufferedReader inputFromClient;

    ReloadConfigRequestHandler(final Socket socket) {

        this.socket = socket;

        try {

			inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        }
        catch (IOException e) {

            inputFromClient = null;
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

			final int usernameLength = Integer.valueOf(
				inputFromClient.readLine());
			
			////////////////////////////
			System.out.println("***usernameLength:" + usernameLength);
			////////////////////////////
			
            // final byte[] ch = new byte[1024];
			final char[] ch = new char[1024];//////////
            int lenTotal = 0, len = 0;
            while (lenTotal < usernameLength && len != -1) {

                // len = inputFromClient.read(ch, lenTotal, usernameLength);
				len = inputFromClient.read(ch, lenTotal, usernameLength);///////////////////
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
