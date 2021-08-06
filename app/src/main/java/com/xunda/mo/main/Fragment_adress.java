package com.xunda.mo.main;

import static com.xunda.mo.network.saveFile.getShareData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hyphenate.easeui.constants.EaseConstant;
import com.xunda.mo.R;
import com.xunda.mo.hx.section.chat.activicy.ChatActivity;
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.main.baseView.NewLazyFragment;
import com.xunda.mo.main.friend.Friend_Add;
import com.xunda.mo.main.login.MainLogin_Register;
import com.xunda.mo.model.adress_Model;
import com.xunda.mo.network.saveFile;
import com.xunda.mo.pinyin.ClearEditText;
import com.xunda.mo.pinyin.PinyinComparator;
import com.xunda.mo.pinyin.PinyinUtils;
import com.xunda.mo.pinyin.SortAdapter;
import com.xunda.mo.pinyin.SortModel;
import com.xunda.mo.pinyin.TitleItemDecoration;
import com.xunda.mo.pinyin.WaveSideBar;
import com.xunda.mo.staticdata.NoDoubleClickListener;
import com.xunda.mo.staticdata.viewTouchDelegate;
import com.xunda.mo.xrecycle.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

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
    private RecyclerView All_XRecy;
    private View more_img;
    private View ac_tab_layout;
    private View below_lin;


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

        //在Fragment中使用Activity中控件的方式
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        ac_tab_layout =  activity.findViewById(R.id.ac_tab_layout);


        mComparator = new PinyinComparator();

        mSideBar = (WaveSideBar) view.findViewById(R.id.sideBar);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
        more_img = view.findViewById(R.id.more_img);
        All_XRecy = view.findViewById(R.id.All_XRecy);
