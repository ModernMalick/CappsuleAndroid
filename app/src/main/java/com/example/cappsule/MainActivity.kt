package com.example.cappsule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.example.cappsule.fragment.Home
import com.example.cappsule.fragment.Settings
import com.example.cappsule.fragment.Wardrobe
import com.example.cappsule.onboardingPackage.Onboarding
import com.example.cappsule.onboardingPackage.Subscription
import com.google.android.material.navigation.NavigationBarView
import java.util.*


class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private var selectedFragment: Fragment = Home()
    private var currentFragmentName: String = "Home"
    private lateinit var billingClient: BillingClient
    private lateinit var fragmentView: View
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!onBoardingFinished()){
            startActivity(Intent(applicationContext, Onboarding::class.java))
            finish()
            return
        }

        val bottomNavigationView = findViewById<NavigationBarView>(R.id.myBottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem -> onNavigationItemSelected(item) }
        bottomNavigationView.menu.getItem(0).isChecked = true
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit()

        fragmentView = findViewById(R.id.fragment_container_view)
        constraintLayout = findViewById(R.id.subscriptionCons)

        val buttonRenew = findViewById<Button>(R.id.buttonRenew)
        buttonRenew.setOnClickListener {
            initiatePurchase()
        }

        isUserSubscribed(this)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                if(currentFragmentName != "Home"){
                    selectedFragment = Home()
                    currentFragmentName = "Home"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
            R.id.wardrobe -> {
                if(currentFragmentName != "Wardrobe"){
                    selectedFragment = Wardrobe()
                    currentFragmentName = "Wardrobe"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
            R.id.menu_settings -> {
                if(currentFragmentName != "Settings"){
                    selectedFragment = Settings()
                    currentFragmentName = "Settings"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit()
        return true
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPreferences = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }

    private fun isUserSubscribed(context: Context) {
        Log.e("1", "1")
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.e("2", "2")
                val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult1: BillingResult, list: List<PurchaseHistoryRecord?>? ->
                    Log.e("billingprocess", "purchasesResult.getPurchasesList():" + purchasesResult.purchasesList)
                    if (billingResult1.responseCode == BillingResponseCode.OK){
                        if(Objects.requireNonNull(purchasesResult.purchasesList).isEmpty()){
                            this@MainActivity.runOnUiThread(java.lang.Runnable {
                                constraintLayout.visibility = View.VISIBLE
                                fragmentView.visibility = View.GONE
                            })
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
            }
        })
    }

    private fun initiatePurchase() {
        val skuList = ArrayList<String>()
        skuList.add("cappsule_sub")
        val param = SkuDetailsParams.newBuilder()
        param.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        val billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
            billingClient.querySkuDetailsAsync(param.build()) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && skuDetailsList.size != 0) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient.launchBillingFlow(this, flowParams)
                    } else {
                        toaster(this, getString(R.string.Sub_unavailable))
                    }
                } else {
                    toaster(this, getString(R.string.Error) + billingResult.debugMessage)
                }
            }
        } else {
            toaster(this, getString(R.string.Sub_notsupported))
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if ((billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            || billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            if (purchases != null) {
                handlePurchase(purchases)
            } else {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    constraintLayout.visibility = View.GONE
                    fragmentView.visibility = View.VISIBLE
                })
            }
        } else {
            toaster(this, getString(R.string.Sub_please))
        }
    }

    private fun handlePurchase(purchases: List<Purchase>){
        for (purchase in purchases){
            if(purchase.skus[0] == "cappsule_sub" && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                if(!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, AcknowledgePurchaseResponseListener {
                        if(it.responseCode == BillingClient.BillingResponseCode.OK) {
                            this@MainActivity.runOnUiThread(java.lang.Runnable {
                                constraintLayout.visibility = View.GONE
                                fragmentView.visibility = View.VISIBLE
                            })
                        }
                    })
                }
            }
        }
    }
}