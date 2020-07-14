#!/usr/bin/env bash
dos2unix .env;
export $(xargs < .env);

sbt -jvm-debug "8003" -Dhttps.port="10003" "run 80";