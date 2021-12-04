package com.example.cappsule.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cappsule.R
import com.example.cappsule.database.DatabaseHelperArticle
import com.example.cappsule.dialog.OutfitSaveDialog
import com.example.cappsule.getData
import com.example.cappsule.getImage
import com.example.cappsule.toaster
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
class Home : Fragment() {
    private var imageViewLayer: ImageView? = null
    private var imageViewTop: ImageView? = null
    private var imageViewBottom: ImageView? = null
    private var imageViewShoes: ImageView? = null
    private var layer: Bitmap? = null
    private var top: Bitmap? = null
    private var bottom: Bitmap? = null
    private var shoes: Bitmap? = null
    private var db: SQLiteDatabase? = null
    private lateinit var warmth: String
    private lateinit var more: FloatingActionButton
    private lateinit var reload: FloatingActionButton
    private lateinit var save: FloatingActionButton
    private lateinit var share: FloatingActionButton
    private var littleButtonVisible = false
    private lateinit var textViewCity: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var imageViewWeather: ImageView
    private lateinit var linearLayoutWeather: LinearLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var switchWarmth: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefDefaultWarmth : SharedPreferences
    private var prefWeatherWarmthStatus = true
    private var prefWeatherUnitMetricStatus = true
    private var prefWeatherMinLightTemp: Int = 0
    private lateinit var weatherUnit: String
    private lateinit var weatherUnitType: String
    private lateinit var progressBar: ProgressBar
    private lateinit var frameHome: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        frameHome = view.findViewById(R.id.home_frame)
        progressBar = requireActivity().requireViewById(R.id.progressBar2)
        imageViewLayer = view.findViewById(R.id.imageLayer)
        imageViewTop = view.findViewById(R.id.imageTop)
        imageViewBottom = view.findViewById(R.id.imageBottom)
        imageViewShoes = view.findViewById(R.id.imageShoes)
        val databaseHelperArticle = DatabaseHelperArticle(context)
        db = databaseHelperArticle.writableDatabase

        more = view.findViewById(R.id.outfit_more_button)
        reload = view.findViewById(R.id.outfit_redo_button)
        save = view.findViewById(R.id.outfit_save_button)
        share = view.findViewById(R.id.outfit_share_button)

        more.setOnClickListener { moreClick() }
        reload.setOnClickListener { setViews() }
        save.setOnClickListener { openSave() }
        share.setOnClickListener{ share() }

        textViewCity = view.findViewById(R.id.location)
        textViewTemperature = view.findViewById(R.id.textViewTemperature)
        imageViewWeather = view.findViewById(R.id.imageViewWeather)
        val textViewLight = view.findViewById<TextView>(R.id.textViewLight)
        val textViewHeavy = view.findViewById<TextView>(R.id.textViewHeavy)
        switchWarmth = view.findViewById(R.id.outfit_warmth_switch)
        prefDefaultWarmth = this.context!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        prefWeatherWarmthStatus = sharedPreferences.getBoolean("weather_warmth", true)
        prefWeatherUnitMetricStatus = sharedPreferences.getBoolean("weather_unit", true)
        prefWeatherMinLightTemp = sharedPreferences.getString("min_temp", "0")?.toInt() ?: 0

        if(prefWeatherUnitMetricStatus){
            weatherUnit = "°C"
            weatherUnitType = "metric"
        } else {
            weatherUnit = "°F"
            weatherUnitType = "imperial"
        }

        warmth = "Light"

