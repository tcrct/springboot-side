package com.springbootside.duang.db.dto;

import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.springbootside.duang.db.annotation.Param;
import com.springbootside.duang.db.model.IdEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchListDto implements java.io.Serializable {

    /**
     * 页数,由0开始
     */
    @Param(label = "页数", desc = "页数,由0开始", defaultValue = "0")
    private int pageNo = 0;
    /**
     * 页行数，默认每页10行
     */
    @Param(label = "页行数", desc = "页行数，默认每页10行", defaultValue = "10")
    private int pageSize = 10;
    /**
     * 搜索字段集合
     */
    @Param(label = "搜索对象集合", desc = "将搜索条件封装成SearchDto", defaultValue = "SearchDto")
    private List<SearchDto> searchDtoList;
    /**
     * 多条件查询时，and 或 or 链接 SearchDto对象值, 如果值为空，默认为and查询
     */
    @Param(label = "查询模式", desc = "多条件查询时，and 或 or 链接 SearchDto对象值, 如果值为空，默认为and查询", defaultValue = "and")
    private String operator = "and";

    @Param(name="key", label = "标识关键字", desc = "写在xml或md文件里的sql标识关键字")
    private String key;

    @Param(label = "排序对象集合", desc = "将排序条件封装成OrderByDto")
    private List<OrderByDto> orderByDtoList;

    @Param(label = "查询字段集合", desc = "指定查询的字段")
    private List<String> fieldList;

    @Param(label = "groupby字段", desc = "groupby字段")
    private List<String> groupByList;

    public SearchListDto() {
    }

    public SearchListDto(String tokenId, int pageNo, int pageSize, List<SearchDto> searchDtoList,
                         List<OrderByDto> orderByDtoList, String operator, List<String> fieldList, List<String> groupByList) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.searchDtoList = searchDtoList;
        this.orderByDtoList = orderByDtoList;
        this.operator = operator;
        this.fieldList = fieldList;
        this.groupByList = groupByList;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<SearchDto> getSearchDtoList() {
        return searchDtoList;
    }

    public void setSearchDtoList(List<SearchDto> searchDtoList) {
        this.searchDtoList = searchDtoList;
    }

    public List<OrderByDto> getOrderByDtoList() {
        return orderByDtoList;
    }

    public void setOrderByDtoList(List<OrderByDto> orderByDtoList) {
        this.orderByDtoList = orderByDtoList;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public List<String> getGroupByList() {
        return groupByList;
    }

    public void setGroupByList(List<String> groupByList) {
        this.groupByList = groupByList;
    }

    public Map<String, Object> toMap() {
        if (null == searchDtoList) {
            throw new NullPointerException("搜索条件集合不能为空！");
        }
        Map<String,Object> paramMap = new HashMap<>(searchDtoList.size());
        for (SearchDto searchDto : searchDtoList) {
            if (null != searchDto) {
                paramMap.put(searchDto.getField(), searchDto.getValue());
            }
        }
        return paramMap;
    }

    public String toOrderByStr() {
        if (null == orderByDtoList) {
            orderByDtoList = new ArrayList<>();
            orderByDtoList.add(new OrderByDto(IdEntity.ID_FIELD, OrderByDto.DESC_FIELD));
        }
        StringBuilder orderStr = new StringBuilder();
        for (OrderByDto orderByDto : orderByDtoList) {
            orderStr.append(orderByDto.getField()).append(" ").append(orderByDto.getDirection()).append(",");
        }
        if (orderStr.length()>1) {
            orderStr.deleteCharAt(orderStr.length()-1);
        }
        return orderStr.toString();
    }

    public String toGroupByStr() {
        if (null == groupByList) {
            return "";
        }
        StringBuilder groupByStr = new StringBuilder();
        for (String str : groupByList) {
            groupByStr.append(str).append(",");
        }
        if (groupByStr.length() > 1) {
            groupByStr.deleteCharAt(groupByStr.length()-1);
        }
        return groupByStr.toString();
    }
}
