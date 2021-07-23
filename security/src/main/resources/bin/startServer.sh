#!/bin/bash
#
# run hadoopJar tool
#
###EOF

prog=hadoopJar

export JAVA_HOME=${JAVA_HOME}
pwd=$(cd `dirname $0`; pwd)
echo $pwd
export DEPLOY_PATH=${pwd%/*}

if [ ! -d $JAVA_HOME ];then
    echo "please set right JAVA_HOME in this file"
    exit 0
fi

if [ ! -d $DEPLOY_PATH ];then
    echo "please set right DEPLOY_PATH in this file"
    exit 0
fi


export JAVA_OPTIONS="-Xmx2048m -Xms1024m -XX:MaxPermSize=1024m"
export CLASSPATH=.:${CLASSPATH}:${DEPLOY_PATH}/resources:${DEPLOY_PATH}/lib
${JAVA_HOME}/bin/java -cp ${DEPLOY_PATH}/resources:${DEPLOY_PATH}/lib/*:${DEPLOY_PATH}/lib/security-1.0.0.jar:. ${JAVA_OPTIONS} com.jonbore.security.SecurityClient --$prog $@ &