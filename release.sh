#!/bin/bash

RELEASE_VERSION=$1
DEVELOPMENT_VERSION=$2

MVN=/opt/maven/apache-maven-3.1.1/bin/mvn

usage(){
	echo "Usage: $0 release_version development_version"
}

run(){
	$MVN versions:set -DnewVersion="$RELEASE_VERSION" &&
	git commit -a -m "prepare to release v.$RELEASE_VERSION" &&
	git push &&
	$MVN clean install -Prelease &&
	$MVN versions:set -DnewVersion="$DEVELOPMENT_VERSION-SNAPSHOT" &&
	git commit -a -m "prepare to develop v.$DEVELOPMENT_VERSION-SNAPSHOT" &&
	git push &&
	find -name "*.versionsBackup"| xargs -I file rm file
}

if [[ -n "$RELEASE_VERSION" && -n "$DEVELOPMENT_VERSION" ]]; then
	run
else
	usage
	exit 1
fi
