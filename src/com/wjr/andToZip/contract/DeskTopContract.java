package com.wjr.andToZip.contract;

/**
 * Created by 王金瑞
 * 2018/12/6 0006
 * 9:02
 * com.wjr.andToZip.contract
 */
public interface DeskTopContract {
    public interface BaseView {
        void startZipping();

        void finishZipping();

        void updateProgress(int progress);

        void showDialog(int type, String message);
    }

    public interface BasePresenter {
        void buildZip(String proPath, String targetPath, String... configs);

        void openTargetDir(String targetPath);
    }
}
