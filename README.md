Wealthdraft
===========
A modelling CLI for that uses user-defined gov constants to predict what your income and taxes will look like for various scenarios. Government constants and scenarios are defined via YAML, with schema as seen in `example-gov-constants.yml` and `example-scenarios.yml`.

Usage
-----
### Development
For easy development, the CLI can be run with the `scripts/build_and_run.sh` script, where any arguments to the script are passed to the CLI. With no arguments, the scenario in `example-scenarios.yml` will be run with the gov constants defined in `example-gov-constants.yml`. To use your own scenarios or gov constants, run `build_and_run.sh --help` to see the flags you'll need for running your own files.

### Building a binary
Run `./gradlew jar` to create a fat JAR that can be transferred to other machines and run with `java -jar wealthdraft.jar`
