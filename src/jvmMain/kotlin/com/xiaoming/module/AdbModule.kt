package com.xiaoming.module

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.FileListingService
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaoming.entity.DeviceInfo
import com.xiaoming.screen.deviceInfo
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.LogUtil
import com.xiaoming.utils.getAdb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * android ddmslib adb
 */
object AdbModule {

    private fun loadDeviceInfo() {
        LogUtil.d("loadDeviceInfo")
        CoroutineScope(Dispatchers.Default).launch {
            val info = DeviceInfo()
            GlobalState.sCurrentDevice.value?.let { it ->
                it.serialNumber?.let { info.serialNo = it }
                it.name.let { info.device = it }
                info.memory = AdbUtil.shell("cat /proc/meminfo | grep MemTotal | awk '{print \$2/1024}'", 200) + "MB"
                info.density = AdbUtil.shell("wm size | awk '{print \$NF}'", 200)
                it.density.let { info.density += "(dpi = $it)" }
                info.cpu = AdbUtil.getProp("ro.soc.model")
                info.cpu += "(" + it.getProperty("ro.product.cpu.abi") + ",core size = " + AdbUtil.shell(
                    "cat /proc/cpuinfo | grep processor | wc -l",
                    200
                ) + ")"
                info.systemVersion = it.getProperty("ro.bootimage.build.fingerprint")?:""
                info.androidVersion = it.getProperty("ro.vendor.build.version.release")
                if (info.androidVersion.isBlank()) {
                    info.androidVersion = it.getProperty("ro.build.version.release")
                }
                info.model = it.getProperty("ro.product.model")
                info.brand = it.getProperty("ro.product.brand")
                info.ip = AdbUtil.shell("ifconfig wlan0 |  grep addr:1 |  awk  '{print \$2}'", 200)
                if (info.ip.contains(":")) {
                    info.ip = info.ip.split(":")[1]
                }
            }
            deviceInfo.value = info
        }
    }

    /**
     * 切换设备
     * @param device 切换的设备
     */
    fun changeDevice(device: IDevice?) {
        LogUtil.d("changeDevice device = $device")
        if (device == null) {
            deviceInfo.value = DeviceInfo()
            GlobalState.sCurrentDevice.value = null
        } else {
            GlobalState.sCurrentDevice.value = device as DeviceImpl
            loadDeviceInfo()
            AdbUtil.root()
            GlobalState.sCurrentDevice.value?.let {
                GlobalState.sFileListingService.value = FileListingService(it)
            }
        }
    }

    /**
     * 设备加入列表
     * @param device 添加的设备
     */
    private fun addDevice(device: IDevice) {
        LogUtil.d("addDevice device = $device")
        GlobalState.sDeviceSet.add(device)
        //当前未选中设备时默认选中第一个
        if (GlobalState.sCurrentDevice.value == null) {
            changeDevice(device)
        }
    }

    /**
     * 设备移出列表
     * @param device 移出的设备
     */
    private fun removeDevice(device: IDevice) {
        LogUtil.d("removeDevice device = $device")
        if (GlobalState.sDeviceSet.contains(device))
            GlobalState.sDeviceSet.remove(device)
        GlobalState.sCurrentDevice.value.let { currentDevice ->
            if (currentDevice == device) {
                changeDevice(if (GlobalState.sDeviceSet.isNotEmpty()) GlobalState.sDeviceSet.first() as DeviceImpl else null)
            }
        }
    }

    /**
     * 切换adb执行环境
     */
    fun changeAdb(value: String) {
        when (value) {
            LocalDataKey.DEFAULT.first -> {
                GlobalState.adb.value = File(GlobalState.workDir, getAdb()).absolutePath
            }

            LocalDataKey.ENV.first -> {
                GlobalState.adb.value = "adb"
            }

            else -> {
                GlobalState.adb.value = GlobalState.adbCustomPath.value

            }
        }

        if (GlobalState.adb.value.isEmpty()) {
            GlobalState.adb.value = "adb"
        }

        GlobalState.sDeviceSet.clear()
        GlobalState.sCurrentDevice.value = null
        AndroidDebugBridge.terminate()
        init()
    }

    /**
     * ddmslib 初始化
     */
    private fun init() {
        LogUtil.d("init adb = ${GlobalState.adb.value}")
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
                        if (device.isOnline) {
                            addDevice(device)
                        }
                    }
                }
            }

        })
        AndroidDebugBridge.init(false)
        AndroidDebugBridge.createBridge(GlobalState.adb.value, true, 5000L, TimeUnit.MILLISECONDS)
    }
}