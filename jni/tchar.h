#ifdef _UNICODE
#define _T L##
#define _tprintf wprintf
#else
#define _T
#define _tprintf printf
#endif  //UNICODE