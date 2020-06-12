package com.springbootside.duang.common;

import com.springbootside.duang.common.utils.SettingKit;

import java.util.List;

public class App {
    public static void main(String[] args) {
        List<String> stringList = SettingKit.duang().key("123").defaultValue("123456").getList();
//        List<Integer> integerList = SettingKit.duang().key("123").defaultValue("123456").getList();
        System.out.println(stringList);
//        System.out.println(integerList);

    }
}
