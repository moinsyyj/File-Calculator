/***
 * @filename : FileOp/FileOpGUI.java
 * @description : GUI实现。1.1：增加多线程功能，UI不会再无响应了。
 * @author : 杨彦军 2018141461237
 * @last_modify : 2020年11月9日 00:25:06
 * @version : 1.1
 * FILE ENCODING : GBK
 */
package FileOp;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileOpGUI extends Application {
    private File rootDir = new File(System.getProperty("user.home") + "\\Desktop");

    @Override
    public void start(Stage primaryStage) {
        final GridPane global = new GridPane();
        // row 1
        final TextField dirPath=new TextField(rootDir.getAbsolutePath());
        dirPath.setEditable(false);
        final Label l1 = new Label("文件夹路径：", dirPath);
        l1.setContentDisplay(ContentDisplay.RIGHT);
        final Button b1 = new Button("浏览");
        b1.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择目标文件夹");
            File targetDir = chooser.showDialog(primaryStage);
            if(targetDir != null) rootDir = targetDir;
            dirPath.setText(rootDir.getAbsolutePath());
        });
        global.add(l1, 0, 0);
        global.add(b1, 1, 0);
        // row 2
        final HBox checkBoxPane = new HBox();
        checkBoxPane.setSpacing(10);
        final CheckBox cBlank = new CheckBox("过滤空行");
        //cBlank.setSelected(true);
        final CheckBox cComment = new CheckBox("过滤注释");
        //cComment.setSelected(true);
        checkBoxPane.getChildren().addAll(cBlank, cComment);
        final Label l2 = new Label("过滤器选择：", checkBoxPane);
        l2.setContentDisplay(ContentDisplay.RIGHT);
        global.add(l2, 0, 1);
        // row 3
        final TextField fileType=new TextField(".cpp" + " .java" + " .py" + " .c" + " .h");
        final Label l3 = new Label("源文件类型：", fileType);
        l3.setContentDisplay(ContentDisplay.RIGHT);
        final Button b3 = new Button("查找");
        final Label l4 = new Label();
        l4.setContentDisplay(ContentDisplay.RIGHT);
        l4.setGraphicTextGap(16);
        final TextArea result=new TextArea();
        result.setEditable(false);
        result.setPrefRowCount(5);
        result.setPrefColumnCount(10);
        final ProgressIndicator indicator = new ProgressIndicator();
        EventHandler<ActionEvent> search = event -> {
            l4.setText("搜索中...");
            l4.setGraphic(indicator);
            dirPath.setDisable(true);
            b1.setDisable(true);
            cBlank.setDisable(true);
            cComment.setDisable(true);
            fileType.setDisable(true);
            b3.setDisable(true);
            // 子进程
            Task task = new Task() {
                int[] stats = null;
                long processingTime;

                @Override
                protected Object call() throws Exception {
                    long  startTime = System.currentTimeMillis();
                    try {
                        String[] keys = fileType.getText().split(" ");
                        ArrayList<File> list1 = FileOp.getFileList(rootDir);
                        ArrayList<File> list2 = FileOp.filterFileList(list1,keys);
                        stats = FileOp.countFileList(list1,list2);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    long endTime = System.currentTimeMillis();
                    processingTime = endTime - startTime;
                    return null;
                }

                @Override
                protected void succeeded(){
                    result.setText("文件夹数：" + stats[0] +
                            "\n总文件数：" + stats[1] +
                            "\n源文件数：" + stats[2] +
                            "\n代码行数：" + stats[3] +
                            "\n处理时间：" + processingTime + " ms.");
                    l4.setText("搜索结果：");
                    l4.setGraphic(result);
                    dirPath.setDisable(false);
                    b1.setDisable(false);
                    cBlank.setDisable(false);
                    cComment.setDisable(false);
                    fileType.setDisable(false);
                    b3.setDisable(false);
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        };
        fileType.setOnAction(search);
        b3.setOnAction(search);
        global.add(l3, 0, 2);
        global.add(b3, 1, 2);
        // row 4
        global.add(l4, 0, 3);
        // display
        primaryStage.setTitle("File Operator GUI");
        primaryStage.setScene(new Scene(global,280,170));
        primaryStage.show();
    }



    public static void main(String[] args){
        Application.launch();
    }

}
