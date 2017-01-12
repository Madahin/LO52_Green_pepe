LOCAL_PATH := $(call my_dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := mlbin-jni.c
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE)

LOCAL_SHARED_LIBRARIES := libusb-lib \
			  libcutils \
			  libutils

LOCAL_MODULE:= missile-jni
LOCAL_MODULE_TAGS:= optional
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
