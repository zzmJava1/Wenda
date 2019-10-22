package com.newcode.service;
/**
 * 敏感词过滤，字典树算法
 * 用Map表示它的子节点
 */

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {


    //测试
    public static void main(String[] args) {
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("色情");
        System.out.println(sensitiveService.filter("你好*/色 +*情"));
    }
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private boolean isSymbol(char c){
        int ic = (int) c;
        //0x2E80到0x9FFF是东亚字符，前面表示英文，若不是这两者返回true
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E80||ic>0x9FFF);
    }


    public String filter(String txt){
        if(StringUtils.isBlank(txt)){
            return txt;
        }
        String replacement = "***";
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;//记录回滚位置
        int position = 0;//比较位置
        while(position<txt.length()){
            char c = txt.charAt(position);
            if (isSymbol(c)) {
                //若前面没有敏感词前缀，将这个字符添加到结果中
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                result.append(txt.charAt(begin));
                //还原
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                //发现前缀中有字符c继续查找
                position++;

            }

        }
        //最后一段，从begin到最后
        result.append(txt.substring(begin));
        return  result.toString();

    }




    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            //读取文件，可以用类加载器
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt;
            while((lineTxt = bf.readLine())!=null){
                addWord(lineTxt.trim());//处理前后空格

            }
                bf.close();

        }catch (Exception e){
            logger.error("读取敏感词失败"+e.getMessage());
        }
    }

    /**
     * 向字典树中加入一个词
     * @param lineTxt
     */
    public void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineTxt.length() ; i++) {
            Character character = lineTxt.charAt(i);
            if(isSymbol(character)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(character);
            if(node==null){
                node = new TrieNode();
                tempNode.addSubNode(character,node);

            }
            tempNode = node;
            if(i==lineTxt.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    private class TrieNode{
        private boolean end = false;
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public void addSubNode(Character key,TrieNode trieNode){
            subNode.put(key,trieNode);
        }
        public TrieNode getSubNode(Character key){
            return subNode.get(key);

        }
        public boolean isKeyWordEnd(){
            return end;

        }
        public void setKeyWordEnd(boolean end){
            this.end = end;
        }


    }
    private TrieNode rootNode = new TrieNode();

}
