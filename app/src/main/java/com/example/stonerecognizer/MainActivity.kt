package com.example.stonerecognizer

import android.R.attr
import android.R.id.button2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel

import com.google.android.gms.tasks.OnSuccessListener

import android.R.attr.data

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.IOException


class MainActivity : AppCompatActivity() {
    var labeler: FirebaseVisionImageLabeler? = null
    private val PIC_IMAGE = 121
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val localModel = FirebaseAutoMLLocalModel.Builder()
            .setAssetFilePath("modelfiles/manifest.json")
            .build()


        try {
            val options = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.0f) // Evaluate your model in the Firebase console
                // to determine an appropriate value.
                .build()
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)
        } catch (e: FirebaseMLException) {
            // ...
        }


        button2.setOnClickListener{
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PIC_IMAGE)

        }

//        resultTv = findViewById<View>(android.R.id.textView)
//        imageView = findViewById<View>(android.R.id.imageView2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === PIC_IMAGE) {
            imageView2.setImageURI(data?.data!!)
            textView.setText("")
            val image: FirebaseVisionImage
            try {
                image = FirebaseVisionImage.fromFilePath(applicationContext,data?.data!!)
                labeler!!.processImage(image)
                    .addOnSuccessListener { labels -> // Task completed successfully
                        // ...
                        for (label in labels) {
                            val text = label.text
                            val confidence = label.confidence
                            textView.append("$text   $confidence\n")
                        }
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
}