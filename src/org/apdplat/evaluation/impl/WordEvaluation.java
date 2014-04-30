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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

/**
 * word分词器分词效果评估
 * @author 杨尚川
 */
public class WordEvaluation extends Evaluation{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        for(final SegmentationAlgorithm segmentationAlgorithm : SegmentationAlgorithm.values()){
            list.add(run(segmentationAlgorithm));
        }
        return list;
    }
    private EvaluationResult run(final SegmentationAlgorithm segmentationAlgorithm) throws Exception{
        //对文本进行分词
        String resultText = "temp/result-text-"+segmentationAlgorithm.name()+".txt";
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                StringBuilder result = new StringBuilder();
                for(Word word : WordSegmenter.segWithStopWords(text, segmentationAlgorithm)){
                    result.append(word.getText()).append(" ");
                }
                return result.toString();
            }
        });
        //对分词结果进行评估
        EvaluationResult evaluationResult = evaluate(resultText, standardText);
        evaluationResult.setAnalyzer("word分词 "+segmentationAlgorithm.getDes());
        evaluationResult.setSegSpeed(rate);
        return evaluationResult;
    }
    public static void main(String[] args) throws Exception{
        List<EvaluationResult> list = new WordEvaluation().run();
        //输出评估结果
        Collections.sort(list);
        System.out.println("******************************************************************************************************************");
        int i=1;
        for(EvaluationResult r : list){
            System.out.println((i++)+"：");
            System.out.println(r+"\n");
        }
        System.out.println("******************************************************************************************************************");
    }
}