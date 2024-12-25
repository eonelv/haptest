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

void GameActivity_SetPrecision(JNIEnv* Env, int precision)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "SetPrecision", "(I)V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID, (jint)precision);
}

void GameActivity_SetCalibration(JNIEnv* Env)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "SetCalibration", "()V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID);
}

void GameActivity_shakeEngine(JNIEnv* Env, int motorIndex, int time)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "shakeEngine", "(II)V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID, (jint)motorIndex, (jint)time);
}

void GameActivity_disconnect(JNIEnv* Env)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "disconnect", "()V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID);
}

void GameActivity_stopScan(JNIEnv* Env)
{
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "stopScan", "()V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID);
}

void GameActivity_startScan(JNIEnv* Env)
{
	UE_LOG(LogTemp, Warning, TEXT("ready to call java method"));
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "startScan", "()V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID);
}

void GameActivity_connect(JNIEnv* Env, const FString& device)
{
	auto JavaID = FJavaHelper::ToJavaString(Env, device);
	jmethodID MethodID = FJavaWrapper::FindMethod(Env, FJavaWrapper::GameActivityClassID, "connect", "(Ljava/lang/String;)V", false);
	FJavaWrapper::CallVoidMethod(Env, FJavaWrapper::GameActivityThis, MethodID, *JavaID);
}

/////////////////////////////////////////////////////////////////////////////////////////////

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onDeviceStateChangedN(JNIEnv* Env, jobject LocalThisz, jstring address, jboolean state)
{
	FString message = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
	FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
		{
			HapAdapter::CallBackHandler->onDeviceStateChanged(message, (bool)state);
		}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_CalibrationCompleteResultN(JNIEnv* Env, jobject LocalThisz, jint modules)
{
	CheckReturn(HapAdapter::CallBackHandler)
	FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
		{
			HapAdapter::CallBackHandler->CalibrationCompleteResult((int32)modules);
		}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_PowerResultN(JNIEnv* Env, jobject LocalThisz, jint deviceMode, jint power)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->PowerResult((int32)deviceMode, (int32)power);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_SetArmorMoveResultN(JNIEnv* Env, jobject LocalThisz, jdouble frontOrBack, jdouble leftOrRight, jdouble around)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->SetArmorMoveResult((double)frontOrBack, (double)leftOrRight, (double)around);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_PushButtonClickN(JNIEnv* Env, jobject LocalThisz, jint buttonId, jboolean state)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->PushButtonClick((int32)buttonId, (bool)state);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_PushButtonStickN(JNIEnv* Env, jobject LocalThisz, jint buttonId)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->PushButtonStick((int32)buttonId);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_InfraredEntryN(JNIEnv* Env, jobject LocalThisz, jint buttonId, jboolean state)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->InfraredEntry((int32)buttonId, (bool)state);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_InfraredSustainedN(JNIEnv* Env, jobject LocalThisz, jint buttonId)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->InfraredSustained((int32)buttonId);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_SetLegMoveResultN(JNIEnv* Env, jobject LocalThisz, jint pos, jdouble frontOrBack, jdouble leftOrRight, jdouble around)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->SetLegMoveResult((int32)pos, (double)frontOrBack, (double)leftOrRight, (double)around);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onInitSuccessN(JNIEnv* Env, jobject LocalThisz)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onInitSuccess();
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onInitFailN(JNIEnv* Env, jobject LocalThisz, jint code)
{
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onInitFail((int32)code);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onScanResultN(JNIEnv* Env, jobject LocalThisz, jstring address, jstring name)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	FString fName = FJavaHelper::FStringFromLocalRef(Env, name);
	UE_LOG(LogTemp, Warning, TEXT("scan results:%s-%s"), *fAddress, *fName);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onScanResult(fAddress, fName);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onConnectionChangeN(JNIEnv* Env, jobject LocalThisz, jstring address, jint connectCode)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onConnectionChange(fAddress, (int32)connectCode);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onConnectFailedN(JNIEnv* Env, jobject LocalThisz, jstring address, jint connectCode)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onConnectFailed(fAddress, (int32)connectCode);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onConnectCancelN(JNIEnv* Env, jobject LocalThisz, jstring address)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onConnectCancel(fAddress);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onNotifySuccessN(JNIEnv* Env, jobject LocalThisz, jstring address)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onNotifySuccess(fAddress);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onWriteSuccessN(JNIEnv* Env, jobject LocalThisz, jstring address)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onWriteSuccess(fAddress);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}


JNI_METHOD void Java_com_epicgames_unreal_GameActivity_onWriteFailedN(JNIEnv* Env, jobject LocalThisz, jstring address, jint code)
{
	FString fAddress = FJavaHelper::FStringFromLocalRef(Env, address);
	CheckReturn(HapAdapter::CallBackHandler)
		FGraphEventRef task = FFunctionGraphTask::CreateAndDispatchWhenReady([=]()
			{
				HapAdapter::CallBackHandler->onWriteFailed(fAddress, (int32)code);
			}, TStatId(), nullptr, ENamedThreads::GameThread);
}

#endif

IHapCallbackHandler* HapAdapter::CallBackHandler = nullptr;
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

void HapAdapter::SetPrecision(int precision)
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_SetPrecision(Env, precision);
	}
#endif
}

void HapAdapter::SetCalibration()
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_SetCalibration(Env);
	}
#endif
}

void HapAdapter::shakeEngine(int motorIndex, int time)
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_shakeEngine(Env, motorIndex, time);
	}
#endif
}

void HapAdapter::disconnect()
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_disconnect(Env);
	}
#endif
}

void HapAdapter::connect(const FString& device)
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_connect(Env, device);
	}
#endif
}

void HapAdapter::stopScan()
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_stopScan(Env);
	}
#endif
}

void HapAdapter::startScan()
{
#if PLATFORM_ANDROID
	JNIEnv* Env = FAndroidApplication::GetJavaEnv();
	if (nullptr != Env)
	{
		GameActivity_startScan(Env);
	}
#endif
}
