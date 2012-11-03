#include <string.h>
#include <stdio.h>
#include "Utils.H"
#include "reWriteID3.h"
//************************************
//作者：清源林客2012 copyright
//语言：C++
//Blog：Http:\\www.qylk.blog.163.com
//Time：2012-10-19
//Ver： v1.0
//************************************

//************************************
// Method:    reCalculateSize
// FullName:  reCalculateSize
// Access:    public 
// Returns:   void
// Qualifier: 重写ID3标签大小
// Parameter: char head[] 10个字节
// Parameter: int id3size 标签大小
//************************************
void reCalculateID3Size(char head[],int id3size){
	head[6]=id3size/0x200000;
	id3size-=head[6]*0x200000;
	head[7]=id3size/0x4000;
	id3size-=head[7]*0x4000;
	head[8]=id3size/0x80;
	id3size-=head[8]*0x80;
	head[9]=id3size;
}


//************************************
// Method:    getHeaderSize
// FullName:  getHeaderSize
// Access:    public 
// Returns:   char *
// Qualifier: 重写帧大小
// Parameter: char header[] 10个字节
// Parameter: int size	帧大小
//************************************
void reCalculateHeaderSize(char header[],int size)
{								  
	 header[4]=size/0x1000000;
	 size-=header[4]*0x1000000;
	 header[5]=size/0x10000;
	 size-=header[5]*0x10000;
	 header[6]=size/0x100;
	 size-=header[6]*0x100;
	 header[7]=size;
}

//************************************
// Method:    writeHead
// FullName:  writeHead
// Access:    public 
// Returns:   void
// Qualifier: 写ID3标签头，10字节
// Parameter: FILE * fp
// Parameter: char header[] 要写的标签头，10字节
//************************************
void writeHead(FILE *fp,char header[]){
	fwrite(header,10,1,fp);
}

//************************************
// Method:    writeFrame
// FullName:  writeFrame
// Access:    public 
// Returns:   void
// Qualifier: 写帧
// Parameter: FILE * fp
// Parameter: char frameheader[]帧头
// Parameter: char * value 帧数据
//************************************
void writeFrame(FILE *fp,char frameheader[],char const *value){
	reCalculateHeaderSize(frameheader,strlen(value)+1);
	fwrite(frameheader,10,1,fp);
	fputc(0x03,fp);//使用UTF-8编码，编码表识0x03，某些软件不支持这种编码，比如Winamp,但Android支持就行了
	fwrite(value,strlen(value),1,fp);
}


//************************************
// Method:    writeID
// FullName:  writeID
// Access:    public 
// Returns:   void
// Qualifier: 写标签(或帧)标识
// Parameter: char head[] 标签头/帧头
// Parameter: const char * Id，比如"APIC"
//************************************
void writeID(char head[],const char *Id){
	int len=strlen(Id);
	for(int i=0;i<len;i++)
	  head[i]=Id[i];
}
//************************************
// Method:    writeID
// FullName:  writeID
// Access:    public 
// Returns:   void
// Qualifier: 写ID3版本号和标志
// Parameter: char head[]
// Parameter: const char * Id
//************************************
void writeID3Ver(char *head,char ver){
	head[3]=ver-'0';//版本
	head[4]=0x00;//无标识
	head[5]=0x00;
}
//************************************
// Method:    setFrameFlags
// FullName:  setFrameFlags
// Access:    public 
// Returns:   void
// Qualifier: 写帧的标志位Flag
// Parameter: char head[]
//************************************
void setFrameFlags(char head[]){
	head[8]=0x00;//Flag位为0
	head[9]=0x00;
}

void JNICALL Java_cn_qylk_app_APPUtils_SaveToID3v2(JNIEnv *env, jobject obj, jstring title,jstring artist,jstring album,jstring mp3path){				     
	const char *mp3=env->GetStringUTFChars(mp3path, false);//jstring转UTF，UTF的中文字符占3字节
	FILE * fp=fopen(mp3,"rb");//打开MP3文件
	if(fp==NULL) {env->ReleaseStringUTFChars(mp3path, mp3);return;}//打开失败返回，记得释放内存
	char head[10];//ID3标签头
	fread(head,sizeof(head),1,fp);//读标签头
	long id3size=getID3size(head);//读标签大小
	int start=10;//copy默认起始位置，为标签头最后
	if(!ArrayEqual(head,"ID3",3)) {//比较表示
		id3size=0;
		writeID(head,"ID3");//写标签标识
		writeID3Ver(head,'3');//写版本号和标志位
		start=0;//从头开始copy
	}
	const char * mtitle=env->GetStringUTFChars(title, false);//字符串转换
	const char * martist=env->GetStringUTFChars(artist, false);
	const char * malbum=env->GetStringUTFChars(album, false);
	char * tmp=new char[strlen(mp3)+4];//临时文件的位置，取mp3同目录
	memset(tmp,0x00,strlen(mp3)+4);//strcat前，必须清零
	strcat(tmp,mp3);
	strcat(tmp,"tmp");//避免同名，加个后缀'tmp'
	reCalculateID3Size(head,33+id3size+strlen(mtitle)+strlen(malbum)+strlen(martist));//重写标签大小,33为3个帧头的体积(10byte per)+编码标识位(1byte per)
	FILE * newfp=fopen(tmp,"wb");//新建临时文件
	writeHead(newfp,head);//写ID3标签头
	setFrameFlags(head);//重复利用head[],提前设置帧Flag位
	writeID(head,"TIT2");//写标题帧标识
	writeFrame(newfp,head,mtitle);//写帧
	writeID(head,"TPE1");//写作者帧标识
	writeFrame(newfp,head,martist);//写帧
	writeID(head,"TALB");//写专辑帧标识
	writeFrame(newfp,head,malbum);//写帧
	fseek(fp,start,SEEK_SET);//文件指针移到copy起始位置，准备copy
	copy(fp,newfp,filesize(fp)-start);//文件数据对拷
	fclose(fp);
	fclose(newfp);
	remove(mp3);//删除源文件
	rename(tmp,mp3);//重命名临时文件=>源文件
	LOGI("reBuild ID3...");
	//释放资源
	delete[] tmp;
	env->ReleaseStringUTFChars(mp3path, mp3);
	env->ReleaseStringUTFChars(title, mtitle);
	env->ReleaseStringUTFChars(artist, martist);
	env->ReleaseStringUTFChars(album, malbum);
}
