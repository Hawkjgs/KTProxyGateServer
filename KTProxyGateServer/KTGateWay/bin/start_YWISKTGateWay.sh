#!/bin/bash

function fnStart()
{

echo "================================================="
echo "====  YWISKTGateWay-1.0.war Check           ==="
echo "================================================="
   
ps_cnt=`ps -ef | grep YWISKTGateWay-1.0.war | grep -v grep | awk '{print $2}' | wc -l`
if [ $ps_cnt -eq 0 ] 
then
	echo "==== YWISKTGateWay-1.0.war Deamon Start             ==="
	echo "================================================="
	nohup   java -jar YWISKTGateWay-1.0.war  1>/dev/null 2>&1 &
else
     echo "==== YWISKTGateWay-1.0.war is already running.     ==="
     echo "================================================="
fi
 
}
 
fnStart
