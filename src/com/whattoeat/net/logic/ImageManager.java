package com.whattoeat.net.logic;

import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.whattoeat.net.AsyncHttpSessionManager;
import com.whattoeat.net.HttpSessionConstant;
import com.whattoeat.net.ImageDownloadSession;
import com.whattoeat.utils.FileUtils;
import com.whattoeat.utils.LogUtils;

public class ImageManager extends AsyncHttpSessionManager
{
    private static ImageManager instance = null;

    // private final Object decodeLock = new Object();
    // private final Object saveFileLock = new Object();
    // private final Object downloadLock = new Object();

    private final LruCache< String , Bitmap > mBitmapCache = new LruCache< String , Bitmap >( 32 );
    private final WeakHashMap< String , Handler > mUrl2HandlerMap = new WeakHashMap< String , Handler >( );

    // private final Map<String, String> mUrl2PathDecodeMap = new
    // ConcurrentHashMap<String, String>();
    // private final Vector<String> mDecodeQueue = new Vector<String>();
    // private final Vector<String> mDownloadQueue = new Vector<String>();

    private ImageManager( int mMaxConnectCount )
    {
	super( mMaxConnectCount );
    }

    public static ImageManager getInstance()
    {
	if( instance == null )
	{
	    LogUtils.i("ImageManager.getInstance " );
	    instance = new ImageManager( HttpSessionConstant.MAX_IMAGE_DOWNLOAD_CONNECTIONS );
	}
	return instance;
    }

    public void clearImage( String url )
    {
	Bitmap bitmap = mBitmapCache.get( url );
	if( bitmap != null )
	{
	    mBitmapCache.remove( url );
	}
    }

    public Bitmap getImage( String url , Handler imageHandler , boolean isListScrollStateIdle )
    {

	Bitmap bitmap = mBitmapCache.get( url );
	if( bitmap != null )
	{
	    return bitmap;
	}

	// //是否在本地解码队列里
	// if (mDecodeQueue.contains(url)) {
	// return null;
	// }

	if( imageHandler != null )
	    mUrl2HandlerMap.put( url , imageHandler );

	// //在本地图片cache文件中找
	//	String path = FileUtils.getSaveImagePathByUrl( url );
	//	if( path != null )
	//	{
	//	    addDecodeBitmapQueue( url , path );
	//	    return null;
	//	}
	//
	// //从网络请求图片
	// addDownloadIconQueue(url);

	handleSingleSession( url );

	return null;
    }

    // private void addDecodeBitmapQueue(String url, String path) {
    // boolean needNofity = false;
    // synchronized (mDecodeQueue) {
    // if (mDecodeQueue.contains(url) == false) {
    // mDecodeQueue.add(url);
    // mUrl2PathDecodeMap.put(url, path);
    // needNofity = true;
    // }
    // }
    //
    // if (needNofity) {
    // synchronized (decodeLock) {
    // decodeLock.notify();
    // }
    // }
    // }
    //
    // private void addDownloadIconQueue(String url) {
    // if (mDownloadQueue.contains(url))
    // return;
    //
    // mDownloadQueue.add(url);
    // synchronized (downloadLock) {
    // downloadLock.notify();
    // }
    // }

    // public void startGettingImages(ArrayList<String> urls) {
    // for (String url : urls) {
    // handleSingleSession(url);
    // }
    // }

    private void handleSingleSession( String url )
    {
	ImageDownloadSession session = new ImageDownloadSession( url );
	session.registerCallBack( mFileDownloadController );
	submit( session );
    }

    private void doCallback( String url , Bitmap bm )
    {

	LogUtils.i(  mUrl2HandlerMap.size( ) +"" );
	Message msg = Message.obtain( );
	Object [] obj = new Object [2];
	obj[0] = url;
	obj[1] = bm;
	msg.what = MSG_CONSTANTS.MSG_IMAGE_DECODE_FINISH;
	msg.obj = obj;

	// 通知UI界面处理Icon请求完成事件
	Handler imageHandler = mUrl2HandlerMap.remove( url );
	if( imageHandler != null )
	{
	    imageHandler.sendMessage( msg );
	}
    }

    private void updateBitmapCache( String url , Bitmap bm )
    {
	mBitmapCache.put( url , bm );
	doCallback( url , bm );
    }

    // class DecoderThread extends Thread {
    // public DecoderThread() {
    // }
    //
    // private boolean bRun = true;
    //
    // @Override
    // public void run() {
    //
    // while (bRun) {
    // synchronized (decodeLock) {
    // try {
    // decodeLock.wait();
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // while (mDecodeQueue.size() > 0) {
    // String url = null;
    // String path = null;
    //
    // synchronized (mDecodeQueue) {
    // url = mDecodeQueue.get(0);
    // path = mUrl2PathDecodeMap.get(url);
    // mDecodeQueue.remove(url);
    // mUrl2PathDecodeMap.remove(url);
    // }
    //
    // if (path != null && path.length() > 0) {
    // Bitmap bitmap = null;
    // try {
    // bitmap = BitmapFactory.decodeFile(path);
    //
    // if (bitmap != null) {
    // mBitmapCache.put(url, bitmap);
    // doCallback(url, bitmap);
    // } else {
    // Log.d(Tag, "bitmapDecode File" + url);
    // }
    // } catch (OutOfMemoryError e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // }
    // }
    //
    // @Override
    // public void destroy() {
    // bRun = false;
    // synchronized (mDecodeQueue) {
    // mDecodeQueue.clear();
    // }
    // synchronized (decodeLock) {
    // decodeLock.notify();
    // }
    // }
    // }

    private final IImageDownloadListener mFileDownloadController = new IImageDownloadListener( )
    {

	@Override
	public void onSucceed( String rspData )
	{

	}

	@Override
	public void onError( int errCode , String strErr )
	{

	}

	@Override
	public void onDownloadIconException( String url , int errorCode )
	{

	}

	@Override
	public void onDownloadIconFinish( String url , byte [] data )
	{
	    // 获取的网络数据，首先放入缓存，然后存为本地文件
	    Bitmap bitmap = BitmapFactory.decodeByteArray( data , 0 , data.length );
	    // mBitmapCache.put(url, bitmap);
	    updateBitmapCache( url , bitmap );

	    FileUtils.saveBitmap( bitmap , url );
	}

	@Override
	public void onGetIcon( String url , Bitmap bitmap )
	{
	    // 在本地获取的图片，直接放入缓存
	    // mBitmapCache.put(url, bitmap);
	    updateBitmapCache( url , bitmap );
	}
    };

}
