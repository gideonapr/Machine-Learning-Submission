package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            binding.resultImage.setImageURI(it)
            classifyImage(it)
        } ?: run {
            Log.e(TAG, "Image URI is null")
            showToast("Image URI is null")
        }
    }

    private fun classifyImage(imageUri: Uri) {
        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                    Log.e(TAG, "Error: $error")
                }

                @SuppressLint("SetTextI18n")
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let {
                        val topResult = it[0]
                        val label = topResult.categories[0].label
                        val score = topResult.categories[0].score

                        fun Float.formatToString(): String {
                            return String.format("%.2f%%", this * 100)
                        }

                        binding.resultText.text = "$label ${score.formatToString()}"
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_img_uri"
        const val TAG = "imagePicker"
    }
}
