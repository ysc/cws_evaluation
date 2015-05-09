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

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * IKAnalyzer分词器分词效果评估
 * @author 杨尚川
 */
public class IKAnalyzerEvaluation extends Evaluation implements WordSegmenter{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 IKAnalyzer 智能切分");
        list.add(run(true));
        Evaluation.generateReport(list, "IKAnalyzer分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 IKAnalyzer 细粒度切分");
        list.add(run(false));
        Evaluation.generateReport(list, "IKAnalyzer分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final boolean useSmart) throws Exception{
        String des = "细粒度切分";
        if(useSmart){
            des = "智能切分";
        }
        // 对文本进行分词
        String resultText = "temp/result-text-"+des+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return segText(text, useSmart);
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("IKAnalyzer " + des);
        result.setSegSpeed(rate);
        return result;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();

        map.put("智能切分", segText(text, true));
        map.put("细粒度切分", segText(text, false));

        return map;
    }
    private String segText(String text, boolean useSmart) {
        StringBuilder result = new StringBuilder();
        IKSegmenter ik = new IKSegmenter(new StringReader(text), useSmart);        
        try {
            Lexeme word = null;
            while((word=ik.next())!=null) {			
                result.append(word.getLexemeText()).append(" ");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new IKAnalyzerEvaluation().run();
    }
}