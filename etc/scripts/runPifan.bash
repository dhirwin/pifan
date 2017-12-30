#!/bin/bash

. /opt/pifan/etc/scripts/runenv.bash

# ===================================================
# Edit the parameters below to configure the Java
# service.
# ===================================================

# ---------------------------------------------------
# Logging related
# ---------------------------------------------------

# Define the log4j logs directory
#
LOG4J_LOG_FILE=pifan.log

# ---------------------------------------------------
# Configuration related
# ---------------------------------------------------

# Define the JVM ID
#
JVM_ID=pifan

# Define the JVM start and max memory allocation
#
JVM_MEM_START=64m
JVM_MEM_MAX=256m

# Define the JVM network ports
#
JVM_PORT_DEBUG=12030
JVM_PORT_JMX=11030

# Define the name of the service configuration file
#
APP_CFG=jvm_pifan.cfg

# ===================================================
# You shouldn't need to modify anything below this!
# ===================================================

JVM_ARGS="-Dtype=${JVM_ID} -server -Xms${JVM_MEM_START} -Xmx${JVM_MEM_MAX} -Dlog.dir=${LOGS_DIR} -Dlog.filename=$LOG4J_LOG_FILE -DjvmId=$JVM_ID -Dcom.sun.management.jmxremote.port=${JVM_PORT_JMX} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" 

APP_ARGS=""

DEBUG_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,address=${JVM_PORT_DEBUG},server=y,suspend=n"

# -----------------------------------------------------------
# Create this JVM's working directory if it doesn't already exist
# -----------------------------------------------------------

WORK_JVM_DIR=${WORK_ROOT_DIR}/${JVM_ID}
mkdir -p ${WORK_JVM_DIR}

# -----------------------------------------------------------
# Copy all of the application built jars to this JVM's working directory. Only copy the jar if
# the source file is newer than the destination file.
# -----------------------------------------------------------

echo "Copying jars to work directory ${WORK_JVM_DIR}"

for jarFile in `ls ${LIB_APP_DIR}/*.jar`; do
    inExclusionList=false

    for i in "${JAR_EXCLUSION_LIST[@]}"
    do     
       if [ $LIB_APP_DIR/$i = $jarFile ]
       then
           inExclusionList=true 
       fi
    done
    
    if $inExclusionList; then
       echo "Not copying '${jarFile}' jar file to JVM's work directory"
    else
       cp -u $jarFile ${WORK_JVM_DIR}
    fi
done

# -----------------------------------------------------------
# Add all of the application jar files to the library path
# -----------------------------------------------------------

for i in `ls ${WORK_JVM_DIR}/*.jar`; do
    APPLIBPATH=$i:$APPLIBPATH
done

# -----------------------------------------------------------
# Add all of the COTS Jar files to the library path
# -----------------------------------------------------------

for i in `ls ${LIB_COTS_DIR}/*.jar`; do
    COTSLIBPATH=$i:$COTSLIBPATH
done

# -----------------------------------------------------------
# Export the combination of the library and ATMS path as the classpath
# -----------------------------------------------------------

export CLASSPATH=.:$APPLIBPATH:$COTSLIBPATH:$CONFIG_DIR

echo ============================================================
echo Starting pifan application
echo ------------------------------------------------------------
echo JAVA_EXE:           $JAVA_EXE
echo ------------------------------------------------------------
echo JVM_ID              $JVM_ID
echo CONFIG_DIR          $CONFIG_DIR
echo ------------------------------------------------------------
echo JVM_ARGS:           $JVM_ARGS
echo APP_ARGS:           $APP_ARGS
echo DEBUG_ARGS:         $DEBUG_ARGS
echo ============================================================

# Change to the ATMS root directory
cd ${ATMS_BASE}

# Run the Java command
#
#nohup $JAVA_EXE $JVM_ARGS $ATMS_ARGS $DEBUG_ARGS org.pifan.PiFanApp $APP_CFG > /dev/null &
$JAVA_EXE $JVM_ARGS $ATMS_ARGS $DEBUG_ARGS org.pifan.PiFanApp $APP_CFG
