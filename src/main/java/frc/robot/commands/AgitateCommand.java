package frc.robot.commands;
import java.time.LocalDateTime;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.AgitatorSubsystem;

public class AgitateCommand extends Command {
    
    private AgitatorSubsystem agitator;

    public int agitatorDirection = 1;
    public LocalDateTime lastSwapTime;
    
    public AgitateCommand(AgitatorSubsystem agitator) {
        this.agitator = agitator;
        
        RobotContainer.tab.addDouble("Agitator Direction", ()->agitatorDirection);

        lastSwapTime = LocalDateTime.now();
        RobotContainer.tab.addString("Agitator Swap Time", 
            ()-> lastSwapTime.getHour() + ":" + lastSwapTime.getMinute() + ":" + lastSwapTime.getSecond());

        addRequirements(agitator);
    }

    public void execute() {
        if(LocalDateTime.now().compareTo(lastSwapTime.plusSeconds(Constants.AGITATOR_REVERSE_TIME)) == 1) {
            agitatorDirection *= -1;
            lastSwapTime = LocalDateTime.now();
        }

        agitator.setMotorPower(agitatorDirection, "A button down.");
    }

    public void end(boolean interrupted) {
        agitator.setMotorPower(0, "stopped shooting");
    }
}
