#!/bin/bash

function fnStart()
{

echo "================================================="
echo "====  YWISKTGateWay-1.0.war Check           ==="
echo "================================================="
   
ps_cnt=`ps -ef | grep YWISKTGateWay-1.0.war | grep -v grep | awk '{print $2}' | wc -l`
if [ $ps_cnt -eq 0 ] 
then
	echo "==== YWISKTGateWay-1.0.war is running              ==="
	echo "================================================="
else
     echo "==== YWISKTGateWay-1.0.war is not running.     ==="
     echo "================================================="
fi
 
}
 
fnStart
