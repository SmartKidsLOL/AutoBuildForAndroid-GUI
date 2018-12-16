package com.wjr.andToZip.view;

import com.wjr.andToZip.Launcher;
import com.wjr.andToZip.contract.DeskTopContract;
import com.wjr.andToZip.presenter.DeskTopPresenter;
import com.wjr.andToZip.utils.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

/**
 * Created by 王金瑞
 * 2018/12/5 0005
 * 15:24
 * com.wjr.andToZip.view
 */
public class DeskTopView implements DeskTopContract.BaseView, ItemListener {
    private JFrame mDeskTopFrame;
    private int mDefaultWidth = 800;
    private int mDefaultHeight = 500;
    private JButton mProPathBt;
    private JButton mTargetPathBt;
    private JTextField mProPathText;
    private JTextField mTargetPathText;
    private JButton mBuildBt;
    private JButton mCloseBt;
    private JRadioButton mCommonPakRb;
    private JRadioButton mTinkerPakRb;
    private JProgressBar mProgressBar;

    private DeskTopContract.BasePresenter mPresenter;

    private int mPacTypeSelCode = 0;
    private JPanel mProgressParent;

    public static final int ERROR_DIALOG = 0;
    public static final int TIPS_DIALOG = 1;
    public static final int OPTIONS_DIALOG = 2;
    private JTextField mSignFileNameText;

    public void initDeskTopView() {
        initParent();
        initChildLayout();
    }

    private void initChildLayout() {
        mPresenter = new DeskTopPresenter(this);
        initOuterLayout();
        initSelAndroidPathView();
        initChooseTinkerSel();
        initEditSignFileView();
        initOutputPathView();
        initBuildBtView();
        initProgressBar();
        initOwnLayout();
        initClickListener();
    }

    private void initProgressBar() {
        mProgressParent = buildDefaultViewGroup();
        mProgressBar = new JProgressBar();
        mProgressBar.setOrientation(JProgressBar.HORIZONTAL);
        mProgressBar.setMinimum(0);
        mProgressBar.setMaximum(100);
        mProgressBar.setValue(0);
        mProgressBar.setPreferredSize(new Dimension(500, 25));
        mProgressBar.setStringPainted(true);
        mProgressBar.setToolTipText("压缩中，请稍后...");
        mProgressParent.add(mProgressBar);
        mDeskTopFrame.add(mProgressParent);
    }

