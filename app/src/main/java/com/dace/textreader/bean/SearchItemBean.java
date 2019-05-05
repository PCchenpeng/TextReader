package com.dace.textreader.bean;

import java.util.List;

public class SearchItemBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"title":"我","image":null,"content":"r","source_image":null,"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=我","search_id":null,"index_id":null,"category":null,"score":null,"format":null,"flag":null,"author":null},{"title":"是","image":null,"content":"v","source_image":null,"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=是","search_id":null,"index_id":null,"category":null,"score":null,"format":null,"flag":null,"author":null},{"title":"中立","image":null,"content":"v","source_image":null,"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=中立","search_id":null,"index_id":null,"category":null,"score":null,"format":null,"flag":null,"author":null},{"title":"哈哈","image":null,"content":"xc","source_image":null,"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=哈哈","search_id":null,"index_id":null,"category":null,"score":null,"format":null,"flag":null,"author":null},{"title":"哈","image":null,"content":"xc","source_image":null,"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=哈","search_id":null,"index_id":null,"category":null,"score":null,"format":null,"flag":null,"author":null}]
     */

    private int status;
    private String msg;
    private List<SubListBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<SubListBean> getData() {
        return data;
    }

    public void setData(List<SubListBean> data) {
        this.data = data;
    }
}