        switchWarmth.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                textViewLight.setTypeface(null, Typeface.NORMAL)
                textViewHeavy.setTypeface(textViewHeavy.typeface, Typeface.BOLD)
                warmth = "Heavy"
                prefDefaultWarmth.edit().putBoolean("switchValue", switchWarmth.isChecked).apply()
            } else {
                textViewLight.setTypeface(textViewLight.typeface, Typeface.BOLD)
                textViewHeavy.setTypeface(null, Typeface.NORMAL)
                warmth = "Light"
                prefDefaultWarmth.edit().putBoolean("switchValue", switchWarmth.isChecked).apply()
            }
            setViews()
        }

        if (prefDefaultWarmth.getBoolean("switchValue", true)){
            warmth = "Heavy"
            switchWarmth.isChecked = false
            switchWarmth.isChecked = true
        } else {
            warmth = "Light"
            switchWarmth.isChecked = true
            switchWarmth.isChecked = false
        }

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        pullToRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh(){
                setViews()
                setWeather()
                pullToRefresh.isRefreshing = false
            }
        })

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            frameHome.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            setViews()
            setWeather()
            delay(500)
            progressBar.visibility = View.GONE
            frameHome.visibility = View.VISIBLE
        }
    }

    private fun setViews() {
        var warmthFR: String = ""
        when (warmth) {
            "Light" -> {warmthFR = "Léger"}
            "Heavy" -> {warmthFR = "Lourd"}
        }
        setViewContent(imageViewLayer, "Layer", "Surcouche", warmthFR)
        setViewContent(imageViewTop, "Top", "Haut", warmthFR)
        setViewContent(imageViewBottom, "Bottom", "Bas", warmthFR)
        setViewContent(imageViewShoes, "Shoes", "Chaussures", warmthFR)
    }

    private fun setViewContent(imageView: ImageView?, type: String, typeFR: String, warmthFR: String) {
        val query = "SELECT rowid, image FROM article WHERE (type LIKE ? OR type LIKE ?) AND available LIKE 1 AND (warmth LIKE 'Both' OR warmth LIKE 'Les 2' OR warmth LIKE ? OR warmth LIKE ?)"
        val cursor = db!!.rawQuery(query, arrayOf(type, typeFR, warmth, warmthFR))
        val min = 0
        val max = cursor.count
        var img: Bitmap? = null
        val random: Int = if (cursor.count > 1) {
            Random().nextInt(max - min) + min
        } else {
            0
        }
        while (cursor.moveToNext()) {
            if (cursor.position == random) {
                img = getImage(cursor.getBlob(1))
                val bitmapDrawableLayer = BitmapDrawable(resources, img)
                requireActivity().runOnUiThread {
                    imageView!!.setImageDrawable(bitmapDrawableLayer)
                }
            }
        }
        if (cursor.count == 0) {
            requireActivity().runOnUiThread {
                imageView!!.setImageDrawable(resources.getDrawable(R.drawable.logo, null))
            }
            img = BitmapFactory.decodeResource(context?.resources, R.drawable.logo)
        }
        when (type) {
            "Layer", "Surcouche" -> {
                layer = img
            }
            "Top", "Haut" -> {
                top = img
            }
            "Bottom", "Bas" -> {
                bottom = img
            }
            "Shoes", "Chaussures" -> {
                shoes = img
            }
        }
        cursor.close()
    }

    private fun moreClick() {
        if (littleButtonVisible) {
            littleButtonVisible = false
            reload.visibility = FloatingActionButton.INVISIBLE
            save.visibility = FloatingActionButton.INVISIBLE
            share.visibility = FloatingActionButton.INVISIBLE
            more.setImageResource(R.drawable.menu)
        } else {
            littleButtonVisible = true
            reload.visibility = FloatingActionButton.VISIBLE
            save.visibility = FloatingActionButton.VISIBLE
            share.visibility = FloatingActionButton.VISIBLE
            more.setImageResource(R.drawable.close)
        }
    }

    private fun openSave() {
        moreClick()
        val outfitSaveDialog = OutfitSaveDialog()
        outfitSaveDialog.layer = layer
        outfitSaveDialog.top = top
        outfitSaveDialog.bottom = bottom
        outfitSaveDialog.shoes = shoes
        outfitSaveDialog.home = this
        outfitSaveDialog.show(childFragmentManager, "OUTFITSAVEDIALOG")
    }

    private fun share() {
        moreClick()
        try {
            val cachePath = File(context!!.cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png")
            getBitmapFromView(requireView().rootView)?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imagePath = File(context!!.cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri? = FileProvider.getUriForFile(context!!, "com.example.cappsule.fileprovider", newFile)
        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.SentByCappsule))
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))
        }
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun isNetworkAvailable(): Boolean
    = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    private fun setWeather(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        linearLayoutWeather = view!!.findViewById(R.id.linearLayoutWeather)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null && isNetworkAvailable()) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val city = addresses[0].locality
                    textViewCity.text = city
                    val thread = Thread {
                            try {
                                val data = getData(city,weatherUnitType)
                                println(data)
                                val jsonObject = JSONObject(data)
                                val main = jsonObject.getJSONObject("main")
                                val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                                val temp = main.getString("temp") + weatherUnit
                                val weatherMain = weather.getString("main")
                                var weatherImg: Drawable? = null
                                val degrees = main.getInt("temp")

                                when (weatherMain) {
                                    "Thunderstrom", "Drizzle", "Rain" -> {
                                        weatherImg = resources.getDrawable(R.drawable.rain, null)
                                    }
                                    "Snow" -> {
                                        weatherImg = resources.getDrawable(R.drawable.snow, null)
                                    }
                                    "Clouds", "Mist", "Smoke", "Haze", "Dust", "Fog", "Sand", "Ash", "Squall", "Tornado" -> {
                                        weatherImg = resources.getDrawable(R.drawable.cloudy, null)
                                    }
                                    "Clear" -> {
                                        weatherImg = resources.getDrawable(R.drawable.sun, null)
                                    }
                                }
                                val isCheckedWeather: Boolean = degrees < prefWeatherMinLightTemp
                                requireActivity().runOnUiThread {
                                    textViewTemperature.text = temp
                                    imageViewWeather.setImageDrawable(weatherImg)
                                    linearLayoutWeather.visibility = View.VISIBLE
                                    if(prefWeatherWarmthStatus){
                                        switchWarmth.isChecked = !isCheckedWeather
                                        switchWarmth.isChecked = isCheckedWeather
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                    }
                    thread.start()
                }
                else {
                        linearLayoutWeather.visibility = View.GONE
                }
            }
    }
}