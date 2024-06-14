#!/bin/sh
set -x
git status && mvn clean && git pull && mvn -B release:prepare && mvn release:perform -Darguments="-Dmaven.deploy.skip=true" && git pull  && git push && git push --tags && git status
