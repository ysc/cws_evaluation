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
import java.util.*;

import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;
import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;

/**
 * Jcseg分词器分词效果评估
 * @author 杨尚川
 */
public class JcsegEvaluation extends Evaluation implements WordSegmenter{
    private static final JcsegTaskConfig CONFIG = new JcsegTaskConfig();
    private static final ADictionary DIC = DictionaryFactory.createDefaultDictionary(new JcsegTaskConfig());
    
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 Jcseg 复杂模式");
        list.add(run(JcsegTaskConfig.COMPLEX_MODE));   
        Evaluation.generateReport(list, "Jcseg分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Jcseg 简易模式");
        list.add(run(JcsegTaskConfig.SIMPLE_MODE));
        Evaluation.generateReport(list, "Jcseg分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final int segMode) throws Exception{
        // 对文本进行分词
        String type = JcsegTaskConfig.COMPLEX_MODE==segMode?"Jcseg 复杂模式":"Jcseg 简易模式";
        String resultText = "temp/result-text-"+type+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return segText(text, segMode);
            }
        });
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer(type);
        result.setSegSpeed(rate);
        return result;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();

        map.put("复杂模式", segText(text, JcsegTaskConfig.COMPLEX_MODE));
        map.put("简易模式", segText(text, JcsegTaskConfig.SIMPLE_MODE));

        return map;
    }
    private String segText(String text, int segMode) {
        StringBuilder result = new StringBuilder();        
        try {
            ISegment seg = SegmentFactory.createJcseg(segMode, new Object[]{new StringReader(text), CONFIG, DIC});
            IWord word = null;
            while((word=seg.next())!=null) {			
                result.append(word.getValue()).append(" ");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new JcsegEvaluation().run();
    }
}