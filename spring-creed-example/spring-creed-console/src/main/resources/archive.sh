@! /bin/bash

archivePath=$1
branchRepo=$2

echo "archivePath is $archivePath and branchRepo is $branchRepo"

git --version 2>&1 >/dev/null # improvement by tripleee
GIT_IS_AVAILABLE=$?

if [ $GIT_IS_AVAILABLE -eq 0 ]; then #
  echo "git installed"
else
  echo "going to install git"
  sudo apt update
  sudo apt install git
fi

if [[ -d $archivePath ]]; then #
  echo "Directory Exists"
else
  mkdir -p $archivePath
fi

cd $archivePath
git clone $branchRepo .
tar -czvf archive.tar ./*  --remove-files

