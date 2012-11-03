#include <string.h>
#include <stdio.h>
#include <wchar.h>
#include "APIC.H"
#include "Utils.H"
//************************************
//作者：清源林客2012 copyright
//语言：C++
//Blog：Http:\\www.qylk.blog.163.com
//Time：2012-10-18
//Ver： v1.1
//************************************

//APIC Frame Struct
/*<Header for 'Attached picture', ID: "APIC">
     Standard Frame Header 	10byte
     Text encoding      	1byte
     MIME type          	<text string> end with 0x00(like"image/jpeg",only JPG and PNG are mentioned specifically in the ID3 v22 Spec,other types may not be portable)
     Picture type      		1byte
     Description        	<text string according to encoding> end with 0x00(0x00)(generally is just one 0x00)
     Picture data       	<binary data>
     
     ID3 supported Text encoding:
     	0x00=>"ISO-8859-1"
    	0x01=>"UTF-16"  
    	0x02=>"UTF-16BE" //Unicode strings must begin with the Unicode BOM($FF FE or $FE FF) to identify the byte order
   	0x03=>"UTF-8"
*/

JNIEXPORT jboolean JNICALL Java_cn_qylk_media_ArtistInfo_ApicFromTag(JNIEnv *env, jobject obj, jstring artist, jstring mp3path)//读取专辑图片，给定参数artist和歌曲文件完整路径
{
	const char * mp3=env->GetStringUTFChars(mp3path, false);//将java中的字符串转为C语言中的字符串(UTF)
	FILE * fp=fopen(mp3,"rb");//只读打开文件 
	env->ReleaseStringUTFChars(mp3path, mp3);
	if(fp==NULL) return JNI_FALSE;
	char head[10];//id3标签头
	fread(head,sizeof(head),1,fp);//读标签头
 	if(!ArrayEqual(head,"ID3",3)) {//对比标识
 		fclose(fp);
 		return JNI_FALSE;
	}	
 	int id3v2size=getID3size(head);//标签大小
 	char frame[4];//帧标识
 	int i=10;//帧起始位置
 	int size;//记录帧大小
 	while(i<id3v2size){
		fread(frame,4,1,fp);//读帧标识
        	if(ArrayEqual(frame,"APIC",4))//对比"APIC"
 	 		 break;		
       		else{
          	  fread(frame,4,1,fp);//读帧大小
          	  size=getFramesize(frame);//转换为int
          	  fseek(fp,size+2,SEEK_CUR);//跳过Flags
          	  i+=size;
          	  i+=10;//帧头占了10字节
        }
 }
   if(i<id3v2size){//找到了APIC帧
	fread(frame,4,1,fp);//读APIC大小
	size=getFramesize(frame);//取帧大小
	fseek(fp,2,SEEK_CUR);//跳过Flags；
	int start=ftell(fp);//帧数据起始位置
	char coding=fgetc(fp);//Text encoding位
	while(fgetc(fp)!=0x00);//读到MIME type字符串结尾
	fgetc(fp);//Picture type位
	if(coding==0x01||coding ==0x02)//UTF-16 coding
	  {
	     while(getwc(fp)!=0x0000)
	     	fgetc(fp);
	     	fgetc(fp);
	  }
	else
	     while(fgetc(fp)!=0x00);//读到Description结尾
	size-=(ftell(fp)-start);//重计算图片实际大小
	//以下是Picture data
	const char * martist=env->GetStringUTFChars(artist,false);
	char * pathto=new char[17+strlen(martist)+4+1];//申请内存，用来放图片的存放位置
	memset(pathto,0x00,17+strlen(martist)+4+1);//strcat前，容器清空
	strcat(pathto,"/sdcard/qylk/pic/");//目录
	strcat(pathto,martist);//文件名
	strcat(pathto,".jpg");//后缀
	FILE *fpp=fopen(pathto,"wb");
	copy(fp,fpp,size);//拷贝Picture data
	fclose(fpp);//关文件，文件将会从内存写回磁盘
	delete[] pathto;
	env->ReleaseStringUTFChars(artist, martist);
	}
	else{
	  fclose(fp);
	  return JNI_FALSE;
	}
	fclose(fp);
	return JNI_TRUE;//返回成功标志
}