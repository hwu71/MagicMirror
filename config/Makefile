all: ReloadConfigRequestServer ReloadConfigRequestHandler

ReloadConfigRequestHandler: ReloadConfigRequestHandler.java
	javac ReloadConfigRequestHandler.java

ReloadConfigRequestServer: ReloadConfigRequestServer.java
	javac ReloadConfigRequestServer.java

clean:
	rm -rf *.class