#!/bin/bash

SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"

if [ $# != 1 ] ;then
    $SETCOLOR_FAILURE
    echo Usage:
    echo You must use a file name as the command line parameter in order to specify which file should be segmented
    echo for example: ./speed.sh text-to-seg.txt
    $SETCOLOR_NORMAL
    exit
fi

if [ ! -f $1 ] ;then
    $SETCOLOR_FAILURE
    echo The file you specified is not existing: $1
    $SETCOLOR_NORMAL
    exit
fi

echo 中文分词器分词速度评估程序
echo 测试文本：$1

export MAVEN_OPTS="-Xms4g -Xmx4g"

mvn clean install exec:java -Dexec.mainClass="org.apdplat.evaluation.Evaluator" -Dexec.args="target/cws_evaluation-1.0.jar -testText=$1 StanfordEvaluation"

#如果需要排除评估某些分词器，则将下面分词器的名称作为参数传递给Evaluator类的main方法
#如：
#mvn clean install exec:java -Dexec.mainClass="org.apdplat.evaluation.Evaluator" -Dexec.args="target/cws_evaluation-1.0.jar -testText=$1 StanfordEvaluation FudanNLPEvaluation"
#WordEvaluation
#StanfordEvaluation
#SmartCNEvaluation
#MMSeg4jEvaluation
#JiebaEvaluation
#JcsegEvaluation
#IKAnalyzerEvaluation
#FudanNLPEvaluation
#AnsjEvaluation
#HanLPEvaluation
