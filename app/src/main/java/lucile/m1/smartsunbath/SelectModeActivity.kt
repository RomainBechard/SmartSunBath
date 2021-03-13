package lucile.m1.smartsunbath

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_mode.*
import lucile.m1.smartsunbath.modes.AutomaticModeActivity
import lucile.m1.smartsunbath.modes.ManuelModeActivity

class SelectModeActivity : AppCompatActivity() {

    //Function called when creating the activity, initializing layout (front end)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_mode)

        val device: BluetoothDevice? = intent.getParcelableExtra<BluetoothDevice>("ble_device")

        //Function that listens to the state of the Auto button -> if you press it you enter
        btAuto.setOnClickListener {
            Toast.makeText(applicationContext, "Mode Auto ON", Toast.LENGTH_LONG).show()
            val intent = Intent(this, AutomaticModeActivity::class.java)
            intent.putExtra("ble_device", device)
            startActivity(intent)
        }

        //Function that listens to the status of the Manual button -> if you press it you enter
        btManuel.setOnClickListener {
            Toast.makeText(applicationContext, "Mode Manuel ON", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ManuelModeActivity::class.java)
            intent.putExtra("ble_device", device)
            startActivity(intent)
        }
    }
}

