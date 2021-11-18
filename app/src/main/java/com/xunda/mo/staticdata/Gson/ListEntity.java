package com.xunda.mo.staticdata.Gson;

import java.util.List;

public class ListEntity<T> {
    private String total;
    private String hasnext;
    private String count;
    private String page;
    private String totalPages;
    private List<T> list;

    //省略set和get ...

}