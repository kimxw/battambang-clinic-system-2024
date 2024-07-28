#!/bin/bash

targetDir="$HOME/Battambang_Clinic_System"

if [ -d "$targetDir" ]; then
  javaFxLib=""
  if [[ $HOSTTYPE == "arm64" ]]; then
    # echo "Setting Environment for macOS ARM arch"
    javaFxLib="sdk/arm64/javafx-sdk-21.0.3/lib"
  elif [[ $HOSTTYPE == "x86_64" ]]; then
    # echo "Setting Environment for macOS Intel arch"
    javaFxLib="sdk/arm64/javafx-sdk-21.0.3/lib"
  else
    echo "Unsupported Architecture $HOSTTYPE"
    echo "Unable to launch Battambang CLinic System Application"
    return 1 2>/dev/null
    exit 1
  fi

  echo "Clinic system is setup in $targetDir location"
  echo "Running Battambang CLinic System ..."

  jarFile="$targetDir/app/battambang-cms-v1.jar"

  cmd="java --module-path $targetDir/$javaFxLib --add-modules javafx.controls,javafx.fxml  -jar $jarFile"
  echo "Executing command $cmd"
  $cmd &

else
  if [ -f "$targetDir" ]; then
    echo "A file exists at $targetDir location"
    mv "$targetDir" "$targetDir.bak"
  fi

  echo "Setting up Clinic system at $targetDir location"
  parentDir="$(dirname "$0")"
  rootDir="$(dirname "$parentDir")"
  echo "Copying files from $rootDir/Battambang_Clinic_System to $targetDir ..."
  cp -r "$rootDir/Battambang_Clinic_System"  $targetDir

  bash "$targetDir/run.sh"
fi
