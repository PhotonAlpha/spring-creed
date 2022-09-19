@! /bin/bash

patchPath=$1
patchName=$2
branchRepo=$3
branchName=$4

dir=/tmp/git/creed

cd $patchPath

echo "patchName is $patchPath/$patchName"
comment=$(echo patchName | cut -d "." -f 1)

git --version 2>&1 >/dev/null # improvement by tripleee
GIT_IS_AVAILABLE=$?

if [ $GIT_IS_AVAILABLE -eq 0 ]; then #
  echo "git installed"
else
  echo "going to install git"
  sudo apt update
  sudo apt install git
fi

if [[ -d $dir ]]; then #
  echo "Directory Exists"
  rm -rf $dir
  mkdir -p $dir
else
  mkdir -p $dir
fi
#[ -d $dir ] && echo "Directory Exists" || mkdir -p $dir

cd $dir
git clone -b $$branchName $branchRepo .
git apply  $patchPath/$patchName
git add .
git commit -m "$comment"
git push
rm $patchPath/$patchName
