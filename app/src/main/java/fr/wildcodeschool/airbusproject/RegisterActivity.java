package fr.wildcodeschool.airbusproject;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import fr.wildcodeschool.airbusproject.Model.User;

public class RegisterActivity extends AppCompatActivity {


    private static final int REQUEST_PHOTO_FROM_GOOGLE_PHOTOS = 333;
    MaterialEditText username, email, password, confirm_password;
    Button btn_register;
    ImageButton add_btn;



    //FIREBASE
    FirebaseAuth auth;
    DatabaseReference reference;

    //UPLOAD IMAGE FIREBASE
    ImageView profile_image;
    Uri pickedImgUri;
    Uri downloadUri;
    String save_URL;
    static int PReqCode = 1;
    static  int REQUESTCODE = 1;

    //progress bar
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        btn_register.bringToFront();
        confirm_password = findViewById(R.id.confirm_password);
        add_btn = findViewById(R.id.add_btn);
        profile_image = findViewById(R.id.profile_image);
        loadingProgress = findViewById(R.id.progressBar);

        loadingProgress.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
                openGallery();
            }
        });

        // BUTTON CREATION COMPTE

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_register.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_confirm_pass = confirm_password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    btn_register.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    btn_register.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (!txt_password.equals(txt_confirm_pass)) {
                    Toast.makeText(RegisterActivity.this, "passwords do not match", Toast.LENGTH_SHORT).show();
                    btn_register.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);

                } else {

                    register(txt_username, txt_email, txt_password, save_URL);
                }

            }
        });
    }

    //CREATE USER ACCOUNT WITH EMAIL AND PASSWORD
    //TODO LIER UPLOAD DE LA PHOTO AU COMPTE

    private void register(final String username, String email, String password, final String saveURL) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                            final String userid = firebaseUser.getUid();

                            //PARTIE STORAGE ESSAI 2
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("users_photo").child(userid);
                            final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
                            /* UploadTask uploadTask = imageFilePath.putFile(pickedImgUri);

                            //PERMET DE RECUPERER L'URL DU FICHIER UPLOAD

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return imageFilePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        downloadUri = task.getResult();
                                        showMessage(downloadUri.toString());
                                        save_URL = downloadUri.toString();

                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            }); */


                            // ESSAI 3
                            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(userid).setValue(imageFilePath.getDownloadUrl().toString());
                                }
                            });


                            //PARTIE AUTHENTIFICATION DATABASE

                            assert firebaseUser != null;
                            //String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "defaut");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register with the email or password", Toast.LENGTH_SHORT).show();
                            btn_register.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    //OPEN GALLERY FOR UPLOAD

    private void openGallery() {
        //Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
        //startActivityForResult(galleryIntent,REQUESTCODE);
        launchGooglePhotosPicker();
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                showMessage("Please accept for required permission");
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else {
            openGallery();
        }
    }

    //AFFICHE LA PHOTO CHOISI DANS PROFILE IMAGE GRACE AU REQUEST CODE + CREATION URI


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_PHOTO_FROM_GOOGLE_PHOTOS && data != null) {
            pickedImgUri = data.getData();
            profile_image.setImageURI(pickedImgUri);
        }
    }


    //METHOD TO GET PICTURE FROM GOOGLE PHOTO

    private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
    public static boolean isGooglePhotosInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(GOOGLE_PHOTOS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void launchGooglePhotosPicker() {
        if (RegisterActivity.this != null && isGooglePhotosInstalled(RegisterActivity.this)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");

            List<ResolveInfo> resolveInfoList = RegisterActivity.this.getPackageManager().queryIntentActivities(intent, 0);
            for (int i = 0; i < resolveInfoList.size(); i++) {
                if (resolveInfoList.get(i) != null) {
                    String packageName = resolveInfoList.get(i).activityInfo.packageName;
                    if (GOOGLE_PHOTOS_PACKAGE_NAME.equals(packageName)) {
                        intent.setComponent(new ComponentName(packageName, resolveInfoList.get(i).activityInfo.name));
                        RegisterActivity.this.startActivityForResult(intent, REQUEST_PHOTO_FROM_GOOGLE_PHOTOS);
                        return;
                    }
                }
            }
        }
    }


    // CODE 2 POUR ENVOYER IMAGE FIREBASE
}
