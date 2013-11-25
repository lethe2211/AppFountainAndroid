package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private ImageButton useful1b;
    private TextView useful2;
    private TextView useful2v;
    private ImageButton useful2b;
    private TextView useful3;
    private TextView useful3v;
    private ImageButton useful3b;
    private TextView useful4;
    private TextView useful4v;
    private ImageButton useful4b;
    private TextView useful5;
    private TextView useful5v;
    private ImageButton useful5b;
    private TextView star1;
    private TextView star1v;
    private ImageButton star1b;
    private TextView star2;
    private TextView star2v;
    private ImageButton star2b;
    private TextView star3;
    private TextView star3v;
    private ImageButton star3b;
    private TextView star4;
    private TextView star4v;
    private ImageButton star4b;
    private TextView star5;
    private TextView star5v;
    private ImageButton star5b;
    private TextView question1;
    private TextView question1v;
    private ImageButton question1b;
    private TextView question2;
    private TextView question2v;
    private ImageButton question2b;
    private TextView question3;
    private TextView question3v;
    private ImageButton question3b;
    private TextView question4;
    private TextView question4v;
    private ImageButton question4b;
    private TextView question5;
    private TextView question5v;
    private ImageButton question5b;
    private TextView comment1;
    private TextView comment1v;
    private ImageButton comment1b;
    private TextView comment2;
    private TextView comment2v;
    private ImageButton comment2b;
    private TextView comment3;
    private TextView comment3v;
    private ImageButton comment3b;
    private TextView comment4;
    private TextView comment4v;
    private ImageButton comment4b;
    private TextView comment5;
    private TextView comment5v;
    private ImageButton comment5b;

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
        useful1b = (ImageButton) v.findViewById(R.id.fragment_user_rank_useful_1_button);
        useful2 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_2);
        useful2v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_2_value);
        useful2b = (ImageButton) v.findViewById(R.id.fragment_user_rank_useful_2_button);
        useful3 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_3);
        useful3v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_3_value);
        useful3b = (ImageButton) v.findViewById(R.id.fragment_user_rank_useful_3_button);
        useful4 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_4);
        useful4v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_4_value);
        useful4b = (ImageButton) v.findViewById(R.id.fragment_user_rank_useful_4_button);
        useful5 = (TextView) v.findViewById(R.id.fragment_user_rank_useful_5);
        useful5v = (TextView) v.findViewById(R.id.fragment_user_rank_useful_5_value);
        useful5b = (ImageButton) v.findViewById(R.id.fragment_user_rank_useful_5_button);
        star1 = (TextView) v.findViewById(R.id.fragment_user_rank_star_1);
        star1v = (TextView) v.findViewById(R.id.fragment_user_rank_star_1_value);
        star1b = (ImageButton) v.findViewById(R.id.fragment_user_rank_star_1_button);
        star2 = (TextView) v.findViewById(R.id.fragment_user_rank_star_2);
        star2v = (TextView) v.findViewById(R.id.fragment_user_rank_star_2_value);
        star2b = (ImageButton) v.findViewById(R.id.fragment_user_rank_star_2_button);
        star3 = (TextView) v.findViewById(R.id.fragment_user_rank_star_3);
        star3v = (TextView) v.findViewById(R.id.fragment_user_rank_star_3_value);
        star3b = (ImageButton) v.findViewById(R.id.fragment_user_rank_star_3_button);
        star4 = (TextView) v.findViewById(R.id.fragment_user_rank_star_4);
        star4v = (TextView) v.findViewById(R.id.fragment_user_rank_star_4_value);
        star4b = (ImageButton) v.findViewById(R.id.fragment_user_rank_star_4_button);
        star5 = (TextView) v.findViewById(R.id.fragment_user_rank_star_5);
        star5v = (TextView) v.findViewById(R.id.fragment_user_rank_star_5_value);
        star5b = (ImageButton) v.findViewById(R.id.fragment_user_rank_star_5_button);
        question1 = (TextView) v.findViewById(R.id.fragment_user_rank_question_1);
        question1v = (TextView) v.findViewById(R.id.fragment_user_rank_question_1_value);
        question1b = (ImageButton) v.findViewById(R.id.fragment_user_rank_question_1_button);
        question2 = (TextView) v.findViewById(R.id.fragment_user_rank_question_2);
        question2v = (TextView) v.findViewById(R.id.fragment_user_rank_question_2_value);
        question2b = (ImageButton) v.findViewById(R.id.fragment_user_rank_question_2_button);
        question3 = (TextView) v.findViewById(R.id.fragment_user_rank_question_3);
        question3v = (TextView) v.findViewById(R.id.fragment_user_rank_question_3_value);
        question3b = (ImageButton) v.findViewById(R.id.fragment_user_rank_question_3_button);
        question4 = (TextView) v.findViewById(R.id.fragment_user_rank_question_4);
        question4v = (TextView) v.findViewById(R.id.fragment_user_rank_question_4_value);
        question4b = (ImageButton) v.findViewById(R.id.fragment_user_rank_question_4_button);
        question5 = (TextView) v.findViewById(R.id.fragment_user_rank_question_5);
        question5v = (TextView) v.findViewById(R.id.fragment_user_rank_question_5_value);
        question5b = (ImageButton) v.findViewById(R.id.fragment_user_rank_question_5_button);
        comment1 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_1);
        comment1v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_1_value);
        comment1b = (ImageButton) v.findViewById(R.id.fragment_user_rank_comment_1_button);
        comment2 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_2);
        comment2v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_2_value);
        comment2b = (ImageButton) v.findViewById(R.id.fragment_user_rank_comment_2_button);
        comment3 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_3);
        comment3v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_3_value);
        comment3b = (ImageButton) v.findViewById(R.id.fragment_user_rank_comment_3_button);
        comment4 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_4);
        comment4v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_4_value);
        comment4b = (ImageButton) v.findViewById(R.id.fragment_user_rank_comment_4_button);
        comment5 = (TextView) v.findViewById(R.id.fragment_user_rank_comment_5);
        comment5v = (TextView) v.findViewById(R.id.fragment_user_rank_comment_5_value);
        comment5b = (ImageButton) v.findViewById(R.id.fragment_user_rank_comment_5_button);
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
        setMoveUserPage(useful1b, users.get(0).getId());
        useful2.setText(users.get(1).getName());
        useful2v.setText(users.get(1).getUsefulCount() + " 回");
        setMoveUserPage(useful2b, users.get(1).getId());
        useful3.setText(users.get(2).getName());
        useful3v.setText(users.get(2).getUsefulCount() + " 回");
        setMoveUserPage(useful3b, users.get(2).getId());
        useful4.setText(users.get(3).getName());
        useful4v.setText(users.get(3).getUsefulCount() + " 回");
        setMoveUserPage(useful4b, users.get(3).getId());
        useful5.setText(users.get(4).getName());
        useful5v.setText(users.get(4).getUsefulCount() + " 回");
        setMoveUserPage(useful5b, users.get(4).getId());
    }

    private void setUpRankView(List<User> users) {
        star1.setText(users.get(0).getName());
        star1v.setText(users.get(0).getUp() + " 回");
        setMoveUserPage(star1b, users.get(0).getId());
        star2.setText(users.get(1).getName());
        star2v.setText(users.get(1).getUp() + " 回");
        setMoveUserPage(star2b, users.get(1).getId());
        star3.setText(users.get(2).getName());
        star3v.setText(users.get(2).getUp() + " 回");
        setMoveUserPage(star3b, users.get(2).getId());
        star4.setText(users.get(3).getName());
        star4v.setText(users.get(3).getUp() + " 回");
        setMoveUserPage(star4b, users.get(3).getId());
        star5.setText(users.get(4).getName());
        star5v.setText(users.get(4).getUp() + " 回");
        setMoveUserPage(star5b, users.get(4).getId());
    }

    private void setQuestionRankView(List<User> users) {
        question1.setText(users.get(0).getName());
        question1v.setText(users.get(0).getQuestionCount() + " 回");
        setMoveUserPage(question1b, users.get(0).getId());
        question2.setText(users.get(1).getName());
        question2v.setText(users.get(1).getQuestionCount() + " 回");
        setMoveUserPage(question2b, users.get(1).getId());
        question3.setText(users.get(2).getName());
        question3v.setText(users.get(2).getQuestionCount() + " 回");
        setMoveUserPage(question3b, users.get(2).getId());
        question4.setText(users.get(3).getName());
        question4v.setText(users.get(3).getQuestionCount() + " 回");
        setMoveUserPage(question4b, users.get(3).getId());
        question5.setText(users.get(4).getName());
        question5v.setText(users.get(4).getQuestionCount() + " 回");
        setMoveUserPage(question5b, users.get(4).getId());
    }

    private void setCommentRankView(List<User> users) {
        comment1.setText(users.get(0).getName());
        comment1v.setText(users.get(0).getCommentCount() + " 回");
        setMoveUserPage(comment1b, users.get(0).getId());
        comment2.setText(users.get(1).getName());
        comment2v.setText(users.get(1).getCommentCount() + " 回");
        setMoveUserPage(comment2b, users.get(1).getId());
        comment3.setText(users.get(2).getName());
        comment3v.setText(users.get(2).getCommentCount() + " 回");
        setMoveUserPage(comment3b, users.get(2).getId());
        comment4.setText(users.get(3).getName());
        comment4v.setText(users.get(3).getCommentCount() + " 回");
        setMoveUserPage(comment4b, users.get(3).getId());
        comment5.setText(users.get(4).getName());
        comment5v.setText(users.get(4).getCommentCount() + " 回");
        setMoveUserPage(comment5b, users.get(4).getId());
    }

    private void setMoveUserPage(ImageButton button, final int id) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(self.getActivity(), UserPageActivity.class);
                intent.putExtra(Intent.EXTRA_UID, id);
                self.getActivity().startActivity(intent);
            }
        });
    }

    private String getUrl(String order, int limit) {
        return Common.getApiBaseUrl(this.getActivity()) + "user/ranking?order=" + order + "&limit=" + limit;
    }
}
