package com.xunda.mo.main;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.xunda.mo.R;
import com.xunda.mo.hx.DemoHelper;
import com.xunda.mo.main.baseView.NewLazyFragment;
import com.xunda.mo.main.chat.activity.UserDetail_Set;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.network.saveFile;

public class Fragment_Person extends NewLazyFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View parentView;

    public Fragment_Person() {
        // Required empty public constructor
    }

    public static Fragment_Person newInstance(String param1, String param2) {
        Fragment_Person fragment = new Fragment_Person();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        if (parentView == null) {
//            parentView = inflater.inflate(R.layout.fragment__person, null);
//
//
//        }
//        ViewGroup parent = (ViewGroup) parentView.getParent();
//        if (parent != null) {
//            parent.removeView(parentView);
//        }
//        return parentView;
//    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment__person;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
       ImageView person_img =  view.findViewById(R.id.person_img);
        person_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetail_Set.actionStart(requireContext());
            }
        });

       RelativeLayout person_set_rel =  view.findViewById(R.id.person_set_rel);
        person_set_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               logout();
            }
        });

    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
//                        finishOtherActivities();
//                        startActivity(new Intent(mContext, LoginActivity.class));
//                        Intent intent = new Intent(getActivity(), MainLogin_Register.class);
//                        startActivity(intent);
//                        finish();
                        saveFile.clearShareData("JSESSIONID", getActivity());
                        Intent intent = new Intent(getActivity(), MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}