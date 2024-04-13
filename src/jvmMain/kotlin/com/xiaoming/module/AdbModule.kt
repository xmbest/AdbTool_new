package com.xiaoming.module

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.FileListingService
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaoming.entity.DeviceInfo
import com.xiaoming.screen.deviceInfo
import com.xiaoming.state.GlobalState
import com.xiaoming.utils.AdbUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * android ddmslib adb
 */
object AdbModule {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private fun convertKBToGB(size: String): String {
        if (size.isBlank())
            return ""
        val df = DecimalFormat("0.00") //格式化小数
        return df.format(size.toDouble() / 1024 / 1024)
    }

    private fun loadDeviceInfo() {
        log.debug("loadDeviceInfo")
        CoroutineScope(Dispatchers.Default).launch {
            val info = DeviceInfo()
            GlobalState.sCurrentDevice.value?.let {
                it.serialNumber?.let { info.serialNo = it }
                it.name.let { info.device = it }
                AdbUtil.getProp("")
                val res = AdbUtil.shell("wm size", 200)
                val regex1 = Regex(pattern = """\d+x\d+""")
                val regex2 = Regex(pattern = """\d+""")
                info.memory = regex2.find(AdbUtil.shell("cat /proc/meminfo | grep MemTotal", 200))?.value ?: ""
                info.memory = "≈ " + convertKBToGB(info.memory) + "GB"
                info.density = regex1.find(res)?.value ?: ""
                it.density.let { info.density += "(dpi = $it)" }
                info.cpu = it.getProperty("ro.product.cpu.abi") + "(size = " + AdbUtil.shell(
                    "cat /proc/cpuinfo | grep processor | wc -l",
                    200
                ) + ")"
                info.systemVersion = it.getProperty("ro.bootimage.build.fingerprint")
                info.androidVersion = it.getProperty("ro.vendor.build.version.release")
                if (info.androidVersion.isBlank()) {
                    info.androidVersion = it.getProperty("ro.build.version.release")
                }
                info.model = it.getProperty("ro.product.model")
                info.brand = it.getProperty("ro.product.brand")
            }
            deviceInfo.value = info
        }
    }

    /**
     * 切换设备
     * @param device 切换的设备
     */
    fun changeDevice(device: IDevice?) {
        log.debug("changeDevice device = {}", device)
        if (device == null) {
            deviceInfo.value = DeviceInfo()
            GlobalState.sCurrentDevice.value = null
        } else {
            GlobalState.sCurrentDevice.value = device as DeviceImpl
            loadDeviceInfo()
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
        log.debug("addDevice device = {}", device)
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
        log.debug("removeDevice device = {}", device)
        if (GlobalState.sDeviceSet.contains(device))
            GlobalState.sDeviceSet.remove(device)
        GlobalState.sCurrentDevice.value.let { currentDevice ->
            if (currentDevice == device) {
                changeDevice(if (GlobalState.sDeviceSet.isNotEmpty()) GlobalState.sDeviceSet.first() as DeviceImpl else null)
            }
        }
    }

    /**
     * ddmslib 初始化
     */
    fun init() {
        log.debug("init")
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
        AndroidDebugBridge.createBridge(GlobalState.adb.value, true, 5000L, TimeUnit.MILLISECONDS)
    }
}