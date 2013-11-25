package com.appfountain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UsersSource;
import com.appfountain.util.Common;

/*
 * UserPageActivityで，ランキング情報を表示するFragment
 */
public class UserRankingFragment extends Fragment {
    private static final String TAG = UserRankingFragment.class.getSimpleName();
    private static final int RANKING_USER_LIMIT = 6;

    private Fragment self = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater
                .inflate(R.layout.fragment_user_ranking, container, false);

        loadData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadData() {
        if (!Common.isInternetAvailable(self.getActivity())) {
            Toast.makeText(
                    self.getActivity(),
                    getString(R.string.common_internet_unavailable),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        Log.d(TAG, "send requests");
        queue.add(getReq("up"));
        queue.add(getReq("useful_count"));
        queue.add(getReq("comment_count"));
        queue.add(getReq("question_count"));
    }

    private GsonRequest<UsersSource> getReq(final String order) {
        return new GsonRequest<UsersSource>(Method.GET, getUrl(order, RANKING_USER_LIMIT), UsersSource.class, null, null, new Listener<UsersSource>() {
            @Override
            public void onResponse(UsersSource usersSource) {
                // TODO implement this
                Log.d(TAG, "get " + order);
                Log.d(TAG, "size = " + usersSource.getUsers().size());
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String responseBody = null;
                try {
                    responseBody = new String(
                            error.networkResponse.data, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(self.getActivity(), responseBody,
                        Toast.LENGTH_SHORT).show();
            }
        }
        );
    }

    private String getUrl(String order, int limit) {
        return Common.getApiBaseUrl(this.getActivity()) + "user/ranking?order=" + order + "&limit=" + limit;
    }
}
