package com.badtudou.view.activity;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badtudou.model.FragmentViewClickListener;
import com.badtudou.view.fragment.CallFragment;
import com.badtudou.view.fragment.ContactsGroupFragment;
import com.badtudou.view.fragment.ContactsListFragment;
import com.badtudou.view.fragment.HistoryGroupFragment;
import com.badtudou.view.fragment.HistoryListFragment;
import com.badtudou.tudou.R;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements
        HistoryListFragment.OnFragmentInteractionListener,
        ContactsListFragment.OnFragmentInteractionListener,
        ContactsGroupFragment.OnFragmentInteractionListener,
        CallFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        FragmentViewClickListener {

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList;
    private Map<Integer,List<Fragment>> navItem2framnetGroup;
    private android.support.v4.app.FragmentTransaction transaction;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()){
                case R.id.navigation_history:
                case R.id.navigation_contacts:
                case R.id.navigation_call:
                    // TODO 根据用户设置切换显示风格
                    showFragment(navItem2framnetGroup.get(item.getItemId()).get(0));
                    setActiviteNavigationItemBar(item.getItemId());
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragments();
//        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
//        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
//        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
//        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
//
//        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu first item clicked
//                Toast.makeText(MainActivity.this, "Fab Clicked1", Toast.LENGTH_LONG).show();
//
//            }
//        });
//        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu second item clicked
//                Toast.makeText(MainActivity.this, "Fab Clicked2", Toast.LENGTH_LONG).show();
//            }
//        });
//        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu third item clicked
//                Toast.makeText(MainActivity.this, "Fab Clicked3", Toast.LENGTH_LONG).show();
//            }
//        });
        //initFloatingActions();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_contacts);

    }

    private void initFloatingActions(){
        List<Integer> floatingActionButtonIds = Arrays.asList(
                R.id.material_design_floating_action_menu_call,
                R.id.material_design_floating_action_menu_mms,
                R.id.material_design_floating_action_menu_share,
                R.id.material_design_floating_action_menu_delete);
//        floatingActionButtonMap = new HashMap<>();
//        materialDesignFAM = (FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
//
//        for(Integer id : floatingActionButtonIds){
//            FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(id);
//            floatingActionButton.setOnClickListener(this);
//            floatingActionButtonMap.put(id, floatingActionButton);
//        }

    }
    // 初始化所有fragment，并绑定导航栏项到fragment组
    private void initFragments(){
        HistoryListFragment historyListFragment = new HistoryListFragment();
        HistoryGroupFragment historyGroupFragment = new HistoryGroupFragment();
        ContactsListFragment contactsListFragment = new ContactsListFragment();
        ContactsGroupFragment contactsGroupFragment = new ContactsGroupFragment();
        CallFragment callFragment = new CallFragment();

        fragmentList = new ArrayList<>();
        navItem2framnetGroup= new HashMap<>();

        // bind NavigationItem id to history fragments
        List<Fragment> historyFragmentList = new ArrayList<>();
        historyFragmentList.add(historyListFragment);
        historyFragmentList.add(historyGroupFragment);
        navItem2framnetGroup.put(R.id.navigation_history, fragmentList);

        // bind NavigationItem id to  contacts fragments
        List<Fragment> contactsFragmentList = new ArrayList<>();
        contactsFragmentList.add(contactsListFragment);
        contactsFragmentList.add(contactsGroupFragment);
        navItem2framnetGroup.put(R.id.navigation_contacts, contactsFragmentList);

        // bind NavigationItem id to  contacts call fragments
        List<Fragment> callFragmentList = new ArrayList<>();
        callFragmentList.add(callFragment);
        callFragmentList.add(callFragment); // 重复项，为了实现上面类型的fragment切换
        navItem2framnetGroup.put(R.id.navigation_call, callFragmentList);

        // merge all fragments
        fragmentList.addAll(historyFragmentList);
        fragmentList.addAll(contactsFragmentList);
        fragmentList.addAll(callFragmentList);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        for(Fragment fragment: fragmentList){
            transaction.add(R.id.content, fragment);
        }

    }

    // 隐藏所有framgnet
    private void hideAllFragments(){
        for(Fragment fragment: fragmentList){
            transaction.hide(fragment);
        }
    }

    // 显示特定的fragment
    private void showFragment(Fragment fragment){
        if(fragment == null){
            initFragments();
        }
        hideAllFragments();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.show(fragment);
        transaction.commit();
    }

    // 切换显示风格：列表 与 分组
    private void switchFramentInGroup(List<Fragment> group){
        int indexOfShowFrament = group.get(0).isVisible()?1:0;
        showFragment(group.get(indexOfShowFrament));

    }

    // 设置活动导航栏的bar
    private void setActiviteNavigationItemBar(int itemId){
        Map<Integer, Integer> item2bar = new HashMap<>();
        item2bar.put(R.id.navigation_history, R.id.navigationItemBar_History);
        item2bar.put(R.id.navigation_contacts, R.id.navigationItemBar_Contacts);
        item2bar.put(R.id.navigation_call, R.id.navigationItemBar_Call);

        for(Integer itemBarId: item2bar.values()){
            ((TextView) findViewById(itemBarId)).setBackgroundResource(R.color.colorActiviBarOff);
        }

        // activite
        ((TextView) findViewById(item2bar.get(itemId))).setBackgroundResource(R.color.colorActiviBarOn);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    // 侧边栏 导航栏 项
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notice) {
            // Handle the camera action
            Log.d("Test", "Click camera");
        }else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_exit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * 隶属于自定义的FragmentViewClickListener，响应Fragment中View发起的Click请求
     *【注意】Fragment主动将特定View的Click事件分发给此函数处理
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_switch_history_style:
                switchFramentInGroup(navItem2framnetGroup.get(R.id.navigation_history));
                break;

            case R.id.button_switch_contact_style:
                switchFramentInGroup(navItem2framnetGroup.get(R.id.navigation_contacts));
                break;

            case R.id.material_design_floating_action_menu_item3:
                Log.d("Test", "click xxx");
                break;


        }
    }

    @Override
    public void viewClick(View v, Map map) {
        int id = v.getId();
        switch (id) {
            case R.id.material_design_floating_action_menu_call:
                Log.d("Test", "click call"+map.toString());
                break;

            case R.id.material_design_floating_action_menu_mms:
                Log.d("Test", "click mms");
                break;

            case R.id.contents_list:
                Log.d("Test", "click button");
                Log.d("Test", "list ddd"+map.toString());
//                materialDesignFAM.setFocusable(true);
//                materialDesignFAM.setFocusableInTouchMode(true);
//                materialDesignFAM.requestFocus();
//                Long personId = (Long)(map.get("id"));
//                if(personId == -1){
//                    materialDesignFAM.close(true);
//                }else{
//                    materialDesignFAM.close(true);
//                    materialDesignFAM.open(true);
//                }


                break;
            default:
                Log.d("Test", String.valueOf(id));
                break;

        }

        Log.d("Test", "dddddd");
    }

}
