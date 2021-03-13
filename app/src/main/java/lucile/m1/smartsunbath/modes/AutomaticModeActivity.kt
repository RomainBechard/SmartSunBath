package lucile.m1.smartsunbath.modes

import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_automatic_mode.*
import lucile.m1.smartsunbath.R

class AutomaticModeActivity : AppCompatActivity() {

    //RESET BRIGHTNESS VALUE
    private var valLuminosite: Int = 0

    //Function called when creating the activity, initializing layout (front end)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_automatic_mode)

        val device: BluetoothDevice? = intent.getParcelableExtra<BluetoothDevice>("ble_device")

        // The brightness value will take the value indicated by the sensor
        tvValLuminosite.text = valLuminosite.toString()
    }
}


