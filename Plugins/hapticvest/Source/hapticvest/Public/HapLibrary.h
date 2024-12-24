// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "Kismet/BlueprintFunctionLibrary.h"
#include "HapAdapter.h"
#include "HapLibrary.generated.h"

DECLARE_DYNAMIC_DELEGATE(FOnSuccess);
DECLARE_DYNAMIC_DELEGATE_OneParam(FOnDevice, const FString&, address);
DECLARE_DYNAMIC_DELEGATE_OneParam(FOnInt, int, code);
DECLARE_DYNAMIC_DELEGATE_TwoParams(FOnScanResult, const FString&, address, const FString&, name);
DECLARE_DYNAMIC_DELEGATE_TwoParams(FOnDeviceStateChanged, const FString&, address, bool, state);
DECLARE_DYNAMIC_DELEGATE_TwoParams(FOnIntInt, int, deviceMode, int, power);
DECLARE_DYNAMIC_DELEGATE_TwoParams(FOnStringInt, const FString&, address, int, power);
DECLARE_DYNAMIC_DELEGATE_ThreeParams(FOnArmorMoveResult, double, deviceMode, double, power, double, around);
DECLARE_DYNAMIC_DELEGATE_TwoParams(FOnIntBool, int, buttonId, bool, state);
DECLARE_DYNAMIC_DELEGATE_FourParams(FOnLegMoveResult, int, pos, double, deviceMode1, double, power1, double, around1);

class HapCallbackHandler : public IHapCallbackHandler
{
public:
	static IHapCallbackHandler* Get();
	HapCallbackHandler();
	~HapCallbackHandler() {}
private:
	virtual void onDeviceStateChanged(FString address, bool state) override;

	virtual void CalibrationCompleteResult(int modules) override;

	virtual void PowerResult(int deviceMode, int power) override;

	virtual void SetArmorMoveResult(double frontOrBack, double leftOrRight, double around) override;

	virtual void PushButtonClick(int buttonId, bool state) override;

	virtual void PushButtonStick(int buttonId) override;

	virtual void InfraredEntry(int buttonId, bool state) override;

	virtual void InfraredSustained(int buttonId) override;

	virtual void SetLegMoveResult(int pos, double frontOrBack, double leftOrRight, double around) override;

	virtual void onInitSuccess() override;

	virtual void onInitFail(int code) override;

	virtual void onScanResult(FString address, FString name) override;

	virtual void onConnectionChange(FString address, int connectCode) override;

	virtual void onConnectFailed(FString address, int errorCode) override;

	virtual void onConnectCancel(FString address) override;

	virtual void onNotifySuccess(FString address) override;

	virtual void onWriteSuccess(FString address) override;

	virtual void onWriteFailed(FString address, int code) override;

private:
	static IHapCallbackHandler* ins;
};

/**
 * 
 */
UCLASS()
class HAPTICVEST_API UHapLibrary : public UBlueprintFunctionLibrary
{
	GENERATED_BODY()
public:
	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void Init();

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetPrecision(int precision);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetCalibration();

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void  shakeEngine(int motorIndex, int time);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void disconnect();

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void connect(const FString& device);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void stopScan();

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void startScan();

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetDeviceStateChanged(FOnDeviceStateChanged device);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetCalibrationCompleteResult(FOnInt pCalibrationCompleteResult);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetPowerResult(FOnIntInt pPowerResult);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetArmorMoveResult(FOnArmorMoveResult ArmorMoveResult);

	/// <summary>
	/// 
	/// </summary>
	/// <param name="ArmorMoveResult"></param>
	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetPushButtonClick(FOnIntBool Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetPushButtonStick(FOnInt Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetInfraredEntry(FOnIntBool Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetInfraredSustained(FOnInt Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetLegMoveResult(FOnLegMoveResult Value);

	//
	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetInitSuccess(FOnSuccess Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetInitFail(FOnInt Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetScanResult(FOnScanResult Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetConnectionChange(FOnStringInt Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetConnectFailed(FOnStringInt Value);

	//
	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetConnectCancel(FOnDevice Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetNotifySuccess(FOnDevice Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetWriteSuccess(FOnDevice Value);

	UFUNCTION(BlueprintCallable, Category = "Hap")
	static void SetWriteFailed(FOnStringInt Value);

	static FOnDeviceStateChanged OnDeviceStateChanged;
	static FOnInt CalibrationCompleteResult;
	static FOnIntInt PowerResult;
	static FOnArmorMoveResult OnArmorMoveResult;

	static FOnIntBool PushButtonClick;
	static FOnInt PushButtonStick;
	static FOnIntBool InfraredEntry;
	static FOnInt InfraredSustained;
	static FOnLegMoveResult  OnLegMoveResult;

	static FOnSuccess  onInitSuccess;
	static FOnInt onInitFail;
	static FOnScanResult  onScanResult;
	static FOnStringInt onConnectionChange;
	static FOnStringInt onConnectFailed;

	static FOnDevice onConnectCancel;
	static FOnDevice onNotifySuccess;
	static FOnDevice onWriteSuccess;
	static FOnStringInt onWriteFailed;
};
