TOPDIR=src/main/java/org/noisesmith/
SRCDIRS=${TOPDIR}noisespew/*.java ${TOPDIR}noisespew/commands/*.java ${TOPDIR}noisegenerator/*.java
TARGET=target/uber-noise-spew-1.0-SNAPSHOT.jar
MAIN=org.noisesmith.noisespew.NoiseSpew
SF1=/home/justin/big/music/justin\ smith/ih/026/AUDIO/AUDIO001.WAV
MAVEN=mvn

default: ${TARGET}
run: ${TARGET}
	java -jar ${TARGET}

${TARGET}: ${SRCDIRS} pom.xml
	${MAVEN} -q package
clean:
	rm -f ${TARGET}
plugindeps:
	${MAVEN} org.apache.maven.plugins:maven-dependency-plugin:2.6:resolve-plugins
