/***
 * @filename : FileOp/FileOp.java
 * @description : 文件搜索底层功能实现。1.3:debug nullpointer。
 * @author : 杨彦军 2018141461237
 * @last_modify : 2020年11月9日 09:48:21
 * @version : 1.3
 */
package FileOp;

import java.io.*;
import java.util.ArrayList;

public class FileOp {
    public static void main(String[] args){

        File rootDir1=new File("Z:\\Downloads\\System");
        File rootDir2=new File("Z:/CODE/CppP");
        File rootDir3=new File("C:\\Windows");
        String[] keys = new String[] {".cpp",".java",".py",".c",".h"};
        System.out.println("Hello World.");
        long  startTime = System.currentTimeMillis();

        try {
            ArrayList<File> list1 = getFileList(rootDir2);
            ArrayList<File> list2 = filterFileList(list1,keys);
            int[] stats = countFileList(list1,list2);
            System.out.println("Total directory number: " + stats[0] +
                    "\nTotal file number: " + stats[1] +
                    "\nTotal code file number: " + stats[2] +
                    "\nTotal code file line number: " + stats[3]);
//            printFileListAbsolutePath(list1);
//            System.out.println("Found file:");
//            printFileListAbsolutePath(list2);
        }catch (IOException e){
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Processing time: " + (endTime - startTime) + " ms.");
    }

    public static void printFileTree(ArrayList<String> fileList){
        int dirlevel=0;
        for(String name: fileList) {
            if (name.equals(".")) {
                dirlevel++;
            } else if (name.equals("..")) {
                dirlevel--;
            } else {
                for (int i = 1; i < dirlevel; i++) {
                    System.out.print("  ");
                }
                if(dirlevel!=0) {
                    System.out.print("├─");
                }
                System.out.println(name);
            }
        }
    }

    public static void printFileListAbsolutePath(ArrayList<File> fileList){
        if(fileList != null){
            for(File t:fileList){
                System.out.println(t.getAbsolutePath());
            }
        }
    }

    public static ArrayList<File> getFileList(File rootDir) throws IOException {
        if(rootDir != null && rootDir.exists() && rootDir.isDirectory()){
            ArrayList<File> list=new ArrayList<File>();
            list.add(rootDir);
            File[] files=rootDir.listFiles();
            if(files != null){ // 访问无权限文件夹时，listFiles()方法返回null
                for(File t: files){
                    if(t.isDirectory()){
                        list.addAll(getFileList(t));
                    }else {
                        list.add(t);
                    }
                }
            }
            return list;
        }else {
            throw new IOException("!!!This is not a directory or doesn't exist.");
        }
    }

    public static ArrayList<File> filterFileList(ArrayList<File> fileList, final String[] keys){
        ArrayList<File> list=new ArrayList<File>();
        if(fileList != null){
            for(File t: fileList){
                if(FilenameFilter.endsWith(t, keys)){
                    list.add(t);
                }
            }
        }
        return list;
    }

    public static ArrayList<File> filterFileList(File rootDir, final String[] keys) throws IOException {
        ArrayList<File> list = getFileList(rootDir);
        return filterFileList(list, keys);
    }

    public static int[] countFileList(ArrayList<File> fileList, ArrayList<File> codeFileList){
        int dirNum = 0;
        int fileNum = 0;
        int codeFileNum = 0;
        int codeLineNum = 0;
        //统计所有文件列表
        if(fileList != null){
            for(File t: fileList){
                if(t.isDirectory()){
                    dirNum++;
                }else {
                    fileNum++;
                }
            }
        }
        //统计代码文件列表
        if(codeFileList != null){
            codeFileNum = codeFileList.size();
            for(File t: codeFileList){
                codeLineNum += getFileLineNum(t);
            }
        }
        return new int[]{dirNum,fileNum,codeFileNum,codeLineNum};
    }

    private static int getFileLineNum(File file) {
        try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
            reader.skip(Long.MAX_VALUE);//直接跳到文件尾
            int lineNumber = reader.getLineNumber();
            return lineNumber + 1;//实际上是读取换行符数量 , 所以需要+1
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int copyFile(String srcPath, String dstPath){
        try{
            File src = new File(srcPath);
            if (!src.exists()||!src.isFile()) {
                throw new Exception("!!!源文件不存在或者不是一个文件！");
            }
            File dst = new File(dstPath);
            if (dst.exists()) {
                throw new Exception("!!!目标文件已存在！");
            }else{
                dst.createNewFile();
            }

            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
                out.flush();
            }
            in.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

}
