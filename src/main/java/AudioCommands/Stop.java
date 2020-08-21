package AudioCommands;


import Audio.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

@CommandInfo(
        name = "stop",
        description = "stop stop stops top",
        requirements = "The Bot needs permission to join VC"
)

@RequiredPermissions(Permission.VOICE_CONNECT)

@Author("Rin#6969")
public class Stop extends Command {


    private AudioPlayerManager playerManager;


    public Stop() {
        this.name = "stop";
        this.help = "stop stop stop stop";
        this.arguments = "No further Arguments";
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
        this.guildOnly = false;
        this.ownerCommand = false;

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }


    @Override
    protected void execute(CommandEvent event) {

        Guild guild = event.getGuild();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if (guild.getAudioManager().isConnected() || guild.getAudioManager().isAttemptingToConnect()) {
            MusicManager manager = getGuildAudioPlayer(event.getGuild());
            if (manager.player.getPlayingTrack() == null) {
                event.getChannel().sendMessage("Error: not playing anything, leaving channel").queue();
                manager.scheduler.getQueue(event.getGuild()).clear();
                guild.getAudioManager().closeAudioConnection();
            } else {
                manager.player.stopTrack();
                manager.scheduler.getQueue(event.getGuild()).clear();
                guild.getAudioManager().closeAudioConnection();
                playerManager.shutdown();
                manager.player.destroy();
            }
        } else {



            event.getChannel().sendMessage("Bruh").queue();
        }


    }


    private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = Play.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            Play.musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }


}
