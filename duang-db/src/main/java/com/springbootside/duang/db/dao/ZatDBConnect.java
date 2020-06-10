package com.springbootside.duang.db.dao;

import com.springbootside.duang.db.model.DBConnect;

public class ZatDBConnect extends DBConnect {

    public ZatDBConnect() {
        super("172.16.10.80", 3306, "coupon_dev", "root", "12345678");
    }

}
