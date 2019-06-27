# Calculator_Android_App
A simple *(basic and scientific)* android calculator app written in Kotlin

<br>

## Portrait Layout -- Basic Calculator
![Simple Calculator Portrait Layout](./images/basic_calculator_layout.png)

<br>

## Landscape Layout -- Scientific Calculator
![Scientific Calculator Landscape Layout](./images/scientific_calculator_layout.png)

<br>

Important build information
* Windows 10
* Kotlin code for back-end
* Android Studio 3.4.1
* target SDK 24
* min/max SDK 21/29
* Packages added to build.gradle listed in the Wiki **(still in development)**
* layout designed checked on:
  * Nexus 5, xxhdpi
  * Nexus 5X, 420dpi
  * Pixel 2, 420dpi
  * Nexus 7, tvhdpi
* Used the following virtual devices in the Android Studio Emulator
Name | Resolution | API | Target
-----|------------|-----|-------
Nexus 5X | 1080 x 1920: 420dpi | 24 | Android 7.0
Nexus 5 | 1080 x 1920: xxhdpi | 21 | Android 5.0
Pixel 2 | 1080 x 1920: 420dpi | 24 | Android 7.0
Pixel 2 | 1080 x 1920: 420dpi | 29 | Android 9.+



Unique Features
* Uses EvalEx
  * Open source repo on GitHub for function/equation evaluations
  * Calculations based on text input.  Uses Reverse Polish Notation (RPN).  I just happened to find this while trolling through StackOverflow *(lifesaver!!)*
  * uses BigDecimal precision
  * I tried using exp4j, but all arithmetic uses double precision.  This led to cases of floating point rounding errors.
* Has two different layouts for portrait and landscape.  I had a hard time finding code examples on the internet that had both these features
* Attempting to include unit testing because it was very difficult to find good articles explaining how to do these.  **(still in development)**
