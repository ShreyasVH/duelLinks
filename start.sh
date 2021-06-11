#!/usr/bin/env bash
sbt -jvm-debug "8003" -Dhttps.port="10003" "run 9003";