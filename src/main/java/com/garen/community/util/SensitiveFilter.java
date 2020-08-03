package com.garen.community.util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Title : 敏感词过滤-Trie
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/8/3 2:57 下午
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";
    private TrieNode root = new TrieNode();
    
    /**
     * @description 前缀树类定义
     */
    private class TrieNode {
        private boolean isEnd = false;
        //子节点（key 下级字符,value 下级节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        
        public boolean isEnd() {
            return isEnd;
        }
        
        public void setEnd(boolean end) {
            isEnd = end;
        }
        
        /**
         * @description 添加子节点
         * @param c
         * @param node
         * @return void
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
        
        /**
         * @description 获取子节点
         * @param c
         * @return com.garen.community.util.SensitiveFilter.TrieNode
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
    
    /**
     * @description 读取敏感词文件 初始化前缀树
     * @param
     * @return void
     */
    @PostConstruct
    public void init() {
        try (//finally中关闭 放在()中自动关闭
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            //缓冲流效率高
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord;
            while((keyWord = reader.readLine()) != null){
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("敏感词文件加载失败:" + e.getMessage());
        }
    }
    private void addKeyWord(String keyWord){
        TrieNode tmpNode = root;
        for (int i = 0; i < keyWord.length(); ++i) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tmpNode.addSubNode(c,subNode);
            }
            tmpNode = subNode;
            //设置结束标志
            if(i == keyWord.length() - 1){
                tmpNode.setEnd(true);
            }
        }
    }
    /**
     * @description 判断特殊字符
     * @param c
     * @return boolean
     */
    public boolean isSymbol(Character c){
        //0x2E80-0x9FFF为东亚文字范围
        return Pattern.matches("[^0-9a-zA-Z\u4e00-\u9fa5]", "" + c);
    }
    
    /**
     * @description 敏感词过滤器
     * @param
     * @return java.lang.String
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //前缀树上的比较指针
        TrieNode tempNode = root;
        //词首指针
        int begin = 0;
        //位置指针
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
    
        while (begin < text.length()) {
            char c = text.charAt(position);
        
            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode == root){
                    sb.append(c);
                    begin++;
                }
                //如果最后一位仍然是符号，那就比较下一个，不能直接加，否则会越界
                if(position==text.length()-1){
                    begin++;
                    position=begin;
                    continue;
                }
                //无论符号在开头或者中间指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {// 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = root;
            } else if (tempNode.isEnd()) {// 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = root;
            } else {
                if (position < text.length() - 1) {// 有这个词，但不是敏感词，继续看看后面的词
                    position++;
                }
            }
        }
        return sb.toString();
    }
}
