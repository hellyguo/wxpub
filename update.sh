#!/bin/sh

git pull

gradle build -x test

cd /home/lnu/srv/tomcat8/bin

./shutdown.sh

rm -rf /home/lnu/srv/tomcat8/webapps/wxpub/WEB-INF/classes/ln
rm -rf /home/lnu/srv/tomcat8/webapps/wxpub/WEB-INF/classes/com

cp -R /home/lnu/code/java/wxpub/build/classes/main/ln /home/lnu/srv/tomcat8/webapps/wxpub/WEB-INF/classes
cp -R /home/lnu/code/java/wxpub/build/classes/main/com /home/lnu/srv/tomcat8/webapps/wxpub/WEB-INF/classes

./startup.sh

#cd /home/lnu/srvlog

#find . -mtime 0 -type f -exec tail -f {} \;

