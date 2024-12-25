// Fill out your copyright notice in the Description page of Project Settings.


#include "HapLibrary.h"

#include "HapAdapter.h"

FOnDeviceStateChanged UHapLibrary::OnDeviceStateChanged;
FOnInt UHapLibrary::CalibrationCompleteResult;
FOnIntInt UHapLibrary::PowerResult;
FOnArmorMoveResult UHapLibrary::OnArmorMoveResult;
FOnIntBool UHapLibrary::PushButtonClick;
FOnInt UHapLibrary::PushButtonStick;
FOnIntBool UHapLibrary::InfraredEntry;
FOnInt UHapLibrary::InfraredSustained;
FOnLegMoveResult UHapLibrary::OnLegMoveResult;
FOnSuccess UHapLibrary::onInitSuccess;

FOnInt UHapLibrary::onInitFail;
FOnScanResult UHapLibrary::onScanResult;
FOnStringInt UHapLibrary::onConnectionChange;
FOnStringInt UHapLibrary::onConnectFailed;
FOnDevice UHapLibrary::onConnectCancel;

FOnDevice UHapLibrary::onNotifySuccess;
FOnDevice UHapLibrary::onWriteSuccess;
FOnStringInt UHapLibrary::onWriteFailed;

void UHapLibrary::Init()
{
	HapAdapter::Init();
	HapAdapter::CallBackHandler = HapCallbackHandler::Get();
}

void UHapLibrary::SetPrecision(int precision)
{
	HapAdapter::SetPrecision(precision);
}

void UHapLibrary::SetCalibration()
{
	HapAdapter::SetCalibration();
}

void UHapLibrary::shakeEngine(int motorIndex, int time)
{
	HapAdapter::shakeEngine(motorIndex, time);
}

void UHapLibrary::disconnect()
{
	HapAdapter::disconnect();
}

void UHapLibrary::connect(const FString& device)
{
	HapAdapter::connect(device);
}

void UHapLibrary::stopScan()
{
	HapAdapter::stopScan();
}

void UHapLibrary::startScan()
{
	UE_LOG(LogTemp, Warning, TEXT("blueprint begin scan device"));
	HapAdapter::startScan();
}

void UHapLibrary::SetDeviceStateChanged(FOnDeviceStateChanged device)
{
	UHapLibrary::OnDeviceStateChanged = MoveTemp(device);
}

void UHapLibrary::SetCalibrationCompleteResult(FOnInt pCalibrationCompleteResult)
{
	UHapLibrary::CalibrationCompleteResult = MoveTemp(pCalibrationCompleteResult);
}

void UHapLibrary::SetPowerResult(FOnIntInt pPowerResult)
{
	UHapLibrary::PowerResult = MoveTemp(pPowerResult);
}

void UHapLibrary::SetArmorMoveResult(FOnArmorMoveResult ArmorMoveResult)
{
	UHapLibrary::OnArmorMoveResult = MoveTemp(ArmorMoveResult);
}

void UHapLibrary::SetPushButtonClick(FOnIntBool Value)
{
	UHapLibrary::PushButtonClick = MoveTemp(Value);
}

void UHapLibrary::SetPushButtonStick(FOnInt Value)
{
	UHapLibrary::PushButtonStick = MoveTemp(Value);
}

void UHapLibrary::SetInfraredEntry(FOnIntBool Value)
{
	UHapLibrary::InfraredEntry = MoveTemp(Value);
}

void UHapLibrary::SetInfraredSustained(FOnInt Value)
{
	UHapLibrary::InfraredSustained = MoveTemp(Value);
}

void UHapLibrary::SetLegMoveResult(FOnLegMoveResult Value)
{
	UHapLibrary::OnLegMoveResult = MoveTemp(Value);
}

void UHapLibrary::SetInitSuccess(FOnSuccess Value)
{
	UHapLibrary::onInitSuccess = MoveTemp(Value);
}

void UHapLibrary::SetInitFail(FOnInt Value)
{
	UHapLibrary::onInitFail = MoveTemp(Value);
}

void UHapLibrary::SetScanResult(FOnScanResult Value)
{
	UHapLibrary::onScanResult = MoveTemp(Value);
}

