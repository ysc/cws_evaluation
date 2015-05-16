echo 中文分词器分词效果评估程序

@echo off

set MAVEN_OPTS=-Xms3g -Xmx3g

call mvn clean install exec:java -Dexec.mainClass="org.apdplat.evaluation.Evaluator" -Dexec.args="target/cws_evaluation-1.0.jar -testText=data/test-text.txt -standardText=data/standard-text.txt  StanfordEvaluation"

rem 如果需要排除评估某些分词器，则将下面分词器的名称作为参数传递给Evaluator类的main方法
rem 如：
rem call mvn -e clean install exec:java -Dexec.mainClass="org.apdplat.evaluation.Evaluator" -Dexec.args="target/cws_evaluation-1.0.jar -testText=data/test-text.txt -standardText=data/standard-text.txt  StanfordEvaluation FudanNLPEvaluation"
rem WordEvaluation
rem StanfordEvaluation
rem SmartCNEvaluation
rem MMSeg4jEvaluation
rem JiebaEvaluation
rem JcsegEvaluation
rem IKAnalyzerEvaluation
rem FudanNLPEvaluation
rem AnsjEvaluation
rem HanLPEvaluation
