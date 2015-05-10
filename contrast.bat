echo 中文分词器分词效果对比程序
echo 第一次启动非常慢，请耐心等待

@echo off

set JAVA_OPTS=-Xms3g -Xmx3g

call mvn clean install exec:java -Dexec.mainClass="org.apdplat.evaluation.WordSegmenter" -Dexec.args="utf-8"