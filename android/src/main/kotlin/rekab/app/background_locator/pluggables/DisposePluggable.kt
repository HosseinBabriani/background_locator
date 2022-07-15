package rekab.app.background_locator.pluggables

import android.content.Context
import android.os.Handler
import io.flutter.plugin.common.MethodChannel
import rekab.app.background_locator.IsolateHolderService
import rekab.app.background_locator.Keys
import rekab.app.background_locator.PreferencesManager

class DisposePluggable : Pluggable {
    override fun setCallback(context: Context, callbackHandle: Long) {
        PreferencesManager.setCallbackHandle(context, Keys.DISPOSE_CALLBACK_HANDLE_KEY, callbackHandle)
    }

    override fun onServiceDispose(context: Context) {
        (PreferencesManager.getCallbackHandle(context, Keys.DISPOSE_CALLBACK_HANDLE_KEY))?.let { disposeCallback ->
            Handler(context.mainLooper)
                    .post {
                        IsolateHolderService.backgroundEngine?.dartExecutor?.binaryMessenger?.let {
                            MethodChannel(
                                it,
                                Keys.BACKGROUND_CHANNEL_ID
                            )
                        }?.invokeMethod(
                            Keys.BCM_DISPOSE,
                            hashMapOf(Keys.ARG_DISPOSE_CALLBACK to disposeCallback)
                        )
                    }
        }
    }
}