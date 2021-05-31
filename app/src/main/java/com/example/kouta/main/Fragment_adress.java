package com.example.kouta.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.kouta.R;
import com.example.kouta.baseview.NewLazyFragment;
import com.example.kouta.model.adress_Model;
import com.example.kouta.network.saveFile;
import com.example.kouta.pinyin.ClearEditText;
import com.example.kouta.pinyin.PinyinComparator;
import com.example.kouta.pinyin.PinyinUtils;
import com.example.kouta.pinyin.SortAdapter;
import com.example.kouta.pinyin.SortModel;
import com.example.kouta.pinyin.TitleItemDecoration;
import com.example.kouta.pinyin.WaveSideBar;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.example.kouta.network.saveFile.getShareData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_adress#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_adress extends NewLazyFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View parentView;


    private RecyclerView mRecyclerView;
    private WaveSideBar mSideBar;
    private SortAdapter mAdapter;
    private ClearEditText mClearEditText;
    private LinearLayoutManager manager;

    private List<SortModel> mDateList;
    private TitleItemDecoration mDecoration;
    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator mComparator;


    public Fragment_adress() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_adress.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_adress newInstance(String param1, String param2) {
        Fragment_adress fragment = new Fragment_adress();
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
////        View parentView = inflater.inflate(R.layout.fragment_adress, container, false);
//        if (parentView == null) {
//            parentView = inflater.inflate(R.layout.fragment_adress, null);
//
//            initView(parentView);
//            initData();
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
        return R.layout.fragment_adress;
    }


    //        public static String[] b = { "Q", "R", "S", "T", "U", "V",
//            "W", "X", "Y", "Z", "#"};
    @Override
    protected void initView(View view) {
        super.initView(view);
//        assort = (AssortView) view.findViewById(R.id.assort);
//        friendList = (HeaderListView) view.findViewById(R.id.friendList);

        mComparator = new PinyinComparator();

        mSideBar = (WaveSideBar) view.findViewById(R.id.sideBar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);


    }

    @Override
    protected void initData() {
        super.initData();
        adressData(getActivity(), saveFile.BaseUrl + saveFile.User_Friendlist_Url, "0");
    }

    //    private void initView(View parentView) {
//
//    }


