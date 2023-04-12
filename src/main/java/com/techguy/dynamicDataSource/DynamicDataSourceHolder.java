package com.techguy.dynamicDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicDataSourceHolder {
  public static final String DB_MASTER = "master";
  public static final String DB_SLAVE = "slave";

  private static final ThreadLocal<String> contextHolder=new ThreadLocal<>();
  public static String getDbType(){
      String db = contextHolder.get();
      if(db ==null){
         db = DB_SLAVE;
      }
    return db;
  }
  public static void setDbType(String type){
      log.info("Current datasource "+type);
      contextHolder.set(type);
  }
  public static void clearDbType(){
      contextHolder.remove();
  }
}
