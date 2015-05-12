/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.evaluation.impl;

import edu.fudan.nlp.cn.tag.CWSTagger;

import java.util.*;

import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;

/**
 * FudanNLP分词器分词效果评估
 * @author 杨尚川
 */
public class FudanNLPEvaluation extends Evaluation implements WordSegmenter{
    private static CWSTagger tagger = null;
    static{
        try{
            tagger = new CWSTagger("lib/fudan/fudannlp/1.6.1/fudannlp_seg.m");
            tagger.setEnFilter(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 FudanNLP");
        list.add(run(tagger));
        
        Evaluation.generateReport(list, "FudanNLP分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final CWSTagger tagger) throws Exception{
        // 对文本进行分词
        String resultText = "temp/result-text-FudanNLP.txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return tagger.tag(text);                
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("FudanNLP");
        result.setSegSpeed(rate);
        return result;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("FudanNLP", tagger.tag(text));
        return map;
    }
    public static void main(String[] args) throws Exception{
        new FudanNLPEvaluation().run();
    }
}