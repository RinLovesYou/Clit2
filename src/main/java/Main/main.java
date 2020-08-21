package Main;


import AudioCommands.*;
import Commands.*;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.log4j.BasicConfigurator;

import javax.security.auth.login.LoginException;
import java.io.IOException;
public class main {


    public static void main(String[] args) throws LoginException, IOException {
        BasicConfigurator.configure();

        CommandClientBuilder builder = new CommandClientBuilder();

        EventWaiter waiter = new EventWaiter();

        builder.setPrefix(info.PREFIX);

        builder.setOwnerId(info.OwnerID);

        builder.setActivity(Activity.listening("\"cli;help for help!\""));

        builder.useHelpBuilder(false);

        builder.addCommands(
                new GuildList(waiter),
                new Help(waiter),
                new UserInfo(waiter),
                new Play(),
                new Skip(),
                new Stop(),
                new Queue(),
                new Playing(),
                new Avatar(waiter)
        );

        JDA jda = new JDABuilder(AccountType.BOT)
                 .setToken(info.TOKEN)
                .addEventListeners(waiter)
                .addEventListeners(builder.build())
                .build();




    }
}
