package com.xunda.mo.model;

import com.baozi.treerecyclerview.annotation.TreeDataType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xunda.mo.main.friend.group.AreaItem;
import com.xunda.mo.main.friend.group.ProvinceItem;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Friend_MyGroupBean {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    @TreeDataType(iClass = ProvinceItem.class, bindField = "type")
    public static class DataDTO {
        @JsonProperty("count")
        private Integer count;
        @JsonProperty("groupList")
        private List<GroupListDTO> groupList;
        @JsonProperty("listName")
        private String listName;

        @NoArgsConstructor
        @Data
//        @TreeDataType(iClass = CountyItem.class)
        @TreeDataType(iClass = AreaItem.class)
        public static class GroupListDTO {
            @JsonProperty("groupHeadImg")
            private String groupHeadImg;
            @JsonProperty("groupHxId")
            private String groupHxId;
            @JsonProperty("groupId")
            private String groupId;
            @JsonProperty("groupName")
            private String groupName;
        }
    }
}
