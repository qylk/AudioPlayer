#include <string.h>
#include <stdio.h>
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


const static BYTE m_wmaHeader[16] =
{
		0x30, 0x26, 0xB2, 0x75,
		0x8E, 0x66, 0xCF, 0x11,
		0xA6, 0xD9, 0x00, 0xAA,
		0x00, 0x62, 0xCE, 0x6C
};

const static BYTE m_stdFrameHeader[16] =
{
		0x33, 0x26, 0xB2, 0x75,
		0x8E, 0x66, 0xCF, 0x11,
		0xA6, 0xD9, 0x00, 0xAA,
		0x00, 0x62, 0xCE, 0x6C
};

const static BYTE m_exFrameHeader[16] =
{
		0x40, 0xA4, 0xD0, 0xD2,
		0x07, 0xE3, 0xD2, 0x11,
		0x97, 0xF0, 0x00, 0xA0,
		0xC9, 0x5E, 0xA8, 0x50
};

int WMA_calSize(const BYTE* buf)
{
    return buf[19] * 0x01000000+ buf[18] * 0x00010000+ buf[17] * 0x00000100+ buf[16];
}
bool readWMAAPIC(FILE *fp,const char *save){
		BYTE head[24];
		fread(head,1,sizeof(head),fp);
    if(!ArrayEqual(head, m_wmaHeader, 16)) return false;
    int frmsLen=WMA_calSize(head)-30;
    fseek(fp,6,SEEK_CUR);//跳过Unknown
    fread(head,1,sizeof(head),fp);
    if(!ArrayEqual(head,m_stdFrameHeader,16)) return false;
    int frmsNonExtLen=WMA_calSize(head)-sizeof(head);
    fseek(fp,frmsNonExtLen,SEEK_CUR);
    fread(head,1,sizeof(head),fp);
    if(!ArrayEqual(head,m_exFrameHeader,16)) return false;
    int frmslen=WMA_calSize(head);
    int FrmNum=fgetc(fp)+fgetc(fp)*0x100;
    BYTE fs[4];
    while(FrmNum-->0)
    {
        int namelen=fgetc(fp)+fgetc(fp)*0x100;
        BYTE *name=new BYTE[namelen];
        fread(name,1,namelen,fp);
        fread(fs,1,sizeof(fs),fp);
        //int flag=fs[0]+fs[1]*0x100;
        int sizev=fs[2]+fs[3]*0x100;
				if(ArrayEqual(name,(const BYTE*)L"WM/Picture",namelen)==false){
					delete [] name;
					fseek(fp,sizev,SEEK_CUR);//文件移到下一帧
					continue;
					}
					delete [] name;
				fgetc(fp);                     //Picture type位
    		size_t datalen=(BYTE)fgetc(fp)+(BYTE)fgetc(fp)*0x100;
    		int i=3;
    		unsigned short dw[1];
    	while(i-->0)
    		{
        fread(dw,2,1,fp);
        while(dw[0]!=0)
        {
            fread(dw,2,1,fp);
        }
    	}
    	FILE *fpp=fopen(save,"wb");
    	copy(fp,fpp,(long)datalen);//复制原始数据
    	fclose(fpp);
    	return true;
    }
    return false;
}


bool readMP3APIC(FILE *fp,const char *save){
	BYTE head[10];//id3标签头
	fread(head,sizeof(head),1,fp);//读标签头
 	if(!ArrayEqual(head,(const BYTE*)"ID3",3)) {//对比标识
 		fclose(fp);
 		return false;
	}	
 	int id3v2size=getID3size(head);//标签大小
 	BYTE frame[4];//帧标识
 	int i=10;//帧起始位置
 	int size;//记录帧大小
 	while(i<id3v2size){
		fread(frame,4,1,fp);//读帧标识
        	if(ArrayEqual(frame,(const BYTE*)"APIC",4))//对比"APIC"
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
	BYTE coding=fgetc(fp);//Text encoding位
	while(fgetc(fp)!=0x00);//读到MIME type字符串结尾
	fgetc(fp);//Picture type位
	if(coding==0x01||coding ==0x02)//UTF-16 coding
	  {	unsigned short t[1];
	  	fread(t,2,1,fp);
	    while(t[0]!=0)
	     	fread(t,2,1,fp);
	  }
	else
	     while(fgetc(fp)!=0x00);//读到Description结尾
	size-=(ftell(fp)-start);//重计算图片实际大小
	//以下是Picture data
	FILE *fpp=fopen(save,"wb");
	copy(fp,fpp,size);//拷贝Picture data
	fclose(fpp);//关文件，文件将会从内存写回磁盘
	}
	else{
	  fclose(fp);
	  return false;
	}
	fclose(fp);
	return true;//返回成功标志
}

JNIEXPORT jboolean JNICALL Java_cn_qylk_media_ArtistInfo_ApicFromTag(JNIEnv *env, jobject obj, jstring artist, jstring mp3path)//读取专辑图片，给定参数artist和歌曲文件完整路径
{		const char * mp3=env->GetStringUTFChars(mp3path, false);//将java中的字符串转为C语言中的字符串(UTF)
		const char * martist=env->GetStringUTFChars(artist,false);
		char * pathto=new char[17+strlen(martist)+4+1];//申请内存，用来放图片的存放位置
		memset(pathto,0x00,17+strlen(martist)+4+1);//strcat前，容器清空
		strcat(pathto,"/sdcard/qylk/pic/");//目录
		strcat(pathto,martist);//文件名
		strcat(pathto,".jpg");//后缀
		jboolean suc;
			FILE * fp=fopen(mp3,"rb");//只读打开文件 
	if(fp==NULL) return false;
		if(checkExt(mp3)==MP3){
			suc= readMP3APIC(fp,pathto);
		}else if(checkExt(mp3)==WMA)
		{
			suc= readWMAAPIC(fp,pathto);
			}
	fclose(fp);
	delete[] pathto;
	env->ReleaseStringUTFChars(mp3path, mp3);
	env->ReleaseStringUTFChars(artist, martist);
return suc;
}



