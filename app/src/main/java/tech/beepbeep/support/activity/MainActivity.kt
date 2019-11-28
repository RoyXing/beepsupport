package tech.beepbeep.support.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import tech.beepbeep.beep_support.BeepManager
import tech.beepbeep.beep_support.telemetry.MachineState
import tech.beepbeep.support.utils.ITEM_NUM
import tech.beepbeep.support.R
import tech.beepbeep.support.adapter.SelectItemAdapter
import tech.beepbeep.support.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(),
    SelectItemAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var selectedNum = StringBuilder()

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this@MainActivity,
                R.layout.activity_main
            )
        supportActionBar?.title = "Vending Machine"

        val builder = StringBuilder()
        GlobalScope.launch(Dispatchers.IO) {
            initStatus().collect(object : FlowCollector<String> {
                override suspend fun emit(value: String) {
                    builder.append(value).append("\n")
                    withContext(Dispatchers.Main) {
                        binding.initStatusDesc.text = builder.toString()
                    }
                }
            })
        }.invokeOnCompletion(object : CompletionHandler {
            override fun invoke(cause: Throwable?) {
                runOnUiThread {
                    binding.keyboardLayout.visibility = VISIBLE
                    BeepManager.getInstance().updateStatus(MachineState.READY, "机器初始化完成，所有状态就绪")
                }
            }
        })
        val adapter = SelectItemAdapter()
        binding.recycler.adapter = adapter
        adapter.setOnItemClickListener(this)

        binding.selectedConfirm.setOnClickListener {
            val bundle = bundleOf(
                ITEM_NUM to selectedNum.toString()
            )
            val intent = Intent(this@MainActivity, PaymentSelectActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            selectedNum.clear()
        }
    }

    /**
     * 模拟 Vending machine开机初始化状态
     * 在该过程中可以调用 BeepManager.getInstance().updateStatus()
     * 方法来更新最新状态
     */
    private fun initStatus(): Flow<String> = flow {
        emit("Start up...")
        BeepManager.getInstance().updateStatus(MachineState.BUSY, "机器正在初始化中...")
        delay(1000)
        emit("check columns...")
        delay(200)
        emit("Ready")
        delay(1000)
        emit("check elevator...")
        delay(200)
        emit("Ready")
        delay(1000)
        emit("check door...")
        delay(200)
        emit("Ready")
        delay(1000)
        emit("check cash...")
        delay(200)
        emit("ERROR")
        BeepManager.getInstance().updateStatus(MachineState.WARNING, "现金支付功能初始化失败...")
        emit("retry")
        emit("Ready")
        delay(100)
        emit("")
        emit("...")
        emit("")
        delay(200)
        emit("All Ready,Machine can use...")
    }

    override fun onItemClick(view: View, value: String) {
        when (value) {
            "clear" -> {
                selectedNum.clear()
            }
            "del" -> {
                if (selectedNum.isNotEmpty())
                    selectedNum.deleteCharAt(selectedNum.length - 1)
            }
            else -> {
                selectedNum.append(value)
            }
        }
        if (selectedNum.isNotEmpty()) {
            binding.selectedNum.text = selectedNum.toString()
            binding.selectedNum.visibility = VISIBLE
        } else {
            binding.selectedNum.visibility = GONE
        }
    }
}
