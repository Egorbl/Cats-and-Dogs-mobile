package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val dictionary = mapOf("catUrl" to "https://aws.random.cat/meow",
                               "dogUrl" to "https://random.dog/woof.json",
                               "catKey" to "file",
                               "dogKey" to "url")

        val listOfItems = arrayOf<String>("dog", "cat")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0)

        binding.getPicture.setOnClickListener {
            val url = if (binding.spinner.selectedItemPosition == 0) {
                dictionary["dogUrl"]
            } else {
                dictionary["catUrl"]
            }
            val key = if (binding.spinner.selectedItemPosition == 0) {
                dictionary["dogKey"]
            } else {
                dictionary["catKey"]
            }

            binding.textView.text = ""
            binding.progressBar.isVisible = true
            if (url != null && key != null) {
                requestToCats(url, key)
            }
        }
    }

    fun requestToCats(url: String, key: String) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
        Response.Listener { response ->
            val jsonCat = JSONObject(response.toString())
            pictureDisplay(jsonCat.get(key).toString())
        },
        Response.ErrorListener {
            binding.textView.text = "Error"
            binding.progressBar.isVisible = false
        })

        queue.add(stringRequest)

    }

    fun pictureDisplay(pngImage:String) {
        var image: Bitmap?
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val url = pngImage

            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                handler.post {
                    binding.imageView.setImageBitmap(image)
                    binding.progressBar.isVisible = false
                }
            }
            catch (e: Exception) {
                Log.d("TAG", e.toString())
                binding.textView.text = "error"
                binding.progressBar.isVisible = false
            }

        }
    }
}

