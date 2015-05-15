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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 中文分词器分词效果评估程序
 * @author 杨尚川
 */
public class Evaluator {
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        String testText = null;
        String standardText = null;
        //可通过命令行参数指定不评估的分词器
        Set<String> excludes = new HashSet<>();
        for(String arg : args){
            System.out.println("命令行参数："+arg);
            if(arg.endsWith(".jar")){
                continue;
            }
            if(arg.startsWith("-testText=")){
                testText=arg.replace("-testText=", "").trim();
                System.out.println("testText："+testText);
                continue;
            }
            if(arg.startsWith("-standardText=")){
                standardText=arg.replace("-standardText=", "").trim();
                System.out.println("standardText："+standardText);
                continue;
            }
            excludes.add(arg);
            System.out.println("不评估：" + arg);
        }
        List<Class> classes = new ArrayList<>();
        if(args.length>0 && new File(args[0]).exists()){
            classes.addAll(processJar(new File(args[0]), excludes));
        }else{
            classes.addAll(processDir(excludes));
        }
        Collections.reverse(classes);
        System.out.println("需要评估的分词器：");
        int i=1;
        for(Class clazz : classes){
            System.out.println((i++)+"："+clazz.getSimpleName());
        }
        List<EvaluationResult> list = new ArrayList<>();
        for(Class clazz : classes){
            Evaluation eval = (Evaluation)clazz.newInstance();
            if(testText!=null) {
                eval.setTestText(testText);
                eval.setStandardText(standardText);
            }
            System.out.println("先预热，再评估："+((WordSegmenter)eval).seg("今天下雨，中华人民共和国，结合成分子"));
            list.addAll(eval.run());
        }
        Evaluation.generateReport(list);
        long cost = System.currentTimeMillis() - start;
        System.out.println("评估耗时："+Evaluation.getTimeDes(cost));
    }
    /**
     * 获取jar中所有Evaluation接口的实现类
     * @param jarFile
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private static List<Class> processJar(File jarFile, Set<String> excludes) throws IOException, ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        JarFile jarfile = new JarFile(jarFile);        
        Enumeration files = jarfile.entries();
        o:while(files.hasMoreElements()){
            JarEntry entry = (JarEntry)files.nextElement();
            for(String exclude : excludes){
                if(entry.getName().contains(exclude)){
                    continue o;
                }
            }
            if(entry.getName().startsWith("org/apdplat/evaluation/impl")
                    && entry.getName().endsWith(".class")){
                String cls = entry.getName().replaceAll("/", ".");
                cls = cls.replaceAll(".class","");
                Class clazz = Class.forName(cls);
                if(Evaluation.class.isAssignableFrom(clazz)){
                    list.add(clazz);  
                }
            }
        }
        return list;
    }
    /**
     * 获取文件夹中所有Evaluation接口的实现类
     * @return
     * @throws ClassNotFoundException 
     */
    private static List<Class> processDir(Set<String> excludes) throws ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        URL url = Evaluator.class.getClassLoader().getResource("org/apdplat/evaluation/Evaluator.class");
        File dir = new File(url.getFile().replace("Evaluator.class", ""), "impl");
        o:for(File file : dir.listFiles()){
            String cls = file.getPath();
            if(cls.endsWith(".java")){
                continue ;
            }
            for(String exclude : excludes){
                if(cls.contains(exclude)){
                    continue o;
                }
            }
            int index = cls.indexOf("org\\apdplat\\evaluation\\impl\\");
            if(index == -1){
                index = cls.indexOf("org/apdplat/evaluation/impl/");
            }
            cls = cls.substring(index);
            cls = cls.replaceAll("\\\\", "\\.");
            cls = cls.replaceAll("/", "\\.");
            cls = cls.replaceAll(".class","");
            Class clazz = Class.forName(cls);
            if(Evaluation.class.isAssignableFrom(clazz)){
                list.add(clazz);
            }
        }
        return list;
    }
}