package com.carpool.facebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

//    public Drawable LoadImageFromURL(String url) {
//        System.out.println("------TESTE URL " + url);
//        try {
//            InputStream is = (InputStream) new URL(url).getContent();
//            Drawable image = Drawable.createFromStream(is, "");
//            return image;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    private Bitmap getBitmapFromURL(String url) {
        try {
            URL src = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) src.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
        System.out.println("--First Name : " + profile.getFirstName());
        System.out.println("--Last Name : " + profile.getLastName());
        System.out.println("--URL Perfil: " + profile.getLinkUri());
        System.out.println("--URL Imagem: " + profile.getProfilePictureUri(500, 500));
        System.out.println("---------------------");
        System.out.println("--ID : " + profile.getId());
        System.out.println("--Name : " + profile.getName());
        System.out.println("---------------------");

        TextView profile_name = (TextView)findViewById(R.id.profile);
        profile_name.setText(profile.getName());

//        ImageView profile_picture = (ImageView)findViewById(R.id.image);
//
//        profile_picture.setVisibility(ImageView.VISIBLE);

//        profile_picture.setImageDrawable(LoadImageFromURL(profile.getProfilePictureUri(100, 100).toString()));

//        profile_picture.setImageURI(profile.getProfilePictureUri(100, 100));

//        if (profile_picture != null) {
//            System.out.println("IMAGEM SETADA");
//
//            Bitmap picture = getImageBitmap(profile.getProfilePictureUri(100, 100).toString());
//            profile_picture.setImageBitmap(picture);
//        }

    }
}
