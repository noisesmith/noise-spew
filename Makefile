SRCDIR=src/main/java/org/noisesmith/app/
TARGET=target/noise-spew-1.0-SNAPSHOT.jar
MAIN=org.noisesmith.app.NoiseSpew

run: ${TARGET}
	java -cp ${TARGET} ${MAIN} 1 2 3

${TARGET}: ${SRCDIR}/NoiseSpew.java
	mvn package -e
