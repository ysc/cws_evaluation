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

import java.util.*;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;

/**
 * Ansj分词器分词效果评估
 * @author 杨尚川
 */
public class AnsjEvaluation extends Evaluation implements WordSegmenter{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 Ansj BaseAnalysis 基本分词");
        list.add(run("BaseAnalysis"));        
        Evaluation.generateReport(list, "Ansj分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Ansj ToAnalysis 精准分词");
        list.add(run("ToAnalysis"));
        Evaluation.generateReport(list, "Ansj分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Ansj NlpAnalysis NLP分词");
        list.add(run("NlpAnalysis"));
        Evaluation.generateReport(list, "Ansj分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Ansj IndexAnalysis 面向索引的分词");
        list.add(run("IndexAnalysis"));
        Evaluation.generateReport(list, "Ansj分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final String analysis) throws Exception{
        // 对文本进行分词
        String analyzer = "Ansj "+analysis;
        switch(analysis){
            case "BaseAnalysis":
                    analyzer += " 基本分词";
                    break;
            case "ToAnalysis":
                    analyzer += " 精准分词";
                    break;
            case "NlpAnalysis":
                    try{
                        analyzer += " NLP分词";
                    }catch(Exception e){}
                    break;
            case "IndexAnalysis":
                    analyzer += " 面向索引的分词";
                    break;
        }
        String resultText = "temp/result-text-"+analysis+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                StringBuilder result = new StringBuilder();
                try{
                    List<Term> terms = null;
                    switch(analysis){
                        case "BaseAnalysis":
                                terms = BaseAnalysis.parse(text);
                                break;
                        case "ToAnalysis":
                                terms = ToAnalysis.parse(text);
                                break;
                        case "NlpAnalysis":
                                terms = NlpAnalysis.parse(text);
                                break;
                        case "IndexAnalysis":
                                terms = IndexAnalysis.parse(text);
                                break;
                    }
                    for(Term term : terms){
                        result.append(term.getName()).append(" ");                    
                    }                    
                }catch(Exception e){
                    e.printStackTrace();
                }
                return result.toString();
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);        
        result.setSegSpeed(rate);
        result.setAnalyzer(analyzer);
        return result;
    }

    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();

        StringBuilder result = new StringBuilder();
        for(Term term : BaseAnalysis.parse(text)){
            result.append(term.getName()).append(" ");
        }
        map.put("BaseAnalysis", result.toString());

        result.setLength(0);
        for(Term term : ToAnalysis.parse(text)){
            result.append(term.getName()).append(" ");
        }
        map.put("ToAnalysis", result.toString());

        result.setLength(0);
        for(Term term : NlpAnalysis.parse(text)){
            result.append(term.getName()).append(" ");
        }
        map.put("NlpAnalysis", result.toString());

        result.setLength(0);
        for(Term term : IndexAnalysis.parse(text)){
            result.append(term.getName()).append(" ");
        }
        map.put("IndexAnalysis", result.toString());

        return map;
    }

    public static void main(String[] args) throws Exception{
        new AnsjEvaluation().run();
    }
}