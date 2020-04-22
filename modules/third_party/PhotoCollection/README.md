# Compile
```sh
make
```
# Execute
```sh
java PhotoCollectionRequestServer
```
* Please use Ctrl+C to interrupt the program, so that Server could shut down properly (otherwise, the port that the server socket binded to might not be freed)

# Description 
* "PhotoCollectionRequestServer.java" implements a photo collection request server that listen to port 2540
* "PhotoCollectionRequestHandler.java" implements a thread that serves a client
