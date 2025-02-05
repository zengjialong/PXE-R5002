package com.chs.mt.pxe_r500.cache.base;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.chs.mt.pxe_r500.cache.util.CacheConfig;
import com.chs.mt.pxe_r500.cache.util.CacheUtils;
import com.chs.mt.pxe_r500.cache.util.LogUtil;

/**
 * Ӳ�̻���.
 *@Title:
 *@Description:
 *@Author:Justlcw
 *@Since:2014-3-5
 *@Version:
 */
public final class ImageDiskLruCache extends DiskLruCache
{
    /** ��־TAG. **/
    private final static String TAG = "ImageDiskLruCache";
    
    /**Hint to the compressor, 0-100. 0 meaning compress for small size, 
     * 100 meaning compress for max quality. 
     * Some formats, like PNG which is lossless, will ignore the quality setting
     */
    private int mCompressQuality = 100;

    /**
     * ���캯��.
     * @param cacheDir �����ļ�Ŀ¼
     * @param maxByteSize ��󻺴��С
     */
    protected ImageDiskLruCache(File cacheDir, long maxByteSize)
    {
        super(cacheDir, maxByteSize);
    }
    
    /**
     * ��һ��ͼƬӲ�̻���.
     * @Description:
     * @Author Justlcw
     * @Date 2014-3-6
     */
    public final static ImageDiskLruCache openImageCache(Context context, String cacheName, long maxByteSize)
    {
        File cacheDir = CacheUtils.getEnabledCacheDir(context, cacheName);
        if (cacheDir.isDirectory() && cacheDir.canWrite() && CacheUtils.getUsableSpace(cacheDir) > maxByteSize)
        {
            return new ImageDiskLruCache(cacheDir, maxByteSize);
        }
        return null;
    }
    
    /**
     * ����ͼƬ.

     * @param bitmap bitmap
     * @Description:
     * @Author Justlcw
     * @Date 2014-3-5
     */
    public final void putImage(String url, Bitmap bitmap)
    {
        synchronized (mLinkedHashMap)
        {
            if (mLinkedHashMap.get(url) == null)
            {
                final String filePath = createFilePath(url);
                
                if (writeBitmapToFile(bitmap, filePath, url))
                {
                    onPutSuccess(url, filePath);
                    flushCache();
                }
            }
        }
    }

    /**
     * ��ȡͼƬ.

     * @return bitmap
     * @Description:
     * @Author Justlcw
     * @Date 2014-3-5
     */
    public final Bitmap getImage(String url)
    {
        synchronized (mLinkedHashMap)
        {
            final String filePath = mLinkedHashMap.get(url);
            if (!TextUtils.isEmpty(filePath))
            {
                LogUtil.d(TAG, "cache hit : " + url);
                return BitmapFactory.decodeFile(filePath);
            }
            else
            {
                final String existFilePath = createFilePath(url);
                if (new File(existFilePath).exists())
                {
                    onPutSuccess(url, existFilePath);
                    LogUtil.d(TAG, "cache hit : " + url);
                    return BitmapFactory.decodeFile(existFilePath);
                }
            }
            return null;
        }
    }

    /**
     * ��bitmapд�뵽�����ļ���.
     * @param bitmap bitmap
     * @param filePath �����ļ�·��
     * @Description:
     * @Author Justlcw
     * @Date 2014-3-5
     */
    private boolean writeBitmapToFile(Bitmap bitmap, String filePath, String url)
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new BufferedOutputStream(new FileOutputStream(filePath), CacheConfig.IO_BUFFER_SIZE);
            return bitmap.compress(getCompressFormat(url), mCompressQuality, outputStream);
        }
        catch (FileNotFoundException e)
        {
            LogUtil.d(TAG, "bitmap compress fail : " + filePath, e);
        }
        finally
        {
            try
            {
                if(outputStream != null)
                {
                    outputStream.close(); 
                }
            }
            catch (IOException e)
            {
                LogUtil.d(TAG, "close outputStream error : "+e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * �����ļ����ͻ��CompressFormat.
     * @Description:
     * @Author Justlcw
     * @Date 2014-3-7
     */
    private CompressFormat getCompressFormat(String url)
    {
        String lowerUrl = url.toLowerCase(Locale.ENGLISH);
        if(lowerUrl.endsWith(".jpg"))
        {
            return CompressFormat.JPEG;
        }
        else if(lowerUrl.endsWith(".png"))
        {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }
}
