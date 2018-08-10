#!/bin/bash

# has to be run from the /home/student/jb421/labs folder

mvn archetype:generate -B \
	-DarchetypeGroupId=org.apache.camel.archetypes \
	-DarchetypeArtifactId=camel-archetype-spring \
	-DarchetypeVersion=2.17.0.redhat-630187 \
	-DgroupId=com.redhat.training.jb421 \
	-DartifactId=fuse-mgmt

