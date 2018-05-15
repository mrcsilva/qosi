LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie

LOCAL_MODULE := iperf3
LOCAL_SRC_FILES :=                         cjson.c \
                                           iperf_api.c \
                                           iperf_error.c \
                   			               iperf_client_api.c \
                                           iperf_locale.c \
                                           iperf_server_api.c \
                                           iperf_tcp.c \
                                           iperf_udp.c \
                   			               iperf_sctp.c \
                                           iperf_util.c \
                                           main.c \
                                           net.c \
                                           tcp_info.c \
                                           tcp_window_size.c \
                                           timer.c \
                                           units.c

include $(BUILD_EXECUTABLE)