void UHapLibrary::SetConnectionChange(FOnStringInt Value)
{
	UHapLibrary::onConnectionChange = MoveTemp(Value);
}

void UHapLibrary::SetConnectFailed(FOnStringInt Value)
{
	UHapLibrary::onConnectFailed = MoveTemp(Value);
}

void UHapLibrary::SetConnectCancel(FOnDevice Value)
{
	UHapLibrary::onConnectCancel = MoveTemp(Value);
}

void UHapLibrary::SetNotifySuccess(FOnDevice Value)
{
	UHapLibrary::onNotifySuccess = MoveTemp(Value);
}

void UHapLibrary::SetWriteSuccess(FOnDevice Value)
{
	UHapLibrary::onWriteSuccess = MoveTemp(Value);
}

void UHapLibrary::SetWriteFailed(FOnStringInt Value)
{
	UHapLibrary::onWriteFailed = MoveTemp(Value);
}

IHapCallbackHandler* HapCallbackHandler::ins = nullptr;

IHapCallbackHandler* HapCallbackHandler::Get()
{
	if (nullptr == ins)
	{
		ins = new HapCallbackHandler();
	}
	return ins;
}

HapCallbackHandler::HapCallbackHandler()
{
}

void HapCallbackHandler::onDeviceStateChanged(FString address, bool state)
{
	UHapLibrary::OnDeviceStateChanged.ExecuteIfBound(address, state);
}

void HapCallbackHandler::CalibrationCompleteResult(int modules)
{
	UHapLibrary::CalibrationCompleteResult.ExecuteIfBound(modules);
}

void HapCallbackHandler::PowerResult(int deviceMode, int power)
{
	UHapLibrary::PowerResult.ExecuteIfBound(deviceMode, power);
}

void HapCallbackHandler::SetArmorMoveResult(double frontOrBack, double leftOrRight, double around)
{
	UHapLibrary::OnArmorMoveResult.ExecuteIfBound(frontOrBack, leftOrRight, around);
}

void HapCallbackHandler::PushButtonClick(int buttonId, bool state)
{
	UHapLibrary::PushButtonClick.ExecuteIfBound(buttonId, state);
}

void HapCallbackHandler::PushButtonStick(int buttonId)
{
	UHapLibrary::PushButtonStick.ExecuteIfBound(buttonId);
}

void HapCallbackHandler::InfraredEntry(int buttonId, bool state)
{
	UHapLibrary::InfraredEntry.ExecuteIfBound(buttonId, state);
}

void HapCallbackHandler::InfraredSustained(int buttonId)
{
	UHapLibrary::InfraredSustained.ExecuteIfBound(buttonId);
}

void HapCallbackHandler::SetLegMoveResult(int pos, double frontOrBack, double leftOrRight, double around)
{
	UHapLibrary::OnLegMoveResult.ExecuteIfBound(pos, frontOrBack, leftOrRight, around);
}

void HapCallbackHandler::onInitSuccess()
{
	UHapLibrary::onInitSuccess.ExecuteIfBound();
}

void HapCallbackHandler::onInitFail(int code)
{
	UHapLibrary::onInitFail.ExecuteIfBound(code);
}

void HapCallbackHandler::onScanResult(FString address, FString name)
{
	UHapLibrary::onScanResult.ExecuteIfBound(address, name);
}

void HapCallbackHandler::onConnectionChange(FString address, int connectCode)
{
	UHapLibrary::onConnectionChange.ExecuteIfBound(address, connectCode);
}

void HapCallbackHandler::onConnectFailed(FString address, int errorCode)
{
	UHapLibrary::onConnectFailed.ExecuteIfBound(address, errorCode);
}

void HapCallbackHandler::onConnectCancel(FString address)
{
	UHapLibrary::onConnectCancel.ExecuteIfBound(address);
}

void HapCallbackHandler::onNotifySuccess(FString address)
{
	UHapLibrary::onNotifySuccess.ExecuteIfBound(address);
}

void HapCallbackHandler::onWriteSuccess(FString address)
{
	UHapLibrary::onWriteSuccess.ExecuteIfBound(address);
}

void HapCallbackHandler::onWriteFailed(FString address, int code)
{
	UHapLibrary::onWriteFailed.ExecuteIfBound(address, code);
}
