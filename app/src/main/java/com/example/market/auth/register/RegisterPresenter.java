package com.example.market.auth.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.market.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPresenter {

    private RegisterView registerView;
    private Context context;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    public RegisterPresenter(RegisterView registerView, Context context) {
        this.registerView = registerView;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }


    public void signUpUser(final String firstName, final String lastName, String email, String password, final String phoneNumber) {
        if (TextUtils.isEmpty(email)) {
            registerView.emailIsEmpty();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            registerView.passwordIsEmpty();
            return;
        }

        if (password.length() < 6) {
            registerView.passwordIsShort();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            registerView.authFailed();
                        } else {
                            //go to the last fragment i don't know how :D
                            Intent intent = new Intent(context, MainActivity.class);
                            CollectionReference reference = fireStore.collection("users/");
                            Map<String, Object> user = new HashMap<>();
                            user.put("first_name", firstName);
                            user.put("last_name", lastName);
                            user.put("phone", phoneNumber);
                            reference.document(firebaseAuth.getCurrentUser().getUid()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            registerView.authSucceeded();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("", "Register Error is :" + e.getMessage());
                                    registerView.authFailed();
                                }
                            });
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }
                });
    }

}
