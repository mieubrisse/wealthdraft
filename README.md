Wealthdraft
===========
A modelling CLI for that uses user-defined gov constants to predict what your income and taxes will look like for various scenarios. Government constants and scenarios are defined via YAML, with schema as seen in `gov-constants-example.yml` and `scenarios-example.yml`.

Usage
-----
### Development
For easy development, the CLI can be run with the `scripts/build_and_run.sh` script, where any arguments to the script are passed to the CLI. With no arguments, the scenario in `scenarios-example.yml` will be run with the gov constants defined in `gov-constants-example.yml`. To use your own scenarios or gov constants, run `build_and_run.sh --help` to see the flags you'll need for running your own files.

### Building a binary
Run `./gradlew jar` to create a fat JAR that can be transferred to other machines. This fat JAR can be executed with `java -jar wealthdraft.jar --help` to see usage information.
