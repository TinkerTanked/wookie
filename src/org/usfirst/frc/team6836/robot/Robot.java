/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6836.robot;
import java.io.Console;

import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GamepadBase;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
//lift=0, frontleft = 1, rear left=2 ,frontright = 3, rearight = 4, right=5 left=6

public class Robot extends IterativeRobot {
	private static final String kLeftAuto = "Left";
	private static final String kRightAuto = "Right";
	private static final String kBasicAuto = "Basic";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	//drive
	private SpeedController frontLeft, rearLeft, frontRight,rearRight ;
	//lift
	private SpeedController lift;
	//intake
	private SpeedController intakeRight, intakeLeft;
	//robotdrive
	private DifferentialDrive robot;
	private Joystick driver;
	private Joystick operator;
	SpeedControllerGroup intakeGroup;
	//leds
//	private PWM red,green,blue;
	//limit switches
	private DigitalInput topSwitch,bottomSwitch;
	private Timer autoT ;


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Left", kLeftAuto);
		m_chooser.addObject("Right", kRightAuto);
		m_chooser.addObject("Basic", kBasicAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		// //drive
		
		frontLeft = new Spark(1);
		frontLeft.setInverted(true);
		rearLeft = new Spark(2);
		rearLeft.setInverted(true);
		frontRight = new Spark(3);
		frontRight.setInverted(true);
		rearRight = new Spark(4);
		rearRight.setInverted(true);
		//intake/lift
		intakeRight = new VictorSP(5);

		intakeLeft = new VictorSP(6);
		intakeLeft.setInverted(false);
		lift = new VictorSP(0);
		lift.setInverted(true);
		topSwitch = new DigitalInput(0);
		bottomSwitch = new DigitalInput(1);
		intakeGroup = new SpeedControllerGroup(intakeLeft, intakeRight);

		CameraServer.getInstance().startAutomaticCapture();

		CvSink cvSink = CameraServer.getInstance().getVideo();

		CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
		//robot
		robot = new DifferentialDrive(new SpeedControllerGroup(frontLeft, rearLeft), new SpeedControllerGroup(frontRight, rearRight));
		//leds
		// red = new PWM(7);
		// green = new PWM(8);
		// blue = new PWM(9);
		// Joystick
		driver = new Joystick(0);
		operator = new Joystick(1);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous mbbbodes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		autoT = new Timer();
		autoT.start();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		if (m_autoSelected == kBasicAuto) {
			basicAuto();
		}
		int dir = 0;
		if (m_autoSelected == kRightAuto) {
			dir = 1;
		} else {
			dir = -1;
		}
		// Left/Right auto
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		if(gameData.length() > 0)
		{
			if(gameData.charAt(0) =='L'){
				//Put left
				if (m_autoSelected == kLeftAuto) {
					System.out.println("L Shaped auto Left (" + dir + ")");
					lShapedPeriodic(dir);
				} else if (m_autoSelected == kRightAuto) {
					System.out.println("S Shaped auto Left (" + dir + ")");	
					SShapedPeriodic(dir);
				}
			}else {
				//put right 
				if (m_autoSelected == kLeftAuto) {
					System.out.println("S Shaped auto Right (" + dir + ")");
					SShapedPeriodic(dir);
				} else if (m_autoSelected == kRightAuto) {
					System.out.println("L Shaped auto Right (" + dir + ")");					
					lShapedPeriodic(dir);
				}
			}
		}
	}
	
	
	
