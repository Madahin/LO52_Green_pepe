LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := 	core.c \
			descriptor.c \
			io.c \
			sync.c \
			os/linux_usbfs.c \
			os/threads_posix.c

LOCAL_C_INCLUDES += 	$(LOCAL_PATH)/os \
			$(LOCAL_PATH)

LOCAL_MODULE := libusb-lib
LOCAL_MODULE_TAGS:= optional
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

LOCAL_PRELINK_MODULE:= false

include $(BUILD_SHARED_LIBRARY)
