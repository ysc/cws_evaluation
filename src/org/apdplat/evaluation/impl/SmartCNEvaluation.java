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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.WordSegmenter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * smartcn分词器分词效果评估
 * @author 杨尚川
 */
public class SmartCNEvaluation extends Evaluation implements WordSegmenter{
    private static final SmartChineseAnalyzer SMART_CHINESE_ANALYZER = new SmartChineseAnalyzer();
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        list.add(run("smartcn"));
        Evaluation.generateReport(list, "smartcn分词器分词效果评估报告.txt");
        return list;
    }
    private EvaluationResult run(String type) throws Exception{
        //对文本进行分词
        String resultText = "temp/result-text-"+type+".txt";
        float rate = segFile(testText, resultText, text -> SmartCNEvaluation.segText(text));
        //对分词结果进行评估
        EvaluationResult evaluationResult = evaluate(resultText, standardText);
        evaluationResult.setAnalyzer(type);
        evaluationResult.setSegSpeed(rate);
        return evaluationResult;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("smartcn", segText(text));
        return map;
    }
    private static String segText(String text) {
        StringBuilder result = new StringBuilder();
        try {
            TokenStream tokenStream = SMART_CHINESE_ANALYZER.tokenStream("text", new StringReader(text));
            tokenStream.reset();
            while (tokenStream.incrementToken()){
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                result.append(charTermAttribute.toString()).append(" ");
            }
            tokenStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new SmartCNEvaluation().run();
    }
}