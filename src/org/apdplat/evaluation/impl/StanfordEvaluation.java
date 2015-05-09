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

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.evaluation.Segmenter;
import org.apdplat.evaluation.WordSegmenter;

/**
 * Stanford分词器分词效果评估
 * @author 杨尚川
 */
public class StanfordEvaluation extends Evaluation implements WordSegmenter{
    private static CRFClassifier<CoreLabel> pkuCRFClassifier = null;
    private static CRFClassifier<CoreLabel> ctbCRFClassifier = null;
    static{
        try{
            String pku = "lib/stanford-segmenter-3.3.1/data/pku.gz";
            String ctb = "lib/stanford-segmenter-3.3.1/data/ctb.gz";
            //github单文件最大不能超过100m，所以分割文件存放，使用时再合并
            //split(pku, 2);
            //split(ctb, 2);
            if(!Files.exists(Paths.get(pku))){
                merge(pku, pku, 2);
            }
            if(!Files.exists(Paths.get(ctb))){
                merge(ctb, ctb, 2);
            }
            pkuCRFClassifier = getCRFClassifier("pku");
            ctbCRFClassifier = getCRFClassifier("ctb");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private static CRFClassifier<CoreLabel> getCRFClassifier(String lang){
        Properties props = new Properties();
        props.setProperty("sighanCorporaDict", "lib/stanford-segmenter-3.3.1/data");
        props.setProperty("NormalizationTable", "lib/stanford-segmenter-3.3.1/data/norm.simp.utf8");
        props.setProperty("normTableEncoding", "UTF-8");
        // below is needed because CTBSegDocumentIteratorFactory accesses it
        props.setProperty("serDictionary","lib/stanford-segmenter-3.3.1/data/dict-chris6.ser.gz");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");

        final CRFClassifier<CoreLabel> segmenter = new CRFClassifier<>(props);
        segmenter.loadClassifierNoExceptions("lib/stanford-segmenter-3.3.1/data/"+lang+".gz", props);
        return segmenter;
    }
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        
        System.out.println("开始评估 Stanford Chinese Treebank segmentation");
        list.add(run("ctb"));
        Evaluation.generateReport(list, "Stanford分词器分词效果评估报告.txt");
        
        System.out.println("开始评估 Stanford Beijing University segmentation");
        list.add(run("pku"));
        Evaluation.generateReport(list, "Stanford分词器分词效果评估报告.txt");
        
        return list;
    }
    private EvaluationResult run(final String lang) throws Exception{
        // 对文本进行分词
        String type = "ctb".equals(lang) ? "Chinese Treebank segmentation" : "Beijing University segmentation";
        String resultText = "temp/result-text-"+type+".txt";
        CRFClassifier<CoreLabel> crfClassifier = "ctb".equals(lang) ? ctbCRFClassifier : pkuCRFClassifier;
        float rate = segFile(testText, resultText, new Segmenter(){
            @Override
            public String seg(String text) {
                return StanfordEvaluation.seg(crfClassifier, text);
            }
        });        
        // 对分词结果进行评估
        EvaluationResult result = evaluate(resultText, standardText);
        result.setAnalyzer("Stanford "+type);
        result.setSegSpeed(rate);
        return result;
    }
    private static void split(String file, int splitCount) throws Exception {
        long length;
        long size;
        try (RandomAccessFile raf = new RandomAccessFile(new File(file), "r")) {
            length = raf.length();
            size = length / splitCount;
        }
        long offset = 0L;
        for (int i = 0; i < splitCount - 1; i++){
            long fbegin = offset;
            long fend = (i + 1) * size;
            offset = write(file, i, fbegin, fend);
        }
        if (length - offset > 0){
            write(file, splitCount - 1, offset, length);
        }
    }
    private static void merge(String file, String splitFiles, int splitCount) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(new File(file), "rw")) {
            for (int i = 0; i < splitCount; i++) {
                try (RandomAccessFile reader = new RandomAccessFile(new File(splitFiles + "_" + i), "r")) {
                    byte[] b = new byte[4096];
                    int n = -1;
                    while ((n = reader.read(b)) != -1) {
                        raf.write(b, 0, n);
                    }
                }
            }
        }
    }
    private static long write(String file, int index, long begin, long end) throws Exception {
        long endPointer;
        try (RandomAccessFile in = new RandomAccessFile(new File(file), "r");
                RandomAccessFile out = new RandomAccessFile(new File(file + "_" + index), "rw")) {
            byte[] b = new byte[4096];
            int n = 0;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }   endPointer = in.getFilePointer();
        }
        return endPointer;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("Stanford Beijing University segmentation", seg(pkuCRFClassifier, text));
        map.put("Stanford Chinese Treebank segmentation", seg(ctbCRFClassifier, text));
        return map;
    }

    private static String seg(CRFClassifier<CoreLabel> crfClassifier, String text){
        StringBuilder result = new StringBuilder();
        for(String word : crfClassifier.segmentString(text)){
            result.append(word).append(" ");
        }
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new StanfordEvaluation().run();
    }
}