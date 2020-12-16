package jp.eplus.diamondseat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DeviceUtils {
    private volatile static String uuid;

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    private static String getAndroidID(Context context) {
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        if ("9774d56d682e549c".equals(id)) return "";
        return id == null ? "" : id;
    }

    /**
     * Return the unique device id.
     * <pre>{prefix}{2}{UUID(androidId )}</pre>
     * <pre>{prefix}{9}{UUID(random    )}</pre>
     *
     * @return the unique device id
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceUUID(Context context, SPUtils spUtils) {
        if (uuid == null) {
            synchronized (DeviceUtils.class) {
                if (uuid == null) {
                    final String id = spUtils.getDeviceId();
                    if (id != null) {
                        uuid = id;
                        return uuid;
                    }
                    String androidId;
                    try {
                        androidId = getAndroidID(context);
                        if (!androidId.isEmpty()) {
                            uuid = UUID.nameUUIDFromBytes(androidId.getBytes(StandardCharsets.UTF_8)).toString();
                        } else {
                            final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes(StandardCharsets.UTF_8)).toString() : UUID.randomUUID().toString();
                        }
                    } catch (Exception ignore) {/**/} finally {
                        if (uuid == null || uuid.isEmpty()) uuid = UUID.randomUUID().toString();
                        spUtils.setDeviceId(uuid);
                    }
                }
            }
        }
        return uuid;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + " " + model;
    }
}
