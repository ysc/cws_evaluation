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

import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.WordSegmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HanLP分词器分词效果评估
 * @author 杨尚川
 */
public class HanLPEvaluation extends Evaluation implements WordSegmenter {
    private static final Segment N_SHORT_SEGMENT = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
    private static final Segment DIJKSTRA_SEGMENT = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);

    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();

        run(list, "标准分词");
        run(list, "NLP分词");
        run(list, "索引分词");
        run(list, "N-最短路径分词");
        run(list, "最短路径分词");
        run(list, "极速词典分词");

        return list;
    }
    private void run(List<EvaluationResult> list, String type) throws Exception{
        System.out.println("开始评估 HanLP分词器  "+type);
        list.add(run(type));
        Evaluation.generateReport(list, "HanLP分词器分词效果评估报告.txt");
    }
    private EvaluationResult run(String type) throws Exception{
        //对文本进行分词
        String resultText = "temp/result-text-HanLP-"+type+".txt";
        float rate = 0;
        switch (type){
            case "标准分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.standard(text));break;
            case "NLP分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.nlp(text));break;
            case "索引分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.index(text));break;
            case "N-最短路径分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.nShort(text));break;
            case "最短路径分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.shortest(text));break;
            case "极速词典分词":rate = segFile(testText, resultText, text -> HanLPEvaluation.speed(text));break;
        }
        //对分词结果进行评估
        EvaluationResult evaluationResult = evaluate(resultText, standardText);
        evaluationResult.setAnalyzer("HanLP分词器 " + type);
        evaluationResult.setSegSpeed(rate);
        return evaluationResult;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("标准分词", standard(text));
        map.put("NLP分词", nlp(text));
        map.put("索引分词", index(text));
        map.put("N-最短路径分词", nShort(text));
        map.put("最短路径分词", shortest(text));
        map.put("极速词典分词", speed(text));
        return map;
    }
    private static String standard(String text) {
        StringBuilder result = new StringBuilder();
        StandardTokenizer.segment(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }
    private static String nlp(String text) {
        StringBuilder result = new StringBuilder();
        NLPTokenizer.segment(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }
    private static String index(String text) {
        StringBuilder result = new StringBuilder();
        IndexTokenizer.segment(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }
    private static String speed(String text) {
        StringBuilder result = new StringBuilder();
        SpeedTokenizer.segment(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }
    private static String nShort(String text) {
        StringBuilder result = new StringBuilder();
        N_SHORT_SEGMENT.seg(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }
    private static String shortest(String text) {
        StringBuilder result = new StringBuilder();
        DIJKSTRA_SEGMENT.seg(text).forEach(term->result.append(term.word).append(" "));
        return result.toString();
    }

    public static void main(String[] args) throws Exception{
        new HanLPEvaluation().run();
    }
}