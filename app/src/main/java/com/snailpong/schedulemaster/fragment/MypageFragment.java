package com.snailpong.schedulemaster.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.TabbedActivity;
import com.snailpong.schedulemaster.dialog.EditNickNameDialog;
import com.snailpong.schedulemaster.dialog.TableClickedDialog;

import org.w3c.dom.Text;

public class MypageFragment extends Fragment {
    private FragmentManager fragmentManager = getFragmentManager();
    private TextView nick;
    private TextView mail;
    private ImageView profileImg;
    private LinearLayout profilelayout;
    private LinearLayout nicknamelayout;
    private LinearLayout nickname;
    private LinearLayout friendlayout;
    private LinearLayout myfriend;
    private boolean logined = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        nick = (TextView) view.findViewById(R.id.mypage_nick);
        mail = (TextView) view.findViewById(R.id.mypage_mail);
        profileImg = (ImageView) view.findViewById(R.id.mypage_profileimg);
        profilelayout = (LinearLayout) view.findViewById(R.id.mypage_profilelayout);
        nicknamelayout = (LinearLayout) view.findViewById(R.id.mypage_nicknamelayout);
        nickname = (LinearLayout) view.findViewById(R.id.mypage_nickname);
        friendlayout = (LinearLayout) view.findViewById(R.id.mypage_friendlayout);
        myfriend = (LinearLayout) view.findViewById(R.id.mypage_friend);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    logined = true;
                    String uid = user.getUid();
                    String email = user.getEmail();
                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users/" + uid);
                    mail.setText(email);

                    dataRef.child("userName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            nick.setText(name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("profileImageUrl").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.getValue(String.class);
                            // 사용자가 이미지를 업로드한 경우
                            if (!url.equals("null")) {
                                while (getActivity() == null);
                                Glide.with(getContext()).load(url).into(profileImg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    myfriend.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            TabbedActivity activity = (TabbedActivity) getActivity();
                            activity.onFragmentChange(0);
                        }
                    });
                    nickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle args = new Bundle();
                            args.putString("userName", nick.getText().toString());
                            EditNickNameDialog dialog = new EditNickNameDialog();
                            dialog.setArguments(args);
                            dialog.show(getActivity().getSupportFragmentManager(),"tag");
                        }
                    });
                    profilelayout.setVisibility(View.VISIBLE);
                    nicknamelayout.setVisibility(View.VISIBLE);
                    nickname.setVisibility(View.VISIBLE);
                    friendlayout.setVisibility(View.VISIBLE);
                    myfriend.setVisibility(View.VISIBLE);
                } else {
                    logined = false;
                    profilelayout.setVisibility(View.GONE);
                    nicknamelayout.setVisibility(View.GONE);
                    nickname.setVisibility(View.GONE);
                    friendlayout.setVisibility(View.GONE);
                    myfriend.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}
