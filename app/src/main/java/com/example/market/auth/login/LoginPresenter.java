package com.example.market.auth.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter {

    private Context context;
    private LoginView loginView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private CollectionReference userRef;

    public LoginPresenter(Context context, LoginView loginView) {
        this.context = context;
        this.loginView = loginView;
        firebaseAuth = FirebaseAuth.getInstance();
        //checkLoginStatus();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userRef = firebaseFirestore.collection("users/");
        callbackManager = CallbackManager.Factory.create();
    }

    public void loginUser(String email , final String password){
        if(TextUtils.isEmpty(email)){
            loginView.emailIsEmpty();
            return;
        }
        if(TextUtils.isEmpty(password)){
            loginView.passwordIsEmpty();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            if(password.length() < 6){
                                //passwordEditText.setError("Password too short, enter minimum 6 characters!");
                            }else{
                                loginView.loginFailed();
                            }
                        }else{
                            //go to the last fragment
                            ((Activity)context).finish();
                        }
                    }
                });
    }

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginView.loginFailed();
                        Log.e("",e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                firebaseAuth.getCurrentUser().getDisplayName();
                Map<String, Object> user = new HashMap<>();
                user.put("first_name", firebaseAuth.getCurrentUser().getDisplayName());
                user.put("last_name", "");
                user.put("phone", "");
                userRef.document(firebaseAuth.getCurrentUser().getUid()).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loginView.loginSucceeded();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("", "Register Error is :" + e.getMessage());
                        loginView.loginFailed();
                    }
                });
                ((Activity)context).finish();
            }
        });
    }

    public void signInWithFacebook(LoginButton facebookLoginButton) {
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    public void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            signInWithFacebook(loginView.getLoginButton());
        }
    }

}
