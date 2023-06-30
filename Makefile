all:
	javac AServer.java
	javac Client.java
	javac FileServer.java
clean:
	rm -f *.class
	rm -f *_log.txt
	rm -rf client/*
fclean:
	rm -f *.class
	rm -f *_log.txt
	rm -rf client/*
	rm -f *.db