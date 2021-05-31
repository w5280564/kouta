package com.example.kouta.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kouta.R;
import com.example.kouta.baseview.NewLazyFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Person#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Person extends NewLazyFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View parentView;

    public Fragment_Person() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Person.
     */
    // TODO: Rename and change types and number of parameters
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
}