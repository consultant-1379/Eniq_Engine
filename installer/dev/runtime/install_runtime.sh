#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ Runtime installation script
#
# Usage: install_runtime.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB LM Ericsson Oy  All rights reserved.
# ---------------------------------------------------------------------

############## THE SCRIPT BEGINS HERE ##############

VERBOSE=0

if [ "$1" = "-v" ] ; then
  VERBOSE=1
fi

if [ -z "${CONF_DIR}" ] ; then
  echo "ERROR: CONF_DIR is not set"
  exit 1
fi

if [ ! -r "${CONF_DIR}/niq.rc" ] ; then
  echo "ERROR: Source file is not readable at ${CONF_DIR}/niq.rc"
  exit 2
fi

. ${CONF_DIR}/niq.rc

if [ ! -d "${LOG_DIR}/platform_installer" ] ; then
  mkdir ${LOG_DIR}/platform_installer
fi

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOG_FILE=${LOG_DIR}/platform_installer/runtime_${TIMESTAMP}.log

currdir=`pwd`

# ---------------------------------------------------------------------
# JAVA installation
# ---------------------------------------------------------------------
PROSTYPE=`uname -p`

if [ -h ${RT_DIR}/java ] ; then
  if [ $VERBOSE = 1 ] ; then
    echo "removing old java..." | tee -a ${LOG_FILE}
  fi

  rm -f ${RT_DIR}/java | tee -a ${LOG_FILE}
  rm -rf ${RT_DIR}/jre* | tee -a ${LOG_FILE}

  if [ $VERBOSE = 1 ] ; then
    echo "old java removed..." | tee -a ${LOG_FILE}
  fi

fi

if [ $VERBOSE = 1 ] ; then
  echo "installing java..." | tee -a ${LOG_FILE}
fi

cd jre

  if [ ${PROSTYPE} = "sparc" ]
  then

    cd sparc

  elif [ ${PROSTYPE} = "i386" ]
  then

    cd i386

  fi

JAVA_TMP=`ls jre*`

cp ${JAVA_TMP} ${RT_DIR} | tee -a ${LOG_FILE}

cd ${RT_DIR}

if [ $VERBOSE = 1 ] ; then
  echo "Extracting java..." | tee -a ${LOG_FILE}
fi

gunzip ${JAVA_TMP} | tee -a ${LOG_FILE}
JAVA_TMP=`basename ${JAVA_TMP} .gz`

if [ $VERBOSE = 1 ] ; then
  tar xvf ${JAVA_TMP} | tee -a ${LOG_FILE}
else
  tar xf ${JAVA_TMP} | tee -a ${LOG_FILE}
fi
  
rm ${JAVA_TMP} | tee -a ${LOG_FILE}

JAVA_DIR=`basename ${JAVA_TMP} .tar`

if [ $VERBOSE = 1 ] ; then
  echo "Linking java..." | tee -a ${LOG_FILE}
fi

if [ -e java ] ; then
  rm java | tee -a ${LOG_FILE}
fi

ln -s ${JAVA_DIR} java | tee -a ${LOG_FILE}

if [ $VERBOSE = 1 ] ; then
  echo "Java is installed." | tee -a ${LOG_FILE}
fi

if [ $VERBOSE = 1 ] ; then
  echo "Setting exec permissions to Java." | tee -a ${LOG_FILE}
fi

chmod 750 ${RT_DIR}/java/bin/*

if [ $VERBOSE = 1 ] ; then
  echo "Exec permissions to Java set." | tee -a ${LOG_FILE}
fi


cd $currdir

# ---------------------------------------------------------------------
# ANT installation
# ---------------------------------------------------------------------

if [ -h ${RT_DIR}/ant ] ; then
  if [ $VERBOSE = 1 ] ; then
    echo "removing old ant..." | tee -a ${LOG_FILE}
  fi

  rm -f ${RT_DIR}/ant | tee -a ${LOG_FILE}
  rm -rf ${RT_DIR}/apache-ant* | tee -a ${LOG_FILE}

  if [ $VERBOSE = 1 ] ; then
    echo "old ant removed..." | tee -a ${LOG_FILE}
  fi

fi

if [ $VERBOSE = 1 ] ; then
  echo "Installing ant..." | tee -a ${LOG_FILE}
fi

cd ant

ANT_TMP=`ls apache-ant*`

cp ${ANT_TMP} ${RT_DIR} | tee -a ${LOG_FILE}

cd ${RT_DIR}

if [ $VERBOSE = 1 ] ; then
  echo "Extracting ant..." | tee -a ${LOG_FILE}
fi

gunzip ${ANT_TMP} | tee -a ${LOG_FILE}
ANT_TMP=`basename ${ANT_TMP} .gz`

if [ $VERBOSE = 1 ] ; then
  tar xvf ${ANT_TMP} | tee -a ${LOG_FILE}
else
  tar xf ${ANT_TMP} | tee -a ${LOG_FILE}
fi

rm ${ANT_TMP}

ANT_DIR=`basename ${ANT_TMP} .tar`

if [ -e ant ] ; then
  rm ant | tee -a ${LOG_FILE}
fi

if [ $VERBOSE = 1 ] ; then
  echo "Linking ant..." | tee -a ${LOG_FILE}
fi

ln -s ${ANT_DIR} ant | tee -a ${LOG_FILE}

if [ $VERBOSE = 1 ] ; then
  echo "Ant is installed." | tee -a ${LOG_FILE}
fi

cd $currdir

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------

if [ $VERBOSE = 1 ] ; then
  echo "Updating version database..." | tee -a ${LOG_FILE}
fi

TVER=`cat install/version.properties | grep module.version`
TBLD=`cat install/version.properties | grep module.build`

VER=${TVER##*=}
BLD=${TBLD##*=}

VTAG="module.runtime=${VER}b${BLD}"

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then

  echo "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  chmod 640 ${INSTALLER_DIR}/versiondb.properties

else

  OLD=`cat ${INSTALLER_DIR}/versiondb.properties | grep module.runtime`

  if [ -z "${OLD}" ] ; then
    echo "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    cp ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    rm ${INSTALLER_DIR}/versiondb.properties.tmp
  fi

fi

if [ $VERBOSE = 1 ] ; then
  echo "Runtime is installed" | tee -a ${LOG_FILE}
fi

exit 0
