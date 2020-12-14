set -euo pipefail
script_dirpath="$(cd "$(dirname "${0}")" && pwd)"
root_dirpath="$(dirname "${script_dirpath}")"
build_dirpath="${root_dirpath}/build"
cli_filepath="${build_dirpath}/cli"

test_resources_dirpath="${root_dirpath}/src/test/resources/examples"

extra_args="${1+"${@}"}"
"${root_dirpath}/gradlew" build run --args=" \
    --gov-constants ${test_resources_dirpath}/gov-constants-example.yml \
    --scenarios ${test_resources_dirpath}/scenarios-example.yml \
    --assets ${test_resources_dirpath}/assets-example.yml \
    --projections ${test_resources_dirpath}/projections-example.yml \
    ${extra_args}"
