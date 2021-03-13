package lucile.m1.smartsunbath.modes

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_manuel_mode.*
import lucile.m1.smartsunbath.BLEUUIDMatching
import lucile.m1.smartsunbath.R
import java.util.*

@SuppressLint("UseSwitchCompatOrMaterialCode")
class ManuelModeActivity : AppCompatActivity() {

    lateinit var bluetoothGatt: BluetoothGatt
    lateinit var characteristicWrite: BluetoothGattCharacteristic
    lateinit var characteristicRead: BluetoothGattCharacteristic
    private lateinit var switch_1: Switch
    private lateinit var switch_2: Switch
    private lateinit var switch_3: Switch
    var problemString: String = "There was a problem"
    var notify: Boolean = false
    var isLightOn = false

    //Function called when creating the activity, initializing layout (front end)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuel_mode)

        val device: BluetoothDevice? = intent.getParcelableExtra<BluetoothDevice>("ble_device")

        if (device != null) {
            connectToDevice(device)
        }
        btBedroom.setOnClickListener {
            if (isLightOn) {
                turnOfflight(Rooms.BEDROOM_1)
                isLightOn = !isLightOn
            }
            else{
                turnOnlight(Rooms.BEDROOM_1)
                isLightOn = !isLightOn
            }

        }
    }

    private fun turnOfflight(room: Rooms) {
        if (room == Rooms.BEDROOM_1)
            writeCharacteristic(0x00000000)
    }


    private fun turnOnlight(room: Rooms) {
        if (room == Rooms.BEDROOM_1)
            writeCharacteristic(0x00000001)
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt,
                status: Int,
                newState: Int
            ) {

                val intentAction: String
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        intentAction = ACTION_GATT_CONNECTED
                        connectionState = STATE_CONNECTED
                        Log.i(TAG, "Connected to GATT server.")
                        Log.i(
                            TAG, "Attempting to start service discovery: " +
                                    bluetoothGatt.discoverServices()
                        )

                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        intentAction = ACTION_GATT_DISCONNECTED
                        connectionState = STATE_DISCONNECTED
                        Log.i(TAG, "Disconnected from GATT server.")
                    }
                }
            }

            // New services discovered
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                runOnUiThread {
                    characteristicRead = bluetoothGatt.getService(
                        UUID.fromString(BLEUUIDMatching.UNKNOWN_SERVICE.uuid)
                    ).getCharacteristic(
                        UUID.fromString(BLEUUIDMatching.UNKNOWN_CHARACTERISTIC_1.uuid)
                    )
                    characteristicWrite = bluetoothGatt.getService(
                        UUID.fromString(BLEUUIDMatching.UNKNOWN_SERVICE.uuid)
                    ).getCharacteristic(
                        UUID.fromString(BLEUUIDMatching.UNKNOWN_CHARACTERISTIC_1.uuid)
                    )
                }
            }

            // Result of a characteristic read operation
            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                val value = characteristic.value
                Log.d("CharRead", "onCharacteristicRead: $value")
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                val value = characteristic.value
                var readValue: Int? = null
                Log.d(
                    "CharWrite",
                    "onCharacteristicWrite: $value"
                )
                runOnUiThread {
                    /*readValue  = characteristicRead.getIntValue(FORMAT_UINT8, 0)
                    if (readValue == null)
                        Log.e("CharRead", "Failed to read value")
                    else{
                        Log.e("CharRead", "Succeeded to read value")
                        DataRxTextView.text = readValue.toString()
                    }*/

                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic
            ) {
                val value = byteArrayToHexString(characteristic.value)
                Log.d(
                    "Char",
                    "onCharacteristicChanged: $value"
                )
                runOnUiThread {
                    //if (characteristic == characteristicRead)
                }
            }
        })
    }

    private fun readLightState() {
        val batteryServiceUuid = UUID.fromString(BLEUUIDMatching.UNKNOWN_SERVICE.uuid)
        val batteryLevelCharUuid = UUID.fromString(BLEUUIDMatching.UNKNOWN_CHARACTERISTIC_1.uuid)
        val batteryLevelChar =
            bluetoothGatt.getService(batteryServiceUuid)?.getCharacteristic(batteryLevelCharUuid)
    }

    private fun writeCharacteristic(action: Int) {
        characteristicWrite.setValue(action, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        characteristicWrite.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
        if (bluetoothGatt.writeCharacteristic(characteristicWrite))
            Log.e("CharWrite", "Write Successfull")
        else
            Log.e("CharWrite", "Write Failed")
    }

    private fun byteArrayToHexString(array: ByteArray): String {
        val result = StringBuilder(array.size * 2)
        for (byte in array) {
            val toAppend = String.format("%X", byte) // hexadecimal
            result.append(toAppend).append("-")
        }
        result.setLength(result.length - 1) // remove last '-'
        return result.toString()
    }

    fun hexStringToByteArray(s: String): ByteArray? {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    enum class Rooms(name: String, lightOnNumber: Int, lightOffNumber: Int, state: Boolean) {
        BEDROOM_1("Bedroom1", 0x0001, 0x0000, false),
        BEDROOM_2("Bedroom2", 0x0002, 0x0000, false),
        KITCHEN("Kitchen", 0x0003, 0x0000, false)
    }

    companion object {
        private val TAG = "BLEDeviceActivity"
        private val STATE_DISCONNECTED = "disconnected"
        private val STATE_CONNECTING = "connecting"
        private val STATE_CONNECTED = "connected"
        val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        private var connectionState = STATE_DISCONNECTED
    }
}

