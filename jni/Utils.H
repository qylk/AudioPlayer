#ifndef _UTILS
#define _UTILS
#include <android/log.h>
#include <stdio.h>
#define LOG_TAG "MYJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)//调试日志输出
extern bool ArrayEqual(char *,char const *,int);
extern int getID3size(char *);
extern int getFramesize(char *);
extern int getInt(FILE *);
extern long filesize(FILE *);
extern void copy(FILE *,FILE*,long);
#endif