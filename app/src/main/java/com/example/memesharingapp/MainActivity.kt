package com.example.memesharingapp

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.Currency.getInstance
var currentimageurl : String? = null
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadmeme()
    }

   private fun loadmeme(){
       val progressBar = findViewById<ProgressBar>(R.id.Progressbar)
       progressBar.visibility = View.VISIBLE
       val url = "https://meme-api.herokuapp.com/gimme"

       val jsonObjectRequest = JsonObjectRequest(Request.Method.GET , url , null ,
           Response.Listener { response ->
               currentimageurl = response.getString("url")
               val imageview = findViewById<ImageView>(R.id.MemeImageView)
               Glide.with(this).load(currentimageurl).listener(object :
                   RequestListener<Drawable> {
                   override fun onLoadFailed(
                       e: GlideException? ,
                       model: Any? ,
                       target: Target<Drawable>? ,
                       isFirstResource: Boolean
                   ): Boolean {
                       progressBar.visibility = View.GONE
                       return false
                   }

                   override fun onResourceReady(
                       resource: Drawable? ,
                       model: Any? ,
                       target: Target<Drawable>? ,
                       dataSource: DataSource? ,
                       isFirstResource: Boolean
                   ): Boolean {
                       progressBar.visibility = View.GONE
                       return false
                   }

               }).into(imageview)
           } ,
           Response.ErrorListener { error ->
               Toast.makeText(this, "Something Went Wrong Please try after Some Time.." , Toast.LENGTH_LONG).show()
           }
       )

// Access the RequestQueue through your singleton class.
       MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
   }


    fun ShareMeme(view: View) {
         val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT ,"Hey chec this cool meme I got from reddit $currentimageurl")
        val chooser = Intent.createChooser(intent, " Share this meme using...")
        startActivity(chooser)


    }
    fun NextMeme(view: View) {
       loadmeme()
    }
}

class MySingleton constructor(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: MySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.

        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

}
