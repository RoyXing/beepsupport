package tech.beepbeep.support.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.*
import tech.beepbeep.beep_support.BeepManager
import tech.beepbeep.beep_support.payment.PaymentResultListener
import tech.beepbeep.beep_support.payment.PaymentResultState
import tech.beepbeep.beep_support.payment.PaymentState
import tech.beepbeep.support.utils.ITEM_NUM
import tech.beepbeep.support.utils.ITEM_PRICE
import tech.beepbeep.support.R
import tech.beepbeep.support.databinding.ActivityPaymentinfoBinding
import tech.beepbeep.support.utils.PAYMENT_OPTION
import tech.beepbeep.support.utils.string2Bitmap

/**
 *desc:
 *Author: roy
 *Date:2019-11-25
 */
class PaymentInfoActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var dialog: ProgressDialog? = null
    private lateinit var binding: ActivityPaymentinfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paymentinfo)
        dialog = ProgressDialog(this)
        dialog!!.setTitle("loading...")
        dialog!!.show()

        supportActionBar?.title = "请支付"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        registerBeepManagerCallback()
        val itemNum = intent.extras?.get(ITEM_NUM) as String
        val itemPrice = intent.extras?.get(ITEM_PRICE) as String
        val paymentOption = intent.extras?.get(PAYMENT_OPTION) as String
        BeepManager.getInstance()
            .requestPayment(PaymentState.REQUEST, itemNum, itemPrice, paymentOption)
    }

    private fun registerBeepManagerCallback() {
        BeepManager.getInstance().addPaymentListener(object : PaymentResultListener {
            override fun paymentUpdate(paymentState: PaymentResultState, vararg any: Any?) {
                runOnUiThread {
                    when (paymentState) {
                        //获取选择的支付方式的详情成功 即PaymentState.REQUEST的回调
                        PaymentResultState.PAYMENT_INFO -> {
                            val qrCodeOrImage = any[0] as String
                            val desc = any[1] as String

                            binding.paymentInfoIcon.setImageBitmap(
                                string2Bitmap(
                                    qrCodeOrImage
                                )
                            )
                            binding.paymentInfoDesc.text = desc
                            dialog?.dismiss()
                        }
                        //sdk告诉Android支付成功，可以告诉机器掉落物品了
                        PaymentResultState.PAYMENT_SUCCESS -> {
                            dialog?.show()
                            dialog?.setTitle("Payment success,waiting vending...")
                            GlobalScope.launch(Dispatchers.Main) {
                                //模拟机器回调
                                delay(1000)
                                val random = (0 until 100).random()
                                if (random % 2 == 0) {
                                    dialog?.setTitle("Payment success,vending success...")
                                    //物品掉落成功，告诉SDK确认本次交易
                                    BeepManager.getInstance()
                                        .requestPayment(PaymentState.CONFIRM, "物品掉落成功,无需退款...")
                                } else {
                                    dialog?.setTitle("Payment success,vending failure...")
                                    //物品掉落失败，告诉SDK确认本次交易失败需要退款
                                    BeepManager.getInstance()
                                        .requestPayment(PaymentState.REFUND, "物品掉落失败,需要退款...")
                                }
                            }
                        }
                        //调用BeepManager.getInstance().requestPayment(PaymentState.CONFIRM/REFUND/CANCEL)之后的回调
                        PaymentResultState.PAYMENT_COMPLETE -> {
                            startActivity(
                                Intent(
                                    this@PaymentInfoActivity,
                                    MainActivity::class.java
                                )
                            )
                            dialog?.dismiss()
                        }
                        //SDK告诉Android结束本次交易
                        PaymentResultState.PAYMENT_CANCEL -> {
                            startActivity(
                                Intent(
                                    this@PaymentInfoActivity,
                                    MainActivity::class.java
                                )
                            )
                            dialog?.dismiss()
                        }
                    }
                }
            }

            //支付失败回调，可以回到选择支付页面 重新开始支付
            override fun paymentFailure(errorMessage: String) {
                startActivity(
                    Intent(
                        this@PaymentInfoActivity,
                        PaymentSelectActivity::class.java
                    )
                )
            }
        })
    }

    override fun onBackPressed() {
        BeepManager.getInstance().requestPayment(PaymentState.CANCEL)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (KeyEvent.KEYCODE_BACK) {
            keyCode -> {
                BeepManager.getInstance().requestPayment(PaymentState.CANCEL)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}