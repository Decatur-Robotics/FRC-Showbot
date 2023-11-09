// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import java.util.Map;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.commands.AgitateCommand;
import frc.robot.commands.ShootCommand;
import frc.robot.commands.ShootModeCommand;
import frc.robot.commands.TankDriveCommand;
import frc.robot.subsystems.AgitatorSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

    public static ShuffleboardTab tab = Shuffleboard.getTab("Main");

    // The robot's subsystems and commands are defined here...

    public static ShooterSubsystem shooter;

    public static DrivetrainSubsystem driveTrain;

    public static AgitatorSubsystem agitator;

    public static Joystick primaryJoystick, secondaryJoystick;
    public static JoystickButton primaryTriggerRight, primaryTriggerLeft;
    public static JoystickButton primaryAButton, primaryBButton;
    public static JoystickButton safetyButton;

    public static double shooterFastSpeed = 0.8, shooterSlowSpeed = 0.5, driveSpeed = 0.5;
    public static GenericEntry fastSpeedEntry, slowSpeedEntry, driveSpeedEntry;

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        shooter = new ShooterSubsystem();
        driveTrain = new DrivetrainSubsystem();
        
        agitator = new AgitatorSubsystem();

        // Configure the button bindings
        configurePrimaryButtonBindings();
        configureSecondaryButtonBindings();

        tab.addBoolean("Safe Mode", ()->!Robot.isTestMode);
        tab.addBoolean("Primary Trigger", ()->primaryTriggerRight.getAsBoolean());
        tab.addBoolean("Secondary Button", ()->safetyButton.getAsBoolean());
        tab.addDouble("Shooter Speed Modifier", ()->(shooter.speedMod.getAsDouble()));
        tab.addBoolean("Shooting", () -> primaryTriggerRight.getAsBoolean() && (Robot.isTestMode || safetyButton.getAsBoolean()));
        tab.addDouble("Agitating", () -> primaryAButton.getAsBoolean() ? 1 : primaryBButton.getAsBoolean() ? -1 : 0 );

        fastSpeedEntry = tab.add("Fast Speed", shooterFastSpeed)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 0, "max", 1))
            .getEntry();
        slowSpeedEntry = tab.add("Slow Speed", shooterSlowSpeed)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 0, "max", 1))
            .getEntry();
        driveSpeedEntry = tab.add("Drive Speed", driveSpeed)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", 0, "max", 1))
            .getEntry();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configurePrimaryButtonBindings() {
        primaryJoystick = new Joystick(0);

        primaryTriggerRight = new JoystickButton(primaryJoystick, LogitechControllerButtons.triggerRight);
        primaryTriggerLeft = new JoystickButton(primaryJoystick, LogitechControllerButtons.triggerLeft);
        primaryAButton = new JoystickButton(primaryJoystick, LogitechControllerButtons.a);
        primaryBButton = new JoystickButton(primaryJoystick, LogitechControllerButtons.b);

        primaryTriggerRight.whileTrue(new ShootCommand(safetyButton, shooter));
            //.whileTrue(new AgitateCommand(agitator));

        primaryTriggerLeft.onTrue(new ShootModeCommand(()->fastSpeedEntry.getDouble(shooterFastSpeed), shooter))
            .onFalse(new ShootModeCommand(()->slowSpeedEntry.getDouble(shooterSlowSpeed), shooter));

        driveTrain.setDefaultCommand(new TankDriveCommand(driveTrain, (() -> primaryJoystick.getY()), () -> primaryJoystick.getThrottle()));
    }

    private void configureSecondaryButtonBindings() {
        secondaryJoystick = new Joystick(1);

        //This is the big red button
        safetyButton = new JoystickButton(secondaryJoystick, LogitechControllerButtons.x);
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return null;
    }
}
