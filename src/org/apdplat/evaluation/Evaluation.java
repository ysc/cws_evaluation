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

import edu.stanford.nlp.io.NullOutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通用评估逻辑
 * @author 杨尚川
 */
public abstract class Evaluation {
    protected String testText = "data/test-text.txt";
    protected String standardText = "data/standard-text.txt";
    public abstract List<EvaluationResult> run() throws Exception;

    public void setTestText(String testText) {
        this.testText = testText;
    }

    public void setStandardText(String standardText) {
        this.standardText = standardText;
    }

    /**
     * 生成评估报告
     * @param list
     * @throws IOException 
     */
    public static void generateReport(List<EvaluationResult> list) throws IOException{
        generateReport(list, "分词效果评估报告.txt");
    }
    /**
     * 生成评估报告
     * @param list
     * @param reportName 保存报告的文件名称
     * @throws IOException 
     */
    public static void generateReport(List<EvaluationResult> list, String reportName) throws IOException{
        if(list == null || list.isEmpty()){
            return;
        }
        Path report = Paths.get("report/"+reportName);
        if(Files.notExists(report.getParent())){
            report.getParent().toFile().mkdir();
        }
        List<String> result = new ArrayList<>();
        if(list.get(0).getPerfectLineCount() > 0) {
            result.add("按行数完美率排序：");
            Collections.sort(list);
            result.addAll(toText(list));
        }
        result.add("按分词速度排序：");
        Collections.sort(list, (a, b)->new Float(b.getSegSpeed()).compareTo(a.getSegSpeed()));
        result.addAll(toText(list));
        Files.write(report, result, Charset.forName("utf-8"));
    }
    private static List<String> toText(List<EvaluationResult> list){
        List<String> result = new ArrayList<>();
        int i=1;
        for(EvaluationResult item : list){
            result.add("");
            result.add("    "+(i++)+"、"+item.toString());
        }
        for(String item : result){
            System.out.println(item);
        }
        return result;
    }
    /**
     * 分词效果评估
     * @param resultText 实际分词结果文件路径
     * @param standardText 标准分词结果文件路径
     * @return 评估结果
     * @throws java.lang.Exception
     */
    protected EvaluationResult evaluate(String resultText, String standardText) throws Exception {
        if(standardText==null){
            System.out.println("没有指定标准文本，仅评估分词速度，不对分词效果进行评估");
            return new EvaluationResult();
        }
        int perfectLineCount=0;
        int wrongLineCount=0;
        int perfectCharCount=0;
        int wrongCharCount=0;
        try(BufferedReader resultReader = new BufferedReader(new InputStreamReader(new FileInputStream(resultText),"utf-8"));
            BufferedReader standardReader = new BufferedReader(new InputStreamReader(new FileInputStream(standardText),"utf-8"))){
            String result;
            while( (result = resultReader.readLine()) != null ){
                result = result.trim();
                String standard = standardReader.readLine().trim();
                if(result.equals("")){
                    continue;
                }
                if(result.equals(standard)){
                    //分词结果和标准一模一样
                    perfectLineCount++;
                    perfectCharCount+=standard.replaceAll("\\s+", "").length();
                }else{
                    //分词结果和标准不一样
                    wrongLineCount++;
                    wrongCharCount+=standard.replaceAll("\\s+", "").length();
                }
            }
        }
        int totalLineCount = perfectLineCount+wrongLineCount;
        int totalCharCount = perfectCharCount+wrongCharCount;
        EvaluationResult er = new EvaluationResult();
        er.setPerfectCharCount(perfectCharCount);
        er.setPerfectLineCount(perfectLineCount);
        er.setTotalCharCount(totalCharCount);
        er.setTotalLineCount(totalLineCount);
        er.setWrongCharCount(wrongCharCount);
        er.setWrongLineCount(wrongLineCount);     
        return er;
    }
    /**
     * 对文件进行分词
     * @param input 输入文件
     * @param output 输出文件
     * @param segmenter 对文本进行分词的逻辑
     * @return 分词速率
     * @throws Exception 
     */
    protected float segFile(final String input, final String output, final Segmenter segmenter) throws Exception{
        //如果分词结果文件存放目录不存在，则创建
        if(!Files.exists(Paths.get(output).getParent())){
            Files.createDirectory(Paths.get(output).getParent());
        }
        float rate = 0;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input),"utf-8"));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(standardText==null?new NullOutputStream():new FileOutputStream(output),"utf-8"))){
            long size = Files.size(Paths.get(input));
            System.out.println("size:"+size);
            System.out.println("文件大小："+(float)size/1024/1024+" MB");
            int textLength=0;
            int progress=0;
            long start = System.currentTimeMillis();
            String line = null;
            while((line = reader.readLine()) != null){
                if("".equals(line.trim())){
                    writer.write("\n");
                    continue;
                }
                try{
                    writer.write(segmenter.seg(line));
                    writer.write("\n");
                    textLength += line.length();
                    progress += line.length();
                    if( progress > 500000){
                        progress = 0;
                        System.out.println("分词进度："+(int)(textLength*2.99/size*100)+"%");
                    }
                }catch(Exception e){
                    System.out.println("分词失败："+line);
                    e.printStackTrace();
                }
            }
            long cost = System.currentTimeMillis() - start;
            rate = textLength/(float)cost;
            System.out.println("字符数目："+textLength);
            System.out.println("分词耗时："+cost+" 毫秒");
            System.out.println("分词速度："+rate+" 字符/毫秒");
        }
        return rate;
    }
    public static String getTimeDes(Long ms) {
        //处理参数为NULL的情况
        if(ms == null){
            return "";
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        
        StringBuilder str=new StringBuilder();
        if(day>0){
            str.append(day).append("天,");
        }
        if(hour>0){
            str.append(hour).append("小时,");
        }
        if(minute>0){
            str.append(minute).append("分钟,");
        }
        if(second>0){
            str.append(second).append("秒,");
        }
        if(milliSecond>0){
            str.append(milliSecond).append("毫秒,");
        }
        if(str.length()>0){
            str=str.deleteCharAt(str.length()-1);
        }

        return str.toString();
    }
}
