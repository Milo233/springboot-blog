package com.yuan.blog.vo.Poem;

import java.util.List;

public class Origin {

    private String title;
    private String dynasty;
    private String author;
    private List<String> content;
    private String translate;
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }
    public String getDynasty() {
        return dynasty;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
    public List<String> getContent() {
        return content;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
    public String getTranslate() {
        return translate;
    }
}
