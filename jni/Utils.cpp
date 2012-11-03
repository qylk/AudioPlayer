#include "Utils.h"
//************************************
//作者：清源林客2012 copyright
//语言：C++
//Blog：Http:\\www.qylk.blog.163.com
//Time：2012-10-19
//Ver： v1.0
//************************************

//************************************
// Method:    ArrayEqual
// FullName:  ArrayEqual
// Access:    public 
// Returns:   int
// Qualifier: 数组比较
// Parameter: char *a1
// Parameter: char const *a2
// Parameter: int len 比较的长度
//************************************
bool ArrayEqual(char *a1,char const *a2,int len){
	int i; 
	for(i=0;i<len;i++) 
	   if(a1[i]!=a2[i]) 
	  	return 0;
	return  1;
}
//************************************
// Method:    getTagSize
// FullName:  getTagSize
// Access:    public 
// Returns:   int
// Qualifier: 获取ID3标签大小
// Parameter: char head[] 标签头
//************************************
int getID3size(char head[]){
	return (head[6]&0x7F)*0x200000 +(head[7]&0x7F)*0x4000 +(head[8]&0x7F)*0x80 +(head[9]&0x7F);
} 
//************************************
// Method:    getFramesize
// FullName:  getFramesize
// Access:    public 
// Returns:   int
// Qualifier: 获取帧大小
// Parameter: char size[]
//************************************
int getFramesize(char size[]){
	return size[0]*0x1000000+size[1]*0x10000+size[2]*0x100+size[3];
}
//************************************
// Method:    getInt
// FullName:  getInt
// Access:    public 
// Returns:   int
// Qualifier: char[4]转int
// Parameter: FILE *fp
//************************************
int getInt(FILE *fp){
	return (fgetc(fp)-'0')*1000+(fgetc(fp)-'0')*100+(fgetc(fp)-'0')*10+(fgetc(fp)-'0');
}
//************************************
// Method:    filesize
// FullName:  filesize
// Access:    public 
// Returns:   long
// Qualifier: 获取文件长度
// Parameter: FILE *stream
//************************************
long filesize(FILE *stream)
{
	long curpos = ftell(stream);
	fseek(stream, 0L, SEEK_END);//移到结尾
	long length = ftell(stream);
	fseek(stream, curpos, SEEK_SET);//恢复位置
	return length;
}

//************************************
// Method:    copy
// FullName:  copy
// Access:    public 
// Returns:   void
// Qualifier: 对拷数据块
// Parameter: FILE * from
// Parameter: FILE * to
// Parameter: int len 长度
//************************************
void copy(FILE *from,FILE* to,long len){
	static unsigned char buffer[4096];
	int factread;//记录实际每次读的大小
	long size=len;
	for (factread=0;size>0;size-=factread)
    {
        factread=fread(buffer,1,sizeof(buffer),from);
        fwrite(buffer,factread,1,to);
    }	
}