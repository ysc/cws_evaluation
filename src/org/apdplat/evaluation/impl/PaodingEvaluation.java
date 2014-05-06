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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;

/**
 * Paoding分词器分词效果评估
 * @author 杨尚川
 */
public class PaodingEvaluation extends Evaluation{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 Paoding MOST_WORDS_MODE");
        list.add(run(PaodingAnalyzer.MOST_WORDS_MODE));
        System.out.println("开始评估 Paoding MAX_WORD_LENGTH_MODE");
        list.add(run(PaodingAnalyzer.MAX_WORD_LENGTH_MODE));
        
        Evaluation.generateReport(list, "Paoding分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final int mode) throws Exception{
        final PaodingAnalyzer analyzer = new PaodingAnalyzer();
        analyzer.setMode(mode);
        final Token reusableToken = new Token();
        String type = PaodingAnalyzer.MAX_WORD_LENGTH_MODE == mode ? "MAX_WORD_LENGTH_MODE" : "MOST_WORDS_MODE";
        // 对文本进行分词
        String resultText = "temp/result-text-"+type+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                StringBuilder result = new StringBuilder();
                try {
                    TokenStream stream = analyzer.tokenStream("", new StringReader(text));                
                    Token token = null;
                    while((token = stream.next(reusableToken)) != null){
                        result.append(token.term()).append(" ");
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return result.toString();                
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("Paoding "+type);
        result.setSegSpeed(rate);
        return result;
    }
    public static void main(String[] args) throws Exception{
        new PaodingEvaluation().run();
    }
}