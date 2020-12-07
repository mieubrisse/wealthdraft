set -euo pipefail
script_dirpath="$(cd "$(dirname "${0}")" && pwd)"
root_dirpath="$(dirname "${script_dirpath}")"
build_dirpath="${root_dirpath}/build"
cli_filepath="${build_dirpath}/cli"

extra_args="${1+"${@}"}"
"${root_dirpath}/gradlew" build run --args="--gov-constants gov-constants-example.yml --scenarios scenarios-example.yml --assets assets-example.yml --projections projections-example.yml ${extra_args}"
