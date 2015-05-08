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

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;
import java.util.ArrayList;
import java.util.List;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;

/**
 * Jieba分词器分词效果评估
 * @author 杨尚川
 */
public class JiebaEvaluation extends Evaluation{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 Jieba "+SegMode.INDEX);
        list.add(run(SegMode.INDEX));
        Evaluation.generateReport(list, "Jieba分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Jieba "+SegMode.SEARCH);
        list.add(run(SegMode.SEARCH));
        Evaluation.generateReport(list, "Jieba分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final SegMode segMode) throws Exception{
        final JiebaSegmenter segmenter = new JiebaSegmenter();
        // 对文本进行分词
        String resultText = "temp/result-text-"+segMode+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                StringBuilder result = new StringBuilder();                
                for(SegToken token : segmenter.process(text, segMode)){
                    result.append(token.token).append(" ");
                }
                return result.toString();                
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("Jieba "+segMode);
        result.setSegSpeed(rate);
        return result;
    }
    @Override
    public List<String> seg(String text) {
        List<String> list = new ArrayList<>();
        JiebaSegmenter segmenter = new JiebaSegmenter();
        list.add(seg(text, segmenter, SegMode.INDEX));
        list.add(seg(text, segmenter, SegMode.SEARCH));
        return list;
    }
    public String seg(String text, JiebaSegmenter segmenter, SegMode segMode) {
        StringBuilder result = new StringBuilder();                
        for(SegToken token : segmenter.process(text, segMode)){
            result.append(token.token).append(" ");
        }
        return result.toString(); 
    }
    public static void main(String[] args) throws Exception{
        new JiebaEvaluation().run();
    }
}