package vandy.mooc.course_project;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Kirill Feoktistov on 20.04.15
 */

public class TaskFragment extends Fragment {
    private Button buttonGet;
    private Button buttonCancel;
    private ImageView imageView;
    private EditText linkTextView;
    private AsyncTask currentTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.download_fragment, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initListeners();
    }

    public void initUI() {
        buttonGet = (Button) getActivity().findViewById(R.id.download_button);
        buttonCancel = (Button) getActivity().findViewById(R.id.cancel_button);
        imageView = (ImageView) getActivity().findViewById(R.id.image);
        linkTextView = (EditText) getActivity().findViewById(R.id.link);
    }

    public void initListeners() {
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = Uri.parse(linkTextView.getText().toString());
                if (URLUtil.isValidUrl(imageUri.toString())) {
                    new DownloadTask().execute(imageUri);
                } else {
                    Utils.showToast(getActivity(), "Image URL is not valid");
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTask != null && !currentTask.isCancelled()) {
                    currentTask.cancel(true);
                }
            }
        });
    }

    public void onCancel() {
        Utils.showToast(getActivity(), "Cancelled");
        imageView.setImageResource(0);
        buttonGet.setEnabled(true);
        currentTask = null;
    }

    private class DownloadTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected void onPreExecute() {
            Utils.showToast(getActivity(), "Downloading");
            imageView.setImageResource(0);
            buttonGet.setEnabled(false);
            currentTask = this;
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            return params.length > 0 ? Utils.downloadImage(getActivity(), params[0]) : null;
        }

        @Override
        protected void onPostExecute(final Uri result) {
            currentTask = null;

            if (result != null) {
                new FilterTask().execute(result);
            } else {
                Utils.showToast(getActivity(), "Download failed");
                TaskFragment.this.onCancel();
            }
        }

        @Override
        public void onCancelled() {
            TaskFragment.this.onCancel();
        }
    }

    private class FilterTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected void onPreExecute() {
            currentTask = this;
            Utils.showToast(getActivity(), "Filtering");
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            return params.length > 0 ? Utils.grayScaleFilter(getActivity(), params[0]) : null;
        }

        @Override
        protected void onPostExecute(final Uri result) {
            currentTask = null;
            Utils.showToast(getActivity(), "Done");
            imageView.setImageURI(result);
            buttonGet.setEnabled(true);
        }

        @Override
        public void onCancelled() {
            TaskFragment.this.onCancel();
        }
    }
}
