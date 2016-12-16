Common_Sources:=mlbin.c
LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE:=missillauncher
LOCAL_MODULE_TAGS:=optional
LOCAL_SRC_FILES:=$(Common_Sources)
LOCAL_SHARED_LIBRARIES := libusb-lib
LOCAL_C_INCLUDES:=$(LOCAL_PATH)/../libusb
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE) 

include $(BUILD_EXECUTABLE)
