package com.erenkaraboga.prayer7

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.SimpleDate
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var image: ImageView
    private lateinit var saat: View
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private var PERMISSION_ID=100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saat=findViewById(R.id.davul2)
        animate()
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
         RequestPermission()

        button.setOnClickListener {

            getLastLocation()
            getNewLocation()
        }

        getLastLocation()

    }



    private fun getLastLocation(){
         if (CheckPermission()){
             val sharedPreferences=this.getSharedPreferences("com.erenkaraboga.prayer7",Context.MODE_PRIVATE)
             if (isLocationEnabled()){

                 fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->

                      var location : Location?=task.result
                      if (location==null){

                             getNewLocation()


                      }else{
                          var enlem = location.latitude
                          var boylam=location.longitude
                          println(enlem)
                          println(boylam)

                          val today= SimpleDate(GregorianCalendar())
                          sharedPreferences.edit().putFloat("enlem",enlem.toFloat()).apply()
                          sharedPreferences.edit().putFloat("boylam",boylam.toFloat()).apply()


                          //Imsak
                          val location6 = com.azan.astrologicalCalc.Location(enlem, boylam, 3.143, 0)
                          val azan6 = Azan(location6, Method.KARACHI_HANAF)
                          val imsaak = azan6.getImsaak(today)
                          imsak.text="İmsak:  "+imsaak.toString()

                          //Gunes
                          val location5 = com.azan.astrologicalCalc.Location(enlem, boylam, 2.9000, 0)
                          val azan5 = Azan(location5, Method.KARACHI_HANAF)
                          val prayerTimes5=azan5.getPrayerTimes(today)
                          sabah.text="Güneş:  "+prayerTimes5.shuruq().toString()

                          //Ogle
                          val location2 = com.azan.astrologicalCalc.Location(enlem, boylam, 3.09, 0)
                          val azan2 = Azan(location2, Method.KARACHI_HANAF)
                          val prayerTimes2=azan2.getPrayerTimes(today)
                          ogle.text="Öğle:  "+prayerTimes2.thuhr().toString()

                          //İkindi
                          val location3 = com.azan.astrologicalCalc.Location(enlem, boylam, 2.1510, 0)
                          val azan3 = Azan(location3, Method.KARACHI_HANAF)
                          val prayerTimes3=azan3.getPrayerTimes(today)
                          ikindi.text="İkindi:  "+prayerTimes3.assr().toString()

                          //Akşam
                          val location = com.azan.astrologicalCalc.Location(enlem, boylam, 3.1100, 0)
                          val azan = Azan(location, Method.KARACHI_HANAF)
                          val prayerTimes=azan.getPrayerTimes(today)
                          aksam.text="Akşam:  "+prayerTimes.maghrib().toString()

                          //Yatsı
                          val location4 = com.azan.astrologicalCalc.Location(enlem, boylam, 2.91500, 0)
                          val azan4= Azan(location4, Method.KARACHI_HANAF)
                          val prayerTimes4=azan4.getPrayerTimes(today)
                          yatsı.text="Yatsı:  "+prayerTimes4.ishaa().toString()

                          val currentTime = Calendar.getInstance().time
                          var year = today.year
                          var month= today.month
                          var day= today.day


                          val endDateDay = "$day.$month.$year ${prayerTimes.maghrib()}"
                          val format1 = SimpleDateFormat("dd.MM.yyyy hh:mm:ss",Locale.getDefault())
                          val endDate = format1.parse(endDateDay)

                          //milliseconds
                          var different = endDate.time - currentTime.time
                          var  countDownTimer = object : CountDownTimer(different, 1000) {

                              override fun onTick(millisUntilFinished: Long) {
                                  var diff = millisUntilFinished
                                  val secondsInMilli: Long = 1000
                                  val minutesInMilli = secondsInMilli * 60
                                  val hoursInMilli = minutesInMilli * 60
                                  val daysInMilli = hoursInMilli * 24

                                  val elapsedDays = diff / daysInMilli
                                  diff %= daysInMilli

                                  val elapsedHours = diff / hoursInMilli
                                  diff %= hoursInMilli

                                  val elapsedMinutes = diff / minutesInMilli
                                  diff %= minutesInMilli

                                  val elapsedSeconds = diff / secondsInMilli

                                  textView8.text=  "İftara $elapsedHours sa $elapsedMinutes dk $elapsedSeconds sn"


                              }

                              override fun onFinish() {

                                    textView8.text="Afiyet Olsun"


                              }
                          }.start()

                          try {
                              var geocoder= Geocoder(this,Locale.getDefault())
                              var Adress= geocoder.getFromLocation(enlem,boylam,1)
                              if (Adress!=null && Adress.size>0){
                                  var bilgiler=Adress.get(0)
                                  if (bilgiler.countryName!=null){
                                      textView2.text=bilgiler.countryName.toString()
                                  }
                                  else{
                                      textView2.text="Türkiye"
                                  }
                                  if (bilgiler.adminArea!=null){
                                      textView3.text=bilgiler.adminArea.toString()
                                      sharedPreferences.edit().putString("sehir",bilgiler.adminArea.toString()).apply()
                                  }
                                  else{

                                      textView3.text="Türkiye"
                                  }

                              }
                          }catch (e:IOException){
                              textView3.text="Türkiye"
                              textView2.text="Türkiye"

                          }

                         /* finally {
                              textView3.text="Türkiye"
                              textView2.text="Türkiye"
                          }*/




                      }
                  }

             }else{

                   var enlem= sharedPreferences.getFloat("enlem",00.0f)
                   var boylam= sharedPreferences.getFloat("boylam",00.0f)
                   val today= SimpleDate(GregorianCalendar())
                   textView2.text="Turkey"


                 textView3.text=sharedPreferences.getString("sehir","Türkiye")

                 if (enlem!=00.0f){


                     //Imsak
                     val location6 = com.azan.astrologicalCalc.Location(enlem.toDouble() , boylam.toDouble(), 3.143, 0)
                     val azan6 = Azan(location6, Method.KARACHI_HANAF)
                     val imsaak = azan6.getImsaak(today)
                     imsak.text="İmsak:  "+imsaak.toString()

                     //Gunes
                     val location5 = com.azan.astrologicalCalc.Location(enlem.toDouble(), boylam.toDouble(), 2.9000, 0)
                     val azan5 = Azan(location5, Method.KARACHI_HANAF)
                     val prayerTimes5=azan5.getPrayerTimes(today)
                     sabah.text="Güneş:  "+prayerTimes5.shuruq().toString()

                     //Ogle
                     val location2 = com.azan.astrologicalCalc.Location(enlem.toDouble(), boylam.toDouble(), 3.09, 0)
                     val azan2 = Azan(location2, Method.KARACHI_HANAF)
                     val prayerTimes2=azan2.getPrayerTimes(today)
                     ogle.text="Öğle:  "+prayerTimes2.thuhr().toString()

                     //İkindi
                     val location3 = com.azan.astrologicalCalc.Location(enlem.toDouble(), boylam.toDouble(), 2.1510, 0)
                     val azan3 = Azan(location3, Method.KARACHI_HANAF)
                     val prayerTimes3=azan3.getPrayerTimes(today)
                     ikindi.text="İkindi:  "+prayerTimes3.assr().toString()

                     //Akşam
                     val location = com.azan.astrologicalCalc.Location(enlem.toDouble(), boylam.toDouble(), 3.1100, 0)
                     val azan = Azan(location, Method.KARACHI_HANAF)
                     val prayerTimes=azan.getPrayerTimes(today)
                     aksam.text="Akşam:  "+prayerTimes.maghrib().toString()

                     var aksam1 = prayerTimes.maghrib()
                     //Yatsı
                     val location4 = com.azan.astrologicalCalc.Location(enlem.toDouble(), boylam.toDouble(), 2.91500, 0)
                     val azan4= Azan(location4, Method.KARACHI_HANAF)
                     val prayerTimes4=azan4.getPrayerTimes(today)
                     yatsı.text="Yatsı:  "+prayerTimes4.ishaa().toString()

                     val today= SimpleDate(GregorianCalendar())
                     val currentTime = Calendar.getInstance().time
                     var year = today.year
                     var month= today.month
                     var day= today.day


                     val endDateDay = "$day.$month.$year ${aksam1}"
                     val format1 = SimpleDateFormat("dd.MM.yyyy hh:mm:ss",Locale.getDefault())
                     val endDate = format1.parse(endDateDay)

                     //milliseconds
                     var different = endDate.time - currentTime.time
                     var  countDownTimer = object : CountDownTimer(different, 1000) {

                         override fun onTick(millisUntilFinished: Long) {
                             var diff = millisUntilFinished
                             val secondsInMilli: Long = 1000
                             val minutesInMilli = secondsInMilli * 60
                             val hoursInMilli = minutesInMilli * 60
                             val daysInMilli = hoursInMilli * 24

                             val elapsedDays = diff / daysInMilli
                             diff %= daysInMilli

                             val elapsedHours = diff / hoursInMilli
                             diff %= hoursInMilli

                             val elapsedMinutes = diff / minutesInMilli
                             diff %= minutesInMilli

                             val elapsedSeconds = diff / secondsInMilli

                             textView8.text=  "İftara $elapsedHours sa $elapsedMinutes dk $elapsedSeconds sn"
                             println("$elapsedDays days $elapsedHours hs $elapsedMinutes min $elapsedSeconds sec")


                         }

                         override fun onFinish() {

                                textView8.text= "Afiyet Olsun"


                         }
                     }.start()


                 }
                 else{
                     Toast.makeText(this,"Konumunuz Kapalı! Güncel Verileri Almak İçin Konumunuzu Açıp Güncelle Butonuna 1 Kez Tıklayınız" +
                             " Bildirim Panelindeki Konum Simgesi Kaybolduktan Sonra Tekrar Tıklayıp Verileri Alabilirsiniz",Toast.LENGTH_LONG).show()
                     imsak.text="İmsak: 00:00:00"
                     sabah.text="Sabah  00:00:00"
                     ogle.text="Öğle: 00:00:00"
                     ikindi.text="İkindi: 00:00:00"
                     aksam.text="Akşam: 00:00:00"
                     yatsı.text="Yatsı: 00:00:00"

                 }


             }
         }else{

             RequestPermission()
         }


    }


    private fun getNewLocation(){
        locationRequest= LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2

        fusedLocationProviderClient!!.requestLocationUpdates(

                locationRequest, locationCallBack, Looper.myLooper()

        )


    }

    private val locationCallBack= object :LocationCallback(){

        override fun onLocationResult(p0: LocationResult) {

            var lastLocation:Location=p0.lastLocation


        }
    }



    private fun CheckPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            return true
        }


        return false
    }

    private fun RequestPermission(){

        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID


        )



    }

    private fun isLocationEnabled():Boolean{

        var locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE)as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==PERMISSION_ID){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Debug", "You Have a Permission")

            }

        }


    }
    fun animate(){

        val rotate= AnimationUtils.loadAnimation(this,R.anim.animate)
        saat.animation=rotate


    }


}



