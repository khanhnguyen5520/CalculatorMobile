package com.example.caculatormobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.caculatormobile.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class MainActivity : ComponentActivity() {

    private var number: String? = null
    private var history: String? = null
    private var result: String? = null
    private var status: String? = null

    private lateinit var binding: ActivityMainBinding

    private var firstNum: Double = 0.0
    private var secondNum: Double = 0.0

    private var operation: Boolean = false
    private var isEqual: Boolean = false
    private var isDot: Boolean = true

    private var isDel: Boolean = false

    private val pattern = "#.##"
    private val format = DecimalFormat(pattern)

    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
        // Create an ad request.
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        binding.adView.loadAd(adRequest)


        binding.btnAds.setOnClickListener{
            loadAd()
        mInterstitialAd?.show(this)
        }
        binding.btnAdd.setOnClickListener {

            if (isEqual){
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history+").also { binding.tvShow.text = it }

            }else{
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history$result+").also { binding.tvShow.text = it }
            }
            isEqual = false
            if (operation){
                Add()
            }
            operation = false
            number = null
            status = "add"
        }

        binding.btnSub.setOnClickListener {
            if (isEqual){
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history-").also { binding.tvShow.text = it }

            }else{
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history$result-").also { binding.tvShow.text = it }
            }
            isEqual=false
            if (operation){
                Sub()
            }
            operation = false
            number = null
            status = "sub"
        }

        binding.btnMul.setOnClickListener {

            if (isEqual){
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history*").also { binding.tvShow.text = it }

            }else{
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history$result*").also { binding.tvShow.text = it }
            }
            isEqual=false
            if (operation){
                Mul()
            }
            operation = false
            number = null
            status = "mul"
        }

        binding.btnDiv.setOnClickListener {
            if (isEqual){
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history/").also { binding.tvShow.text = it }

            }else{
                history = binding.tvShow.text.toString()
                result = binding.tvResult.text.toString()
                ("$history$result/").also { binding.tvShow.text = it }
            }
            isEqual = false
            if (operation){
                Div()
            }
            operation = false
            number = null
            status = "div"
        }

        binding.btnEqual.setOnClickListener {

            history = binding.tvShow.text.toString()
            result = binding.tvResult.text.toString()
            ("$history$result").also { binding.tvShow.text = it }
            if (operation){
                when(status){
                    "mul" -> Mul()
                    "div" -> Div()
                    "sub" -> Sub()
                    "add" -> Add()
                    else -> firstNum = binding.tvResult.text.toString().toDouble()
                }
            }
            operation = false
            isEqual = true
            isDot = false
        }

        binding.btnC.setOnClickListener {
            number = null
            operation = false
            binding.tvResult.text = ""
            binding.tvShow.text = ""
            firstNum = 0.0
            secondNum = 0.0
            isDot = true
            isDel = false
            isEqual = false
        }

        binding.btnDel.setOnClickListener {
            if (isDel){
                number = number?.substring(0, number!!.length-1)
                if (number!!.isEmpty()){
                    binding.btnDel.isClickable = false
                }else{
                    if(number!!.contains(".")){
                        isDot=false
                    }else{
                        isDot = true
                    }
                }
            }
            binding.tvResult.text = number
        }
        binding.btnDot.setOnClickListener {
            if (isDot){
                number = if (number == null) "0." else "$number."
            }
            isDot = false
            binding.tvResult.text = number
        }
    }

    private fun btnClick(btn:String) {
        number = if(number == null) btn else number + btn
        binding.tvResult.text = number
        operation = true
        isDel = true
        binding.btnDel.isClickable = true
    }

    fun Sub(){
        if(firstNum == 0.0){
            firstNum = binding.tvResult.text.toString().toDouble()
        } else {
            secondNum = binding.tvResult.text.toString().toDouble()
            firstNum -= secondNum
        }
        binding.tvResult.text = format.format(firstNum)
        isDot = true
    }

    fun Add(){
        secondNum = binding.tvResult.text.toString().toDouble()
        firstNum += secondNum
        binding.tvResult.text = format.format(firstNum)
        isDot = true
    }

    fun Mul(){
        if (firstNum == 0.0) firstNum = 1.0
        secondNum = binding.tvResult.text.toString().toDouble()
        firstNum *= secondNum
        binding.tvResult.text = format.format(firstNum)
        isDot = true
    }

    fun Div(){

        if (firstNum == 0.0){
            secondNum = binding.tvResult.text.toString().toDouble()
            firstNum = secondNum
        } else {
            secondNum = binding.tvResult.text.toString().toDouble()
            firstNum /= secondNum
        }
        binding.tvResult.text = format.format(firstNum)
        isDot = true
    }

    private fun init(){
        binding.btn0.setOnClickListener { btnClick(binding.btn0.text.toString()) }
        binding.btn1.setOnClickListener { btnClick(binding.btn1.text.toString()) }
        binding.btn2.setOnClickListener { btnClick(binding.btn2.text.toString()) }
        binding.btn3.setOnClickListener { btnClick(binding.btn3.text.toString()) }
        binding.btn4.setOnClickListener { btnClick(binding.btn4.text.toString()) }
        binding.btn5.setOnClickListener { btnClick(binding.btn5.text.toString()) }
        binding.btn6.setOnClickListener { btnClick(binding.btn6.text.toString()) }
        binding.btn7.setOnClickListener { btnClick(binding.btn7.text.toString()) }
        binding.btn8.setOnClickListener { btnClick(binding.btn8.text.toString()) }
        binding.btn9.setOnClickListener { btnClick(binding.btn9.text.toString()) }
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }
}