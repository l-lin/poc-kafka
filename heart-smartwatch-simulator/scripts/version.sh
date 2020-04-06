#!/bin/bash
# Script used to compute the version of the agent by using git tags.
#
# If the env variable VERSION is already set, then it will returns its value.
# Otherwise, it will return the tag version, or the next SNAPSHOT version.

set -e

function isTag() {
    return $(git rev-list --count $(git describe --abbrev=0 --tags)..HEAD)
}

if [[ -z ${VERSION} ]]; then
    if git describe --tags --abbrev=0 > /dev/null 2>&1; then
        version=$(git describe --tags --abbrev=0)
        if isTag; then
            echo "${version}"
        else
            semver=( ${version//./ } )
            major=${semver[0]}
            minor=${semver[1]}
            patch=$((semver[2] +1))
            echo "${major}.${minor}.${patch}-SNAPSHOT"
        fi
    else
      echo "0.0.0-SNAPSHOT"
    fi
else
    echo "${VERSION}"
fi

exit 0

