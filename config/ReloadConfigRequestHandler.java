import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.lang.ProcessBuilder;

public class ReloadConfigRequestHandler implements Runnable {
	
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

        // receive a reload message with username and its length from client
        try {

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
		if (reloadSuccessFlag && username != null && 
			(usingUser.equals(UNDEFINED) || usingUser.equals(username))) {
			
			System.out.println("Serving " + username + "...");
			usingUser = username;
			
			final ProcessBuilder builder = new ProcessBuilder();
			builder.command("python", configGenerationScript, username);
			try {
				
				final Process process = builder.start();
				final int exitVal = process.waitFor();
				
				if (exitVal == 0) {
					
					reloadSuccessFlag = true;
					
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
			
		} else {
			
			// send confirmation message back to client
			try {
					
				outputToClient.writeBoolean(reloadSuccessFlag);
				outputToClient.flush();
				System.out.println("Reloaded config.js for " + username + "!");
				
			} catch (IOException e) {

				e.printStackTrace();

			}
			
		}
		
    }

}
