package com.xunda.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupMember_Bean  implements Serializable{
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO implements Serializable{
        @JsonProperty("headImg")
        private String headImg;
        @JsonProperty("identity")
        private Integer identity;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userNum")
        private Integer userNum;
        @JsonProperty("vipType")
        private Integer vipType;
        @JsonProperty("hxUserName")
        private String hxUserName;
    }
}
