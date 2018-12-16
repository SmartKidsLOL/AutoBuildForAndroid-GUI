package com.wjr.andToZip.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 王金瑞
 * 2018/12/6 0006
 * 15:04
 * com.wjr.andToZip.utils
 */
public class ZipUtils {
    private int sourceFileLen;
    private int zippingLen;
    // 自定义的split规则：
    private String splitRegex = "-AutoBuild-";

    private IZippingListener mListener;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

    public void setListener(IZippingListener listener) {
        mListener = listener;
    }

    public static ZipUtils getInstance() {
        return ZipUtilsHolder.UTILS;
    }

    private static class ZipUtilsHolder {
        private static final ZipUtils UTILS = new ZipUtils();
    }

    // 递归计算文件数量
    public void calculateFilesLen(File file) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();

            for (File fi : listFiles) {
                if (fi.isDirectory()) {
                    calculateFilesLen(fi);
                } else {
                    sourceFileLen++;
                }
            }
        } else {
            sourceFileLen++;
        }
    }

    // 开始压缩文件
    public void startZipping(File sourceFile, String targetPath) throws IOException {
        sourceFileLen = 0;
        zippingLen = 0;

        String timeName = mSimpleDateFormat.format(new Date(System.currentTimeMillis()));
        String zipFileName = sourceFile.getName() + splitRegex + timeName + ".zip";
        String zipFilePath = targetPath + File.separator + zipFileName;

        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        // 先计算所有文件数量
        calculateFilesLen(sourceFile);

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
        compressZip(zos, sourceFile, sourceFile.getName());
        zos.closeEntry();
        zos.close();
    }

    // 递归
    public void compressZip(ZipOutputStream zos, File file, String zipFileName) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();

            for (File fi : listFiles) {
                if (fi.isDirectory()) {
                    compressZip(zos, fi, zipFileName + File.separator + fi.getName());
                } else {
                    zipping(zos, fi, zipFileName);
                }
            }
        } else {
            zipping(zos, file, zipFileName);
        }
    }

    // 压缩具体操作
    public void zipping(ZipOutputStream zos, File file, String zipFileName) throws IOException {
        ZipEntry entry = new ZipEntry(zipFileName + File.separator + file.getName());
        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[1024];
        int read;
        while ((read = bis.read(buffer)) != -1) {
            zos.write(buffer, 0, read);
            zos.flush();
        }
        bis.close();

        zippingLen++;
        int progress = (int) ((zippingLen * 1.0 / sourceFileLen) * 100);
        System.out.println(progress + "---" + zippingLen + "---" + sourceFileLen);
        if (mListener != null) {
            mListener.updateProgress(progress);
        }
    }
}
