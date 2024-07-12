set -euo pipefail
script_dirpath="$(cd "$(dirname "${0}")" && pwd)"
root_dirpath="$(dirname "${script_dirpath}")"

project_mount_dirpath="/home/gradle/project"
test_resources_dirpath="${project_mount_dirpath}/src/test/resources/examples"

extra_args="${1+"${@}"}"
docker run --rm \
    -u gradle \
    -v "${root_dirpath}:${project_mount_dirpath}" \
    -w "${project_mount_dirpath}" \
    gradle:6-jdk11-focal \
    bash ${project_mount_dirpath}/scripts/build_and_run.sh "${extra_args}"