	private void lShapedPeriodic(int dir) {
		double x1 = 5.0; // Drive forward time
		double x2 = 0.8; // Turn time
		double x3 = 1; // Drive forward time
		double x4 = 5; // outtake time
		
		double spd = 0.7;
		// Drive forward until x1 seconds
		if (autoT.get() < x1) {
			System.out.println("Step 1");

			// Drive forward
			robot.arcadeDrive(-spd, 0);
		} else if (autoT.get() < x1+x2) {
			System.out.println("Step 2");
			// Turn *dir*
			robot.arcadeDrive(0, spd*dir);
		} else if (autoT.get() < x1+x2+x3) {
			robot.arcadeDrive(-spd, 0);			
		}else if (autoT.get()< x1+x2+x3+x4) {			
			robot.arcadeDrive(0, 0);
			intakeGroup.set(1);
		}else {
			robot.arcadeDrive(0, 0);
			intakeGroup.set(0);
		}
		// Turn *dir* until x1+x2 seconds
		
		// Drive forward until x1+x2+x3 seconds
		
		// Outtake until x1+x2+x3+x3+x4 secnods

	}
	private void basicAuto() {
		double x1 = 3.0;
		double spd = 0.7;
		// Drive forward until x1 seconds
		if (autoT.get() < x1) {
			System.out.println("Step 1");

			// Drive forward
			robot.arcadeDrive(-spd, 0);
		} else if (autoT.get() < x1*2) {
			robot.arcadeDrive(spd, 0);
		} else {
			robot.arcadeDrive(0, 0);
			intakeGroup.set(0);
		}
		// Turn *dir* until x1+x2 seconds
		
		// Drive forward until x1+x2+x3 seconds
		
		// Outtake until x1+x2+x3+x3+x4 secnods

	}
	private void SShapedPeriodic(int dir) {
		double x1 = 1.2; // turn
		double x2 = 5.5; // forward
		double x3 = x1; //turn
		double x4 = 2.0; //forward
		double x5 = 5; //outtake
		double spd = 0.7;
		// Drive forward until x1 seconds
		if (autoT.get() < x1) {
			robot.arcadeDrive(-spd, spd*dir);
		} else if (autoT.get() < x1+x2) {
			robot.arcadeDrive(-spd, 0);
		} else if (autoT.get() < x1+x2+x3) {
			robot.arcadeDrive(-spd, -spd*dir);
		}else if (autoT.get()< x1+x2+x3+x4) {			
			robot.arcadeDrive(-spd, 0);
		}else if (autoT.get()< x1+x2+x3+x4+x5) {			
			robot.arcadeDrive(0, 0);
			intakeGroup.set(1);
		}else {
			robot.arcadeDrive(0, 0);
			intakeGroup.set(0);
		}
		// Turn *dir* until x1+x2 seconds
		
		// Drive forward until x1+x2+x3 seconds
		
		// Outtake until x1+x2+x3+x3+x4 secnods

	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {


		intakeGroup.set(-operator.getRawAxis(5));
		
		double speedSlow = 0.8;
		double speedFast = 1.0;
		
		SmartDashboard.putString("Gamepad Operator", "Lift Joystick "+operator.getRawAxis(1));
		double dSpd = 0;
		double tSpd = 0;
		if(driver.getRawButton(6)) {
			dSpd = driver.getRawAxis(1)*speedFast;
		} else {
			dSpd = driver.getRawAxis(1)*speedSlow;
		}
		if (driver.getRawButton(5)) {
			tSpd = -driver.getRawAxis(4)*speedSlow;
		} else {
			tSpd = -driver.getRawAxis(4)*speedFast;
		}
		robot.arcadeDrive(dSpd,tSpd, true);
		double output = operator.getRawAxis(1); //Moves the joystick based on Y value
        if (bottomSwitch.get()) // If the forward limit switch is pressed, we want to keep the values between -1 and 0
            output = Math.min(output, 0);
        else if(topSwitch.get()) // If the reversed limit switch is pressed, we want to keep the values between 0 and 1
            output = Math.max(output, 0);
        lift.set(output);
		
		//leds
		// if(operator.getRawButton(1)) {
		// 	red.setRaw(255);
		// }else {
		// 	red.setRaw(0);
		// }
		// if(operator.getRawButton(2)) {
		// 	green.setRaw(255);
		// }else {
		// 	green.setRaw(0);
		// }
		// if(operator.getRawButton(3)) {
		// 	blue.setRaw(255);
		// }else {
		// 	blue.setRaw(0);
		// }
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
