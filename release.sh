#!/bin/bash

DEVELOPMENT_VERSION=$1

MVN=/opt/maven/apache-maven-3.1.1/bin/mvn

usage(){
	echo "Usage: $0 next_development_version"
	echo
}

run(){
	RELEASE_VERSION=`grep -oP '(?<=<version>).*(?=</version>)' pom.xml|sed -n "1p"|grep -oE "[0-9\.]+"`
	echo "$RELEASE_VERSION"
	$MVN versions:set -DnewVersion="$RELEASE_VERSION" &&
	git commit -a -m "prepare to release v.$RELEASE_VERSION" &&
	git push &&
	$MVN clean install -Prelease &&
	$MVN versions:set -DnewVersion="$DEVELOPMENT_VERSION-SNAPSHOT" &&
	git commit -a -m "prepare to develop v.$DEVELOPMENT_VERSION-SNAPSHOT" &&
	git push &&
	find -name "*.versionsBackup"| xargs -I file rm file && find -name "*.releaseBackup"| xargs -I file rm file
}

if [[ -n "$DEVELOPMENT_VERSION" ]]; then
	run
else
	usage
	exit 1
fi
