#ifndef _UTILS
#define _UTILS
#include <android/log.h>
#include <stdio.h>
#define LOG_TAG "MYJNI"
typedef unsigned char BYTE;
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)//调试日志输出
enum AUDIO{MP3,WMA,UNSUPPORT};
extern bool ArrayEqual(const BYTE *,const BYTE *,int);
extern bool ArrayEqual(const BYTE *,const BYTE *,int,int);
extern int getID3size(BYTE *);
extern int getFramesize(BYTE *);
extern int getInt(FILE *);
extern long filesize(FILE *);
extern void copy(FILE *,FILE*,long);
extern AUDIO checkExt(const char *);
#endif