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

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;
import com.chenlb.mmseg4j.Word;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;

/**
 * MMSeg4j分词器分词效果评估
 * @author 杨尚川
 */
public class MMSeg4jEvaluation extends Evaluation{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        Dictionary dic = Dictionary.getInstance();
        
        System.out.println("开始评估 MMSeg4j ComplexSeg");
        list.add(run(new ComplexSeg(dic)));
        Evaluation.generateReport(list, "MMSeg4j分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 MMSeg4j SimpleSeg");
        list.add(run(new SimpleSeg(dic)));
        Evaluation.generateReport(list, "MMSeg4j分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 MMSeg4j MaxWordSeg");
        list.add(run(new MaxWordSeg(dic)));
        Evaluation.generateReport(list, "MMSeg4j分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final Seg seg) throws Exception{
        // 对文本进行分词
        String resultText = "temp/result-text-"+seg.getClass().getSimpleName()+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return segText(text, seg);                
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("MMSeg4j "+seg.getClass().getSimpleName());
        result.setSegSpeed(rate);
        return result;
    }
    private String segText(String text, Seg seg) {
        StringBuilder result = new StringBuilder();
        MMSeg mmSeg = new MMSeg(new StringReader(text), seg);        
        try {
            Word word = null;
            while((word=mmSeg.next())!=null) {			
                result.append(word.getString()).append(" ");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return result.toString();
    }
    @Override
    public List<String> seg(String text) {
        List<String> list = new ArrayList<>();
        Dictionary dic = Dictionary.getInstance();
        list.add(segText(text, new ComplexSeg(dic)));
        list.add(segText(text, new SimpleSeg(dic)));
        list.add(segText(text, new MaxWordSeg(dic)));
        return list;
    }
    public static void main(String[] args) throws Exception{
        new MMSeg4jEvaluation().run();
    }
}