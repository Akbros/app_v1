package com.oadevelopers.winbazi.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.oadevelopers.winbazi.R;
import com.oadevelopers.winbazi.adapter.LiveAdapter;
import com.oadevelopers.winbazi.common.Config;
import com.oadevelopers.winbazi.common.Constant;
import com.oadevelopers.winbazi.model.LivePojo;
import com.oadevelopers.winbazi.session.SessionManager;
import com.oadevelopers.winbazi.utils.ExtraOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingFragment extends Fragment {

    private View view;

    private ShimmerFrameLayout mShimmerViewContainer;
    private NestedScrollView nestedScrollView;
    private LinearLayout noOngoing;
    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;
    private LinearLayout upcomingLinearLayout;
    private LinearLayout participatedLinearLayout;
    private RecyclerView participatedRecyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<LivePojo> livePojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;

    private SessionManager session;
    private String id;
    private String username;
    private String password;

    private Bundle bundle;
    private String strId, strTitle;

    public OngoingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ongoing, container, false);

        initView();
        initListener();
        initSession();
        loadBundle();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.participatedRecyclerView.setHasFixedSize(true);
        this.participatedRecyclerView.setNestedScrollingEnabled(false);
        this.participatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                    loadMatch();
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Default load for first time
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                    loadMatch();
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void initListener() {
        livePojoList = new ArrayList<>();
    }


    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.participatedRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewParticipated);
        this.upcomingLinearLayout = (LinearLayout) view.findViewById(R.id.upcomingLL);
        this.participatedLinearLayout = (LinearLayout) view.findViewById(R.id.participatedLL);
        this.noOngoing = (LinearLayout) view.findViewById(R.id.noOnGoingLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
    }

    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }

    private void loadMatch() {
        recyclerView.setVisibility(View.GONE);
        noOngoing.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.ONGOING_MATCH_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("game_id", strId);
        builder.appendQueryParameter("user_id", id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noOngoing.setVisibility(View.VISIBLE);
                    }
                });

        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        livePojoList.clear();
        for (int i = 0; i < response.length(); i++) {
            LivePojo livePojo = new LivePojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                livePojo.setId(json.getString("id"));
                livePojo.setTitle(json.getString("title"));
                livePojo.setTime(json.getString("time"));
                livePojo.setPlatform(json.getString("platform"));
                livePojo.setPool_type(json.getString("pool_type"));
                livePojo.setPrize_pool(json.getInt("prize_pool"));
                livePojo.setMatch_desc(json.getString("match_desc"));
                //livePojo.setImage(json.getString("image"));
                livePojo.setPer_kill(json.getInt("per_kill"));
                livePojo.setEntry_fee(json.getInt("entry_fee"));
                livePojo.setEntry_type(json.getString("entry_type"));
                livePojo.setVersion(json.getString("version"));
                livePojo.setMap(json.getString("map"));
                livePojo.setIs_private(json.getString("is_private"));
                livePojo.setMatch_type(json.getString("match_type"));
                livePojo.setSponsored_by(json.getString("sponsored_by"));
                livePojo.setSpectate_url(json.getString("spectate_url"));
                livePojo.setRules(json.getString("rules"));
                livePojo.setMatch_status(json.getString("match_status"));
                livePojo.setMatch_id(json.getString("match_id"));
                livePojo.setRoom_id(json.getString("room_id"));
                livePojo.setRoom_pass(json.getString("room_pass"));
                livePojo.setRoom_size(json.getInt("room_size"));
                livePojo.setTotal_joined(json.getInt("total_joined"));
                livePojo.setJoined_status(json.getString("joined_status"));
                livePojo.setIs_cancel(json.getString("is_cancel"));
                livePojo.setCancel_reason(json.getString("cancel_reason"));
                livePojo.setGame(json.getString("game"));
                livePojo.setUrl(json.getString("url"));
                livePojo.setAdmin_share(json.getInt("admin_share"));
                livePojo.setSlot(json.getInt("slot"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            livePojoList.add(livePojo);
        }
        if (!livePojoList.isEmpty()) {
            adapter = new LiveAdapter(livePojoList, getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noOngoing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        } else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noOngoing.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
        }
    }

    public void onResume() {
        loadMatch();
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            mShimmerViewContainer.startShimmer();
        }
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    private void loadBundle() {
        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null) {
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
        }
    }
}
