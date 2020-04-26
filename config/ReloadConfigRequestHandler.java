import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.lang.ProcessBuilder;

public class ReloadConfigRequestHandler implements Runnable {
	
	// trigger message header
	private static final int INVALID_HEADER = -1;
	private static final int MODULE_UPDATE = 1;
	private static final int LOGIN = 2;
	
	// indicate no user at cold start
	private static final String UNDEFINED = "undefined";

	// config file generation script
	private static final String configGenerationScript = "generate_config.py";
	
	// the user that is using the smart mirror
	private static volatile String usingUser = UNDEFINED;

    // connection to app side client
    private final Socket socket;
	private BufferedReader inputFromClient;
    private DataOutputStream outputToClient;

    ReloadConfigRequestHandler(final Socket socket) {

        this.socket = socket;

        try {

			inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
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
		boolean reloadSuccessFlag = false;
		int header = INVALID_HEADER;

        // receive a reload message with header, username, and its length from client
        try {

			header = Integer.valueOf(inputFromClient.readLine());
			final int usernameLength = Integer.valueOf(
				inputFromClient.readLine());
			
			final char[] ch = new char[1024];
            int lenTotal = 0, len = 0;
            while (lenTotal < usernameLength && len != -1) {

				len = inputFromClient.read(ch, lenTotal, usernameLength);
                if (len != -1)
                    lenTotal += len;

            }
            username = new String(ch, 0, usernameLength);
			reloadSuccessFlag = true;

        } catch (IOException e) {

            e.printStackTrace();
			reloadSuccessFlag = false;

        }

        // run a process to trigger config.js generation shell script with given username
		if (reloadSuccessFlag && username != null && (header == LOGIN || 
			(header == MODULE_UPDATE && usingUser.equals(username)))) {
			
			System.out.println("Serving " + username + "...");
			usingUser = username;
			
			final ProcessBuilder builder = new ProcessBuilder();
			builder.command("python", configGenerationScript, username);
			try {
				
				final Process process = builder.start();
				final int exitVal = process.waitFor();
				
				if (exitVal == 0) {
					
					reloadSuccessFlag = true;
					System.out.println("Reloaded config.js for " + username + "!");
					
				} else {
					
					reloadSuccessFlag = false;
					
				}
				
			} catch (IOException | InterruptedException e) {
				
				e.printStackTrace();
				reloadSuccessFlag = false;

			}
			
		} else {
			
			reloadSuccessFlag = false;
			
		}
		
		if (!reloadSuccessFlag) {
			
			System.out.println("Failed to reload config.js for " + username + "!");
			
		}
			
		// send confirmation message back to client
		try {
					
			outputToClient.writeBoolean(reloadSuccessFlag);
			outputToClient.flush();
				
		} catch (IOException e) {

			e.printStackTrace();

		}
		
    }

}
