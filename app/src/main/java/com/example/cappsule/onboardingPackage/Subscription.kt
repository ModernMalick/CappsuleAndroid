package com.example.cappsule.onboardingPackage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.*
import com.example.cappsule.MainActivity
import com.example.cappsule.R
import com.example.cappsule.toaster


class Subscription : Fragment(), PurchasesUpdatedListener {

    private lateinit var billingClient : BillingClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subscription, container, false)

        activity?.findViewById<ViewPager2>(R.id.myViewPager)

        val accept = view.findViewById<Button>(R.id.accept)

        accept.setOnClickListener {
            subscribe()
        }

        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases().setListener(this).build()

        return view
    }

    private fun subscribe(){
        if (billingClient.isReady){
            initiatePurchase()
        } else {
            billingClient = BillingClient.newBuilder(requireContext())
                .enablePendingPurchases().setListener(this).build()
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    toaster(requireContext(), getString(R.string.Sub_disconnected))
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        initiatePurchase()
                    } else {
                        toaster(requireContext(), getString(R.string.Error) + billingResult.debugMessage)
                    }
                }
            })
        }
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
                        billingClient.launchBillingFlow(requireActivity(), flowParams)
                    } else {
                        toaster(requireContext(), getString(R.string.Sub_unavailable))
                    }
                } else {
                    toaster(requireContext(), getString(R.string.Error) + billingResult.debugMessage)
                }
            }
        } else {
            toaster(requireContext(), getString(R.string.Sub_notsupported))
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if ((billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
                    || billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            if (purchases != null) {
                handlePurchase(purchases)
            } else {
                onBoardingFinished()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        } else {
            toaster(requireContext(), getString(R.string.Sub_please))
        }
    }

    private fun handlePurchase(purchases: List<Purchase>){
        for (purchase in purchases){
            if(purchase.skus[0] == "cappsule_sub" && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                if(!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, {
                        if(it.responseCode == BillingClient.BillingResponseCode.OK) {
                            onBoardingFinished()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                    })
                }
            }
        }
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putString("min_temp", "20").apply()
    }
}