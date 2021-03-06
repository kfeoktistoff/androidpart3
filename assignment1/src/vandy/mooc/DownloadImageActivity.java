package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        final Uri uri = getIntent().getData();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        Runnable downloadTask = new Runnable() {
            @Override
            public void run() {
                final Uri imageLocalUri = DownloadUtils.downloadImage(DownloadImageActivity.this, uri);

                if (imageLocalUri != null) {
                    DownloadImageActivity.this.setResult(RESULT_OK,
                            DownloadImageActivity.this.getIntent().putExtra(MainActivity.IMAGE_PATH_CODE, imageLocalUri.toString())
                    );
                } else {
                    DownloadImageActivity.this.setResult(RESULT_CANCELED);
                }

                DownloadImageActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        DownloadImageActivity.this.finish();
                    }
                });
            }
        };

        new Thread(downloadTask).start();

    }
}
