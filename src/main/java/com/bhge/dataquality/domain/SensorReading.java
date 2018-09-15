package com.bhge.dataquality.domain;

public class SensorReading {

    private String tagId;
    private Long tagType;
    private Long timestamp;
    private String value;
    private Long quality;
    private String datatype;
    private AdditionalProperties additionalProperties;

    private Boolean invalid = false;

    public SensorReading() {
    }

    public SensorReading(String tagId, Long tagType, Long timestamp, String value, Long quality, String datatype, AdditionalProperties additionalProperties) {
        this.tagId = tagId;
        this.tagType = tagType;
        this.timestamp = timestamp;
        this.value = value;
        this.quality = quality;
        this.datatype = datatype;
        this.additionalProperties = additionalProperties;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }


    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Long getTagType() {
        return tagType;
    }

    public void setTagType(Long tagType) {
        this.tagType = tagType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getQuality() {
        return quality;
    }

    public void setQuality(Long quality) {
        this.quality = quality;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public AdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(AdditionalProperties additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

}