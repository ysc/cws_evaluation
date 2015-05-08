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

import org.apdplat.evaluation.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取文本的所有分词结果, 对比不同分词器结果
 * @author 杨尚川
 */
public interface WordSegmenter {
    /**
     * 获取文本的所有分词结果
     * @param text 文本
     * @return 所有的分词结果
     */
    public Set<String> seg(String text);
    public static Map<String, Set<String>> contrast(String text){
        Map<String, Set<String>> map = new HashMap<>();
        map.put("word分词器", new WordEvaluation().seg(text));
        //map.put("Stanford分词器", new StanfordEvaluation().seg(text));
        map.put("Ansj分词器", new AnsjEvaluation().seg(text));
        map.put("FudanNLP分词器", new FudanNLPEvaluation().seg(text));
        map.put("Jieba分词器", new JiebaEvaluation().seg(text));
        map.put("Jcseg分词器", new JcsegEvaluation().seg(text));
        map.put("MMSeg4j分词器", new MMSeg4jEvaluation().seg(text));
        map.put("IKAnalyzer分词器", new IKAnalyzerEvaluation().seg(text));
        map.put("Paoding分词器", new PaodingEvaluation().seg(text));
        return map;
    }
    public static void show(Map<String, Set<String>> map){
        map.keySet().forEach(k->{
            System.out.println(k + " 的分词结果：");
            AtomicInteger i = new AtomicInteger();
            map.get(k).forEach(v->{
                System.out.println("\t" + i.incrementAndGet() + "、" + v);
            });
        });
    }
    public static void main(String[] args) {
        Map<String, Set<String>> map = contrast("杨尚川是APDPlat应用级产品开发平台的作者");
        show(map);
    }
}
