package com.wjr.andToZip.presenter;

import com.wjr.andToZip.contract.DeskTopContract;
import com.wjr.andToZip.utils.IZippingListener;
import com.wjr.andToZip.utils.StringUtils;
import com.wjr.andToZip.utils.ZipUtils;
import com.wjr.andToZip.view.DeskTopView;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 王金瑞
 * 2018/12/6 0006
 * 9:04
 * com.wjr.andToZip.presenter
 */
public class DeskTopPresenter implements DeskTopContract.BasePresenter {
    private DeskTopContract.BaseView mBaseView;
    private List<String> configList = new ArrayList<>();

    public DeskTopPresenter(DeskTopContract.BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void buildZip(String proPath, String targetPath, String... configs) {
        if (StringUtils.isEmptys(proPath, targetPath)) {
            mBaseView.showDialog(DeskTopView.ERROR_DIALOG, "项目路径或目标路径不能为空！");
            return;
        }
        // 先进行写入配置文件
        buildConfigList(configs);
        mBaseView.startZipping();

        new Thread() {
            @Override
            public void run() {
                File sourceFile;
                try {
                    sourceFile = wirteConfigFile(proPath);
                    ZipUtils.getInstance().setListener(new IZippingListener() {
                        @Override
                        public void updateProgress(int progress) {
                            mBaseView.updateProgress(progress);
                        }
                    });
                    ZipUtils.getInstance().startZipping(sourceFile, targetPath);
                    mBaseView.showDialog(DeskTopView.OPTIONS_DIALOG, "打包完成，是否打开所在目录？");
                } catch (Exception e) {
                    e.printStackTrace();
                    mBaseView.showDialog(DeskTopView.ERROR_DIALOG, "压缩失败：" + e.getMessage());
                } finally {
                    mBaseView.finishZipping();
                }
            }
        }.start();
    }

    private void buildConfigList(String[] config) {
        configList.clear();
        if (config != null && config.length > 0) {
            configList.addAll(Arrays.asList(config));
        }
    }

    /**
     * 在指定目录下写入配置文件
     */
    private File wirteConfigFile(String proPath) throws Exception {
        String configFileName = "AndroidToZip.properties";
        File sourceFile = new File(proPath);
        if (!sourceFile.exists()) {
            throw new Exception("Project Dir Not Found!");
        }
        if (!sourceFile.isDirectory()) {
            throw new Exception("SourceFile Must Be A Directory!");
        }
        // 校验是否为Android项目目录
        File[] files = sourceFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileName = pathname.getName();
                if (fileName.equals("build.gradle")) {
                    return true;
                } else if (fileName.equals("gradle.properties")) {
                    return true;
                }
                return false;
            }
        });
        if (files == null || files.length < 1) {
            throw new Exception("The Dir Not Android Project Dir!");
        }

        File configFile = new File(sourceFile, configFileName);
        if (configFile.exists()) {
            configFile.delete();
        }
        configFile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
        int flag = 0;
        for (String conf : configList) {
            if (flag > 0) {
                bw.newLine();
            }
            bw.write(conf);
            bw.flush();
            flag++;
        }
        bw.close();

        return sourceFile;
    }

    @Override
    public void openTargetDir(String targetPath) {
        try {
            Desktop.getDesktop().open(new File(targetPath));
        } catch (IOException e) {
            e.printStackTrace();
            mBaseView.showDialog(DeskTopView.ERROR_DIALOG, "打开失败，请自行打开");
        }
    }
}
