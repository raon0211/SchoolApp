package in.suhj.banpo.Infrastructure.Helpers;

import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import in.suhj.banpo.App;
import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-01.
 */
public class StorageManager
{
    private static Context context;

    static
    {
        context = App.getContext();
    }

    public static boolean CopyResourceToStorage(int resourceId, String path, boolean force)
    {
        File file = new File(path);

        // 파일이 존재하면 true return
        if (file.exists() && !force)
        {
            return true;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            // Stream 복사
            inputStream = context.getResources().openRawResource(resourceId);
            outputStream = context.openFileOutput(path, Context.MODE_PRIVATE);

            IOUtils.copy(inputStream, outputStream);
        }
        catch (Exception e)
        {
            path = e.getMessage();
            return false;
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }

        return true;
    }
}
