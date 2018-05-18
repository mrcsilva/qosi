LOCAL_PATH:= $(call my-dir)

SOURCES := \
	src/cjson.c		\
	src/dscp.c  \
	src/iperf_api.c	    	\
	src/iperf_auth.c    \
	src/iperf_client_api.c  \
	src/iperf_error.c	\
	src/iperf_locale.c	\
	src/iperf_sctp.c \
	src/iperf_server_api.c  \
	src/iperf_tcp.c	        \
	src/iperf_udp.c	        \
	src/iperf_util.c	\
	src/main.c		\
	src/net.c		\
	src/tcp_info.c	    	\
	src/timer.c		\
	src/units.c

include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(SOURCES)
LOCAL_CFLAGS := -g -O2 -Wall -all-static
LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)
LOCAL_MODULE_TAGS := optional 
LOCAL_MODULE := iperf3
include $(BUILD_EXECUTABLE)
