package com.yuan.blog.vo.Poem;

import java.util.Date;
import java.util.List;

public class Data {

    private String id;
    private String content;
    private long popularity;
    private Origin origin;
    private List<String> matchTags;
    private String recommendedReason;
    private Date cacheAt;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }
    public long getPopularity() {
        return popularity;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }
    public Origin getOrigin() {
        return origin;
    }

    public void setMatchTags(List<String> matchTags) {
        this.matchTags = matchTags;
    }
    public List<String> getMatchTags() {
        return matchTags;
    }

    public void setRecommendedReason(String recommendedReason) {
        this.recommendedReason = recommendedReason;
    }
    public String getRecommendedReason() {
        return recommendedReason;
    }

    public void setCacheAt(Date cacheAt) {
        this.cacheAt = cacheAt;
    }
    public Date getCacheAt() {
        return cacheAt;
    }
}
