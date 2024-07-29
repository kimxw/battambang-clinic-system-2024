#!/bin/bash

targetDir="$HOME/Battambang_Clinic_System"

if [ -d "$targetDir" ]; then
  javaFxLib=""
  if [[ $HOSTTYPE == "arm64" ]]; then
    # echo "Setting Environment for macOS ARM arch"
    javaFxLib="sdk/arm64/javafx-sdk-21.0.3/lib"
  elif [[ $HOSTTYPE == "x86_64" ]]; then
    # echo "Setting Environment for macOS Intel arch"
    javaFxLib="sdk/x64/javafx-sdk-21.0.3/lib"
  else
    echo "Unsupported Architecture $HOSTTYPE"
    echo "Unable to launch Battambang CLinic System Application"
    return 1 2>/dev/null
    exit 1
  fi

  echo "Clinic system is setup in $targetDir location"
  echo "Running Battambang CLinic System ..."

  jarFile="$targetDir/app/battambang-cms.jar"

  cmd="java --module-path $targetDir/$javaFxLib --add-modules javafx.controls,javafx.fxml  -jar $jarFile"
  echo "Executing command $cmd"
  $cmd &

else
  echo "Battambang Clinic System is not installed."
  echo "Please execute install.sh script present in the zip file."
  echo ""
fi
