/*
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.apdplat.evaluation.impl;

import edu.stanford.nlp.io.NullOutputStream;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stanford分词器分词效果评估
 * @author 杨尚川
 */
public class StanfordEvaluation extends Evaluation implements WordSegmenter{
    private static final StanfordCoreNLP CTB = new StanfordCoreNLP("StanfordCoreNLP-chinese-ctb");
    private static final StanfordCoreNLP PKU = new StanfordCoreNLP("StanfordCoreNLP-chinese-pku");
    private static final PrintStream NULL_PRINT_STREAM = new PrintStream(new NullOutputStream(), false);
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();

        System.out.println("开始评估 Stanford Chinese Treebank segmentation");
        list.add(run("ctb"));
        Evaluation.generateReport(list, "Stanford分词器分词效果评估报告.txt");

        System.out.println("开始评估 Stanford Beijing University segmentation");
        list.add(run("pku"));
        Evaluation.generateReport(list, "Stanford分词器分词效果评估报告.txt");

        return list;
    }
    private EvaluationResult run(final String lang) throws Exception{
        // 对文本进行分词
        String type = "ctb".equals(lang) ? "Chinese Treebank segmentation" : "Beijing University segmentation";
        String resultText = "temp/result-text-"+type+".txt";
        StanfordCoreNLP stanfordCoreNLP = "ctb".equals(lang) ? CTB : PKU;
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return StanfordEvaluation.seg(stanfordCoreNLP, text);
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("Stanford "+type);
        result.setSegSpeed(rate);
        return result;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("Stanford Beijing University segmentation", seg(PKU, text));
        map.put("Stanford Chinese Treebank segmentation", seg(CTB, text));
        return map;
    }
    private static String seg(StanfordCoreNLP stanfordCoreNLP, String text){
        PrintStream err = System.err;
        System.setErr(NULL_PRINT_STREAM);
        Annotation document = new Annotation(text);
        stanfordCoreNLP.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        StringBuilder result = new StringBuilder();
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);;
                result.append(word).append(" ");
            }
        }
        System.setErr(err);
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new StanfordEvaluation().run();
    }
}