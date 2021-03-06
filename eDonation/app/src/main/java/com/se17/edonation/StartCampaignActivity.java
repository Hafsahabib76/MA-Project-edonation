package com.se17.edonation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class StartCampaignActivity extends AppCompatActivity implements DialogBox.onMultiChoiceListner {

    Context ctx=StartCampaignActivity.this;
    ImageButton AddImageBtn;
    ImageView AddImageIV;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button startCampaign;
    EditText title, aboutCampaign;
    Spinner citySpinner, categorySpinner;
    DatabaseReference reff;
    Campaign campaign;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_campaign);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        title = findViewById(R.id.title);
        aboutCampaign = findViewById(R.id.aboutCampaign);
        citySpinner = findViewById(R.id.citySpinner);
        categorySpinner = findViewById(R.id.categorySpinner);

        //coding for moving data to database
        campaign = new Campaign();
        reff= FirebaseDatabase.getInstance().getReference().child("Campaign");

        //startCampaign Button with dialog box coding
        startCampaign = (Button) findViewById(R.id.startCampaignBtn);
        startCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment multiChoiceDialog = new DialogBox();
                multiChoiceDialog.setCancelable(false);
                multiChoiceDialog.show(getSupportFragmentManager(), "Dialog Box");

            }
        });


        //adding image coding
        AddImageBtn = (ImageButton) findViewById(R.id.addimageBtn);
        AddImageIV = (ImageView) findViewById(R.id.addimageIV);

        AddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }
    //open gallery for adding image coding
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            AddImageIV.setImageURI(imageUri);
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] text, ArrayList<String> selectedItemList) {
        //saving data in database after btn click
        campaign.setTitle(title.getText().toString().trim());
        campaign.setAboutCampaign(aboutCampaign.getText().toString().trim());
        campaign.setCity(citySpinner.getSelectedItem().toString());
        campaign.setCategory(categorySpinner.getSelectedItem().toString());
        reff.push().setValue(campaign);
        startActivity(new Intent(ctx,MycampaignsActivity.class));
        Fileuploader();
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void Fileuploader(){
        StorageReference sRef=mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imageUri));

        sRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
