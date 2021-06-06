package com.mirea.veremeev.l.m.httpurlconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    val uiScope = CoroutineScope(Dispatchers.IO)

    private lateinit var ip: TextView
    private lateinit var country: TextView
    private lateinit var region: TextView
    private lateinit var zip: TextView
    private val url = "http://ip-api.com/json/"
    private var receivedData : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ip = findViewById(R.id.ip);
        country = findViewById(R.id.country);
        region = findViewById(R.id.region);
        zip = findViewById(R.id.zip);
    }

    fun onClick(view: View?) {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkinfo: NetworkInfo? = null
        if (connectivityManager != null) {
            networkinfo = connectivityManager.activeNetworkInfo
        }
        if (networkinfo != null && networkinfo.isConnected) {
            CoroutineScope(Dispatchers.Main).launch {
                val coroutine = uiScope.launch {
                    receivedData = downloadIpInfo(url)!!
                }
                coroutine.join()

                Log.d(MainActivity::class.java.simpleName, receivedData)
                try {
                    val responseJson = JSONObject(receivedData)
                    val ipStr = responseJson.getString("query")
                    val countyStr = responseJson.getString("country")
                    val regionStr = responseJson.getString("regionName")
                    val zipStr = responseJson.getString("zip")
                    ip.text = ipStr
                    country.text = countyStr
                    region.text = regionStr
                    zip.text = zipStr
                    Log.d(MainActivity::class.java.simpleName, ipStr)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun downloadIpInfo(address: String): String? {
        var inputStream: InputStream? = null
        var data: String? = ""
        try {
            val url = URL(address)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.readTimeout = 100000
            connection.connectTimeout = 100000
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true
            connection.useCaches = false
            connection.doInput = true
            val responseCode: Int = connection.getResponseCode()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream()
                val bos = ByteArrayOutputStream()
                var read = 0
                while (inputStream.read().also { read = it } != -1) {
                    bos.write(read)
                }
                val result: ByteArray = bos.toByteArray()
                bos.close()
                data = String(result)
            } else {
                data =
                    connection.getResponseMessage().toString() + " . Error Code : " + responseCode
            }
            connection.disconnect()

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return data
    }

}