package com.dace.textreader.bean;

/**
 * 精读模块的划词
 * Created by 70391 on 2017/10/12.
 */

public class WordBean {

    private int start;  //划词开始的位置
    private int length;  //划词内容的长度
    private String grammarId;  //划词内容的语法ID
    private String noteId;  //划词内容的笔记ID

    public WordBean() {
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(String grammarId) {
        this.grammarId = grammarId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

}
