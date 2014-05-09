SRCDIR=src/main/java/org/noisesmith/app/
TARGET=target/noise-spew-1.0-SNAPSHOT.jar
MAIN=org.noisesmith.app.NoiseSpew
SF1=/home/justin/big/music/justin\ smith/ih/026/AUDIO/AUDIO001.WAV
default: ${TARGET}
run: ${TARGET}
	java -cp ${TARGET} ${MAIN} ${SF1} ${SF1}

${TARGET}: ${SRCDIR}/NoiseSpew.java
	mvn package
clean:
	rm -f ${TARGET}
