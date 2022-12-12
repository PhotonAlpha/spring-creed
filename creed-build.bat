@echo off

call echo "start"

(
start "" cmd /c "mvn -f /workspace/<path1>/pom.xml clean install
start "" cmd /c "mvn -f /workspace/<path2>/pom.xml clean install
) | pause

(
start "" cmd /c "mvn -f /workspace/<path3>/pom.xml clean install
start "" cmd /c "mvn -f /workspace/<path4>/pom.xml clean install
start "" cmd /c "mvn -f /workspace/<path5>/pom.xml clean install
) | pause

call mvn -f /workspace/<path6>/pom.xml clean install
call mvn -f /workspace/<path7>/pom.xml clean install
pause

