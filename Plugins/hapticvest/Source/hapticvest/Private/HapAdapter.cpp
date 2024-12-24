#include "HapAdapter.h"

#if PLATFORM_ANDROID
//所有示例都可以在AndroidJNI.h AndroidJNI.cpp AndroidJavaEnv.cpp中看到用法
#include "Android/AndroidApplication.h"
#include "Android/AndroidJNI.h"
#include "Android/AndroidJavaEnv.h"
#include "Android/AndroidJava.h"


void GameActivity_InitSdk(JNIEnv* Env)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "initBle", "()V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID);
}
#endif

void HapAdapter::Init()
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_InitSdk(Env);
	}
#endif
}
