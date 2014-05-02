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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 中文分词器分词效果评估程序
 * @author 杨尚川
 */
public class Evaluator {
    public static void main(String[] args) throws Exception{
        File jarFile = new File("cws_evaluation.jar");
        List<Class> classes = new ArrayList<>();
        if(jarFile.exists()){
            classes.addAll(processJar(jarFile));
        }else{
            classes.addAll(processDir());
        }
        //可通过命令行参数指定不评估的分词器
        Iterator<Class> iter = classes.iterator();
        for(String exclude : args){
            while(iter.hasNext()){
                Class clazz = iter.next();
                if(clazz.getSimpleName().startsWith(exclude)){
                    iter.remove();
                    System.out.println("不评估："+clazz.getSimpleName());
                }
            }
        }
        System.out.println("需要评估的分词器：");
        int i=1;
        for(Class clazz : classes){
            System.out.println((i++)+"："+clazz.getSimpleName());
        }
        Collections.reverse(classes);
        List<EvaluationResult> list = new ArrayList<>();
        for(Class clazz : classes){
            Evaluation eval = (Evaluation)clazz.newInstance();
            list.addAll(eval.run());
        }
        Evaluation.generateReport(list);
    }
    /**
     * 获取jar中所有Evaluation接口的实现类
     * @param jarFile
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private static List<Class> processJar(File jarFile) throws IOException, ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        JarFile jarfile = new JarFile(jarFile);        
        Enumeration files = jarfile.entries();
        while(files.hasMoreElements()){   
            JarEntry entry = (JarEntry)files.nextElement();  
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
    private static List<Class> processDir() throws ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        URL url = Evaluator.class.getClassLoader().getResource("org/apdplat/evaluation/Evaluator.class");
        File dir = new File(url.getFile().replace("Evaluator.class", ""), "impl");
        for(File file : dir.listFiles()){
            String cls = file.getPath();
            cls = cls.substring(cls.indexOf("org\\apdplat\\evaluation\\impl\\"));
            cls = cls.replaceAll("\\\\", "\\.");
            cls = cls.replaceAll(".class","");
            Class clazz = Class.forName(cls);
            if(Evaluation.class.isAssignableFrom(clazz)){
                list.add(clazz);
            }
        }
        return list;
    }
}