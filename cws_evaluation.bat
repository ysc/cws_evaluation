@echo off
set JAVA_OPTS=-Xmx2400m
set CLASS_PATH=cws_evaluation.jar;lib/word-1.1-SNAPSHOT.jar;lib/slf4j-api-1.6.4.jar;lib/ansj_seg-1.4.jar;lib/tree_split-1.2.jar;lib/mmseg4j-core-1.9.1.jar;lib/IKAnalyzer2012_u6.jar;lib/jcseg-core-1.9.2.jar;lib/fudannlp.jar;lib/trove.jar;lib/paoding-analysis-2.0.4-beta.jar;lib/lucene-core-2.4.1.jar;lib/commons-logging.jar;lib/jieba-analysis-0.0.2.jar;lib/stanford-segmenter-3.3.1.jar
set EXECUTOR=java %JAVA_OPTS% -cp %CLASS_PATH%
call %EXECUTOR% org.apdplat.evaluation.Evaluator
