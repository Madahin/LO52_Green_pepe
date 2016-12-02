$(call inherit-product, device/linaro/pandaboard/pandaboard.mk)

DEVICE_PACKAGE_OVERLAYS := device/utbm/green_pepe/overlay
PRODUCT_COPY_FILE := overlay/system/media/bootanimation.zip:system/media/bootanimation.zip

PRODUCT_NAME:= green_pepe
PRODUCT_DEVICE:= green_pepe
PRODUCT_BRAND:= Android
PRODUCT_MODEL:= Android

include $(call all-subdir makefiles)
