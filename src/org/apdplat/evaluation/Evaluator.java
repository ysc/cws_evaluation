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

package org.apdplat.evaluation;

import org.apdplat.evaluation.impl.MMSeg4jEvaluation;
import org.apdplat.evaluation.impl.WordEvaluation;
import org.apdplat.evaluation.impl.IKAnalyzerEvaluation;
import org.apdplat.evaluation.impl.AnsjEvaluation;
import java.util.List;

/**
 * 中文分词器分词效果评估程序
 * @author 杨尚川
 */
public class Evaluator {
    public static void main(String[] args) throws Exception{
        List<EvaluationResult> list = new WordEvaluation().run();
        list.addAll(new AnsjEvaluation().run());
        list.addAll(new MMSeg4jEvaluation().run());
        list.addAll(new IKAnalyzerEvaluation().run());
        
        Evaluation.generateReport(list);
    }

}