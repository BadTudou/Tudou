package com.badtudou.view.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.badtudou.controller.CallController;
import com.badtudou.controller.ContactsController;
import com.badtudou.controller.SmsController;
import com.badtudou.model.FragmentViewClickListener;
import com.badtudou.tudou.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ContactsController contactsController;
    private CallController callController;
    private SmsController smsController;
    private List<Map<String,String>> contactsList;
    private SimpleAdapter adapter;
    private View view;
    private ListView listView;

    private FragmentViewClickListener fragmentViewClickListener;
    private View.OnClickListener onClickListener;
    private List<Integer> floatingActionButtonIds;
    private FloatingActionMenu floatingActionMenu;
    private int activiteContactsIndex = -1;

    public ContactsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsListFragment newInstance(String param1, String param2) {
        ContactsListFragment fragment = new ContactsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the expandable_selector for this fragment
        view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        initDates();
        initViews();
        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        floatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                if (opened) {
                    text = "Menu opened";
                } else {
                    text = "Menu closed";
                }
                //Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(activiteContactsIndex == -1){
                    return;
                }
                Map<String,String> map = new HashMap<>();
                map = contactsList.get(activiteContactsIndex);
                Log.d("Test", map.toString());
                switch (id){
                    case R.id.material_design_floating_action_menu_call:
                        Log.d("Test", map.get("number"));
                        callController.callPhone(map.get("number"));
                        break;
                    case R.id.material_design_floating_action_menu_sms:
                        smsController.sendSms(map.get("number"), "");
                        break;
                    case R.id.material_design_floating_action_menu_share:
                        break;
                    case R.id.material_design_floating_action_menu_delete:
                        int result = contactsController.deleteContacts(Long.valueOf(map.get("id")));
                        if (result ==1) {
                            contactsList.remove(activiteContactsIndex);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Log.d("Test", String.valueOf(id));
                        break;
                }
                activiteContactsIndex = -1;
                floatingActionMenu.close(true);

            }
        };

        floatingActionButtonIds = new ArrayList<>();
        floatingActionButtonIds.add(R.id.material_design_floating_action_menu_call);
        floatingActionButtonIds.add(R.id.material_design_floating_action_menu_sms);
        floatingActionButtonIds.add(R.id.material_design_floating_action_menu_share);
        floatingActionButtonIds.add(R.id.material_design_floating_action_menu_delete);

        for(Integer floatingActionButtonId : floatingActionButtonIds){
            FloatingActionButton fb = (FloatingActionButton)view.findViewById(floatingActionButtonId);
            fb.setOnClickListener(onClickListener);
        }



    }

    @Override
    public void onAttach(Context context) {
        fragmentViewClickListener = (FragmentViewClickListener)context;
        super.onAttach(context);
    }

    private void initViews(){
        ImageButton button_add = (ImageButton)view.findViewById(R.id.button_add_contact);
        ImageButton button_switch_contact_style = (ImageButton)view.findViewById(R.id.button_switch_contact_style);

        button_add.setOnClickListener((FragmentViewClickListener)getActivity());
        button_switch_contact_style.setOnClickListener((FragmentViewClickListener)getActivity());

        listView = (ListView)view.findViewById(R.id.contents_list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                activiteContactsIndex = -1;
                floatingActionMenu.close(true);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activiteContactsIndex = position;
//                Long personId = Long.parseLong(contactsList.get(position).get("id"));
//
//                Uri personUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, personId);// info.id联系人ID
//                Intent intent = new Intent(new Intent(Intent.ACTION_VIEW, personUri));
//                Map<Object , Object> map = new HashMap<Object, Object>();
//                map.put("id", personId);
                //fragmentViewClickListener.viewClick(listView, map);
                //startActivity(intent);
                floatingActionMenu.close(true);
                floatingActionMenu.open(true);

            }
        });
    }

    private void initDates(){
        contactsController = new ContactsController(getActivity());
        callController = new CallController(getActivity());
        smsController = new SmsController(getActivity());
        contactsList = contactsController.getContactsList();

        adapter = new SimpleAdapter(view.getContext(), contactsList, R.layout.contacts_list_item,
                new String[]{"id", "name", "number"}, new int[]{R.id.img_head, R.id.txt_name, R.id.txt_phone});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView){
                    String idstring = String.valueOf(data);
                    Long id = Long.valueOf(idstring);
                    InputStream inputStream = contactsController.openPhoto(Long.valueOf(id));
                    Bitmap bmp;
                    if(inputStream != null){
                        bmp = BitmapFactory.decodeStream(inputStream);
                    }else{
                        // ((ImageView) view).setBackgroundResource(R.drawable.vector_drawable_about);
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.vector_drawable_photo_default);
                    }
                    ((ImageView) view).setImageBitmap(bmp);

                    return  true;
                }
                return false;
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
