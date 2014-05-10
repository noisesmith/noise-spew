SRCDIR=src/main/java/org/noisesmith/noisespew/
TARGET=target/noise-spew-1.0-SNAPSHOT.jar
MAIN=org.noisesmith.noisespew.NoiseSpew
SF1=/home/justin/big/music/justin\ smith/ih/026/AUDIO/AUDIO001.WAV
default: ${TARGET}
run: ${TARGET}
	java -cp ${TARGET} ${MAIN} ${SF1} ${SF1} ${SF1} ${SF1}

${TARGET}: ${SRCDIR}/NoiseSpew.java ${SRCDIR}/BiMap.java
	mvn package
clean:
	rm -f ${TARGET}
