package com.onlyknow.toy.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.data.OKDatabase;
import com.onlyknow.toy.utils.image.OKGlideCache;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class OKSettingsActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_setting_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_setting);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStatusBar(colorInTheme);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.ok_activity_setting_frame, new SettingPreferenceFragment(colorInTheme)).commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (toolbar != null) {
            toolbar.setBackgroundColor(colorInTheme);

            toolbar.setTitle(getText(R.string.setting_act_toolbar));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @SuppressLint("ValidFragment")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingPreferenceFragment extends PreferenceFragment {
        private int colorInTheme;

        @SuppressLint("ValidFragment")
        public SettingPreferenceFragment(int color) {
            super();

            colorInTheme = color;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.setting_pref);
        }

        @Override
        public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            findPreference("clear_cache").setSummary(getText(R.string.setting_clear_cache_tip) + OKGlideCache.getCacheSize(getActivity()));

            findPreference("clear_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder AlertDialog = new AlertDialog.Builder(getActivity());

                    AlertDialog.setTitle(getText(R.string.setting_alert_title));

                    AlertDialog.setMessage(getText(R.string.setting_alert_tip));

                    AlertDialog.setIcon(R.mipmap.ic_launcher);

                    AlertDialog.setPositiveButton(getText(R.string.setting_alert_sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            OKGlideCache.clearImageDiskCache(getActivity());

                            OKDatabase helper = OKDatabase.getHelper(getActivity());

                            helper.onUpgrade(helper.getWritableDatabase(), helper.getConnectionSource(), 2, 3);

                            findPreference("clear_cache").setSummary(OKGlideCache.getCacheSize(getActivity()));

                            Snackbar.make(view, getText(R.string.setting_clear_complete), Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog.setNegativeButton(getText(R.string.setting_alert_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

                    AlertDialog.show();

                    return true;
                }
            });

            findPreference("open_source").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();

                    bundle.putString(OKWebActivity.WEB_LINK, getString(R.string.action_open_github_url));
                    bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.action_open_github_title));
                    bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                    Intent intent = new Intent(getActivity(), OKWebActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    return true;
                }
            });

            findPreference("legal").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();

                    bundle.putString(OKWebActivity.WEB_LINK, getString(R.string.action_open_legal_url));
                    bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.action_open_legal_title));
                    bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                    Intent intent = new Intent(getActivity(), OKWebActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    return true;
                }
            });

            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();

                    bundle.putString(OKWebActivity.WEB_LINK, getString(R.string.action_open_about_url));
                    bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.action_open_about_title));
                    bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                    Intent intent = new Intent(getActivity(), OKWebActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    return true;
                }
            });

            findPreference("donation").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();

                    bundle.putString(OKWebActivity.WEB_LINK, getString(R.string.action_open_donation_url));
                    bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.action_open_donation_title));
                    bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                    Intent intent = new Intent(getActivity(), OKWebActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), OKSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
