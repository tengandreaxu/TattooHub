package fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import classes.Constants;
import interfaces.AsyncResponse;
import tasks.GetUserSelfInfoTask;
import tasks.GoogleCustomSearchTask;
import xu_aaabeck.tattoohub.LoginActivity;
import xu_aaabeck.tattoohub.R;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 18.03.18.
 */

public class ProfileFragment extends Fragment implements AsyncResponse {

    private String access_token;
    private ImageView my_photo;
    private TextView logout;
    private TextView profile;
    private TextView help;
    private TextView about;


    public static ProfileFragment newInstance() {
        ProfileFragment fragmentFirst = new ProfileFragment();
        Bundle args = new Bundle();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent i = getActivity().getIntent();

        SharedPreferences prefs = getActivity().getSharedPreferences("shared",MODE_PRIVATE);

      access_token = i.getStringExtra("access_token");
        //access_token = prefs.getString("access_token", "diomerda");

        new GetUserSelfInfoTask(this,"profile_picture").execute(access_token);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        my_photo = (ImageView) view.findViewById(R.id.header_cover_image);
        logout = (TextView)view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                clearCookies(getContext());
                                Intent i = new Intent(getContext(), LoginActivity.class);
                                startActivity(i);
                                SharedPreferences prefs = getActivity().getSharedPreferences("shared",MODE_PRIVATE);
                                prefs.edit().putBoolean("logged",false).apply();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        profile = view.findViewById(R.id.profileName);
        profile.setText(((Constants)getActivity().getApplication()).getHash());

        help = view.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TengXu94/TattooHub"));
                startActivity(browserIntent);
            }
        });

        about = view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Info");
                alertDialog.setMessage("DEVELOPERS:\n\tXu Teng Andrea\n\tTomassi Valerio\nICON DESIGNERS:\n\tAliano Giorgia\n\tLim Stella Marie");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return view;
    }

    @Override
    public void processFinish(String result){
        Picasso.with(getContext())
                .load(result).fit()
                .into(my_photo);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }



}
