package com.example.currentplacedetailsonmap.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.HelpActivity;
import com.example.currentplacedetailsonmap.LoginActivity;
import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.activities.Settings_activity;
import com.example.currentplacedetailsonmap.model.alertdialoghelper;
import com.example.currentplacedetailsonmap.utils.Dbhelper;
import com.google.android.material.snackbar.Snackbar;

@SuppressWarnings("ALL")
@SuppressLint("ValidFragment")
public class account_fragment extends Fragment {
    static ProgressDialog pDialog;
    Activity activity;
    TextView txtaddnumber,acc_phone,textViewsettings;
    ImageView notification_button;
    public account_fragment(Activity _activity){
        activity=_activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;


        if (new Dbhelper(getContext()).check_user()==1){
            view=inflater.inflate(R.layout.account, container, false);
            final TextView txtname=view.findViewById(R.id.acc_name),txtemail=view.findViewById(R.id.acc_email),
                    txtpaymentmtd=view.findViewById(R.id.payment_method),
            txtlogout=view.findViewById(R.id.logout),tv_help=view.findViewById(R.id.help);
            textViewsettings=view.findViewById(R.id.acsettings);
            notification_button=view.findViewById(R.id.notificationbutton);

            tv_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, HelpActivity.class));
                }
            });
            textViewsettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), Settings_activity.class));
                }
            });

            txtemail.setText(new Dbhelper(getContext()).get_user_email());


            txtaddnumber=view.findViewById(R.id.addnumber);
            acc_phone=view.findViewById(R.id.acc_phone);
            if (new Dbhelper(getContext()).getuser_no().equals("0")){

            }
            else {
                acc_phone.setText(new Dbhelper(getContext()).getuser_no());
            }
            if (acc_phone.getText().toString().length()>3){
                txtaddnumber.setText("Edit Phone Number");
            }

            txtaddnumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View alertLayout = getLayoutInflater().inflate(R.layout.number_input, null);
                    alertdialoghelper.add_number(getActivity(),alertLayout,acc_phone);
                }
            });

            if (new Dbhelper(activity).getnotistate().equals("1")){
                notification_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp));

            }
            else {
                notification_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp));

            }

                notification_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new Dbhelper(activity).getnotistate().equals("1")){
                        new Dbhelper(activity).setnotistate("0");
                        notification_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp));

                        Snackbar snackbar = Snackbar.make(view, "Notification Disabled!", Snackbar.LENGTH_LONG);
//                        snackbar.setAnchorView(mActivity.findViewById(R.id.nav_view));
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                        snackbar.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                    else {
                        new Dbhelper(activity).setnotistate("1");
                        notification_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp));
                        Snackbar snackbar = Snackbar.make(view, "Notification Enabled!", Snackbar.LENGTH_LONG);
//                        snackbar.setAnchorView(mActivity.findViewById(R.id.nav_view));
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                        snackbar.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();

                    }

                }
            });

//        txtname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_edit_acc, null);
//                alertdialoghelper.editDialog(getActivity(), alertLayout,txtname.getText().toString(),txtname);
//
//
//            }
//        });
//        txtemail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_edit_acc, null);
//                alertdialoghelper.editDialog(getActivity(), alertLayout,txtemail.getText().toString(),txtemail);
//            }
//        });

            txtlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new sign_out_task().execute();
                }
            });
//            txteditinfo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_edit_acc, null);
//                    alertdialoghelper.editDialog(getActivity(), alertLayout,txtname.getText().toString(),txtemail.getText().toString(),txtname,txtemail);
//                }
//            });

            txtpaymentmtd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomsheet_payment bottomSheetFragment = new bottomsheet_payment();
                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
                }
            });


        }



        else {
            view=inflater.inflate(R.layout.layout_no_account,container,false);

            TextView textView_sign_in=view.findViewById(R.id.account_sign_in);
            TextView textView_create_account=view.findViewById(R.id.account_signup);

//            CREATE ACCOUNT
            textView_create_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View alertLayout = getLayoutInflater().inflate(R.layout.layout_signup, null);
                    alertdialoghelper.sign_up(getActivity(),alertLayout,getActivity());

                }
            });

//            CREATE SIGN IN
            textView_sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View alertLayout = getLayoutInflater().inflate(R.layout.layout_signin, null);
                    alertdialoghelper.sign_in(getActivity(),alertLayout,getActivity());
                }
            });






        }


        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left));
//        setContentView(view);

        return view;
    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    public static void hideactivityitems(Activity activity){
        activity.findViewById(R.id.borderedsearchlay).setVisibility(View.GONE);
//        activity.findViewById(R.id.message).setVisibility(View.GONE);

    }


    private class sign_out_task extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Signing out. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {


            new Dbhelper(getContext()).delete_user();
            new Dbhelper(getContext()).delete_all_booking();


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Toast.makeText(getContext(), "Signed out...", Toast.LENGTH_SHORT).show();
//            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
//                    .setTitleText("Signed out")
//                    .setContentText("click ok to exit")
//                    .show();
            pDialog.dismiss();

            activity.finish();
            startActivity(new Intent(activity, LoginActivity.class));
//            loadFragment(new account_fragment(activity));

        }
    }


}
