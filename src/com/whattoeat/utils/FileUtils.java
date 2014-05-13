package com.whattoeat.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.whattoeat.App;

public class FileUtils {
	/**
	 * 程序数据保存的根路径
	 */
	public static final String BASE_DIR = "/Pada/PadaAppStore/";
	/** APK保存路径 */
	public static final String APK_DIR = BASE_DIR + "apk/";
	/** 图片保存路径 */
	public final static String ICON_CACHE_PATH = BASE_DIR + "img/";
	/** 闪屏图片保存路径 */
	public final static String SPLASH_IMAGE_PATH = ICON_CACHE_PATH
			+ "SplashImage/";
	/** 自更新包路径 */
	public final static String UPDATE_PATH = BASE_DIR + "update/";

	/**
	 * 获取当前有效的存储路径, 以"/"结尾
	 * 
	 * @param path
	 * @return
	 */
	public static String getStorePath(String path) {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				File file = new File(android.os.Environment
						.getExternalStorageDirectory().getPath() + path);
				if (!file.exists()) {
					file.mkdirs();
				}
				String absolutePath = file.getAbsolutePath();
				if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
					absolutePath += "/";
				}
				return absolutePath;
			}
		}
		String absolutePath = App.getAppContext().getFilesDir()
				.getAbsolutePath();
		if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
			absolutePath += "/";
		}
		return absolutePath;
	}

	/**
	 * 从下载链接中提取文件名
	 * 
	 * @param fileUrl
	 * @return
	 */
	public synchronized static String decodeUrl2FileName(String fileUrl,
			String suffix) {
		String fileName = "";
		if (fileUrl != null) {
			int idx = fileUrl.indexOf("?");
			if (idx > 0) {
				fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, idx);
			} else {
				fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			}
		}

		int index = fileName.indexOf(suffix);
		if (index > 0) {
			fileName = fileName.substring(0, index);
		}
		// Log.v(TAG, "fileName:"+fileName);
		// 保证文件名的唯一性
		// fileName = fileName + System.currentTimeMillis( );
		// 转换成英文小写
		return fileName.toLowerCase();
	}

	public static String getImageFileNameByUrl(String url) {
		String path = FileUtils.getStorePath(FileUtils.ICON_CACHE_PATH);
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path + url.hashCode() + ".pada";
	}

	public static String getSplashImageFileNameByUrl(String url) {
		return url.hashCode() + ".pada";
	}

	/*
	 * 返回存储的image图片path 如果为null，则图片不存在
	 */
	public static String getSaveImagePathByUrl(String url) {
		String filePath = getImageFileNameByUrl(url);
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		return filePath;
	}

	public static String saveBitmap(Bitmap bitmap, String url) {
		try {
			String fileName = getImageFileNameByUrl(url);
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// modify at 20140104 allenduan
	// 取消包检测
	public static boolean isApkFileBroken(String apkPath) {
		// PackageManager pm = App.getAppContext( ).getPackageManager( );
		// PackageInfo info = pm.getPackageArchiveInfo( apkPath ,
		// PackageManager.GET_SIGNATURES );
		// if( info == null || info.applicationInfo == null )
		// {
		// return true;
		// }
		return false;
	}

	/**
	 * 获取可用的手机SD卡或内存的容量大小
	 * 
	 * @return
	 */
	public static long getAvailableStorageSize() {
		String root = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			root = Environment.getExternalStorageDirectory().getPath();
		} else {
			root = Environment.getDataDirectory().getPath();
		}
		LogUtils.e("检测的磁盘，root=" + root);
		File base = new File(root);
		StatFs stat = new StatFs(base.getPath());
		long nAvailableCount = stat.getBlockSize()
				* ((long) stat.getAvailableBlocks() - 4);
		LogUtils.e("磁盘剩余的空间，nAvailableCount=" + nAvailableCount);
		return nAvailableCount;
	}

	/**
	 * 在SPLASH_IMAGE_PATH文件夹下创建文件
	 */
	public static File creatFile(String path) throws IOException {
		File file = new File(path);
		file.createNewFile();
		return file;
	}

	/**
	 * 判断SPLASH_IMAGE_PATH文件夹下的文件夹是否存在
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入storage
	 */
	public static File write2StorageFromInput(String path, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			file = creatFile(path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static void makeSplashDir() {
		File directory = new File(FileUtils.getStorePath(SPLASH_IMAGE_PATH));
		if (!directory.exists()) {
			directory.mkdir();
		}
	}
}
