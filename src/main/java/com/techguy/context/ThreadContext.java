package com.techguy.context;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThreadContext {
    private static final ThreadLocal<Map<String, Object>> threadLocalMap = ThreadLocal.withInitial(HashMap::new);

     private ThreadContext(){};

     @SuppressWarnings("unchecked")
     public static<T> T get(String key){
         return (T)threadLocalMap.get().get(key);
     }

     public static <T> T get(String key, T defaultValue){
         T value = get(key);
         return value==null? defaultValue : value;
     }

     public static void  set(String key, Object value){
         threadLocalMap.get().put(key, value);
     }

     public static void remove(String key){
         threadLocalMap.get().remove(key);
     }
   public  static  void close(){
         threadLocalMap.remove();
   }
}
