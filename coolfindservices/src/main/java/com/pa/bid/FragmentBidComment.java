package com.pa.bid;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.PARestClient;
import com.pa.parser.ParserCommentPost;
import com.pa.pojo.CommentPostResult;
import com.pa.pojo.CommentResult;
import com.coolfindservices.androidconsumer.R;

import org.apache.http.Header;

import java.util.ArrayList;

public class FragmentBidComment extends MyFragment implements View.OnClickListener {

    final public static String ARG_COMMENTS = "comments";
    final public static String ARG_SERIAL = "serial";

    public FragmentBidComment() {
        // Required empty public constructor
    }

    private ListView mListView;
    private CommentAdapter mAdapter;
    private Button mBtnSend;
    private EditText mInputComment;

    private String mSerial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid_comment, container, false);
        v.findViewById(R.id.btnBack).setOnClickListener(this);

        mListView = (ListView) v.findViewById(R.id.listView);

        if (getArguments() != null) {
            ArrayList<CommentResult.Comment> commentArrayList =
                    getArguments().getParcelableArrayList(ARG_COMMENTS);
            mAdapter = new CommentAdapter(commentArrayList);
            mListView.setAdapter(mAdapter);
            scrollListViewToBottom();

            mSerial = getArguments().getString(ARG_SERIAL);
        }

        mBtnSend = (Button) v.findViewById(R.id.btnSend);
        mInputComment = (EditText) v.findViewById(R.id.inputComment);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mInputComment.getText().toString().trim();

                if ("".equals(comment)) return;

                // Post comment
                postComment(comment);
            }
        });

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void updateUIBeforePosting(){
        mBtnSend.setEnabled(false);
        loadingDialog.show();
        hideKeyboard(mInputComment);
    }

    private void updateUIAfterPosting(){
        loadingDialog.hide();
        mBtnSend.setEnabled(true);
        mInputComment.setText("");
    }

    private void scrollListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }


    private void postComment(final String text) {
        updateUIBeforePosting();

        RequestParams params = new RequestParams();
        params.add("service_request_serial", mSerial);
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token",
                pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("comment", text);

        PARestClient.post(pref.getPref(Config.SERVER),
                Config.API_POST_BID_COMMENT, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        updateUIAfterPosting();
                        Log.d("Comments", "Response " + new String(responseBody));

                        CommentPostResult result = new ParserCommentPost(new String(responseBody))
                                .getData();

                        if ("success".equalsIgnoreCase(result.status))
                            mAdapter.addComment(text);
                        else
                            Toast.makeText(getActivity(), result.reason, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        super.onFailure(statusCode, headers, responseBody, error);

                        String errorContent = new String(responseBody);
                        Log.d("Comments", "Error " + errorContent);
                        updateUIAfterPosting();
                    }
                });
    }

    public class CommentAdapter extends BaseAdapter {

        private ArrayList<CommentResult.Comment> mDataset;

        class ViewHolder {
            public TextView author, date, text;
        }

        public CommentAdapter(ArrayList<CommentResult.Comment> mDataset) {
            this.mDataset = mDataset;
        }

        public void addComment(String text) {
            CommentResult.Comment newComment = new CommentResult.Comment();
            newComment.author = "You";
            newComment.comment = text;
            newComment.date = "Today";

            mDataset.add(newComment);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataset.size();
        }

        @Override
        public CommentResult.Comment getItem(int position) {
            return mDataset.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_comment, null);

                holder = new ViewHolder();

                holder.author = (TextView) convertView.findViewById(R.id.commentAuthor);
                holder.date = (TextView) convertView.findViewById(R.id.commentDate);
                holder.text = (TextView) convertView.findViewById(R.id.commentText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CommentResult.Comment comment = mDataset.get(position);

            if (comment.author.equalsIgnoreCase("consumer"))
                holder.author.setText("You");
            else
                holder.author.setText(comment.author);
            holder.date.setText(comment.date);
            holder.text.setText(comment.comment);

            return convertView;
        }
    }
}
