#pragma once

class IHapCallbackHandler
{
public:
	virtual ~IHapCallbackHandler() {};
public:
   virtual void onDeviceStateChanged(FString address, bool state) =  0;

   virtual void CalibrationCompleteResult(int modules) = 0;

   virtual void PowerResult(int deviceMode, int power) = 0;

   virtual void SetArmorMoveResult(double frontOrBack, double leftOrRight, double around) = 0;

   virtual void PushButtonClick(int buttonId, bool state) = 0;

   virtual void PushButtonStick(int buttonId) = 0;

   virtual void InfraredEntry(int buttonId, bool state) = 0;

   virtual void InfraredSustained(int buttonId) = 0;

   virtual void SetLegMoveResult(int pos, double frontOrBack, double leftOrRight, double around) = 0;

   virtual void onInitSuccess() = 0;

   virtual void onInitFail(int code) = 0;

   virtual void onScanResult(FString address, FString name) = 0;

   virtual void onConnectionChange(FString address, int connectCode) = 0;

   virtual void onConnectFailed(FString address, int errorCode) = 0;

   virtual void onConnectCancel(FString address) = 0;

   virtual void onNotifySuccess(FString address) = 0;

   virtual void onWriteSuccess(FString address) = 0;

   virtual void onWriteFailed(FString address, int code) = 0;
};


#define CheckReturn(X)  if (nullptr == X) {\
	return;	\
}

class HAPTICVEST_API HapAdapter
{
public:
	static void Init();
	static void SetPrecision(int precision);

	static void SetCalibration();

	static void  shakeEngine(int motorIndex, int time);

	static void disconnect();

	static void connect(const FString& device);

	static void stopScan();

	static void startScan();
	static IHapCallbackHandler* CallBackHandler;
};
