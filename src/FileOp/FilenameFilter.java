/***
 * @filename : FileOp/FilenameFilter.java
 * @description : 文件名过滤器类。
 * @author : 杨彦军 2018141461237
 * @last_modify : 2020年11月15日 20:59:53
 * @version : 1.0.1
 * @What'sNew : nullpointer.
 */
package FileOp;

import java.io.File;

public class FilenameFilter {
    /**
     * 通过后缀名过滤文件。
     * @param file 输入的文件。
     * @param keys 一系列后缀名关键词。
     * @return 是否符合过滤规则。
     *
     * How to use?
     * File test=new File("Z:\\CODE");
     * if(FilenameFilter.endsWith(test, null)){};
     * if(FilenameFilter.endsWith(test, new String[] {})){};
     * if(FilenameFilter.endsWith(test, new String[]{"C","O","DE"})){};
     */
    public static boolean endsWith(File file, final String[] keys){
        if(file != null && file.exists()){
            if(keys == null || keys.length == 0){
                return true;
            }else {
                for(String key: keys){
                    if(file.getName().endsWith(key)){
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
