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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 获取文本的所有分词结果, 对比不同分词器结果
 * seg和segMore两个方法的区别在于返回值
 * 每一个分词器都可能有多种分词模式，每种模式的分词结果都可能不相同
 * 第一个方法忽略分词器模式，返回所有模式的所有不重复分词结果
 * 第二个方法返回每一种分词器模式及其对应的分词结果
 * @author 杨尚川
 */
public interface WordSegmenter {
    /**
     * 获取文本的所有分词结果
     * @param text 文本
     * @return 所有的分词结果，去除重复
     */
    default public Set<String> seg(String text) {
        return segMore(text).values().stream().collect(Collectors.toSet());
    }
    /**
     * 获取文本的所有分词结果
     * @param text 文本
     * @return 所有的分词结果，KEY 为分词器模式，VALUE 为分词器结果
     */
    public Map<String, String> segMore(String text);
    
    public static Map<String, Set<String>> contrast(String text){
        Map<String, Set<String>> map = new LinkedHashMap<>();
        map.put("word分词器", new WordEvaluation().seg(text));
        map.put("Stanford分词器", new StanfordEvaluation().seg(text));
        map.put("Ansj分词器", new AnsjEvaluation().seg(text));
        map.put("HanLP分词器", new HanLPEvaluation().seg(text));
        map.put("smartcn分词器", new SmartCNEvaluation().seg(text));
        map.put("FudanNLP分词器", new FudanNLPEvaluation().seg(text));
        map.put("Jieba分词器", new JiebaEvaluation().seg(text));
        map.put("Jcseg分词器", new JcsegEvaluation().seg(text));
        map.put("MMSeg4j分词器", new MMSeg4jEvaluation().seg(text));
        map.put("IKAnalyzer分词器", new IKAnalyzerEvaluation().seg(text));
        return map;
    }
    public static Map<String, Map<String, String>> contrastMore(String text){
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("word分词器", new WordEvaluation().segMore(text));
        map.put("Stanford分词器", new StanfordEvaluation().segMore(text));
        map.put("Ansj分词器", new AnsjEvaluation().segMore(text));
        map.put("HanLP分词器", new HanLPEvaluation().segMore(text));
        map.put("smartcn分词器", new SmartCNEvaluation().segMore(text));
        map.put("FudanNLP分词器", new FudanNLPEvaluation().segMore(text));
        map.put("Jieba分词器", new JiebaEvaluation().segMore(text));
        map.put("Jcseg分词器", new JcsegEvaluation().segMore(text));
        map.put("MMSeg4j分词器", new MMSeg4jEvaluation().segMore(text));
        map.put("IKAnalyzer分词器", new IKAnalyzerEvaluation().segMore(text));
        return map;
    }
    public static void show(Map<String, Set<String>> map){
        map.keySet().forEach(k -> {
            System.out.println(k + " 的分词结果：");
            AtomicInteger i = new AtomicInteger();
            map.get(k).forEach(v -> {
                System.out.println("\t" + i.incrementAndGet() + " 、" + v);
            });
        });
    }
    public static void showMore(Map<String, Map<String, String>> map){
        map.keySet().forEach(k->{
            System.out.println(k + " 的分词结果：");
            AtomicInteger i = new AtomicInteger();
            map.get(k).keySet().forEach(a -> {
                System.out.println("\t" + i.incrementAndGet()+ " 、【"   + a + "】\t" + map.get(k).get(a));
            });
        });
    }
    public static void run(String encoding) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, encoding))){
            String line = null;
            while((line = reader.readLine()) != null){
                if("exit".equals(line)){
                    System.exit(0);
                    return;
                }
                if(line.trim().equals("")){
                    continue;
                }
                process(line);
                showUsage();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void process(String text){
        System.out.println("********************************************");
        show(contrast(text));
        System.out.println("********************************************");
        showMore(contrastMore(text));
        System.out.println("********************************************");
    }
    public static void showUsage(){
        System.out.println("输入exit退出程序");
        System.out.println("输入要分词的文本后回车确认：");
    }
    public static void main(String[] args) {
        process("我爱楚离陌");
        process("结合成分子");
        String encoding = "utf-8";
        if(args==null || args.length == 0){
            showUsage();
            run(encoding);
        }else if(Charset.isSupported(args[0])){
            showUsage();
            run(args[0]);
        }
    }
}
