package com.lambdasoup.notyet

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class DeviceAdminRec : DeviceAdminReceiver()

class DeviceAdmin(context: Context) {
    private val devicePolicyManager: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val deviceAdminComponentName = ComponentName(context, DeviceAdminRec::class.java)

    private val explainerString = context.getString(R.string.device_admin_needed_explainer)

    fun isEnabled() = devicePolicyManager.isAdminActive(deviceAdminComponentName)

    fun getEnableIntent(): Intent =
        Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName)
            .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explainerString)

    fun lockScreen() {
        if (isEnabled()) {
            devicePolicyManager.lockNow()
        }
    }
}