//    private void initData() {
//        adressData(getActivity(), saveFile.BaseUrl + saveFile.User_Friendlist_Url, "0");
//    }

    adress_Model model;

    //联系人列表
    public void adressData(final Context context, String baseUrl, String projectId) {
        RequestParams params = new RequestParams(baseUrl);
        if (getShareData("JSESSIONID", context) != null) {
            params.setHeader("Authorization", getShareData("JSESSIONID", context));
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String resultString) {

//                resultString = "{msg:dclO,code:200, data:[{ciphertext:sSE3,fireType:436905937989336.8,headImg:X%kU,hxUserName:5CQo,isCiphertext:-7165580097951464, lightStatus:6140465580380565,remarkName:JCC39l,userId:gSlQQ,userNum:5762441269959617,vipType:-3782876530108332.5}]}";
                if (resultString != null) {
                    model = new Gson().fromJson(resultString, adress_Model.class);
                    if (model.getCode() == 200) {

//                        initView();
                        initViews();
                    } else if (model.getCode() == -1) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                String errStr = throwable.getMessage();
                if (errStr.equals("Unauthorized")) {
                    Intent intent = new Intent(context, MainLogin_Register.class);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(Callback.CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

//    public static List<SortModel> choiceModel = new ArrayList<>();
//    private HeaderListView friendList;
//    private AssortView assort;
//    private List<SortModel> ListModel;
//    private SortAdapter hotadapter;
//    private CharacterParser characterParser;//汉字转换成拼音的类
//    private PinyinComparator pinyinComparator;//根据拼音来排列ListView里面的数据类
//
//    private void initView() {
//        // 实例化汉字转拼音类
//        characterParser = CharacterParser.getInstance();
//
//        pinyinComparator = new PinyinComparator();
//
//        if (model != null) {
//            ListModel = filledData(model);
//            // 根据a-z进行排序源数据
//            Collections.sort(ListModel, pinyinComparator);
//        }
//
//        assort.setStringList(b);
//        Map<Integer, Boolean> mcheckflag = new HashMap<>();
//        for (int i = 0; i < ListModel.size(); i++) {//初始化
//            mcheckflag.put(i, false);
//        }
////        if (choiceModel != null || choiceModel.size() > 0) {//有选择@人
////            for (int k = 0; k < ListModel.size(); k++) {
////                for (int i = 0; i < choiceModel.size(); i++) {
////                    if (choiceModel.get(i).getId().equals(ListModel.get(k).getId())) {
////                        mcheckflag.put(k, true);
////                    }
////                }
////            }
////        }
////        choiceLin_List(choiceModel);//加载@人
//
//        hotadapter = new SortAdapter(getActivity(), ListModel, mcheckflag);
//        friendList.setAdapter(hotadapter);
//        //创建HeaderView
//        View HeaderView = getLayoutInflater().inflate(R.layout.item_header, friendList, false);
//        friendList.setPinnedHeader(HeaderView);
//        friendList.setOnScrollListener(hotadapter);
//
//        // 设置右侧触摸监听
//        assort.setOnTouchingLetterChangedListener(new AssortView.OnTouchingLetterChangedListener() {
//            @Override
//            public void onTouchingLetterChanged(String s) {
//                // 该字母首次出现的位置
//                int position = hotadapter.getPositionForSection(s.charAt(0));
//                if (position != -1) {
//                    friendList.setSelection(position);
//                }
//            }
//        });
//
//
//        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SortAdapter.ViewHolder viewHolder = (SortAdapter.ViewHolder) view.getTag();
////                if (choiceModel.size() < 10) {
////                    viewHolder.choice_check.toggle();
////                    hotadapter.mcheckflag.put(position, viewHolder.choice_check.isChecked());
////                    if (viewHolder.choice_check.isChecked()) {
////                        choiceModel.add(ListModel.get(position));
////                    } else {
////                        removeData(position);
////                    }
////                } else {
////                    //人数大于10人
////                    if (viewHolder.choice_check.isChecked()) {
////                        viewHolder.choice_check.toggle();
////                        hotadapter.mcheckflag.put(position, viewHolder.choice_check.isChecked());
////                        removeData(position);
////
////                    } else {
////                        Toast.makeText(getActivity(), "最多选择10人", Toast.LENGTH_SHORT).show();
////                    }
////
////                }
////                choiceLin_List(choiceModel);
//
////                Intent intent = new Intent();
////                intent.putExtra("result", mSortList.get(position).getName());
////                /*
////                 * 调用setResult回传值，在onActivityResult取值
////                 */
////                setResult(2000, intent);
////                finish();
//
//            }
//        });
//    }
//
//    //删除数据
//    private void removeData(int pos) {
//        for (int i = 0; i < choiceModel.size(); i++) {
//            if (choiceModel.get(i).getId().equals(ListModel.get(pos).getId())) {
//                choiceModel.remove(i);
//            }
//        }
//    }
//
//    /**
//     * 为ListView填充数据
//     *
//     * @param
//     * @return
//     */
//    List<SortModel> mSortList;
//    List<String> nameList = new ArrayList<>();
//    private List<SortModel> filledData(adress_Model list) {
//        mSortList = new ArrayList<SortModel>();
//        for (int i = 0; i < list.getData().size(); i++) {
//            SortModel sortModel = new SortModel();
//            sortModel.setId(list.getData().get(i).getUserId() + "");
//            sortModel.setName(list.getData().get(i).getHxUserName());
//            sortModel.setImgUrl(list.getData().get(i).getHeadImg());
//            // 汉字转换成拼音
//            if (TextUtils.isEmpty(list.getData().get(i).getHxUserName())) {
//                sortModel.setSortLetters("#");
//            } else {
//                String pinyin = characterParser.getSelling(list.getData().get(i).getHxUserName());
//                String sortString = pinyin.substring(0, 1).toUpperCase();
//                // 正则表达式，判断首字母是否是英文字母
//                if (sortString.matches("[A-Z]")) {
//                    sortModel.setSortLetters(sortString.toUpperCase());
//
////                    nameList.add(sortString.toUpperCase());
////                    LinkedHashSet<String> linked = new LinkedHashSet<>(nameList);
//////                  List<String> listWithoutDup = new ArrayList<String>(new LinkedHashSet<String>(nameList));
////                    b = linked.toArray(new String[linked.size()]);
//                } else {
//                    sortModel.setSortLetters("#");
//
////                    nameList.add("#");
////                    b = nameList.toArray(new String[nameList.size()]);
//                }
//            }
//            mSortList.add(sortModel);
//        }
//        return mSortList;
//    }
//
//
//    @Override
//    public int getPinnedHeaderState(int position) {
//        return 0;
//    }
//
//    @Override
//    public void configurePinnedHeader(View headerView, int position, int alpaha) {
//
//    }


    private void initViews() {

        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });


        mDateList = filledData(getResources().getStringArray(R.array.date));
//        mDateList = filledData(model);

        // 根据a-z进行排序源数据
        Collections.sort(mDateList, mComparator);

        mSideBar.setStringList(listWithoutDup);//右侧栏

        //RecyclerView设置manager
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SortAdapter(getActivity(), mDateList);
        mRecyclerView.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(getActivity(), mDateList);
        //如果add两个，那么按照先后顺序，依次渲染。
        mRecyclerView.addItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,  int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
    List<String> nameList = new ArrayList<>();
    private List<String> listWithoutDup;

    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<>();

        String[] nameArr = getResources().getStringArray(R.array.date);
        int size = nameArr.length;

        for (int i = 0; i < size; i++) {
            SortModel sortModel = new SortModel();
            String name = nameArr[i];
            sortModel.setName(name);
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
                nameList.add(sortString.toUpperCase());//右边排序
            } else {
                sortModel.setLetters("#");
                nameList.add("#");
            }

            mSortList.add(sortModel);
        }
        listWithoutDup = new ArrayList<String>(new LinkedHashSet<String>(nameList));
        Collections.sort(listWithoutDup);
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
//            filterDateList = filledData(model);
            filterDateList = filledData(getResources().getStringArray(R.array.date));
        } else {
            filterDateList.clear();
            for (SortModel sortModel : mDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                ) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();
    }


}