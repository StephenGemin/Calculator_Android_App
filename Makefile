.PHONY: build install launch run test lint clean help

PKG = com.example.calculator_v01
ACTIVITY = .MainActivity
DEVICE_FLAG = $(if $(DEVICE),-s $(DEVICE))

help:
	@echo "Calculator Android App - Development Tasks"
	@echo ""
	@echo "  make build                Build debug APK"
	@echo "  make install              Install APK to device/emulator"
	@echo "  make install DEVICE=<id>  Install to specific device"
	@echo "  make launch               Launch the app"
	@echo "  make launch DEVICE=<id>   Launch on specific device"
	@echo "  make run                  Build, install, and launch"
	@echo "  make run DEVICE=<id>      Build, install, and launch on specific device"
	@echo "  make test                 Run unit tests"
	@echo "  make lint                 Run lint checks"
	@echo "  make clean                Clean build files"
	@echo ""
	@echo "  adb devices               List connected devices and their serials"

build:
	./gradlew assembleDebug

install: build
ifdef DEVICE
	adb -s $(DEVICE) install -r app/build/outputs/apk/debug/app-debug.apk
else
	./gradlew installDebug
endif

launch:
	adb $(DEVICE_FLAG) shell am start -n $(PKG)/$(PKG)$(ACTIVITY)

run: install launch

test:
	./gradlew testDebugUnitTest

lint:
	./gradlew lint

clean:
	./gradlew clean
