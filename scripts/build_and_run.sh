set -euo pipefail
script_dirpath="$(cd "$(dirname "${0}")" && pwd)"
root_dirpath="$(dirname "${script_dirpath}")"
build_dirpath="${root_dirpath}/build"
cli_filepath="${build_dirpath}/cli"

go build -o "${cli_filepath}" "${root_dirpath}/main.go"
"${cli_filepath}" \
    --gov-constants "${root_dirpath}/gov-constants.yml" \
    --scenarios "${root_dirpath}/scenarios.yml" \
    `# In Bash, this is how you feed arguments exactly as-is to a child script (since ${*} loses quoting and ${@} trips set -e if no arguments are passed)` \
    `# It basically says, "if and only if ${1} exists, evaluate ${@}"` \
    ${1+"${@}"} \

