##源文件在当前目录
LOCAL_PATH := $(call my-dir)
##开始，清除变量
include $(CLEAR_VARS)
##编译目标
LOCAL_MODULE := tagjni
##支持异常处理
##LOCAL_CPPFLAGS += -fexceptions
##要编译的源文件
LOCAL_SRC_FILES := Utils.cpp\
		   APIC.cpp\
		   lyrics.cpp
##添加log日志输出
LOCAL_LDLIBS += -llog
##编译成静态库		
include $(BUILD_SHARED_LIBRARY)

##第二个
include $(CLEAR_VARS)
LOCAL_MODULE := ID3rw
LOCAL_SRC_FILES := reWriteID3.cpp\
		   Utils.cpp
LOCAL_LDLIBS += -llog		
include $(BUILD_SHARED_LIBRARY)

##第三个
include $(CLEAR_VARS)
LOCAL_MODULE := feedback
LOCAL_SRC_FILES := feedback.cpp
LOCAL_LDLIBS += -llog	
include $(BUILD_SHARED_LIBRARY)