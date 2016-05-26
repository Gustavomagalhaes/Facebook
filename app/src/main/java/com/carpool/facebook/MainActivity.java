package com.carpool.facebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        printKeyHash();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                getFacebookData(accessToken, profile);
                Toast.makeText(MainActivity.this.getApplicationContext(), "Sucesso ao Logar!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Login Cancelado pelo usuário!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Login sem sucesso!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.carpool.facebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void getFacebookData(AccessToken accessToken, Profile profile) {
        System.out.println("---------------------");
        System.out.println("--Facebook Login Successful!");
        System.out.println("--Logged in user Details : ");
        System.out.println("---------------------");
        System.out.println("--User ID : " + accessToken.getUserId());
        System.out.println("--Authentication Token : " + accessToken.getToken());
        System.out.println("---------------------");
        System.out.println("---------------------First Name : " + profile.getFirstName());
        System.out.println("---------------------Last Name : " + profile.getLastName());
        System.out.println("---------------------Middle Name : " + profile.getMiddleName());
        System.out.println("---------------------URL : " + profile.getLinkUri());

        profile.getProfilePictureUri(500,500);

//        /* make the API call */
//        new GraphRequest(
//                accessToken,
//                "/{user-id}",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//            /* handle the result */
//                    }
//                }
//        ).executeAsync();
    }
}
