# Cultures Multitool

This program is a multitool for the game series [Cultures 2](https://de.wikipedia.org/wiki/Cultures). It allows decoding and viewing various game files. Just pass it your `DataX/Libs/data0001.lib` and you are off to the races.

# Building
To build this project, you need [Maven](https://maven.apache.org/) and [Java 11](https://adoptopenjdk.net/releases.html?variant=openjdk11&jvmVariant=hotspot) installed. Run `mvn package -Ppackage` to build a self-contained executable. It will be placed into `./package/target/jlink-image` with the run script `bin/CulturesMultitool`.
