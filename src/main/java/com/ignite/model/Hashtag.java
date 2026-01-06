package com.ignite.model;

public class Hashtag {
    private int hashtagId;
    private String phrase;
    private int usageCount;

    // Constructors
    public Hashtag() {
        this.usageCount = 0;
    }

    public Hashtag(String phrase) {
        this();
        this.phrase = phrase;
    }

    public Hashtag(String phrase, int usageCount) {
        this(phrase);
        this.usageCount = usageCount;
    }

    // Business methods
    public void incrementUsage() {
        this.usageCount++;
    }

    public void decrementUsage() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }

    public boolean isTrending() {
        return usageCount >= 10; // Threshold for trending
    }

    public String getFormattedHashtag() {
        return "#" + phrase;
    }

    // Getters and Setters
    public int getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(int hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "hashtagId=" + hashtagId +
                ", phrase='" + phrase + '\'' +
                ", usageCount=" + usageCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return hashtagId == hashtag.hashtagId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(hashtagId);
    }
}