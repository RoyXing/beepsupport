package tech.beepbeep.support.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.*
import tech.beepbeep.beep_support.BeepManager
import tech.beepbeep.beep_support.payment.PaymentOptions
import tech.beepbeep.beep_support.payment.bean.CashPayment
import tech.beepbeep.support.utils.ITEM_NUM
import tech.beepbeep.support.utils.ITEM_PRICE
import tech.beepbeep.support.R
import tech.beepbeep.support.adapter.PaymentOptionAdapter
import tech.beepbeep.support.databinding.ActivityPaymentBinding
import tech.beepbeep.support.utils.PAYMENT_OPTION
import java.util.*

/**
 *desc:
 *Author: roy
 *Date:2019-11-25
 */
class PaymentSelectActivity : AppCompatActivity(), CoroutineScope by MainScope(),
    PaymentOptionAdapter.OnItemClickListener {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var itemNum: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_payment
        )

        supportActionBar?.title = "请选择支付方式"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemNum = intent.extras?.getString(ITEM_NUM).toString()
        Log.e("roy", itemNum + "")
        val adapter = PaymentOptionAdapter()
        binding.paymentRecycler.adapter = adapter
        adapter.setOnItemClickListener(this)
    }

    override fun onItemClick(view: View, value: PaymentOptions) {
        if (value.name == "Cash") {
            val dialog = ProgressDialog(this)
            dialog.setTitle("请投入现金...")
            dialog.show()
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)
                dialog.setTitle("支付成功...")
                delay(2000)

                // 添加现金支付信息收集
                BeepManager.getInstance()
                    .addCashTransactionHistory(CashPayment("111", "2.80", Date().toString()))

                startActivity(
                    Intent(
                        this@PaymentSelectActivity,
                        MainActivity::class.java
                    )
                )
                dialog.dismiss()
            }
        } else {
            val bundle = bundleOf(
                ITEM_NUM to itemNum,
                ITEM_PRICE to "1.0",
                PAYMENT_OPTION to value.name
            )
            val intent = Intent(this, PaymentInfoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}