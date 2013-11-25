package com.appfountain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UsersSource;
import com.appfountain.model.User;
import com.appfountain.util.Common;

import java.util.List;

/*
 * UserPageActivityで，ランキング情報を表示するFragment
 */
public class UserRankingFragment extends Fragment {
    private static final String TAG = UserRankingFragment.class.getSimpleName();
    private static final int RANKING_USER_LIMIT = 5;

    private Fragment self = this;

    private TextView useful1;
    private TextView useful1v;
    private TextView useful2;
    private TextView useful2v;
    private TextView useful3;
    private TextView useful3v;
    private TextView useful4;
    private TextView useful4v;
    private TextView useful5;
    private TextView useful5v;
    private TextView star1;
    private TextView star1v;
    private TextView star2;
    private TextView star2v;
    private TextView star3;
    private TextView star3v;
    private TextView star4;
    private TextView star4v;
    private TextView star5;
    private TextView star5v;
    private TextView question1;
    private TextView question1v;
    private TextView question2;
    private TextView question2v;
    private TextView question3;
    private TextView question3v;
    private TextView question4;
    private TextView question4v;
    private TextView question5;
    private TextView question5v;
    private TextView comment1;
    private TextView comment1v;
    private TextView comment2;
    private TextView comment2v;
    private TextView comment3;
    private TextView comment3v;
    private TextView comment4;
    private TextView comment4v;
    private TextView comment5;
    private TextView comment5v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater
                .inflate(R.layout.fragment_user_ranking, container, false);

        initViews(v);
        loadData();
        return v;
    }

    private void initViews(View v) {
        useful1 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_1);
        useful1v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_1_value);
        useful2 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_2);
        useful2v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_2_value);
        useful3 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_3);
        useful3v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_3_value);
        useful4 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_4);
        useful4v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_4_value);
        useful5 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_5);
        useful5v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_5_value);
        star1 = (TextView) v.findViewById(R.id.fragment_user_rank_star_1);
        star1v = (TextView) v.findViewById(R.id.fragment_user_rank_star_1_value);
        star2 = (TextView) v.findViewById(R.id.fragment_user_rank_star_2);
        star2v = (TextView) v.findViewById(R.id.fragment_user_rank_star_2_value);
        star3 = (TextView) v.findViewById(R.id.fragment_user_rank_star_3);
        star3v = (TextView) v.findViewById(R.id.fragment_user_rank_star_3_value);
        star4 = (TextView) v.findViewById(R.id.fragment_user_rank_star_4);
        star4v = (TextView) v.findViewById(R.id.fragment_user_rank_star_4_value);
        star5 = (TextView) v.findViewById(R.id.fragment_user_rank_star_5);
        star5v = (TextView) v.findViewById(R.id.fragment_user_rank_star_5_value);
        question1 = (TextView) v.findViewById(R.id.fragment_user_rank_question_1);
        question1v = (TextView) v.findViewById(R.id.fragment_user_rank_question_1_value);
        question2 = (TextView) v.findViewById(R.id.fragment_user_rank_question_2);
        question2v = (TextView) v.findViewById(R.id.fragment_user_rank_question_2_value);
        question3 = (TextView) v.findViewById(R.id.fragment_user_rank_question_3);
        question3v = (TextView) v.findViewById(R.id.fragment_user_rank_question_3_value);
        question4 = (TextView) v.findViewById(R.id.fragment_user_rank_question_4);
        question4v = (TextView) v.findViewById(R.id.fragment_user_rank_question_4_value);
        question5 = (TextView) v.findViewById(R.id.fragment_user_rank_question_5);
        question5v = (TextView) v.findViewById(R.id.fragment_user_rank_question_5_value);
        comment1 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_1);
        comment1v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_1_value);
        comment2 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_2);
        comment2v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_2_value);
        comment3 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_3);
        comment3v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_3_value);
        comment4 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_4);
        comment4v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_4_value);
        comment5 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_5);
        comment5v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_5_value);
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
                List<User> users = usersSource.getUsers();
                if(users.size() != RANKING_USER_LIMIT)
                    return;
                if (order.equals("up"))
                    setUpRankView(usersSource.getUsers());
                else if (order.equals("useful_count"))
                    setUsefulRankView(usersSource.getUsers());
                else if (order.equals("comment_count"))
                    setCommentRankView(usersSource.getUsers());
                else if (order.equals("question_count"))
                    setQuestionRankView(usersSource.getUsers());
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

    private void setUsefulRankView(List<User> users) {
        useful1.setText(users.get(0).getName());
        useful1v.setText(users.get(0).getUsefulCount() + " 回");
        useful2.setText(users.get(1).getName());
        useful2v.setText(users.get(1).getUsefulCount() + " 回");
        useful3.setText(users.get(2).getName());
        useful3v.setText(users.get(2).getUsefulCount() + " 回");
        useful4.setText(users.get(3).getName());
        useful4v.setText(users.get(3).getUsefulCount() + " 回");
        useful5.setText(users.get(4).getName());
        useful5v.setText(users.get(4).getUsefulCount() + " 回");
    }

    private void setUpRankView(List<User> users) {
        star1.setText(users.get(0).getName());
        star1v.setText(users.get(0).getUp() + " 回");
        star2.setText(users.get(1).getName());
        star2v.setText(users.get(1).getUp() + " 回");
        star3.setText(users.get(2).getName());
        star3v.setText(users.get(2).getUp() + " 回");
        star4.setText(users.get(3).getName());
        star4v.setText(users.get(3).getUp() + " 回");
        star5.setText(users.get(4).getName());
        star5v.setText(users.get(4).getUp() + " 回");
    }

    private void setQuestionRankView(List<User> users) {
        question1.setText(users.get(0).getName());
        question1v.setText(users.get(0).getQuestionCount() + " 回");
        question2.setText(users.get(1).getName());
        question2v.setText(users.get(1).getQuestionCount() + " 回");
        question3.setText(users.get(2).getName());
        question3v.setText(users.get(2).getQuestionCount() + " 回");
        question4.setText(users.get(3).getName());
        question4v.setText(users.get(3).getQuestionCount() + " 回");
        question5.setText(users.get(4).getName());
        question5v.setText(users.get(4).getQuestionCount() + " 回");
    }

    private void setCommentRankView(List<User> users) {
        comment1.setText(users.get(0).getName());
        comment1v.setText(users.get(0).getCommentCount() + " 回");
        comment2.setText(users.get(1).getName());
        comment2v.setText(users.get(1).getCommentCount() + " 回");
        comment3.setText(users.get(2).getName());
        comment3v.setText(users.get(2).getCommentCount() + " 回");
        comment4.setText(users.get(3).getName());
        comment4v.setText(users.get(3).getCommentCount() + " 回");
        comment5.setText(users.get(4).getName());
        comment5v.setText(users.get(4).getCommentCount() + " 回");
    }

    private String getUrl(String order, int limit) {
        return Common.getApiBaseUrl(this.getActivity()) + "user/ranking?order=" + order + "&limit=" + limit;
    }
}
