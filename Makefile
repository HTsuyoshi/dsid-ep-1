build:
	mkdir bin
	./gradlew client:jar
	mv ./client/build/libs/client-1.0.0.jar ./bin/
	./gradlew server:jar
	mv ./server/build/libs/server-1.0.0.jar ./bin/
