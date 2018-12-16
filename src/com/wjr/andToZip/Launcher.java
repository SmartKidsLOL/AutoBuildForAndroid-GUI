package com.wjr.andToZip;

import com.wjr.andToZip.view.DeskTopView;

/**
 * Created by 王金瑞
 * 2018/12/5 0005
 * 15:24
 * com.wjr.andToZip
 */
public class Launcher {
    public static void main(String[] args) {
        DeskTopView view = new DeskTopView();
        view.initDeskTopView();
        view.startGUI();
    }
}
