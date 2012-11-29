#include <sys/types.h>      
#include <sys/socket.h>      
#include <netinet/in.h>      
#include <netdb.h>      
#include <stdio.h> 
#include <string.h> 
#include <unistd.h>
#include <android/log.h>
#include "feedback.h"
#define LOG_TAG "SMTP"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)//调试日志输出          
#define EHLO "EHLO 139.com\r\n"     
#define DATA "data\r\n"      
#define QUIT "QUIT\r\n"      
int sock;
struct hostent *hp, *gethostbyname();       
static char buf[BUFSIZ+1];
//发送command      
void send_socket(const char *s)     
{         
	write(sock,s,strlen(s));
}
//读取状态
int read_socket()
{
	read(sock,buf,BUFSIZ);
	return (buf[0]-'0')*100+(buf[1]-'0')*10+buf[2]-'0';
}

//单个字符解密
char CutcodeChar(const char c,const char key){
    return c^key;
}

//解密
char* Cutecode(char *pstr,const char *pkey){
    int len=strlen(pstr);
    for(int i=0;i<len;i++)
        *(pstr+i)=CutcodeChar(*(pstr+i),pkey[i%8]);
    return pstr;
}

//UNICODE转GB2312
char* jstringTostring(JNIEnv* env, jstring jstr)
{
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("gb2312");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
	rtn = (char*)malloc(alen + 1);
	memcpy(rtn, ba, alen);
	rtn[alen] = 0;
}
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

/*=目前大多数SMTP服务是需要验证身份的ESMTP，不再是以前的垃圾邮件到处飞的无需验证的SMTP了=====*/
JNIEXPORT jboolean JNICALL Java_cn_qylk_FeedBackUI_FeedBack(JNIEnv *env, jclass obj, jstring content)
{
const char *host_id="smtp.139.com";//ESMTP server
const char *from_id="xiaofang99@139.com";//sender email address   
const char *to_id=from_id;//receiver email address
const char *sub="QPlayer的反馈意见";
char cc[]={0x3f,0x3d,0x21,0x11,0x4f,0x6a,0x40,0x7b,0x3e,0x2e,0x2a,0x02,0x5b,0x77,0x3c,0x3f,0x00};//根据我的密钥加密算出的读不懂的密码
const char key[]={};//8位密钥，自己填   

struct sockaddr_in server;
/*=====Create Socket=====*/
sock = socket(AF_INET, SOCK_STREAM, 0);
if (sock==-1)     
{
	LOGI("open stream socket error");
	return JNI_FALSE;   
}     
else  LOGI("socket created");
/*=====Verify host=====*/
server.sin_family = AF_INET;
hp = gethostbyname(host_id);
/*=====Connect to port 25 on remote host=====*/
if (hp==(struct hostent *) 0)
{
	LOGI("unknown host");
	return JNI_FALSE;
}
memcpy((char *) &server.sin_addr, (char *) hp->h_addr, hp->h_length);     	 				
server.sin_port=htons(25); /* SMTP PORT */
if (connect(sock, (struct sockaddr *) &server, sizeof server)==-1)
{
	LOGI("connect stream socket error");
	return JNI_FALSE;
}     
else    LOGI("Connected");
/*=====Write some data then read some =====*/
int code;//return code
code=read_socket(); /* SMTP Server logon string */
if(code!=220) return JNI_FALSE;
send_socket(EHLO); /* introduce ourselves */
code=read_socket(); /*Read reply */
if(code!=250) return JNI_FALSE;
send_socket("AUTH LOGIN \r\n");
code=read_socket();
if(code!=334) return JNI_FALSE;
send_socket("MTU4OTE3NTE3NjlAMTM5LmNvbQ==");//Base64 -username
send_socket("\r\n");
code=read_socket();
if(code!=334) return JNI_FALSE;
send_socket(Cutecode(cc,key));//Base64 -password,为了安全，这里使用密钥产生密码
send_socket("\r\n");
code=read_socket();
if(code!=235) return JNI_FALSE;
send_socket("mail from <");
send_socket(from_id);
send_socket(">");
send_socket("\r\n");
code=read_socket(); /* Sender OK */
if(code!=250) return JNI_FALSE;
send_socket("rcpt to <"); /*Mail to*/
send_socket(to_id);
send_socket(">");
send_socket("\r\n");
read_socket(); // Recipient OK*/
	
send_socket(DATA);// body to follow*/
read_socket(); 
send_socket("subject:");
LOGI("start to send");
send_socket(sub);
send_socket("\r\n\r\n\r\n");
char* data=jstringTostring(env,content);
send_socket(data);
send_socket("\r\n.\r\n");
read_socket();
send_socket(QUIT); /* quit */
read_socket(); // log off */
//=====Close socket and finish=====*/
close(sock);
free(data);
return JNI_TRUE;
} 