package dgg.net.dggrecmui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class ScrollingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        String xm = DeviceUtils.getManufacturer().toUpperCase();
        if (!"XIAOMI".equals(xm)) {
            ToastUtils.showLong("只能在小米手机端运行");
            tvInfo.setText("只能在小米手机端运行");
        } else {
            getPrem();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] items = new String[getMicFiles().size()];
                    for (int i = 0; i < getMicFiles().size(); i++) {
                        items[i] = getMicFiles().get(i).getName();
                    }
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ScrollingActivity.this);
                    dialog.setTitle("选择播放内容");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                MediaPlayer player = new MediaPlayer();
                                player.setDataSource(getMicFiles().get(which).getPath());
                                player.prepare();
                                player.start();
                            } catch (IOException e) {
                            }
                        }
                    });
                    dialog.create().show();
                }
            });
        }
    }

    private void getPrem() {
        List<PermissionItem> permissions = new ArrayList<>();
        permissions.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE));
        permissions.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        permissions.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE));
        HiPermission.create(this)
                .permissions(permissions)
                .animStyle(R.style.PermissionAnimFade)//设置动画
                .style(R.style.PermissionDefaultNormalStyle)//设置主题
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        LogUtils.i("用户关闭权限申请");
                    }

                    @Override
                    public void onFinish() {
                        LogUtils.i("所有权限申请完成");
                        getMicFiles();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        LogUtils.i("onDeny");
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        LogUtils.i("onGuarantee");
                    }
                });
    }

    private List<File> getMicFiles() {
        List<File> files = new ArrayList<>();
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            String path = SDCardUtils.getSDCardPathByEnvironment() + "/MIUI/sound_recorder/call_rec";
            files = FileUtils.listFilesInDir(path);
        }
        StringBuilder sb = new StringBuilder();
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                sb.append(files.get(i).getPath());
                sb.append("\n\n");
            }
            tvInfo.setText(sb.toString());
            return files;
        } else {
            return new ArrayList<>();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
