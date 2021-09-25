#!/usr/bin/env bash

VERSION="$1"

if [ -z "$VERSION" ]
then
  echo "You need to pass the version"
  echo "Example:"
  echo "    $0 1.0.0"
  exit 1
fi

sed -i -E "s/^version.*/version '$VERSION'/" build.gradle