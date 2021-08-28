package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Friend_Details_Bean {

    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("data")
    private DataDTO data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend_Details_Bean that = (Friend_Details_Bean) o;
        return Objects.equals(msg, that.msg) &&
                Objects.equals(code, that.code) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, code, data);
    }

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("fireType")
        private String fireType;
        @JsonProperty("grade")
        private Double grade;
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("hxUserName")
        private String hxUserName;
        @JsonProperty("lightStatus")
        private Long lightStatus;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("remarkName")
        private String remarkName;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("source")
        private String source;
        @JsonProperty("tag")
        private String tag;
        @JsonProperty("isFriend")
        private Long isFriend;
        @JsonProperty("teamNames")
        private String teamNames;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Double userNum;
        @JsonProperty("vipType")
        private int vipType;
        @JsonProperty("areaName")
        private String areaName;
        @JsonProperty("friendStatus")
        private String friendStatus;
        @JsonProperty("isSilence")
        private Long isSilence;
    }
}