    // 初始化点击事件
    private void initClickListener() {
        mCloseBt.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGUI();
            }
        });

        mBuildBt.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buildType = "build_pak_type=" + mPacTypeSelCode;
                String signName = mSignFileNameText.getText();
                if (StringUtils.isEmptys(signName) || signName.contains(".")) {
                    showDialog(ERROR_DIALOG, "请输入正确签名文件名，且不带后缀.jks!");
                    return;
                }
                String signFileName = "sign_file_name=" + signName;
                mPresenter.buildZip(mProPathText.getText(), mTargetPathText.getText(), buildType, signFileName);
            }
        });

        mProPathBt.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择路径
                File file = popSelFileWindow();
                if (file != null) {
                    mProPathText.setText(file.getAbsolutePath());
                }
            }
        });

        mTargetPathBt.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择zip输出路径
                File file = popSelFileWindow();
                if (file != null) {
                    mTargetPathText.setText(file.getAbsolutePath());
                }
            }
        });

        mCommonPakRb.addItemListener(this);
        mTinkerPakRb.addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == mCommonPakRb) {
            boolean selected = mCommonPakRb.isSelected();
            if (selected) {
                mPacTypeSelCode = 0;
            }
        } else {
            boolean selected = mTinkerPakRb.isSelected();
            if (selected) {
                mPacTypeSelCode = 1;
            }
        }
    }

    // 弹出选择目录的界面
    private File popSelFileWindow() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showDialog(new JLabel(), "选择目录");
        return fileChooser.getSelectedFile();
    }

    private void initOwnLayout() {
        JPanel jp = buildDefaultViewGroup(30, 0, 0, 0);
        JTextField ownText = new JTextField();
        ownText.setBackground(new Color(0, 0, 0, 0));
        jp.setBackground(new Color(0, 0, 0, 0));
        ownText.setFont(new Font(null, Font.BOLD, 15));
        ownText.setText("感谢使用！");
        ownText.setForeground(Color.WHITE);
        ownText.setEditable(false);
        ownText.setColumns(50);
        ownText.setHorizontalAlignment(JTextField.CENTER);
        ownText.setBorder(new EmptyBorder(0, 0, 0, 0));
        jp.add(ownText);
        mDeskTopFrame.add(jp);
    }

    private void initBuildBtView() {
        JPanel jp = buildDefaultViewGroup();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(50);
        jp.setLayout(flowLayout);
        mBuildBt = new JButton("打Zip包");
        mCloseBt = new JButton("关闭程序");
        jp.add(mBuildBt);
        jp.add(mCloseBt);

        mDeskTopFrame.add(jp);
    }

    private void initOutputPathView() {
        JPanel jp = buildDefaultViewGroup();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(30);
        jp.setLayout(flowLayout);
        mTargetPathBt = new JButton("Zip输出路径：");
        jp.add(mTargetPathBt);

        mTargetPathText = new JTextField();
        mTargetPathText.setFont(new Font(null, 0, 15));
        mTargetPathText.setColumns(40);
        mTargetPathText.setMargin(new Insets(0, 5, 0, 5));
        // 构建默认路径为桌面路径
        File deskTopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        if (deskTopDir != null) {
            mTargetPathText.setText(deskTopDir.getAbsolutePath());
        }
        jp.add(mTargetPathText);

        mDeskTopFrame.add(jp);
    }

    private JPanel buildDefaultViewGroup(int... margins) {
        JPanel jp = new JPanel();
        if (margins != null && margins.length > 0) {
            jp.setBorder(new EmptyBorder(margins[0], margins[1], margins[2], margins[3]));
        } else {
            jp.setBorder(new EmptyBorder(20, 10, 0, 10));
        }
        jp.setOpaque(false);
        return jp;
    }

    // 手写签名文件
    private void initEditSignFileView() {
        JPanel jp = buildDefaultViewGroup();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(10);
        jp.setLayout(flowLayout);
        JLabel label = new JLabel("输入签名文件名(不含后缀.jks，且服务器已经配置相关签名)：");
        label.setForeground(Color.RED);
        label.setFont(new Font(null, Font.BOLD, 14));
        label.setBackground(Color.BLACK);
        jp.add(label);

        mSignFileNameText = new JTextField();
        mSignFileNameText.setFont(new Font(null, 0, 15));
        mSignFileNameText.setColumns(10);
        jp.add(mSignFileNameText);

        mDeskTopFrame.add(jp);
    }

    // 构建是否打Tinker包
    private void initChooseTinkerSel() {
        JPanel jp = buildDefaultViewGroup();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(80);
        jp.setLayout(flowLayout);
        JLabel label = new JLabel("是否选择打Tinker包：");
        label.setForeground(Color.WHITE);
        label.setFont(new Font(null, Font.BOLD, 14));
        label.setBackground(Color.BLACK);
        jp.add(label);

        mCommonPakRb = new JRadioButton("构建普通包", true);
        mTinkerPakRb = new JRadioButton("构建Tinker包");
        ButtonGroup buildTypeRg = new ButtonGroup();
        buildTypeRg.add(mCommonPakRb);
        buildTypeRg.add(mTinkerPakRb);
        jp.add(mCommonPakRb);
        jp.add(mTinkerPakRb);

        mDeskTopFrame.add(jp);
    }

    // 构建选择Android项目路径View
    private void initSelAndroidPathView() {
        JPanel jp = buildDefaultViewGroup();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(30);
        jp.setLayout(flowLayout);
        mProPathBt = new JButton("选择项目路径：");
        jp.add(mProPathBt);
        mProPathText = new JTextField();
        mProPathText.setFont(new Font(null, 0, 15));
        mProPathText.setColumns(40);
        mProPathText.setMargin(new Insets(0, 5, 0, 5));
        jp.add(mProPathText);

        // 添加到parent
        mDeskTopFrame.add(jp);
    }

    // 初始化第一层布局格式
    private void initOuterLayout() {
        FlowLayout outerLayout = new FlowLayout();
        mDeskTopFrame.setLayout(outerLayout);
    }

    /**
     * 初始化桌面布局
     */
    private void initParent() {
        mDeskTopFrame = new JFrame("AndroidToZip工具(GUI)版本");
        mDeskTopFrame.setSize(mDefaultWidth, mDefaultHeight);
        mDeskTopFrame.setResizable(false);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) dimension.getWidth();
        int screenHeight = (int) dimension.getHeight();
        // 设置坐标为windows桌面的中心
        int wLoc = screenWidth / 2 - mDefaultWidth / 2;
        int hLoc = screenHeight / 2 - mDefaultHeight / 2;
        mDeskTopFrame.setLocation(wLoc, hLoc);
        mDeskTopFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 绘制背景
        ImageIcon bg = new ImageIcon(Launcher.class.getResource("black_bg.jpg"));
        JLabel label = new JLabel(bg);
        label.setBounds(0, 0, mDefaultWidth, mDefaultHeight);
        JLayeredPane layeredPane = mDeskTopFrame.getLayeredPane();
        layeredPane.add(label, new Integer(Integer.MIN_VALUE));

        JPanel jp = (JPanel) mDeskTopFrame.getContentPane();
        jp.setOpaque(false);
    }

    public void startGUI() {
        if (mDeskTopFrame != null) {
            mDeskTopFrame.setVisible(true);
        }
    }

    public void exitGUI() {
        if (mDeskTopFrame != null && mDeskTopFrame.isVisible()) {
            mDeskTopFrame.setVisible(false);
            mDeskTopFrame.removeAll();
            mDeskTopFrame = null;
        }
        System.exit(-1);
    }

    // 使所有控件不能点击
    @Override
    public void startZipping() {
        mProPathBt.setEnabled(false);
        mTargetPathBt.setEnabled(false);
        mBuildBt.setEnabled(false);
        mCloseBt.setEnabled(false);
        mProPathText.setEditable(false);
        mTargetPathText.setEditable(false);
        mSignFileNameText.setEditable(false);
        mCommonPakRb.setEnabled(false);
        mTinkerPakRb.setEnabled(false);
    }

    @Override
    public void finishZipping() {
        mProPathBt.setEnabled(true);
        mTargetPathBt.setEnabled(true);
        mBuildBt.setEnabled(true);
        mCloseBt.setEnabled(true);
        mProPathText.setEditable(true);
        mTargetPathText.setEditable(true);
        mSignFileNameText.setEditable(true);
        mCommonPakRb.setEnabled(true);
        mTinkerPakRb.setEnabled(true);
    }

    @Override
    public void updateProgress(int progress) {
        mProgressBar.setValue(progress);
    }

    @Override
    public void showDialog(int type, String message) {
        switch (type) {
            case ERROR_DIALOG:
                buildErrorDialog(message);
                break;
            case TIPS_DIALOG:
                buildTipsdDialog(message);
                break;
            case OPTIONS_DIALOG:
                buildOptionsDialog(message);
                break;
        }
    }

    private void buildErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "错误提示：", JOptionPane.ERROR_MESSAGE);
    }

    private void buildTipsdDialog(String message) {
        JOptionPane.showConfirmDialog(null, message, "提示：", JOptionPane.YES_OPTION);
    }

    private void buildOptionsDialog(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "选择提示：", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            // 打开目标文件夹
            mPresenter.openTargetDir(mTargetPathText.getText());
        }
        mProgressBar.setValue(0);
    }
}
