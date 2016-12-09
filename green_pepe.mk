$(call inherit-product, device/linaro/pandaboard/pandaboard.mk)

DEVICE_PACKAGE_OVERLAYS := device/utbm/green_pepe/overlay
PRODUCT_COPY_FILES := $(LOCAL_PATH)/bootanimation.zip:/system/media/bootanimation.zip

PRODUCT_NAME:= green_pepe
PRODUCT_DEVICE:= green_pepe
PRODUCT_BRAND:= Android
PRODUCT_MODEL:= Android
PRODUCT_PACKAGES+= missillauncher

include $(call all-subdir makefiles)
