#! /bin/bash

ulimit -n 1024

# Define the application base directory
#
export APP_BASE=/opt/pifan

# ===================================================
# Edit the parameters below to configure the Java
# service.
# ===================================================

# Define the Java executable
#
export JAVA_HOME=/usr
JAVA_EXE=$JAVA_HOME/bin/java

# ---------------------------------------------------
# Directory related
# ---------------------------------------------------

# Define the configuration directory
#
CONFIG_DIR=${APP_BASE}/etc/
LIB_APP_DIR=${APP_BASE}/lib/app
LIB_COTS_DIR=${APP_BASE}/lib/cots
LIB_NATIVE_DIR=${APP_BASE}/lib/native
LOGS_DIR=${APP_BASE}/logs
WORK_ROOT_DIR=${APP_BASE}/work
ROOT_BACKUP_DIR=${APP_BASE}/backups/

# Tomcat server jars
#
TOMCAT_COTS_DIR=tomcat/tomcat-8.5.23

# ---------------------------------------------------
# Jar file exclusion related
# ---------------------------------------------------

JAR_EXCLUSION_LIST=()