//        All_XRecy.setPullRefreshEnabled(false);
//        All_XRecy.setLoadingMoreEnabled(false);
//        initAddHeadView(All_XRecy, getActivity());
//        All_XRecy.setLoadingListener(this);//添加事件

        viewTouchDelegate.expandViewTouchDelegate(more_img, 50, 50, 50, 50);
        more_img.setOnClickListener(new more_imgClickLister());
    }


    private void initAddHeadView(XRecyclerView myView, Context context) {
        View header = LayoutInflater.from(context).inflate(R.layout.adress_headview, null);
//        View rend_Txt_One = header.findViewById(R.id.rend_Txt_One);
//        View rend_Txt_Two = header.findViewById(R.id.rend_Txt_Two);
//        View rend_Txt_Three = header.findViewById(R.id.rend_Txt_Three);
//        View rend_Txt_Four = header.findViewById(R.id.rend_Txt_Four);

        myView.addHeaderView(header);
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
                    if (model.getCode() == -1 || model.getCode() == 500) {
                        Intent intent = new Intent(context, MainLogin_Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (model.getCode() == 200) {

//                        BaseEMRepository baseEMRepository = new BaseEMRepository();
//                        List<EaseUser> users = new ArrayList<>();
//                        for (int i = 0; i < model.getData().size(); i++) {
//                            EaseUser user = new EaseUser();
//                            adress_Model.DataDTO dataDTO = model.getData().get(i);
//                            user.setUsername(dataDTO.getHxUserName());
//                            user.setNickname(dataDTO.getNickName());
//                            user.setAvatar(dataDTO.getHeadImg());
//                            user.setEmail("");
//                            user.setGender(1);
//                            user.setBirth("");
//                            user.setSign("");
//                            user.setExt("");
//                            users.add(user);
//                        }
//                        DemoModel demoModel = new DemoModel(getActivity());
//                        demoModel.updateContactList(users);
//                        baseEMRepository.getUserDao().insert(EmUserEntity.parseList(users));

//                        EaseIM.getInstance().setUserProvider(new EaseUserProfileProvider() {
//                            @Override
//                            public EaseUser getUser(String username) {
//                                //根据username，从数据库中或者内存中取出之前保存的用户信息，如从数据库中取出的用户对象为DemoUserBean
////                                DemoUserBean bean = getUserFromDbOrMemery(username);
//                                model.getData().get(0).getHxUserName()
//                                EaseUser user = DemoHelper.getInstance().getUserInfo(myInfo.getUserInfo().getHxUserName());
////                                EaseUser user = new EaseUser(username);
//                                //设置用户昵称
//                                user.setNickname(bean.getNickname());
//                                //设置头像地址
//                                user.setAvatar(bean.getAvatar());
//                                //最后返回构建的EaseUser对象
//                                return user;
//                            }
//                        });

//                        initView();
                        initViews();
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
                if (errStr.equals("Authorization")) {
                    Intent intent = new Intent(context, MainLogin_Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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


//        mDateList = filledData(getResources().getStringArray(R.array.date));
        mDateList = filledData(model);

        // 根据a-z进行排序源数据
        Collections.sort(mDateList, mComparator);

//        mSideBar.setStringList(listWithoutDup);//右侧栏

        //RecyclerView设置manager
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        All_XRecy.setLayoutManager(manager);
        mAdapter = new SortAdapter(getActivity(), mDateList);
        All_XRecy.setAdapter(mAdapter);
//        All_XRecy.addHeaderView();

//        RecyclerViewHeader header = RecyclerViewHeader.fromXml(requireActivity(), R.layout.adress_headview);
//        header.attachTo(All_XRecy, true);

        mDecoration = new TitleItemDecoration(requireActivity(), mDateList, 1);
        //如果add两个，那么按照先后顺序，依次渲染。
        All_XRecy.addItemDecoration(mDecoration);
//        All_XRecy.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//分割线


        mAdapter.setOnItemClickListener(new SortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
                ChatActivity.actionStart(getActivity(), mDateList.get(position).getHxUserName(), EaseConstant.CHATTYPE_SINGLE);

            }
        });

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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

    private List<String> listWithoutDup;

    private List<SortModel> filledData(adress_Model data) {
        List<String> nameList = new ArrayList<>();
        List<SortModel> mSortList = new ArrayList<>();
//        String[] nameArr = getResources().getStringArray(R.array.date);
//        int size = nameArr.length;
        int size = data.getData().size();
//        adress_Model.DataDTO indexda = new adress_Model.DataDTO();
        for (int i = 0; i < size; i++) {
            adress_Model.DataDTO indexda = data.getData().get(i);
            SortModel sortModel = new SortModel();
//            String name = nameArr[i];
            String name = indexda.getNickname();
            sortModel.setName(name);
            sortModel.setHeadImg(indexda.getHeadImg());
            sortModel.setUserNum(indexda.getUserNum());
            sortModel.setLightStatus(indexda.getLightStatus());
            sortModel.setVipType(indexda.getVipType());
            sortModel.setHxUserName(indexda.getHxUserName());
            //汉字转换成拼音
//            String namePY = name.substring(0, 1);
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
        listWithoutDup = new ArrayList<>(new LinkedHashSet<>(nameList));
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
            filterDateList = filledData(model);
//            filterDateList = filledData(getResources().getStringArray(R.array.date));
        } else {
            filterDateList.clear();
            for (SortModel sortModel : mDateList) {
                String name = sortModel.getName();
                if (name.contains(filterStr) || PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
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

    //更多
    private void showMore(final Context mContext, final View view, final int pos) {
        View contentView = View.inflate(mContext, R.layout.popup_morefriend, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
//        if (MorePopup.isShowing()){
//            return;
//        }
        MorePopup.setWidth(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
//        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        MorePopup.showAsDropDown(view, 20, 12);
        LinearLayout add_lin = contentView.findViewById(R.id.add_lin);
//        TextView newregistr_txt = contentView.findViewById(R.id.newregistr_txt);
        add_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Friend_Add.class);
                startActivity(intent);
                MorePopup.dismiss();
            }
        });

//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MorePopup.dismiss();
//            }
//        });
    }


    private class more_imgClickLister extends NoDoubleClickListener {
        @Override
        protected void onNoDoubleClick(View v) {
            showMore(getActivity(), more_img, 0);
        }
    }
}