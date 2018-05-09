# FRC Team 6836 Robot code: Wookie #

This repository contains robot code for "Wookie Wobot", the 2018 robot for Team 6836 The Tinkerers.

## Setup ##

To edit and deploy this code, you need to install Eclipse according to the [2018 WPIlib instructions](https://wpilib.screenstepslive.com/s/currentCS/m/java/l/599681-installing-eclipse-c-java)

The code is based on the IterativeRobot template, and follows this structure:

RobotInit: All variables and systems are defined and initialised
AutonomousInit: Autonomous mode is initialised
AutonomousPeriodic: Autonomous code is run periodically
TeleopPeriodic: Every few milliseconds this function is called, and decisions on what the robot does are made

## Autonomous Mode

To be figured out...

The driver station can provide a signal explaining which side of the switch to drop a cube on, [find the instructions and example code here](https://wpilib.screenstepslive.com/s/currentCS/m/getting_started/l/826278-2018-game-data-details)

## Software design ##

### I/O

| Inputs                                                  | Outputs        |
| ------------------------------------------------------- |:--------------:|
| Driver joysticks (3 axes, 12 buttons, 1 slider each)    | Driving        |
| Operator gamepad (6 axes, 12 buttons)                   | Lift           |
| Microswitches on robot                                  | Intake         |
| SmartDashboard                                          | LEDs           |
|                                                         | SmartDashboard |

### Controls
* Drive: 2 Axes on the driver joystick
..* Switch between Tank and Arcade in the SmartDashboard
..* Driver can use two joysticks, one joystick or a gamepad
* Intake: 2 Buttons on the operator gamepad
..* One button for in, one button for out
..* A robot microswitch tells when the cube is in the intake so we know to stop intaking
..* The switch can indicate on the LEDs and on the SmartDashboard
* Lift: Y axis on the operator gamepad for controlled speed
..* 2 microswitches on the robot so we can tell when it's all the way up and all the way down
..* Again, LEDs and SDs can provide an indicator of where the lift is
* LED's: Default colour is alliance colour as chosen by the SmartDashboard (or provided by the driver station)

### Extras (modifiers and semi-autonomous)

* Sliders on the joystick/s control top speed
* Buttons on the joystick/s toggle speed mode (e.g. trigger for halfspeed, top buttons for toggle to halfspeed mode)
* "Twitchy intake" button: While intaking, every now and then do a brief output to help shuffle the cube in
* Binary lift buttons - one button that says "put the lift all the way up", one that says "put the lift all the way down"
* Switch override toggle - a button which turns off "wait for switch" things, in case the switches break or fall off.


# Joystick Mappings
![Flight stick](http://team358.org/files/programming/ControlSystem2009-/Extreme_3D_Pro.png)

![Logitech F310](http://team358.org/files/programming/ControlSystem2009-/Logitech-F310_ControlMapping.png)