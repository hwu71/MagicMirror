# Compile
```sh
make
```
# Execute
```sh
java ReloadConfigRequestServer
```
* Please use Ctrl+C to interrupt the program, so that Server could shut down properly (otherwise, the port that the server socket binded to might not be freed)

# Description 
* "ReloadConfigRequestServer.java" implements a config reloading request server that listen to port 2540
* "ReloadConfigRequestHandler.java" implements a thread that serves a client
* "generate_config.py" is a configuration file generator that reads configuration data from the Cloud Database and generates a "config.js" file for the given user