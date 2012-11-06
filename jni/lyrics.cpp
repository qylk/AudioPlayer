#include <string.h>
#include <stdio.h>
#include "lyrics.h"
#include "Utils.H"
//************************************
//作者：清源林客2012 copyright
//语言：C++
//Blog：Http:\\www.qylk.blog.163.com
//Time：2012-10-19
//Ver： v1.0
//************************************
JNIEXPORT jboolean JNICALL Java_cn_qylk_lrc_MediaLyric_LyricFromTag(JNIEnv *env, jobject obj, jstring title, jstring mp3path){//从文件尾部读取歌词
	const char * mp3=env->GetStringUTFChars(mp3path, false);//将java中的字符串转为C语言中的字符串数组(UFT)
	FILE * fp=fopen(mp3,"rh");//打开mp3文件
	env->ReleaseStringUTFChars(mp3path, mp3);//释放字符串资源 
	if(fp==NULL)return JNI_FALSE;//打开失败返回
	fseek(fp,-137,SEEK_END);//定位到文件尾倒数第137个字节
	char identifer[9];//标识
	fread(identifer,1,sizeof(identifer),fp);//读入LYRICS200标识
if(ArrayEqual(identifer,"LYRICS200",sizeof(identifer))){//对比标志
	fseek(fp,-141,SEEK_END);//定位到文件尾倒数第141个字节
	int i=getInt(fp)-30;//读取歌词长度，30是固定的
	if(i<50){fclose(fp);return JNI_FALSE;}//长度太短，一般不是歌词
	char log[100];//写log
	sprintf(log,"Reading Lyrics From %s",mp3);
	LOGI(log);
	char *lrc=new char[i];//分配所需内存
	const char *file=env->GetStringUTFChars(title,false); //字符串转换
	char *pathto=new char[17 +strlen(file)+5];//准备再拼个歌词路径
	fseek(fp,-143-i,SEEK_END);//定位到歌词开始位置
	fread(lrc,1,i,fp);//读歌词数据
	memset(pathto,0x00,sizeof(pathto));//strcat前，容器清空
	strcat(pathto,"/sdcard/qylk/lrc/");//存放路径
	strcat(pathto,file);//文件名
	strcat(pathto,".lrc");//后缀
	FILE *fpp=fopen(pathto,"wb+");
	fwrite(lrc,i,1,fpp);//歌词写入文件
	sprintf(log,"Writting Lyrics to %s",pathto);
	LOGI(log);
	fclose(fpp);
	env->ReleaseStringUTFChars(title, file);
	delete[] lrc;//释放堆上资源
	delete[] pathto;
	fclose(fp);
	return JNI_TRUE;
}else{
	fclose(fp);
	return JNI_FALSE;
}
	
}