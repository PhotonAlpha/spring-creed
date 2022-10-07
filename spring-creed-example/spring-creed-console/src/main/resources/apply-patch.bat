@echo off

set patchPath=%1
set patchName=%2
set branchRepo=%3
set branchName=%4

set dir=C:\workspace\PSL\source\test

cd %patchPath%

echo "patchName is %patchPath%/%patchName%"
@REM set comment=$(echo %patchName%)

@REM git --version 2>&1 >/dev/null # improvement by tripleee
@REM GIT_IS_AVAILABLE=$?

@REM if [ $GIT_IS_AVAILABLE -eq 0 ]; then #
@REM   echo "git installed"
@REM else
@REM   echo "going to install git"
@REM   sudo apt update
@REM   sudo apt install git
@REM fi

@REM if [[ -d $dir ]]; then #
@REM   echo "Directory Exists"
@REM   rm -rf $dir
@REM   mkdir -p $dir
@REM else
@REM   mkdir -p $dir
@REM fi
@REM #[ -d $dir ] && echo "Directory Exists" || mkdir -p $dir

cd %dir%
git clone -b %branchName% %branchRepo% .
git apply  %patchPath%/%patchName%
git add .
git commit -m "%patchName%"
git push
@REM rm %patchPath%/%patchName%
