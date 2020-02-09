package com.example.market.account;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class AccountPresenter {

    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    private CollectionReference userRef;
    private boolean isLoggedIn = false;

    private AccountView view;
    private Context context;

    public AccountPresenter(AccountView view, Context context) {
        this.view = view;
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = firebaseFirestore.collection("users/");
        checkUser();
    }

    void setLoggedIn(boolean isLoggedIn){
        this.isLoggedIn = isLoggedIn;
    }

    void signOut(){
        mAuth.signOut();
    }

    boolean isLoggedIn(){
        return isLoggedIn;
    }

    void onStart() {
        mAuth.addAuthStateListener(listener);
    }

    void onStop() {
        if (mAuth != null) {
            mAuth.addAuthStateListener(listener);
        }
    }

    private void checkUser() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    isLoggedIn = true;
                    view.hideLoginButton();
                    view.setLogoutText();
                    view.setAccountDetails(firebaseAuth.getCurrentUser().getProviderData().get(1).getEmail());
                    DocumentReference documentReference = userRef.document(firebaseAuth.getCurrentUser().getUid());
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("", "Error is :" + e.getMessage());
                                view.accountError(e.getMessage());
                            } else {
                                view.setWelcomeText(documentSnapshot.get("first_name").toString());
                            }
                        }
                    });
                }
            }
        };

    }
}
