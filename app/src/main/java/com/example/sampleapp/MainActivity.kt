package com.example.sampleapp

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.payu.onepayujssdk.OnePayUJS
import com.payu.onepayujssdk.enums.EnvironmentType
import com.payu.onepayujssdk.listeners.OnePayUJSHashGenerationListener
import com.payu.onepayujssdk.listeners.OnePayUJSListener
import com.payu.onepayujssdk.models.OnePayUJSParams

class MainActivity : FragmentActivity(), OnePayUJSListener {

    //Test Key and Salt
    private val testKey = "smsplus"
    private val testSalt = "1b1b0"

    //Prod Key and Salt
    private val prodKey = "3TnMpV"
    private val prodSalt = "g0nGFe03"
    private var etEnvironment: EditText? = null
    private var etKey: EditText? = null
    private var etSalt: EditText? = null
    private var etRedirectUrl: EditText? = null
    private var etPhone: EditText? = null
    private var etWalletIdentifier: EditText? = null
    private var etUrn: EditText? = null
    private var etRequestID: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        etEnvironment = findViewById(R.id.etEnvironment)
        etKey = findViewById(R.id.etKey)
        etSalt = findViewById(R.id.etSalt)
        etRedirectUrl = findViewById(R.id.etRedirectUrl)
        etPhone = findViewById(R.id.etPhone)
        etWalletIdentifier = findViewById(R.id.etWalletIdentifier)
        etUrn = findViewById(R.id.etUrn)
        etRequestID = findViewById(R.id.etRequestID)
        setInitialData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setInitialData(){
        etKey?.setText(testKey)
        etSalt?.setText(testSalt)
        etPhone?.setText("9528340384")
        etWalletIdentifier?.setText("OLW")
        etUrn?.setText("1003097")
    }

    fun openCards(view: View) {
        val onePayUJSParams = prepareParams()
        Log.i(
            "OnePayULog",
            "Data - onePayUJSParams: $onePayUJSParams"
        )

        OnePayUJS.showCards(this, onePayUJSParams, this)
    }

    override fun onCancel() {
        Log.i(
            "OnePayULog",
            "onCancel called"
        )
        Toast.makeText(this, "onCancel called", Toast.LENGTH_SHORT).show()
    }

    override fun onError(code: Int, message: String) {
        Log.i(
            "OnePayULog",
            "onError called - code: $code | message: $message"
        )
        Toast.makeText(this, "message: $message | $code", Toast.LENGTH_SHORT).show()
    }

    override fun generateHash(
        map: HashMap<String, String?>,
        hashGenerationOnePayUJKListener: OnePayUJSHashGenerationListener
    ) {
        if (map.containsKey(Constants.HASH_STRING) && map.containsKey(Constants.HASH_NAME)
        ) {
            val hashData = map[Constants.HASH_STRING]
            val hashName = map[Constants.HASH_NAME]
            println("OnePayULog hashName and hashData : $hashName | $hashData ")

            val hash: String? = HashGenerationUtils.generateHashFromSDK(
                hashData!!, etSalt?.text.toString()
            )

            if (!TextUtils.isEmpty(hash)) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap[hashName!!] = hash!!
                Log.i(
                    "OnePayULog",
                    "generateHash called hash: $hash"
                )
                hashGenerationOnePayUJKListener.onHashGenerated(hashMap)
            }
        }
    }

    private fun prepareParams(): OnePayUJSParams {
        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        val requestId = System.currentTimeMillis().toString()+"payu"

        return OnePayUJSParams(
            environment = etEnvironment?.text.toString().uppercase(),
            merchantKey = etKey?.text.toString(),
            mobileNumber = etPhone?.text.toString(),
            walletIdentifier = etWalletIdentifier?.text.toString(),
            walletUrn = etUrn?.text.toString(),
            referenceId = requestId,
            additionalParams = additionalParamsMap
        )
    }
}