include $(CLEAR_VARS)

LOCAL_MODULE:=com.android.utbm.green_pepe.missillauncher_JNI.missillauncher.xml
LOCAL_MODULE_TAGS:=optional
LOCAL_SRC_FILES:=$(LOCAL_MODULE)
LOCAL_MODULE_CLASS:=ETC

LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions

include $(BUILD_PREBUILT)