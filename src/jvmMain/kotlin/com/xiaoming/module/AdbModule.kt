package com.xiaming.module

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaoming.state.GlobalState

/**
 * android ddmslib adb
 */
object AdbModule {


    /**
     * 切换设备
     * @param device 切换的设备
     */
    fun changeDevice(device: IDevice) {
        if (GlobalState.sDeviceSet.contains(device)) {
            GlobalState.sCurrentDevice.value = device as DeviceImpl
        }
    }

    /**
     * 设备加入列表
     * @param device 添加的设备
     */
    private fun addDevice(device: IDevice) {
        GlobalState.sDeviceSet.add(device)
        //当前未选中设备时默认选中第一个
        if (GlobalState.sCurrentDevice.value == null) {
            GlobalState.sCurrentDevice.value = device as DeviceImpl
        }
    }

    /**
     * 设备移出列表
     * @param device 移出的设备
     */
    private fun removeDevice(device: IDevice) {
        if (GlobalState.sDeviceSet.contains(device))
            GlobalState.sDeviceSet.remove(device)
        GlobalState.sCurrentDevice.value.let { currentDevice ->
            if (currentDevice == device) {
                GlobalState.sCurrentDevice.value =
                    if (GlobalState.sDeviceSet.isNotEmpty()) GlobalState.sDeviceSet.first() as DeviceImpl else null
            }
        }
    }

    /**
     * ddmslib 初始化
     */
    fun init() {
        AndroidDebugBridge.addDeviceChangeListener(object : AndroidDebugBridge.IDeviceChangeListener {
            override fun deviceConnected(device: IDevice?) {
                device?.let {
                    addDevice(device)
                }
            }

            override fun deviceDisconnected(device: IDevice?) {
                device?.let {
                    removeDevice(it)
                }
            }

            override fun deviceChanged(device: IDevice?, changeMask: Int) {
                device?.let {
                    if (GlobalState.sDeviceSet.contains(device)) {
                        if (device.isOffline) {
                            removeDevice(device)
                        }
                    } else {
                        if (device.isOffline) {
                            addDevice(device)
                        }
                    }
                }
            }

        })
        AndroidDebugBridge.init(false)
        AndroidDebugBridge.createBridge()
    }
}