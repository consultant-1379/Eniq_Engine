#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ Installer installation script
#
# Usage: install_installer.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2006 AB LM Ericsson Oy  All rights reserved.
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

. ${CONF_DIR}/niq.rc

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOGFILE=${LOG_DIR}/platform_installer/installer_${TIMESTAMP}.log

if [ -z "${INSTALLER_DIR}" ] ; then
  echo "ERROR: INSTALLER_DIR is not defined" | tee -a ${LOGFILE}
  exit 2
fi

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing platform_installer" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/platform_installer ] ; then
  rm -f ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}
fi
cp installer/platform_installer ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/platform_installer | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_platform_installer.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_platform_installer.xml ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_platform_installer.xml | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing tp_installer" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/tp_installer ] ; then
  rm -f ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}
fi
cp installer/tp_installer ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}
chmod 540 ${INSTALLER_DIR}/tp_installer | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_tp_installer.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_tp_installer.xml ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_tp_installer.xml | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_install_utils.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_install_utils.xml ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_install_utils.xml | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/tasks_extract_reportpacks.xml ] ; then
  rm -f ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}
fi
cp installer/tasks_extract_reportpacks.xml ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/tasks_extract_reportpacks.xml | tee -a ${LOGFILE}

if [ ! -d ${INSTALLER_DIR}/lib ] ; then
  mkdir ${INSTALLER_DIR}/lib | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/lib/installer.jar ] ; then
  rm -f ${INSTALLER_DIR}/lib/installer.jar | tee -a ${LOGFILE}
fi
cp lib/installer.jar ${INSTALLER_DIR}/lib/ | tee -a ${LOGFILE}
chmod 440 ${INSTALLER_DIR}/lib/installer.jar | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/extract_report_packages.bsh ] ; then
  rm -f ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}
fi
cp installer/extract_report_packages.bsh ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/extract_report_packages.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/activate_interface ] ; then
  rm -f ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}
fi
cp installer/activate_interface ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/activate_interface | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/deactivate_interface ] ; then
  rm -f ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}
fi
cp installer/deactivate_interface ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/deactivate_interface | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/installed_techpacks ] ; then
  rm -f ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}
fi
cp installer/installed_techpacks ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/installed_techpacks | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing snapshot_functions.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/snapshot_functions.bsh ] ; then
  rm -f ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}
fi
cp installer/snapshot_functions.bsh ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/snapshot_functions.bsh | tee -a ${LOGFILE}

if [ ${VERBOSE} = 1 ] ; then
  echo "Installing change_db_users_perm.bsh" | tee -a ${LOGFILE}
fi
if [ -f ${INSTALLER_DIR}/change_db_users_perm.bsh ] ; then
  rm -f ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}
fi
cp installer/change_db_users_perm.bsh ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/change_db_users_perm.bsh | tee -a ${LOGFILE}

if [ -f ${INSTALLER_DIR}/SGSN_migration_script.bsh ] ; then
  rm -f ${INSTALLER_DIR}/SGSN_migration_script.bsh | tee -a ${LOGFILE}
fi
cp installer/SGSN_migration_script.bsh ${INSTALLER_DIR}/SGSN_migration_script.bsh | tee -a ${LOGFILE}
chmod 550 ${INSTALLER_DIR}/SGSN_migration_script.bsh | tee -a ${LOGFILE}

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------

if [ ${VERBOSE} = 1 ] ; then
  echo "Updating version database..." | tee -a ${LOGFILE}
fi

TVER=`cat install/version.properties | grep module.version`
TBLD=`cat install/version.properties | grep module.build`

VER=${TVER##*=}
BLD=${TBLD##*=}

VTAG="module.installer=${VER}b${BLD}"

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then

  echo "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  chmod 640 ${INSTALLER_DIR}/versiondb.properties

else

  OLD=`cat ${INSTALLER_DIR}/versiondb.properties | grep module.installer`

  if [ -z "${OLD}" ] ; then
    echo "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    cp ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    rm ${INSTALLER_DIR}/versiondb.properties.tmp
  fi

fi


if [ $VERBOSE = 1 ] ; then
  echo "Installer installed" | tee -a ${LOGFILE}
fi

exit 0
