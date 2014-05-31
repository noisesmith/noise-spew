VERSION=1.0-SNAPSHOT
TOPDIR=src/main/java/org/noisesmith/
SPEW=${TOPDIR}/noisespew/
GEN=${TOPDIR}/noisegenerator/
SRCDIRS=${SPEW}*.java ${SPEW}/commands/*.java ${GEN}*.java ${GEN}ugens/*.java
TARGET=target/uber-noise-spew-${VERSION}.jar
MAIN=org.noisesmith.noisespew.NoiseSpew
MAVEN=mvn

default: ${TARGET}
run: ${TARGET}
	java -jar ${TARGET}

${TARGET}: ${SRCDIRS} pom.xml
	${MAVEN} -q package

clean:
	rm -f ${TARGET}

META=org.apache.maven.plugins
plugindeps:
	${MAVEN} ${META}:maven-dependency-plugin:2.6:resolve-plugins
