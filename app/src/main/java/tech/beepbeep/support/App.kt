package tech.beepbeep.support

import android.app.Application
import android.content.Context
import tech.beepbeep.beep_support.BeepManager
import tech.beepbeep.beep_support.payment.PaymentOptions
import tech.beepbeep.beep_support.serial.SerialPort
import tech.beepbeep.beep_support.serial.SerialPortNumber
import java.io.InputStream
import java.io.OutputStream

/**
 *desc:
 *Author: roy
 *Date:2019-11-19
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        val demoSerialPorts = arrayListOf(
            SerialPort(SerialPortNumber.ZERO, "/dev/ttyS0"),
            SerialPort(SerialPortNumber.ONE, "/dev/ttyS1"),
            SerialPort(SerialPortNumber.TWO, "/dev/ttyS2")
        )
        BeepManager.getInstance()
            .register(
                "uuid",
                BuildConfig.DEBUG,
                demoSerialPorts,
                object : BeepManager.BeepConnector {
                    override fun getPaymentOptionsInfo(paymentOptions: List<PaymentOptions>) {
                        //Machine Ready之后返回支持的所有支付方式，可以在本地做存储 使用时直接取
                        payments = paymentOptions
                    }

                    override fun secureStore(key: String, value: String) {
                        //帮助SDK存储一些需要持久化的内容如token
                        sharedPreferences.edit().putString(key, value).apply()
                    }

                    override fun secureRetrieve(key: String): Any? {
                        //取出存在Android中的内容
                        val mutableMap = sharedPreferences.all
                        return mutableMap[key]
                    }

                    override fun accessSerialPort(serialPort: SerialPort): Pair<InputStream, OutputStream>? {
                        //例如SDK返回 SerialPort(SerialPortNumber.ZERO, "/dev/ttyS0")，请求打开端口"/dev/ttyS0"
                        //并且返回该端口的输入输出流
                        return null
                    }
                })
    }

    companion object {
        private var payments: List<PaymentOptions>? = null
        fun getPaymentOptions(): List<PaymentOptions>? {
            return payments
        }
    }
}