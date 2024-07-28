#!/bin/bash

targetDir="$HOME/Battambang_Clinic_System"
sigDir="$targetDir/sig"
sigFile="$sigDir/v2.sig"

if [ ! -f "$sigFile" ]; then

  rm -rf "$targetDir/app"
  rm -rf "$targetDir/sdk"
  rm -f "$targetDir/command.txt"
  rm -f "$targetDir/run.sh"
  rm -f "$targetDir/install.sh"

  if [ -f "$targetDir" ]; then
    echo "A file exists at $targetDir location"
    mv "$targetDir" "$targetDir.bak"
  fi

  echo "Setting up Clinic system at $targetDir location"
  parentDir="$(dirname "$0")"
  rootDir="$(dirname "$parentDir")"
    
  if [ ! -d "$targetDir" ]; then
    mkdir "$targetDir"
  fi
  
  echo "Copying files from $rootDir/Battambang_Clinic_System to $targetDir ..."
  cp -r "$rootDir/Battambang_Clinic_System/app"  $targetDir/app
  cp -r "$rootDir/Battambang_Clinic_System/sdk"  $targetDir/sdk
  cp -f "$rootDir/Battambang_Clinic_System/run.sh"  $targetDir/.
  cp -f "$rootDir/Battambang_Clinic_System/install.sh"  $targetDir/.

  if [ ! -d "$sigDir" ]; then
    mkdir "$sigDir"
  fi
  
  echo "Setup done for v2" >> $sigFile
fi

bash "$targetDir/run.sh"
