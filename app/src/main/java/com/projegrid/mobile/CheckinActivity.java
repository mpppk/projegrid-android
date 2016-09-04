package com.projegrid.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckinActivity extends AppCompatActivity {
    private String TAG = "CheckinActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String checkinEndpoint = "https://projegrid-stg.herokuapp.com/api/screen/check_in";
    private final OkHttpClient client = new OkHttpClient();
    private String screenId = null;
    private String screenToken = null;
    private String screenIdQueryKey = "screenId";
    private String screenTokenQueryKey = "screenToken";
    SharedPreferences prefer;


    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    public class CheckinPostData {
        private String userUid;
        private String screenId;
        private String screenToken;

        public CheckinPostData(String userUid, String screenId, String screenToken){
            this.userUid = userUid;
            this.screenId = screenId;
            this.screenToken = screenToken;
        }

        public String getScreenToken() {
            return screenToken;
        }

        public void setScreenToken(String screenToken) {
            this.screenToken = screenToken;
        }

        public String getUserUid() {
            return userUid;
        }

        public void setUserUid(String userUid) {
            this.userUid = userUid;
        }

        public String getScreenId() {
            return screenId;
        }

        public void setScreenId(String screenId) {
            this.screenId = screenId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        prefer = getSharedPreferences("screen", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the userUid. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in userUid can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(CheckinActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    // screenIdとscreenTokenはcustom URL schemeによって呼び出された場合、query parameterからsetされる
                    // 一度もcustom URL schemeから呼び出されていない場合や、URLにscreenIdなどが含まれていなかった場合これらの値はnullになる
                    if(screenId == null || screenToken == null){
                        Log.d(TAG, "screen id or screen token is null");
                        return;
                    }

                    // post用のオブジェクトを作成
                    CheckinPostData checkinPostData = new CheckinPostData(user.getUid(), screenId, screenToken);

                    // json文字列に変換
                    ObjectMapper mapper = new ObjectMapper();
                    String checkinPostDataStr = "";
                    try {
                        checkinPostDataStr = mapper.writeValueAsString(checkinPostData);
                    } catch (JsonProcessingException e) {
                        Log.d(TAG, "check in apiへpostするデータの、json文字列へのparseに失敗しました");
                        e.printStackTrace();
                    }

                    Request request = new Request.Builder()
                            .url(checkinEndpoint)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, checkinPostDataStr))
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "check in api failed");
                            e.getStackTrace();
                            Log.d(TAG, call.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "check in api response returned");
                            Log.d(TAG, response.body().string());

                            if(response.code() == 200){
                                Log.d(TAG, "check in api post success");
                            }else{
                                Log.d(TAG, String.valueOf(response.code()));
                                Log.d(TAG, "check in api post parameter invalid");
                            }
                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        // queryにscreen idとscreen tokenが含まれていればset
        // 含まれていなければnullをset
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                try{
                    screenId = uri.getQueryParameter(screenIdQueryKey);
                    screenToken = uri.getQueryParameter(screenTokenQueryKey);
                    SharedPreferences.Editor editor = prefer.edit();
                    editor.putString(screenIdQueryKey, screenId);
                    editor.putString(screenTokenQueryKey, screenToken);
                    editor.apply();
                }catch(NullPointerException e){
                    screenId = null;
                    screenToken = null;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
