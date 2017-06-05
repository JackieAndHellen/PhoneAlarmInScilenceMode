package com.example.cnp.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity implements InputDialog.DialogClickListener {
    InputDialog mInputDialog;
    ListView mPhoneNumberListView;
    ArrayAdapter mAdapter;
    FloatingActionButton mFab;
    ArrayList<UserItem> mUserItems = new ArrayList<>();
    static class ViewHolder{
        private TextView userName;
        private TextView phoneNumber;
        private TextView musicPath;
        private ImageView edit;
        private ImageView delete;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        registerBroadCast();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mPhoneNumberListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<UserItem>(getApplicationContext(),R.layout.item_layout,mUserItems) {


            @Override
            public int getCount() {
                return mUserItems.size();
            }

            @Override
            public UserItem getItem(int position) {
                return mUserItems.get(position);
            }


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final UserItem item = mUserItems.get(position);
                ViewHolder holder = null;
                if(convertView == null){
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.item_layout,null);

                    holder.userName = (TextView) convertView.findViewById(R.id.tv_user_name);
                    holder.phoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
                    holder.musicPath = (TextView) convertView.findViewById(R.id.tv_music_path);
                    holder.edit = (ImageView) convertView.findViewById(R.id.iv_edit);
                    holder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                    convertView.setTag(holder);
                }
                holder = (ViewHolder) convertView.getTag();
                holder.userName.setText(item.userName);
                holder.phoneNumber.setText(item.phoneNumber);
                holder.musicPath.setText(item.musicPath);
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInputDialog == null){
                            mInputDialog = new InputDialog(ScrollingActivity.this);
                            mInputDialog.setInitData(item,position);
                        }
                        if(!mInputDialog.isAdded() && !mInputDialog.isVisible() && !mInputDialog.isRemoving()){
                            mInputDialog.setInitData(item,position);
                            mInputDialog.show(getFragmentManager(),"");
                        }
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUserItems.remove(item);
                        mAdapter.notifyDataSetChanged();
                        SaveObjectUtils.setObject(ScrollingActivity.this,"phone","useritem",mUserItems);

                    }
                });
                return convertView;
            }

        };
        mPhoneNumberListView.setAdapter(mAdapter);
        mPhoneNumberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        setSupportActionBar(toolbar);

        mFab= (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "好用的话,请分享给好友", Snackbar.LENGTH_LONG)
                        .setAction("点赞,支付宝微信集成", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
        Intent phone_listener = new Intent(ScrollingActivity.this , Phone_listener.class);
        startService(phone_listener);
        mFab.setImageResource(android.R.drawable.star_on);
        ArrayList<UserItem> userItems = SaveObjectUtils.getObject(this,"phone","useritem");
        if(userItems!=null){
            mUserItems.clear();
            mUserItems.addAll(userItems);
            mAdapter.notifyDataSetChanged();
        }


    }

    private BroadcastReceiver mBroadcastReceiver = null;

    private void registerBroadCast() {
        IntentFilter iF = createBroadCast();
        if(iF != null &&iF.countActions()>0){
            mBroadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals("refresh_ui")){
                        if(intent.getExtras().getInt("id") == R.id.fab){
                            if(mFab!= null){
                                mFab.setImageResource(android.R.drawable.star_on);
                            }

                        }
                    }
                }
            };
            registerReceiver(mBroadcastReceiver,iF);
        }
    }

    private IntentFilter createBroadCast() {
        IntentFilter iF = new IntentFilter();
        iF.addAction("refresh_ui");
        return iF;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open) {
            Intent phone_listener = new Intent(ScrollingActivity.this , Phone_listener.class);
            startService(phone_listener);
            mFab.setImageResource(android.R.drawable.star_on);
            return true;
        }else if(id == R.id.action_add){
            if(mInputDialog == null){
                mInputDialog = new InputDialog(this);
                mInputDialog.setInitData(null,-1);

            }
            if(!mInputDialog.isAdded() && !mInputDialog.isVisible() && !mInputDialog.isRemoving()){
                mInputDialog.setInitData(null, -1);
                mInputDialog.show(getFragmentManager(),"");
            }
        }else if(id == R.id.action_clear){
            mUserItems.clear();
            SaveObjectUtils.setObject(this,"phone","useritem",mUserItems);
            mAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void cancel() {
        if(mInputDialog != null && mInputDialog.isVisible()){
            mInputDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if(mInputDialog != null && mInputDialog.isVisible()){
            mInputDialog.dismiss();
            mInputDialog =null;
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();

    }

    @Override
    public void confirm() {
        String nickName = mInputDialog.getNickName();
        String phoneNumber = mInputDialog.getPhoneNumber();
        Uri musicUri = mInputDialog.getMusicUri();


        if(mInputDialog.getPosition() == -1){
            for(int i = mUserItems.size() -1;i>=0;i--){
                if(mUserItems.get(i).userName.toString().equals(nickName)){
                    Snackbar.make(mFab, "昵称已存在", Snackbar.LENGTH_LONG)
                            .setAction("请点赞！", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                    return;
                }
            }
            mUserItems.add(new UserItem(nickName,phoneNumber,musicUri == null ? null:musicUri.toString()));

        }else{
            for(int i = mUserItems.size() -1;i>=0;i--){
                if(i != mInputDialog.getPosition() && mUserItems.get(i).userName.toString().equals(nickName)){
                    Snackbar.make(mFab, "昵称已存在", Snackbar.LENGTH_LONG)
                            .setAction("请点赞！", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                    return;
                }
            }
            mUserItems.set( mInputDialog.getPosition(),new UserItem(nickName,phoneNumber,musicUri == null ? null:musicUri.toString()));
        }
        mAdapter.notifyDataSetChanged();

       SaveObjectUtils.setObject(this,"phone","useritem",mUserItems);


        if(mInputDialog != null && mInputDialog.isVisible()){
            mInputDialog.dismiss();
            mInputDialog.hideSoftInputFromWindow();
        }
    }

}
