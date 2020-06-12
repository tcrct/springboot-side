package com.springbootside.duang.db.model;

public abstract class DBConnect {

    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "database";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String URL_FIELD = "url";
    public static final String DRIVER_FIELD = "driver";

    protected String host;
    protected int port;
    protected String database;
    protected String username;
    protected String password;
    protected String url;
    protected String driver;

    public DBConnect(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, null, null);
    }

    public DBConnect(String host, int port, String database, String username, String password, String url, String driver) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.url = getUrl();
        this.driver = getDriver();
    }

    public DBConnect() {

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        if (null == url || url.isEmpty()) {
            StringBuilder urlSb = new StringBuilder();
            urlSb.append("jdbc:mysql://").append(host).append(":").append(port).append("/")
                    .append(database)
                    .append("?autoReconnect=true&useUnicode=true&allowMultiQueries=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
            url = urlSb.toString();
        }
        return url;
    }

    public String getDriver() {
        return null == driver ? "com.mysql.cj.jdbc.Driver" : driver;
    }

    @Override
    public String toString() {
        return "DBConnect{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", driver='" + driver + '\'' +
                '}';
    }
}
